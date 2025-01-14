package chilltrip.triplocationrelation.model;
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
import org.springframework.stereotype.Repository;

@Repository
public class TriplocationrelationDAOImplJDBC implements TriplocationrelationDAO, AutoCloseable {

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

	// 新增
	private static final String INSERT_STMT = "INSERT INTO trip_location_relation (sub_trip_id, location_id, `index`, time_start, time_end) VALUES (?,?,?,?,?)";
	// 修改
	private static final String UPDATE = "UPDATE trip_location_relation SET sub_trip_id=?, location_id=?, `index`=?, time_start=?, time_end=? WHERE trip_location_relation_id=?";
	// 刪除
	private static final String DELETE = "DELETE FROM trip_location_relation WHERE trip_location_relation_id=?";
	// 列出某子行程中的所有景點行程關係(按照時間排序）
	private static final String GET_TRIP_LOCATION_RELATION_BY_SUB_TRIP_ID = "SELECT trip_location_relation_id, sub_trip_id, location_id, `index`, time_start, time_end FROM trip_location_relation ORDER BY `index`";
	// 透過ID查找特定景點行程關係
	private static final String GET_TRIP_LOCATION_RELATION_BY_ID = "SELECT trip_location_relation_id, sub_trip_id, location_id, `index`, time_start, time_end FROM trip_location_relation WHERE trip_location_relation_id=?";

	public TriplocationrelationDAOImplJDBC() {

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

	// 編輯行程景點關係
	@Override
	public void update(TriplocationrelationVO triplocationrelationVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE)) {
			pstmt.setInt(1, triplocationrelationVO.getSub_trip_id());
			pstmt.setInt(2, triplocationrelationVO.getLocation_id());
			pstmt.setInt(3, triplocationrelationVO.getIndex());
			pstmt.setTimestamp(4, triplocationrelationVO.getTime_start());
			pstmt.setTimestamp(5, triplocationrelationVO.getTime_end());
			pstmt.setInt(6, triplocationrelationVO.getTrip_location_relation_id());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 刪除行程景點關係
	@Override
	public void delete(Integer trip_location_relation_id) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(DELETE)) {
			pstmt.setInt(1, trip_location_relation_id);
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 新增行程景點關係
	@Override
	public void insert(TriplocationrelationVO triplocationrelationVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT)) {
			pstmt.setInt(1, triplocationrelationVO.getSub_trip_id());
			pstmt.setInt(2, triplocationrelationVO.getLocation_id());
			pstmt.setInt(3, triplocationrelationVO.getIndex());
			pstmt.setTimestamp(4, triplocationrelationVO.getTime_start());
			pstmt.setTimestamp(5, triplocationrelationVO.getTime_end());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 列出某子行程中的所有景點行程關係(按照index排序）
	@Override
	public List<TriplocationrelationVO> getAllTriplocationrelationByTrip() {
		List<TriplocationrelationVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_LOCATION_RELATION_BY_SUB_TRIP_ID);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				TriplocationrelationVO triplocationrelationVO = new TriplocationrelationVO();
				triplocationrelationVO.setTrip_location_relation_id(rs.getInt("trip_location_relation_id"));
				triplocationrelationVO.setSub_trip_id(rs.getInt("sub_trip_id"));
				triplocationrelationVO.setLocation_id(rs.getInt("location_id"));
				triplocationrelationVO.setIndex(rs.getInt("index"));
				triplocationrelationVO.setTime_start(rs.getTimestamp("time_start"));
				triplocationrelationVO.setTime_end(rs.getTimestamp("time_end"));
				list.add(triplocationrelationVO);
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getAllTriplocationrelationByTripPro() {
		List<Map<String, Object>> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_LOCATION_RELATION_BY_SUB_TRIP_ID);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				Map<String, Object> map = new HashMap<>();
				
				map.put("trip_location_relation_id", rs.getInt("trip_location_relation_id"));
				map.put("sub_trip_id", rs.getInt("sub_trip_id"));
				map.put("location_id", rs.getInt("location_id"));
				map.put("index", rs.getInt("index"));
				map.put("time_start", rs.getTimestamp("time_start"));
				map.put("time_end", rs.getTimestamp("time_end"));
				list.add(map);
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}
	
	@Override
	public TriplocationrelationVO getTriplocationrelationById(Integer trip_location_relation_id) {
		TriplocationrelationVO triplocationrelationVO =null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_LOCATION_RELATION_BY_ID)) {
			pstmt.setInt(1, trip_location_relation_id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					triplocationrelationVO = new TriplocationrelationVO();
					triplocationrelationVO.setTrip_location_relation_id(rs.getInt("trip_location_relation_id"));
					triplocationrelationVO.setSub_trip_id(rs.getInt("sub_trip_id"));
					triplocationrelationVO.setLocation_id(rs.getInt("location_id"));
					triplocationrelationVO.setIndex(rs.getInt("index"));
					triplocationrelationVO.setTime_start(rs.getTimestamp("time_start"));
					triplocationrelationVO.setTime_end(rs.getTimestamp("time_end"));
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return triplocationrelationVO;
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