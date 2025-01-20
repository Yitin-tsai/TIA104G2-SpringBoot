package chilltrip.member.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import chilltrip.trackmember.model.TrackMemberDAO;
import chilltrip.tripcomment.model.TripCommentVO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class MemberService {

	@Autowired
	private MemberDAO_interface dao;

	@Autowired
	private TrackMemberDAO trackDao;

	@Autowired
	private JedisPool jedisPool;

	@Autowired
	private JavaMailSender mailSender;

	// 從 application.properties 注入郵件帳號與密碼
	@Value("${mail.smtp.user}")
	private String fromEmail; // 郵件帳號

	@Value("${mail.smtp.password}")
	private String fromPassword; // 郵件密碼

	private final ResourceLoader resourceLoader;

	public MemberService(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public MemberVO addMember(MemberVO memberVO) {
		dao.insert(memberVO);
		return memberVO;
	}

	public MemberVO updateMember(MemberVO memberVO) {
		Integer id = memberVO.getMemberId();
		memberVO.setFansNumber(trackDao.getFansQty(id));
		memberVO.setTrackingNumber(trackDao.getTracksQty(id));
		dao.update(memberVO);
		System.out.println("更新後會員資料：" + memberVO);
		return memberVO;
	}

	public List<MemberVO> getAll() {
		return dao.getAll();
	}

	public MemberVO getOneMember(Integer memberId) {
		return dao.findByPrimaryKey(memberId);
	}

	public Set<TripCommentVO> getTripCommentByMember(Integer memberId) {
		return dao.getTripCommentByMember(memberId);
	}

	public void deleteMember(Integer memberId) {
		dao.delete(memberId);
	}

	public void setTempPassword(String email, int expirationTime, String token) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.select(5); // 選擇 db5
			jedis.setex(email, expirationTime, token);
		}
	}

	public String getTempPassword(String email) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.select(5); // 選擇 db5
			return jedis.get(email);
		}
	}

	public void deleteTempPassword(String email) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.select(5); // 選擇 db5
			jedis.del(email);
		}
	}
    
	public boolean updatePassword(String email, String password) {
		return dao.updatePasswordByEmail(email, password);
	}

	public void sendEmail(String to, String subject, String body) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		// 使用從 application.properties 注入的帳號
		helper.setFrom(fromEmail); // 設置發件人
		helper.setTo(to); // 設置收件人
		helper.setSubject(subject); // 設置郵件主題
		helper.setText(body); // 設置郵件內容

//		mailSender.send(message); // 發送郵件
		
		new Thread(()->mailSender.send(message)).start();
	}

	public MemberVO login(String email, String password) {
		MemberVO memberVO = dao.findByEmail(email);
		if (memberVO != null && memberVO.getPassword().equals(password)) {
			return memberVO;
		}
		return null;
	}

	public String encodePhotoToBase64(MemberVO memberVO) {
		byte[] photo = memberVO.getPhoto();
		if (photo != null) {
			System.out.println("照片Base64: " + Base64.getEncoder().encodeToString(photo));
			return Base64.getEncoder().encodeToString(photo);
		}
		return null;
	}

	public boolean checkEmailExists(String email) {
		return dao.isEmailExist(email);
	}

	public String generateCode() {
		StringBuilder verificationCode = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			int randomDigit = (int) (Math.random() * 9) + 1;
			verificationCode.append(randomDigit);
		}
		return verificationCode.toString();
	}

	public void sendVerificationEmail(String email, String code) throws MessagingException {
		String subject = "您的信箱驗證碼";
		String body = "親愛的用戶您好,您的信箱驗證碼為: " + code + "，請於 5 分鐘內輸入驗證碼完成註冊。";
		sendEmail(email, subject, body); // 使用 sendEmail 方法發送郵件
	}

	public boolean verifyEmailCode(String email, String emailCode) {
		try (Jedis jedis = jedisPool.getResource()) {
			String storedCode = jedis.get("verifyEmailCode:" + email);
			return storedCode != null && storedCode.equals(emailCode);
		}
	}

	public byte[] processPhoto(MultipartFile photoFile) throws IOException {
	    if (photoFile == null || photoFile.isEmpty()) { 
	        return getDefaultAvatar(); // 沒有上傳檔案時返回預設圖片
	    } else if (photoFile.getSize() > 5000000) { 
	        throw new IOException("圖片檔案過大，請選擇小於 5MB 的檔案");
	    }
	    return photoFile.getBytes(); // 讀取內容
	}


	public byte[] getDefaultAvatar() {
	    try {
	    	Resource resource = resourceLoader.getResource("classpath:static/img/avatar.png");
	        if (!resource.exists()) {
	            throw new IOException("預設大頭照未找到！");
	        }
	        try (InputStream inputStream = resource.getInputStream();
	             ByteArrayOutputStream byteout = new ByteArrayOutputStream()) {
	            byte[] buffer = new byte[1024];
	            int length;
	            while ((length = inputStream.read(buffer)) != -1) {
	                byteout.write(buffer, 0, length);
	            }
	            return byteout.toByteArray();
	        }
	    } catch (IOException e) {
	    	// 返回一個默認的錯誤圖示圖片
	        return new byte[0];
	    }
	}


	public MemberVO getMemberByEmail(String email) {
		return dao.findByEmail(email);
	}
	
	// 根據使用者ID查詢使用者資料
    public MemberVO getMemberById(Integer memberId) {
        return dao.getMemberById(memberId);
    }
    
    
 // 假設照片存放在 resources/images/member 目錄下
    private static final String PHOTO_PATH = "src/main/resources/ststic/img/member/";
    
    public void updateMemberPhotos() {
        try {
            // 取得所有會員
            List<MemberVO> members = dao.getAll();
            
            for (MemberVO member : members) {
                int memberId = member.getMemberId();
                String photoPath = PHOTO_PATH + memberId + ".jpg"; // 照片命名為 1.jpg, 2.jpg, 3.jpg...
                
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    // 讀取照片文件
                    byte[] photoBytes = Files.readAllBytes(photoFile.toPath());
                    member.setPhoto(photoBytes);
                    
                    // 更新資料庫
                    dao.update(member);
                    System.out.println("成功更新會員 " + memberId + " 的照片");
                } else {
                    System.out.println("找不到會員 " + memberId + " 的照片檔案");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
