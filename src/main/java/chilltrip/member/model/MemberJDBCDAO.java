package chilltrip.member.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import chilltrip.tripcomment.model.TripCommentVO;

public class MemberJDBCDAO implements MemberDAO_interface {
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
	String userid = "root";
	String password = "123456";

	private static final String INSERT_STMT = "INSERT INTO member (email,account,password,name,phone,status,create_time,nick_name,gender,birthday,company_id,E_receipt_carrier,credit_card,tracking_number,fans_number,photo) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String GET_ALL_STMT = "SELECT member_id,email,account,password,name,phone,status,create_time,nick_name,gender,birthday,company_id,E_receipt_carrier,credit_card,tracking_number,fans_number,photo FROM member ORDER BY member_id";
	private static final String GET_ONE_STMT = "SELECT member_id,email,account,password,name,phone,status,create_time,nick_name,gender,birthday,company_id,E_receipt_carrier,credit_card,tracking_number,fans_number,photo FROM member WHERE member_id = ?";
	private static final String DELETE_MEMBER = "DELETE FROM member WHERE member_id = ?";
	private static final String DELETE_TRIP_COMMENT = "DELETE FROM trip_comment WHERE member_id = ?";
	private static final String UPDATE = "UPDATE member set email=?,account=?,password=?,name=?,phone=?,status=?,create_time=?,nick_name=?,gender=?,birthday=?,company_id=?,E_receipt_carrier=?,credit_card=?,tracking_number=?,fans_number=?,photo=? WHERE member_id = ?";
	private static final String GET_TRIPCOMMENT_BY_MEMBER = "SELECT * FROM tia104g2.trip_comment WHERE member_id=? ORDER BY trip_comment_id";
	private static final String GET_ONE_BY_EMAIL = "SELECT * FROM member WHERE email = ?";
	private static final String CHECK_EMAIL = "SELECT 1 FROM member WHERE email = ?";
	private static final String UPDATE_PASSWORD_BY_EMAIL = "UPDATE member SET password = ? WHERE email = ?";

	@Override
	public void insert(MemberVO memberVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setString(1, memberVO.getEmail());
			pstmt.setString(2, memberVO.getAccount());
			pstmt.setString(3, memberVO.getPassword());
			pstmt.setString(4, memberVO.getName());
			pstmt.setString(5, memberVO.getPhone());
			pstmt.setInt(6, memberVO.getStatus());
			pstmt.setTimestamp(7, memberVO.getCreateTime());
			pstmt.setString(8, memberVO.getNickName());
			pstmt.setInt(9, memberVO.getGender());
			pstmt.setDate(10, memberVO.getBirthday());
			pstmt.setString(11, memberVO.getCompanyId());
			pstmt.setString(12, memberVO.getEreceiptCarrier());
			pstmt.setString(13, memberVO.getCreditCard());
			pstmt.setInt(14, memberVO.getTrackingNumber());
			pstmt.setInt(15, memberVO.getFansNumber());
			pstmt.setBytes(16, memberVO.getPhoto() != null ? memberVO.getPhoto() : null);

			pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void update(MemberVO memberVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setString(1, memberVO.getEmail());
			pstmt.setString(2, memberVO.getAccount());
			pstmt.setString(3, memberVO.getPassword());
			pstmt.setString(4, memberVO.getName());
			pstmt.setString(5, memberVO.getPhone());
			pstmt.setInt(6, memberVO.getStatus());
			pstmt.setTimestamp(7, memberVO.getCreateTime());
			pstmt.setString(8, memberVO.getNickName());
			pstmt.setInt(9, memberVO.getGender());
			pstmt.setDate(10, memberVO.getBirthday());
			pstmt.setString(11, memberVO.getCompanyId());
			pstmt.setString(12, memberVO.getEreceiptCarrier());
			pstmt.setString(13, memberVO.getCreditCard());
			pstmt.setInt(14, memberVO.getTrackingNumber());
			pstmt.setInt(15, memberVO.getFansNumber());
			pstmt.setBytes(16, memberVO.getPhoto() != null ? memberVO.getPhoto() : null);
			pstmt.setInt(17, memberVO.getMemberId());

			pstmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void delete(Integer memberId) {
		int updateCount_TripComments = 0;

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行

			// 1.設定於 pstm.executeUpdate()之前
			con.setAutoCommit(false);

			// 先刪除行程留言
			pstmt = con.prepareStatement(DELETE_TRIP_COMMENT);
			pstmt.setInt(1, memberId);
			updateCount_TripComments = pstmt.executeUpdate();

			// 再刪除會員
			pstmt = con.prepareStatement(DELETE_MEMBER);
			pstmt.setInt(1, memberId);
			pstmt.executeUpdate();

			// 2.設定於 pstm.executeUpdate()之後
			con.commit();
			con.setAutoCommit(true);
			System.out.println("刪除會員編號" + memberId + "時,共有行程留言" + updateCount_TripComments + "則同時被刪除");

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			if (con != null) {
				try {
					// 3.設定於當有exception發生時之catch區塊內
					con.rollback();
				} catch (SQLException excep) {
					throw new RuntimeException("發生 rollback 錯誤" + excep.getMessage());
				}
			}
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public MemberVO findByPrimaryKey(Integer memberId) {

		MemberVO memberVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, memberId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				memberVO = new MemberVO();
				memberVO.setMemberId(rs.getInt("member_id"));
				memberVO.setEmail(rs.getString("email"));
				memberVO.setAccount(rs.getString("account"));
				memberVO.setPassword(rs.getString("password"));
				memberVO.setName(rs.getString("name"));
				memberVO.setPhone(rs.getString("phone"));
				memberVO.setStatus(rs.getInt("status"));
				memberVO.setCreateTime(rs.getTimestamp("create_time"));
				memberVO.setNickName(rs.getString("nick_name"));
				memberVO.setGender(rs.getInt("gender"));
				memberVO.setBirthday(rs.getDate("birthday"));
				memberVO.setCompanyId(rs.getString("company_id"));
				memberVO.setEreceiptCarrier(rs.getString("E_receipt_carrier"));
				memberVO.setCreditCard(rs.getString("credit_card"));
				memberVO.setTrackingNumber(rs.getInt("tracking_number"));
				memberVO.setFansNumber(rs.getInt("fans_number"));
				memberVO.setPhoto(rs.getBytes("photo"));
//				if(rs.getBytes("photo") != null) {
//					memberVO.setPhoto_base64(new String(Base64.getEncoder().encodeToString(rs.getBytes("photo"))));	
//				}
			}

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

		return memberVO;
	}

	@Override
	public List<MemberVO> getAll() {

		List<MemberVO> list = new ArrayList<MemberVO>();
		MemberVO memberVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				memberVO = new MemberVO();
				memberVO.setMemberId(rs.getInt("member_id"));
				memberVO.setEmail(rs.getString("email"));
				memberVO.setAccount(rs.getString("account"));
				memberVO.setPassword(rs.getString("password"));
				memberVO.setName(rs.getString("name"));
				memberVO.setPhone(rs.getString("phone"));
				memberVO.setStatus(rs.getInt("status"));
				memberVO.setCreateTime(rs.getTimestamp("create_time"));
				memberVO.setNickName(rs.getString("nick_name"));
				memberVO.setGender(rs.getInt("gender"));
				memberVO.setBirthday(rs.getDate("birthday"));
				memberVO.setCompanyId(rs.getString("company_id"));
				memberVO.setEreceiptCarrier(rs.getString("E_receipt_carrier"));
				memberVO.setCreditCard(rs.getString("credit_card"));
				memberVO.setTrackingNumber(rs.getInt("tracking_number"));
				memberVO.setFansNumber(rs.getInt("fans_number"));
				memberVO.setPhoto(rs.getBytes("photo"));
//				if(rs.getBytes("photo") != null) {
//					memberVO.setPhoto_base64(new String(Base64.getEncoder().encodeToString(rs.getBytes("photo"))));	
//				}

				list.add(memberVO); // 將該行資料儲存在 list 集合中

			}

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

		return list;
	}

	@Override
	public Set<TripCommentVO> getTripCommentByMember(Integer memberId) {
		Set<TripCommentVO> set = new LinkedHashSet<TripCommentVO>();
		TripCommentVO tripCommentVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_TRIPCOMMENT_BY_MEMBER);
			pstmt.setInt(1, memberId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				tripCommentVO = new TripCommentVO();
				tripCommentVO.setTripCommentId(rs.getInt("trip_comment_id"));
				tripCommentVO.setTripId(rs.getInt("trip_id"));
				tripCommentVO.setScore(rs.getInt("score"));
				tripCommentVO.setPhoto(rs.getBytes("photo"));
				tripCommentVO.setCreateTime(rs.getTimestamp("create_time"));
				tripCommentVO.setContent(rs.getString("content"));
				tripCommentVO.setMemberId(rs.getInt("member_id"));
				if (rs.getBytes("photo") != null) {
					tripCommentVO.setPhoto_base64(new String(Base64.getEncoder().encodeToString(rs.getBytes("photo"))));
				}
				set.add(tripCommentVO);
			}

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

		return set;
	}

	@Override
	public MemberVO findByEmail(String email) {
		MemberVO memberVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_ONE_BY_EMAIL);
			
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				memberVO = new MemberVO();
				memberVO.setMemberId(rs.getInt("member_id"));
				memberVO.setEmail(rs.getString("email"));
				memberVO.setAccount(rs.getString("account"));
				memberVO.setPassword(rs.getString("password"));
				memberVO.setName(rs.getString("name"));
				memberVO.setPhone(rs.getString("phone"));
				memberVO.setStatus(rs.getInt("status"));
				memberVO.setCreateTime(rs.getTimestamp("create_time"));
				memberVO.setNickName(rs.getString("nick_name"));
				memberVO.setGender(rs.getInt("gender"));
				memberVO.setBirthday(rs.getDate("birthday"));
				memberVO.setCompanyId(rs.getString("company_id"));
				memberVO.setEreceiptCarrier(rs.getString("E_receipt_carrier"));
				memberVO.setCreditCard(rs.getString("credit_card"));
				memberVO.setTrackingNumber(rs.getInt("tracking_number"));
				memberVO.setFansNumber(rs.getInt("fans_number"));
				memberVO.setPhoto(rs.getBytes("photo"));
//					if(rs.getBytes("photo") != null) {
//						memberVO.setPhoto_base64(new String(Base64.getEncoder().encodeToString(rs.getBytes("photo"))));	
//					}
			}

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

		return memberVO;
	}
	
	// 判斷 email 是否已經存在
    public boolean isEmailExist(String email) {
    	Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

        try {
        	Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
            pstmt = con.prepareStatement(CHECK_EMAIL);
            pstmt.setString(1, email);

            rs = pstmt.executeQuery();

            // 如果查詢結果不為空，則表示 email 已存在
            return rs.next();  // 若有結果返回 true，否則返回 false
        } catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
            se.printStackTrace();
            return false;
        } finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

    }
    
    public boolean updatePasswordByEmail(String email, String newPassword) {
    	Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(UPDATE_PASSWORD_BY_EMAIL);

			pstmt.setString(1, newPassword);
	        pstmt.setString(2, email);

	        int rowsUpdated = pstmt.executeUpdate();  // 執行更新操作
	        return rowsUpdated > 0; // 返回是否有資料被更新

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
    	
    	
    }

	public static void main(String[] args) {
		MemberJDBCDAO dao = new MemberJDBCDAO();

//		// 新增
//		MemberVO memberVO1 = new MemberVO();
//		memberVO1.setEmail(new String("123@abc.com"));
//		memberVO1.setAccount(new String("123"));
//		memberVO1.setPassword(new String("123"));
//		memberVO1.setName(new String("王小明"));
//		memberVO1.setPhone(new String("0912345678"));
//		memberVO1.setStatus(new Integer("0"));
//		memberVO1.setCreateTime(java.sql.Timestamp.valueOf("2024-12-05 18:30:45"));
//		memberVO1.setNickName(new String("mike"));
//		memberVO1.setGender(new Integer("0"));
//		
//		// 使用 java.sql.Date.valueOf() 方法來轉換日期
//		Date birthday = Date.valueOf("2004-12-05");
//		memberVO1.setBirthday(birthday);
//		
//		memberVO1.setCompanyId(null);
//		memberVO1.setEreceiptCarrier(null);
//		memberVO1.setCreditCard(null);
//		memberVO1.setTrackingNumber(0);
//		memberVO1.setFansNumber(0);
//		
//		// 讀取圖片並轉換為 byte[]
//		byte[] photoBytes = null;
//		BufferedInputStream bis = null;
//		try {
//			FileInputStream fis = new FileInputStream("src/main/webapp/frontend/img/user.jpg");
//			bis = new BufferedInputStream(fis);
//			photoBytes = bis.readAllBytes();  // 讀取檔案並轉換為 byte[]
//		}catch(IOException e) {
//			e.printStackTrace();
//		}finally {
//			if(bis != null) {
//				try {
//					bis.close();
//				}catch(IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		memberVO1.setPhoto(photoBytes);   // 將圖片設置到 memberVO1 物件的 photo 屬性
//
//		// 呼叫 DAO 方法插入資料
//		dao.insert(memberVO1);
//		
//		
//		// 修改
//		MemberVO memberVO2 = new MemberVO();
//		memberVO2.setMemberId(new Integer(4));
//		memberVO2.setEmail(new String("455@abc.com"));
//		memberVO2.setAccount(new String("456"));
//		memberVO2.setPassword(new String("456"));
//		memberVO2.setName(new String("陳阿泰"));
//		memberVO2.setPhone(new String("0987654321"));
//		memberVO2.setStatus(new Integer("0"));
//		memberVO2.setCreateTime(java.sql.Timestamp.valueOf("2024-12-20 18:30:45"));
//		memberVO2.setNickName(new String("Tom"));
//		memberVO2.setGender(new Integer("0"));
//		
//		// 使用 java.sql.Date.valueOf() 方法來轉換日期
//		Date birthday2 = Date.valueOf("2004-12-05");
//		memberVO2.setBirthday(birthday2);
//		
//		memberVO2.setCompanyId(null);
//		memberVO2.setEreceiptCarrier(null);
//		memberVO2.setCreditCard(null);
//		memberVO2.setTrackingNumber(0);
//		memberVO2.setFansNumber(0);
//		
//		// 讀取圖片並轉換為 byte[]
//		byte[] photoBytes = null;
//		BufferedInputStream bis = null;
//		try {
//			FileInputStream fis = new FileInputStream("src/main/webapp/frontend/img/course-2.jpg");
//			bis = new BufferedInputStream(fis);
//			photoBytes = bis.readAllBytes();  // 讀取檔案並轉換為 byte[]
//		}catch(IOException e) {
//			e.printStackTrace();
//		}finally {
//			if(bis != null) {
//				try {
//					bis.close();
//				}catch(IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		memberVO2.setPhoto(photoBytes);   // 將圖片設置到 memberVO2 物件的 photo 屬性
//
//		// 呼叫 DAO 方法插入資料
//		dao.update(memberVO2);
//		
//		// 查詢
//		MemberVO memberVO3 = dao.findByPrimaryKey(1);
//		System.out.println(memberVO3.getMemberId() + ",");
//		System.out.println(memberVO3.getEmail() + ",");
//		System.out.println(memberVO3.getAccount() + ",");
//		System.out.println(memberVO3.getPassword() + ",");
//		System.out.println(memberVO3.getName() + ",");
//		System.out.println(memberVO3.getPhone() + ",");
//		System.out.println(memberVO3.getStatus() + ",");
//		System.out.println(memberVO3.getCreateTime() + ",");
//		System.out.println(memberVO3.getNickName() + ",");
//		System.out.println(memberVO3.getGender() + ",");
//		System.out.println(memberVO3.getBirthday() + ",");
//		System.out.println(memberVO3.getCompanyId() + ",");
//		System.out.println(memberVO3.getEreceiptCarrier() + ",");
//		System.out.println(memberVO3.getCreditCard() + ",");
//		System.out.println(memberVO3.getTrackingNumber() + ",");
//		System.out.println(memberVO3.getFansNumber() + ",");
//		System.out.println(memberVO3.getPhoto());
//		System.out.println("---------------------");
//		
//		// 查詢全部會員
//		List<MemberVO> list = dao.getAll();
//		for(MemberVO member : list) {
//			System.out.println(member.getMemberId() + ",");
//			System.out.println(member.getEmail() + ",");
//			System.out.println(member.getAccount() + ",");
//			System.out.println(member.getPassword() + ",");
//			System.out.println(member.getName() + ",");
//			System.out.println(member.getPhone() + ",");
//			System.out.println(member.getStatus() + ",");
//			System.out.println(member.getCreateTime() + ",");
//			System.out.println(member.getNickName() + ",");
//			System.out.println(member.getGender() + ",");
//			System.out.println(member.getBirthday() + ",");
//			System.out.println(member.getCompanyId() + ",");
//			System.out.println(member.getEreceiptCarrier() + ",");
//			System.out.println(member.getCreditCard() + ",");
//			System.out.println(member.getTrackingNumber() + ",");
//			System.out.println(member.getFansNumber() + ",");
//			System.out.println(member.getPhoto());
//			System.out.println();
//		}
//		
//		// 查詢某會員的留言
//		Set<TripCommentVO> set = dao.getTripCommentByMember(1);
//		for(TripCommentVO aTripcom : set) {
//			System.out.println(aTripcom.getTripCommentId() + ",");
//			System.out.println(aTripcom.getMemberId() + ",");
//			System.out.println(aTripcom.getTripId() + ",");
//			System.out.println(aTripcom.getScore() + ",");
//			System.out.println(aTripcom.getPhoto() + ",");
//			System.out.println(aTripcom.getCreateTime() + ",");
//			System.out.println(aTripcom.getContent());
//			System.out.println();
//		}
//
//		// 呼叫 DAO 刪除資料
//		dao.delete(4);

	}

}
