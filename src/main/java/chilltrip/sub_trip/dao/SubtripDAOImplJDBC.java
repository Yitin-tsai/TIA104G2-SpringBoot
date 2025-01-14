package chilltrip.sub_trip.dao;

import java.sql.Clob;
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

import javax.sql.rowset.serial.SerialException;

import org.springframework.stereotype.Repository;

import chilltrip.sub_trip.model.SubtripVO;

@Repository
public class SubtripDAOImplJDBC implements SubtripDAO, AutoCloseable {

	// 屬性建立用private，因為只有這個類別會用到
	// 建議資料庫連線字串設定在外部設定檔，這裡只是為了方便範例，所以直接寫在程式碼中(不然每次都要寫相同字串搞人)
	// 建議userid和passwd也設定在外部設定檔，這裡只是為了方便範例，所以直接寫在程式碼中(不然每次都要寫相同字串搞人)
	// 這裡沒有使用finally關閉連線，是因為這裡使用了AutoCloseable，所以在try-with-resources中使用，會自動關閉連線
	// PreparedStatement、ResultSet也是AutoCloseable，所以也會自動關閉
	// LocationDAOImplJDBC自己本身也是AutoCloseable，所以在try-with-resources中使用，會自動關閉

	private Connection connection;
	private String driver = "com.mysql.cj.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/TIA104G2?severTimezone=Asia/Taipei";
	private String userid = "root";
	private String passwd = "123456";

	private static final String INSERT_STMT = "INSERT INTO sub_trip(trip_id, `index`, content)VALUES(?,?,?)";
	// 按照子行程id排序查詢的SQL
	private static final String GET_ALLSTMT_BY_SUBTRIPID = "SELECT sub_trip_id, trip_id, `index`, content FROM sub_trip order by sub_trip_id";
	// 按照子行程的index排序查詢的SQL（用在部落格文章中展示）
	private static final String GET_ALLSTMT_BY_TRIP_INDEX = "SELECT sub_trip_id, trip_id, `index`, content FROM sub_trip where trip_id=? order by `index`";

	// 用id查詢單一子行程
	private static final String GET_ONE_STMT_BY_ID = "SELECT sub_trip_id, trip_id, `index`, content FROM sub_trip where sub_trip_id=?";
	// 用名稱查詢單一子行程
//	private static final String GET_ONE_STMT_BY_?? = "SELECT sub_trip_id, trip_id, `index`, content FROM sub_trip where sub_trip_id=?";
	private static final String DELETE = "DELETE FROM sub_trip where sub_trip_id=?";
	private static final String UPDATE = "UPDATE sub_trip set trip_id=?, `index`=?, content=? where sub_trip_id=?";

	public SubtripDAOImplJDBC() {

		// 建構子一開始確認是否有載入驅動程式，並且建立連線
		try {
			Class.forName(driver);
			this.connection = DriverManager.getConnection(url, userid, passwd);
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException("Couldn't load database driver or connect to database." + e.getMessage());
		}
	}

	private Connection getConnection() {
		return this.connection;
	}

	@Override
	public SubtripVO insert(SubtripVO subtripVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT)) {
			pstmt.setInt(1, subtripVO.getTripid());
			pstmt.setInt(2, subtripVO.getIndex());
			pstmt.setClob(3, subtripVO.getContent());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return subtripVO;
	}
	
	@Override
	public SubtripVO insertAndGetId (SubtripVO subtripVO) {
		System.out.println("SubtripDAO - 開始執行插入");
	    try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT, Statement.RETURN_GENERATED_KEYS)) {  // 加入 RETURN_GENERATED_KEYS
	        pstmt.setInt(1, subtripVO.getTripid());
	        pstmt.setInt(2, subtripVO.getIndex());
	        pstmt.setClob(3, subtripVO.getContent());  
	        
	        int affectedRows = pstmt.executeUpdate();
	        
	        if (affectedRows > 0) {
	            // 獲取自動生成的 ID
	            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    subtripVO.setSubtripid(generatedKeys.getInt(1));
	                } else {
	                    throw new SQLException("建立子行程失敗，無法獲取ID");
	                }
	            }
	        }
	        return subtripVO;
	        
	    } catch (SQLException se) {
	        throw new RuntimeException("A database error occured. " + se.getMessage());
	    }
	}

	@Override
	public void update(SubtripVO subtripVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE)) {
			pstmt.setInt(1, subtripVO.getTripid());
			pstmt.setInt(2, subtripVO.getIndex());
			pstmt.setClob(3, subtripVO.getContent());
			pstmt.setInt(4, subtripVO.getSubtripid());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	@Override
	public void delete(Integer subtripid) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(DELETE)) {
			pstmt.setInt(1, subtripid);
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	@Override
	public List<SubtripVO> getallsubtrip() {
		List<SubtripVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALLSTMT_BY_SUBTRIPID);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				SubtripVO subtripVO = new SubtripVO();
				subtripVO.setSubtripid(rs.getInt("sub_trip_id"));
				subtripVO.setTripid(rs.getInt("trip_id"));
				subtripVO.setIndex(rs.getInt("index"));
				Clob clob = rs.getClob("content");
				subtripVO.setContent(clob.getSubString(1, (int) clob.length()));
				list.add(subtripVO);
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getByTripId(Integer tripid) {

		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALLSTMT_BY_TRIP_INDEX)) {
			pstmt.setInt(1, tripid);
			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {

					Map<String, Object> map = new HashMap<>();

					map.put("sub_trip_id", rs.getInt("sub_trip_id"));
					map.put("trip_id", rs.getInt("trip_id"));
					map.put("index", rs.getInt("index"));
					map.put("content", rs.getClob("content"));
					list.add(map);
				}
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}
	

	@Override
	public SubtripVO getBySubtripId(Integer subtripid) {
		SubtripVO subtripVO = null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ONE_STMT_BY_ID)) {
			pstmt.setInt(1, subtripid);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					subtripVO = new SubtripVO();
					subtripVO.setSubtripid(rs.getInt("sub_trip_id"));
					subtripVO.setTripid(rs.getInt("trip_id"));
					subtripVO.setIndex(rs.getInt("index"));
					Clob clob = rs.getClob("content");
					subtripVO.setContent(clob.getSubString(1, (int) clob.length()));
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return subtripVO;
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

	public static void main(String[] args) {
		try (SubtripDAOImplJDBC dao = new SubtripDAOImplJDBC()) {

			// 測試程式碼
			// 插入SQL 測試
			SubtripVO subtripVO = new SubtripVO();

			subtripVO.setSubtripid(1);
			subtripVO.setTripid(2);
			subtripVO.setIndex(1);
			subtripVO.setContent("哈哈哈哈哈");

			dao.insert(subtripVO);
		} catch (RuntimeException e) {
			System.err.println("An unexpected error occurred: " + e.getMessage());
			e.printStackTrace();
		} catch (SerialException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
