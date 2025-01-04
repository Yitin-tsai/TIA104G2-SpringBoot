package chilltrip.trip.dao;

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

import chilltrip.trip.model.TripVO;

public class TripDAOImplJDBC implements TripDAO, AutoCloseable {

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

	// 新增一篇文章
	private static final String INSERT_STMT = "INSERT INTO trip (member_id, abstract, create_time, collections, status, overall_score, overall_scored_people, location_number, article_title, visitors_number, likes) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	// 更新一篇文章
	private static final String UPDATE = "UPDATE trip SET member_id=?, abstract=?, create_time=?, collections=?, status=?, overall_score=?, overall_scored_people=?, location_number=?, article_title=?, visitors_number=?, likes=? WHERE trip_id=?";
	// 取得所有文章列表-->按照建立時間先後順序排列
	private static final String GET_ALL_ORDER_BY_CREATE_TIME = "SELECT trip_id, member_id, abstract, create_time, collections, status, overall_score, overall_scored_people, location_number, article_title, visitors_number, likes FROM trip ORDER BY create_time";
	// 獲取某位用戶所有的文章（包含私人的與公開的）
	private static final String GET_ALL_BY_MEMBERID = "SELECT trip_id, member_id, abstract, create_time, collections, status, overall_score, overall_scored_people, location_number, article_title, visitors_number, likes FROM trip WHERE member_id = ?";
	// 更改文章狀態 Ex:文章下架-->某用戶刪除文章或是文章被檢舉遭到管理員下架
	private static final String CHANGE_STATUS = "UPDATE trip SET status=? WHERE trip_id = ?";
	// 拿到某用戶的私人/公開/下架文章
	private static final String GET_MEMBER_TRIP_BY_STATUS = "SELECT trip_id, member_id, abstract, create_time, collections, status, overall_score, overall_scored_people, location_number, article_title, visitors_number, likes FROM trip WHERE member_id = ? AND status = ?";
	// 透過景點搜尋到相關文章
	private static final String GET_TRIP_BY_LOCATION = "SELECT DISTINCT trip.trip_id, trip.member_id, trip.abstract, trip.create_time, trip.collections, trip.status, trip.overall_score, trip.overall_scored_people, trip.location_number, trip.article_title, trip.visitors_number, trip.likes, sub_trip.content, sub_trip.`index`, location.location_name FROM location JOIN trip_location_relation ON location.location_id = trip_location_relation.location_id JOIN sub_trip ON trip_location_relation.sub_trip_id = sub_trip.sub_trip_id JOIN trip ON sub_trip.trip_id = trip.trip_id WHERE location.location_name LIKE ? ORDER BY trip.trip_id, sub_trip.`index`";
	// 刪除一篇文章
	private static final String DELETE = "DELETE FROM trip WHERE trip_id=?";
	// 根據ID查詢文章
	private static final String GET_TRIP_BY_ID = "SELECT trip_id, member_id, abstract, create_time, collections, status, overall_score, overall_scored_people, location_number, article_title, visitors_number, likes FROM trip WHERE trip_id=?";
	// 根據文章標題模糊查詢文章
	private static final String GET_TRIP_BY_NAME = "SELECT trip_id, member_id, abstract, create_time, collections, status, overall_score, overall_scored_people, location_number, article_title, visitors_number, likes FROM trip WHERE article_title LIKE ?";

	public TripDAOImplJDBC() {

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

	// 編輯一個行程
	@Override
	public void update(TripVO tripVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(UPDATE)) {
			pstmt.setInt(1, tripVO.getMemberId());
			pstmt.setString(2, tripVO.getTrip_abstract());
			pstmt.setTimestamp(3, tripVO.getCreate_time());
			pstmt.setInt(4, tripVO.getCollections());
			pstmt.setInt(5, tripVO.getStatus());
			pstmt.setInt(6, tripVO.getOverall_score());
			pstmt.setInt(7, tripVO.getOverall_scored_people());
			pstmt.setInt(8, tripVO.getLocation_number());
			pstmt.setString(9, tripVO.getArticle_title());
			pstmt.setInt(10, tripVO.getVisitors_number());
			pstmt.setInt(11, tripVO.getLikes());
			pstmt.setInt(12, tripVO.getTrip_id());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 刪除一個行程
	@Override
	public void delete(Integer tripid) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(DELETE)) {
			pstmt.setInt(1, tripid);
			pstmt.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 新增一行程
	@Override
	public void insert(TripVO tripVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(INSERT_STMT)) {
			pstmt.setInt(1, tripVO.getMemberId());
			pstmt.setString(2, tripVO.getTrip_abstract());
			pstmt.setTimestamp(3, tripVO.getCreate_time());
			pstmt.setInt(4, tripVO.getCollections());
			pstmt.setInt(5, tripVO.getStatus());
			pstmt.setInt(6, tripVO.getOverall_score());
			pstmt.setInt(7, tripVO.getOverall_scored_people());
			pstmt.setInt(8, tripVO.getLocation_number());
			pstmt.setString(9, tripVO.getArticle_title());
			pstmt.setInt(10, tripVO.getVisitors_number());
			pstmt.setInt(11, tripVO.getLikes());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 獲取行程列表-->按照時間順序排列
	@Override
	public List<TripVO> getAllTrip() {
		List<TripVO> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALL_ORDER_BY_CREATE_TIME);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				TripVO tripVO = new TripVO();
				tripVO.setTrip_id(rs.getInt("trip_id"));
				tripVO.setMemberId(rs.getInt("member_id"));
				tripVO.setTrip_abstract(rs.getString("abstract"));
				tripVO.setCreate_time(rs.getTimestamp("create_time"));
				tripVO.setCollections(rs.getInt("collections"));
				tripVO.setStatus(rs.getInt("status"));
				tripVO.setOverall_score(rs.getInt("overall_score"));
				tripVO.setOverall_scored_people(rs.getInt("overall_scored_people"));
				tripVO.setLocation_number(rs.getInt("location_number"));
				tripVO.setArticle_title("article_title");
				tripVO.setVisitors_number(rs.getInt("visitors_number"));
				tripVO.setLikes(rs.getInt("likes"));
				list.add(tripVO);
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	// 用list的格式拿到行程列表-->按照時間順序排列
	@Override
	public List<Map<String, Object>> getAllTripPro() {
		List<Map<String, Object>> list = new ArrayList<>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALL_ORDER_BY_CREATE_TIME);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {

				Map<String, Object> map = new HashMap<>();

				map.put("trip_id", rs.getInt("trip_id"));
				map.put("member_id", rs.getInt("member_id"));
				map.put("abstract", rs.getString("abstract"));
				map.put("create_time", rs.getTimestamp("create_time"));
				map.put("collections", rs.getInt("collections"));
				map.put("status", rs.getInt("status"));
				map.put("overall_score", rs.getInt("overall_score"));
				map.put("overall_scored_people", rs.getInt("overall_scored_people"));
				map.put("location_number", rs.getInt("location_number"));
				map.put("article_title", rs.getString("article_title"));
				map.put("visitors_number", rs.getInt("visitors_number"));
				map.put("likes", rs.getInt("likes"));
				list.add(map);

			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	// 獲取某位用戶所有的文章（包含私人的與公開的）
	@Override
	public List<Map<String, Object>> getByMemberId(Integer memberId) {

		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_ALL_BY_MEMBERID)) {
			pstmt.setInt(1, memberId);
			try (ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();

					map.put("trip_id", rs.getInt("trip_id"));
					map.put("member_id", rs.getInt("member_id"));
					map.put("abstract", rs.getString("abstract"));
					map.put("create_time", rs.getTimestamp("create_time"));
					map.put("collections", rs.getInt("collections"));
					map.put("status", rs.getInt("status"));
					map.put("overall_score", rs.getInt("overall_score"));
					map.put("overall_scored_people", rs.getInt("overall_scored_people"));
					map.put("location_number", rs.getString("location_number"));
					map.put("article_title", rs.getString("article_title"));
					map.put("visitors_number", rs.getInt("visitors_number"));
					map.put("likes", rs.getInt("likes"));
					list.add(map);
				}
			}
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
	}

	// 更改文章狀態 Ex:文章下架-->某用戶刪除文章或是文章被檢舉遭到管理員下架
	public void changeTripStatus(TripVO tripVO) {
		try (PreparedStatement pstmt = getConnection().prepareStatement(CHANGE_STATUS)) {
			pstmt.setInt(1, tripVO.getStatus());
			pstmt.setInt(2, tripVO.getTrip_id());
			pstmt.executeUpdate();

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
	}

	// 拿到某用戶的私人/公開/下架文章 GET_MEMBER_TRIP_BY_STATUS
	public static final int STATUS_PRIVATE = 0;
	public static final int STATUS_PUBLIC = 1;
	public static final int STATUS_REMOVED = 2;

	public List<Map<String, Object>> getMemberTripByStatus(Integer memberId, Integer status) {

		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_MEMBER_TRIP_BY_STATUS)) {
			// 設置查詢參數
			pstmt.setInt(1, memberId);
			pstmt.setInt(2, status);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();

					map.put("trip_id", rs.getInt("trip_id"));
					map.put("member_id", rs.getInt("member_id"));
					map.put("abstract", rs.getString("abstract"));
					map.put("create_time", rs.getTimestamp("create_time"));
					map.put("collections", rs.getInt("collections"));
					map.put("status", rs.getInt("status"));
					map.put("overall_score", rs.getInt("overall_score"));
					map.put("overall_scored_people", rs.getInt("overall_scored_people"));
					map.put("location_number", rs.getString("location_number"));
					map.put("article_title", rs.getString("article_title"));
					map.put("visitors_number", rs.getInt("visitors_number"));
					map.put("likes", rs.getInt("likes"));

					list.add(map);
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("查詢會員文章時發生錯誤: " + se.getMessage());
		}

		return list;
	}

	//用景點查詢行程
	public List<Map<String, Object>> getTripByLocation(String location_name) {
		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_BY_LOCATION)) {
			// 使用模糊搜尋，前後加上 %
			pstmt.setString(1, "%" + location_name + "%");
//			System.out.println(location_name);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();

					// 根據 SQL 查詢的欄位來設置
					map.put("trip_id", rs.getInt("trip_id"));
					map.put("member_id", rs.getInt("member_id"));
					map.put("abstract", rs.getString("abstract"));
					map.put("create_time", rs.getTimestamp("create_time"));
					map.put("collections", rs.getInt("collections"));
					map.put("status", rs.getInt("status"));
					map.put("overall_score", rs.getInt("overall_score"));
					map.put("overall_scored_people", rs.getInt("overall_scored_people"));
					map.put("location_number", rs.getString("location_number"));
					map.put("article_title", rs.getString("article_title"));
					map.put("visitors_number", rs.getInt("visitors_number"));
					map.put("likes", rs.getInt("likes"));

					list.add(map);
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("搜尋景點相關文章時發生錯誤: " + se.getMessage());
		}

		return list;
	}

	@Override
	public TripVO getById(Integer tripid) {
		TripVO tripVO = null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_BY_ID)) {
			pstmt.setInt(1, tripid);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					tripVO = new TripVO();
					tripVO.setTrip_id(rs.getInt("trip_id"));
					tripVO.setMemberId(rs.getInt("member_id"));
					tripVO.setTrip_abstract(rs.getString("abstract"));
					tripVO.setCreate_time(rs.getTimestamp("create_time"));
					tripVO.setCollections(rs.getInt("collections"));
					tripVO.setStatus(rs.getInt("status"));
					tripVO.setOverall_score(rs.getInt("overall_score"));
					tripVO.setOverall_scored_people(rs.getInt("overall_scored_people"));
					tripVO.setLocation_number(rs.getInt("location_number"));
					tripVO.setLikes(rs.getInt("likes"));
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return tripVO;
	}

	//根據文章標題查詢文章
	@Override
	public List<Map<String, Object>> getTripByName(String article_title) {
		List<Map<String, Object>> list = new ArrayList<>();
		
		try (PreparedStatement pstmt = getConnection().prepareStatement(GET_TRIP_BY_NAME);) {
			// 設定參數
			pstmt.setString(1, "%" + article_title + "%");
			System.out.println(article_title);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {

					Map<String, Object> map = new HashMap<>();

					// 根據 SQL 查詢的欄位來設置
					map.put("trip_id", rs.getInt("trip_id"));
					map.put("member_id", rs.getInt("member_id"));
					map.put("abstract", rs.getString("abstract"));
					map.put("create_time", rs.getTimestamp("create_time"));
					map.put("collections", rs.getInt("collections"));
					map.put("status", rs.getInt("status"));
					map.put("overall_score", rs.getInt("overall_score"));
					map.put("overall_scored_people", rs.getInt("overall_scored_people"));
					map.put("location_number", rs.getString("location_number"));
					map.put("article_title", rs.getString("article_title"));
					map.put("visitors_number", rs.getInt("visitors_number"));
					map.put("likes", rs.getInt("likes"));
					list.add(map);
				}
			}

		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		}
		return list;
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
