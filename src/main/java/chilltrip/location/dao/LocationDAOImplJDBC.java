package chilltrip.location.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

	private static final String INSERT_STMT = "INSERT INTO location (google_place_id, location_name, address, latitude, longitude, score, rating_count, comments_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	private static final String GET_ALL_STMT =  "SELECT location_id, google_place_id, location_name, address, latitude, longitude, score, rating_count, comments_number, create_time, update_time FROM location ORDER BY location_id";
	private static final String GET_ONE_STMT = "SELECT location_id, google_place_id, location_name, address, latitude, longitude, score, rating_count, comments_number, create_time, update_time FROM location WHERE location_id = ?";
	private static final String GET_BY_LOCATION_NAME_STMT = "SELECT location_id, google_place_id, location_name, address, latitude, longitude, score, rating_count, comments_number, create_time, update_time FROM location WHERE location_name = ?";
	private static final String DELETE = "DELETE FROM location where location_id=?";
	private static final String UPDATE =  "UPDATE location SET google_place_id = ?, location_name = ?, address = ?, latitude = ?, longitude = ?, score = ?, rating_count = ?, comments_number = ? WHERE location_id = ?";

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
	public void insertOrUpdate(LocationVO locationVO) {
	    // 先檢查是否存在該 google_place_id
	    String checkSql = "SELECT location_id FROM location WHERE google_place_id = ?";
	    try (PreparedStatement checkStmt = getConnection().prepareStatement(checkSql)) {
	        checkStmt.setString(1, locationVO.getGooglePlaceId());
	        ResultSet rs = checkStmt.executeQuery();
	        
	        if (rs.next()) {
	            // 如果存在，執行更新
	            try (PreparedStatement updateStmt = getConnection().prepareStatement(UPDATE)) {
	                updateStmt.setString(1, locationVO.getGooglePlaceId());
	                updateStmt.setString(2, locationVO.getLocation_name());
	                updateStmt.setString(3, locationVO.getAddress());
	                updateStmt.setBigDecimal(4, locationVO.getLatitude());
	                updateStmt.setBigDecimal(5, locationVO.getLongitude());
	                updateStmt.setFloat(6, locationVO.getScore());
	                updateStmt.setInt(7, locationVO.getRatingCount());
	                updateStmt.setInt(8, locationVO.getComments_number());
	                updateStmt.setInt(9, rs.getInt("location_id"));
	                updateStmt.executeUpdate();
	            }
	        } else {
	            // 如果不存在，執行插入
	            try (PreparedStatement insertStmt = getConnection().prepareStatement(INSERT_STMT)) {
	                insertStmt.setString(1, locationVO.getGooglePlaceId());
	                insertStmt.setString(2, locationVO.getLocation_name());
	                insertStmt.setString(3, locationVO.getAddress());
	                insertStmt.setBigDecimal(4, locationVO.getLatitude());
	                insertStmt.setBigDecimal(5, locationVO.getLongitude());
	                insertStmt.setFloat(6, locationVO.getScore());
	                insertStmt.setInt(7, locationVO.getRatingCount());
	                insertStmt.setInt(8, locationVO.getComments_number());
	                insertStmt.executeUpdate();
	            }
	        }
	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occured. " + se.getMessage());
	    }
	}

	@Override
	public void insert(LocationVO locationVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT)) {
			pstmt.setString(1, locationVO.getGooglePlaceId());
			pstmt.setString(2, locationVO.getLocation_name());
			pstmt.setString(3, locationVO.getAddress());pstmt.setBigDecimal(4, locationVO.getLatitude());    // DECIMAL(10,8)
	        pstmt.setBigDecimal(5, locationVO.getLongitude());   // DECIMAL(11,8)
	        pstmt.setFloat(6, locationVO.getScore());            // FLOAT(2,1)
	        pstmt.setInt(7, locationVO.getRatingCount());        
	        pstmt.setInt(8, locationVO.getComments_number());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}
	
	
	//返回自增主鍵
	@Override
	public Integer insertAndGetPk (LocationVO locationVO) {
	    Integer generatedId = null;
	    try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT, 
	            Statement.RETURN_GENERATED_KEYS)) {  // 請求返回生成的鍵
	            
	        pstmt.setString(1, locationVO.getGooglePlaceId());
	        pstmt.setString(2, locationVO.getLocation_name());
	        pstmt.setString(3, locationVO.getAddress());
	        pstmt.setBigDecimal(4, locationVO.getLatitude());
	        pstmt.setBigDecimal(5, locationVO.getLongitude());
	        pstmt.setFloat(6, locationVO.getScore());
	        pstmt.setInt(7, locationVO.getRatingCount());
	        pstmt.setInt(8, locationVO.getComments_number());
	        
	        pstmt.executeUpdate();
	        
	        // 獲取自動生成的主鍵
	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                generatedId = rs.getInt(1);
	                locationVO.setLocationId(generatedId);  // 設置 ID 到 VO 物件
	            }
	        }
	        
	        return generatedId;
	        
	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occured. " + se.getMessage());
	    }
	}

	@Override
	public void update(LocationVO locationVO) {
	    try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE)) {
	        // 按照 UPDATE SQL 的順序設置參數
	        pstmt.setString(1, locationVO.getGooglePlaceId());
	        pstmt.setString(2, locationVO.getLocation_name());
	        pstmt.setString(3, locationVO.getAddress());
	        pstmt.setBigDecimal(4, locationVO.getLatitude());
	        pstmt.setBigDecimal(5, locationVO.getLongitude());
	        pstmt.setFloat(6, locationVO.getScore());
	        pstmt.setInt(7, locationVO.getRatingCount());
	        pstmt.setInt(8, locationVO.getComments_number());
	        pstmt.setInt(9, locationVO.getLocationId());
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
	            locationVO.setLocationId(rs.getInt("location_id"));
	            locationVO.setGooglePlaceId(rs.getString("google_place_id"));
	            locationVO.setLocation_name(rs.getString("location_name"));
	            locationVO.setAddress(rs.getString("address"));
	            locationVO.setLatitude(rs.getBigDecimal("latitude"));
	            locationVO.setLongitude(rs.getBigDecimal("longitude"));
	            locationVO.setScore(rs.getFloat("score"));
	            locationVO.setRatingCount(rs.getInt("rating_count"));
	            locationVO.setComments_number(rs.getInt("comments_number"));
	            locationVO.setCreate_time(rs.getTimestamp("create_time"));
	            locationVO.setUpdateTime(rs.getTimestamp("update_time"));
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
	            map.put("location_id", rs.getInt("location_id"));
	            map.put("google_place_id", rs.getString("google_place_id"));
	            map.put("location_name", rs.getString("location_name"));
	            map.put("address", rs.getString("address"));
	            map.put("latitude", rs.getBigDecimal("latitude"));
	            map.put("longitude", rs.getBigDecimal("longitude"));
	            map.put("score", rs.getFloat("score"));
	            map.put("rating_count", rs.getInt("rating_count"));
	            map.put("comments_number", rs.getInt("comments_number"));
	            map.put("create_time", rs.getTimestamp("create_time"));
	            map.put("update_time", rs.getTimestamp("update_time"));
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
	                locationVO.setLocationId(rs.getInt("location_id"));
	                locationVO.setGooglePlaceId(rs.getString("google_place_id"));
	                locationVO.setLocation_name(rs.getString("location_name"));
	                locationVO.setAddress(rs.getString("address"));
	                locationVO.setLatitude(rs.getBigDecimal("latitude"));
	                locationVO.setLongitude(rs.getBigDecimal("longitude"));
	                locationVO.setScore(rs.getFloat("score"));
	                locationVO.setRatingCount(rs.getInt("rating_count"));
	                locationVO.setComments_number(rs.getInt("comments_number"));
	                locationVO.setCreate_time(rs.getTimestamp("create_time"));
	                locationVO.setUpdateTime(rs.getTimestamp("update_time"));
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
	    try (PreparedStatement pstmt = getConnection().prepareStatement(GET_BY_LOCATION_NAME_STMT)) {
	        pstmt.setString(1, location_name);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Map<String, Object> map = new HashMap<>();
	                map.put("location_id", rs.getInt("location_id"));
	                map.put("google_place_id", rs.getString("google_place_id"));
	                map.put("location_name", rs.getString("location_name"));
	                map.put("address", rs.getString("address"));
	                map.put("latitude", rs.getBigDecimal("latitude"));
	                map.put("longitude", rs.getBigDecimal("longitude"));
	                map.put("score", rs.getFloat("score"));
	                map.put("rating_count", rs.getInt("rating_count"));
	                map.put("comments_number", rs.getInt("comments_number"));
	                map.put("create_time", rs.getTimestamp("create_time"));
	                map.put("update_time", rs.getTimestamp("update_time"));
	                list.add(map);
	            }
	        }
	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occured. " + se.getMessage());
	    }
	    return list;
	}

	// 用googleID找Location物件 --> 為了要確認這個物件是不是被創建了
	@Override
	public LocationVO findByGooglePlaceId(String googlePlaceId) {
		LocationVO locationVO = null;
		String GET_BY_GOOGLE_PLACE_ID = "SELECT * FROM location WHERE google_place_id = ?";

		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_BY_GOOGLE_PLACE_ID)) {
			pstmt.setString(1, googlePlaceId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					locationVO = new LocationVO();
					locationVO.setLocationId(rs.getInt("location_id"));
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