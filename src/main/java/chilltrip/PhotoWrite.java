package chilltrip;

import java.sql.*;
import java.io.*;

class PhotoWrite {

    public static void main(String argv[]) {
        Connection con = null;
        PreparedStatement pstmt = null;
        InputStream fin = null;
        int count = 1;
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
        String userid = "root";
        String password = "123456";
        String photos = "static/img/banner-carousel" + count + ".jpg"; // 測試用圖片已置於【專案錄徑】底下的【resources/DB_photos1】目錄內
        String update = "update announcement set cover_photo =? where announcement_id=?";

        try {
            con = DriverManager.getConnection(url, userid, password);
            pstmt = con.prepareStatement(update);

            // 用 ClassLoader 讀取檔案 (從 classpath)
            for (int i = 1; i <= 3; i++) {
                // 確保照片路徑正確
                String photoPath = "static/img/banner-carousel" + i + ".jpg";
                fin = PhotoWrite.class.getClassLoader().getResourceAsStream(photoPath);
                
                if (fin == null) {
                    System.out.println("檔案未找到: " + photoPath);
                    continue;
                }

                pstmt.setInt(2, count); // 設定 announcement_id
                pstmt.setBinaryStream(1, fin); // 設定圖片二進位流
                pstmt.executeUpdate(); // 執行更新
                count++;

                System.out.println("更新資料庫: " + photoPath);
            }

            fin.close();
            pstmt.close();
            System.out.println("加入圖片 - 更新成功...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
