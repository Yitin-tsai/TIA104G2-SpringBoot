package chilltrip.tripphoto.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import chilltrip.tripphoto.model.TripphotoVO;

public class TripphotoDAOImplJDBC implements TripphotoDAO, AutoCloseable {

	// 屬性建立用private，因為只有這個類別會用到
	// 建議資料庫連線字串設定在外部設定檔，這裡只是為了方便範例，所以直接寫在程式碼中(不然每次都要寫相同字串搞人)
	// 建議userid和passwd也設定在外部設定檔，這裡只是為了方便範例，所以直接寫在程式碼中(不然每次都要寫相同字串搞人)
	// 這裡沒有使用finally關閉連線，是因為這裡使用了AutoCloseable，所以在try-with-resources中使用，會自動關閉連線
	// PreparedStatement、ResultSet也是AutoCloseable，所以也會自動關閉
	// LocationDAOImplJDBC自己本身也是AutoCloseable，所以在try-with-resources中使用，會自動關閉
//	private SessionFactory factory;
	private Connection connection;
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/TIA104G2?serverTimezone=Asia/Taipei";
	private String userid = "root";
	private String passwd = "123456";

	private static final String INSERT_STMT = "INSERT INTO trip_photo (trip_id, photo, photo_type) VALUES (?,?,?)";
	private static final String UPDATE = "UPDATE trip_photo SET trip_id=?, photo=?, photo_type=? WHERE trip_photo_id=?";
	private static final String DELETE = "DELETE FROM trip_photo WHERE trip_photo_id=?";
	// 獲取某篇文章的封面/內文照片
	private static final String GET_TRIP_PHOTO = "SELECT trip_photo_id, trip_id , photo, photo_type FROM trip_photo WHERE trip_id=? AND photo_type=?";
	//獲取一個照片物件
	private static final String GET_TRIPPHOTO_BY_ID = "SELECT trip_photo_id, trip_id , photo, photo_type FROM trip_photo WHERE trip_photo_id=?";

	public TripphotoDAOImplJDBC() {

		// 建構子一開始確認是否有載入驅動程式，並且建立連線
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(url, userid, passwd);
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException("Couldn't load database driver or connect to database. " + e.getMessage());
		}
	}

	private Connection getConnection() {
		return this.connection;
	}

	// 新增照片
	public void insert(TripphotoVO tripphotoVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT)) {
			pstmt.setInt(1, tripphotoVO.getTrip_id());
			pstmt.setBytes(2, tripphotoVO.getPhoto());
			pstmt.setInt(3, tripphotoVO.getPhoto_type());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 更新照片
	public void update(TripphotoVO tripphotoVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE)) {
			pstmt.setInt(1, tripphotoVO.getTrip_id());
			pstmt.setBytes(2, tripphotoVO.getPhoto());
			pstmt.setInt(3, tripphotoVO.getPhoto_type());
			pstmt.setInt(4, tripphotoVO.getTrip_photo_id());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 刪除照片
	public void delete(Integer trip_photo_id) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(DELETE)) {
			pstmt.setInt(1, trip_photo_id);
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 獲取某篇文章的封面照片/內文照片
	public List<Map<String, Object>> getOneTripPhotoByType(Integer tripid, Integer triptype) {
		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_PHOTO)) {
			pstmt.setInt(1, tripid);
			pstmt.setInt(2, triptype);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();

					map.put("trip_photo_id", rs.getInt("trip_photo_id"));
					map.put("trip_id", rs.getInt("trip_id"));
					map.put("photo", rs.getBytes("photo"));
					map.put("photo_type", rs.getInt("photo_type"));
					list.add(map);
				}
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}
	
	//通過ID拿到一個行程照片物件  GET_TRIPPHOTO_BY_ID
	public TripphotoVO getTripphotoById (Integer tripphotoid) {
		TripphotoVO tripphotoVO = null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIPPHOTO_BY_ID)) {
			pstmt.setInt(1, tripphotoid);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					tripphotoVO = new TripphotoVO();
					tripphotoVO.setTrip_photo_id(rs.getInt("trip_photo_id"));
					tripphotoVO.setTrip_id(rs.getInt("trip_id"));
					tripphotoVO.setPhoto(rs.getBytes("photo"));
					tripphotoVO.setPhoto_type(rs.getInt("photo_type"));
				}
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return tripphotoVO;
	}
	
	@Override
	public void close() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				throw new RuntimeException("Failed to close the connection. " + e.getMessage());
			}
		}
	}
	

}
