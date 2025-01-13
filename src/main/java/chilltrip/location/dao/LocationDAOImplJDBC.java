package chilltrip.location.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import chilltrip.location.model.LocationVO;
import chilltrip.util.HibernateUtil;

@Repository
public class LocationDAOImplJDBC implements LocationDAO, AutoCloseable {

	// 屬性建立用private，因為只有這個類別會用到
	// 建議資料庫連線字串設定在外部設定檔，這裡只是為了方便範例，所以直接寫在程式碼中(不然每次都要寫相同字串搞人)
	// 建議userid和passwd也設定在外部設定檔，這裡只是為了方便範例，所以直接寫在程式碼中(不然每次都要寫相同字串搞人)
	// 這裡沒有使用finally關閉連線，是因為這裡使用了AutoCloseable，所以在try-with-resources中使用，會自動關閉連線
	// PreparedStatement、ResultSet也是AutoCloseable，所以也會自動關閉
	// LocationDAOImplJDBC自己本身也是AutoCloseable，所以在try-with-resources中使用，會自動關閉
	private SessionFactory factory;
	private Connection connection;
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/TIA104G2?serverTimezone=Asia/Taipei";
	private String userid = "root";
	private String passwd = "123456";

	private static final String INSERT_STMT = "INSERT INTO location (address, comments_number, score, location_name) VALUES (?,?,?,?);";
	private static final String GET_ALL_STMT = "SELECT location_id,address, create_time, comments_number ,score, location_name FROM location order by location_id";
	private static final String GET_ONE_STMT = "SELECT location_id,address, create_time, comments_number ,score, location_name FROM location WHERE location_id=?";
	private static final String GET_BY_LOCATION_NAME_STMT = "SELECT location_id, address, create_time, comments_number ,score, location_name FROM location WHERE location_name =?";
	private static final String DELETE = "DELETE FROM location where location_id=?";
	private static final String UPDATE = "UPDATE location set address=?, comments_number=?, score=?, location_name=? where location_id = ?";

	public LocationDAOImplJDBC() {
		
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

	@Override
	public void insert(LocationVO locationVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT)) {
			pstmt.setString(1, locationVO.getAddress());
			pstmt.setInt(2, locationVO.getComments_number());
			pstmt.setFloat(3, locationVO.getScore());
			pstmt.setString(4, locationVO.getLocation_name());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	@Override
	public void update(LocationVO locationVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE)) {
			pstmt.setString(1, locationVO.getAddress());
			pstmt.setInt(2, locationVO.getComments_number());
			pstmt.setFloat(3, locationVO.getScore());
			pstmt.setString(4, locationVO.getLocation_name());
			pstmt.setInt(5, locationVO.getLocationid());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	@Override
	public void delete(Integer locationid) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(DELETE)) {
			pstmt.setInt(1, locationid);
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}
	
	
	private Session getSession() {
		return factory.getCurrentSession();
	}
	@Override
	public List<LocationVO> getAll() {
		List<LocationVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALL_STMT);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				LocationVO locationVO = new LocationVO();
				locationVO.setLocationid(rs.getInt("location_id"));
				locationVO.setAddress(rs.getString("address"));
				locationVO.setCreate_time(rs.getTimestamp("create_time"));
				locationVO.setComments_number(rs.getInt("comments_number"));
				locationVO.setScore(rs.getFloat("score"));
				locationVO.setLocation_name(rs.getString("location_name"));
				list.add(locationVO);
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getAllPro() {
		List<Map<String, Object>> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALL_STMT);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				Map<String, Object> map = new HashMap<>();

				map.put("Location_id", rs.getInt("location_id"));
				map.put("address", rs.getString("address"));
				map.put("create_time", rs.getString("create_time"));
				map.put("comments_number", rs.getString("comments_number"));
				map.put("score", rs.getString("score"));
				map.put("location_name", rs.getString("location_name"));
				list.add(map);
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	@Override
	public LocationVO getById(Integer locationid) {
		LocationVO locationVO = null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ONE_STMT)) {
			pstmt.setInt(1, locationid);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					locationVO = new LocationVO();
					locationVO.setLocationid(rs.getInt("location_id"));
					locationVO.setAddress(rs.getString("address"));
					locationVO.setCreate_time(rs.getTimestamp("create_time"));
					locationVO.setComments_number(rs.getInt("comments_number"));
					locationVO.setScore(rs.getFloat("score"));
					locationVO.setLocation_name(rs.getString("location_name"));
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return locationVO;
	}

	@Override
	public List<Map<String, Object>> getLocationByName(String location_name) {
		List<Map<String, Object>> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_BY_LOCATION_NAME_STMT);) {
			// 設定參數
			pstmt.setString(1, location_name);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {

					Map<String, Object> map = new HashMap<>();

					map.put("location_id", rs.getInt("location_id"));
					map.put("address", rs.getString("address"));
					map.put("create_time", rs.getString("create_time"));
					map.put("comments_number", rs.getString("comments_number"));
					map.put("score", rs.getString("score"));
					map.put("location_name", rs.getString("location_name"));
					list.add(map);
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}
	
	//用googleID找Location物件 --> 為了要確認這個物件是不是被創建了
	@Override
	public LocationVO findByGooglePlaceId(String googlePlaceId) {
	    LocationVO locationVO = null;
	    String GET_BY_GOOGLE_PLACE_ID = 
	        "SELECT * FROM location WHERE google_place_id = ?";
	    
	    try (PreparedStatement pstmt = getConnection().prepareStatement(GET_BY_GOOGLE_PLACE_ID)) {
	        pstmt.setString(1, googlePlaceId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                locationVO = new LocationVO();
	                locationVO.setLocationid(rs.getInt("location_id"));
	                locationVO.setGooglePlaceId(rs.getString("google_place_id"));
	                locationVO.setLocation_name(rs.getString("location_name"));
	                locationVO.setAddress(rs.getString("address"));
	                locationVO.setLatitude(rs.getBigDecimal("latitude"));
	                locationVO.setLongitude(rs.getBigDecimal("longitude"));
	                locationVO.setScore(rs.getFloat("score"));
	                locationVO.setCreate_time(rs.getTimestamp("create_time"));
	                locationVO.setUpdateTime(rs.getTimestamp("update_time"));
	                locationVO.setComments_number(rs.getInt("comments_number"));
	                locationVO.setRatingCount(rs.getInt("rating_count"));
	            }
	        }
	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occured. " + se.getMessage());
	    }
	    return locationVO;
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

//	public static void main(String[] args) {
//		try (LocationDAOImplJDBC dao = new LocationDAOImplJDBC()) {
//			// 測試程式碼
//			// 插入SQL 測試
//			LocationVO locationVO = new LocationVO();
//			locationVO.setAddress("日本東京都文京區後樂");
//			
//			locationVO.setComments_number(100);
//			locationVO.setScore(5.0f);
//			locationVO.setLocation_name("東京巨蛋");
//			dao.insert(locationVO);
//		} catch (RuntimeException e) {
//			System.err.println("An unexpected error occurred: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}
}