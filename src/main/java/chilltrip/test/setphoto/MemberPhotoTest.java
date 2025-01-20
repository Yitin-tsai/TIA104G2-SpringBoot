package chilltrip.test.setphoto;

import java.io.File;
import java.nio.file.Files;
import chilltrip.member.model.MemberJDBCDAO;
import chilltrip.member.model.MemberVO;

public class MemberPhotoTest {
    
    public static void main(String[] args) {
        try {
            // 批次更新1-20號會員的照片
            for(int i = 1; i <= 20; i++) {
                try {
                    updateSingleMemberPhoto(i);
                } catch (Exception e) {
                    System.out.println("會員 " + i + " 更新失敗: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("照片批次更新完成");
        } catch (Exception e) {
            System.out.println("執行時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void updateSingleMemberPhoto(int memberId) throws Exception {
        try {
            // 1. 準備 DAO
            MemberJDBCDAO dao = new MemberJDBCDAO();
            
            // 2. 設定照片路徑 (改用webapp下的路徑)
            String photoPath = "src/main/resources/static/img/member/" + memberId + ".jpg";
            System.out.println("正在處理會員 " + memberId + " 的照片...");
            
            // 3. 檢查檔案是否存在
            File photoFile = new File(photoPath);
            if (!photoFile.exists()) {
                throw new Exception("找不到照片檔案: " + photoPath);
            }
            
            // 4. 讀取照片檔案
            byte[] photoBytes = Files.readAllBytes(photoFile.toPath());
            System.out.println("成功讀取照片 " + memberId + ".jpg，大小: " + photoBytes.length + " bytes");
            
            // 5. 取得會員資料
            MemberVO member = dao.findByPrimaryKey(memberId);
            if (member == null) {
                throw new Exception("找不到會員ID: " + memberId);
            }
            
            // 6. 更新照片
            member.setPhoto(photoBytes);
            dao.update(member);
            
            System.out.println("成功更新會員 " + memberId + " 的照片");
            System.out.println("----------------------------------------");
            
        } catch (Exception e) {
            System.out.println("更新會員 " + memberId + " 照片時發生錯誤");
            throw e; // 將錯誤往上拋出
        }
    }
}