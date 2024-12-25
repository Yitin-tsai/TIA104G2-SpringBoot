package chilltrip.triparea.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TripAreaJDBCDAO implements TripAreaDAO_interface {
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "123456";

	private static final String INSERT_STMT = "INSERT INTO itinerary_area(trip_id,region_content) VALUES (?,?)";
	private static final String GET_ALL_STMT = "SELECT trip_location_id,trip_id,region_content FROM itinerary_area ORDER BY trip_location_id";
	private static final String GET_ONE_STMT = "SELECT trip_location_id,trip_id,region_content FROM itinerary_area WHERE trip_location_id = ?";
	private static final String DELETE = "DELETE FROM itinerary_area WHERE trip_location_id = ?";
	private static final String UPDATE = "UPDATE itinerary_area SET trip_id=?, region_content=? WHERE trip_location_id = ?";

	@Override
	public void insert(TripAreaVO tripAreaVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setInt(1, tripAreaVO.getTripid());
			pstmt.setString(2, tripAreaVO.getRegioncontent());

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
	public void update(TripAreaVO tripAreaVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, tripAreaVO.getTripid());
			pstmt.setString(2, tripAreaVO.getRegioncontent());
			pstmt.setInt(3, tripAreaVO.getTriplocationid());

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

				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void delete(Integer triplocationid) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, triplocationid);

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
	public TripAreaVO findByPrimaryKey(Integer triplocationid) {

		TripAreaVO tripAreaVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, triplocationid);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				tripAreaVO = new TripAreaVO();
				tripAreaVO.setTriplocationid(rs.getInt("trip_location_id"));
				tripAreaVO.setTripid(rs.getInt("trip_id"));
				tripAreaVO.setRegioncontent(rs.getString("region_content"));
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

		return tripAreaVO;
	}

	@Override
	public List<TripAreaVO> getAll() {
		
		List<TripAreaVO> list = new ArrayList<TripAreaVO>();
		TripAreaVO tripAreaVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				tripAreaVO = new TripAreaVO();
				tripAreaVO.setTriplocationid(rs.getInt("trip_location_id"));
				tripAreaVO.setTripid(rs.getInt("trip_id"));
				tripAreaVO.setRegioncontent(rs.getString("region_content"));
			
				list.add(tripAreaVO);  // 將該行資料儲存在 list 集合中
			}
			
		}catch(ClassNotFoundException e){
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		}catch(SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		}finally{
			if(rs != null) {
				try {
					rs.close();
				}catch(SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				}catch(SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if(con != null) {
				try {
					con.close();
				}catch(Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
		return list;
	}

	public static void main(String[] args) {

		TripAreaJDBCDAO dao = new TripAreaJDBCDAO();
		TripAreaVO tripAreaVO = new TripAreaVO();
		
		// 新增
//		tripAreaVO.setTripid(1);
//		tripAreaVO.setRegioncontent("台中");
//		
//		dao.insert(tripAreaVO);
//
//		// 修改
//		tripAreaVO.setTriplocationid(4);
//		tripAreaVO.setTripid(2);
//		tripAreaVO.setRegioncontent("高雄");
//		
//		dao.update(tripAreaVO);
//		
//		// 用主鍵查詢單筆行程地區
//		TripAreaVO tripAreaVO2 = dao.findByPrimaryKey(4);
//		System.out.println(tripAreaVO2.getTriplocationid() + ",");
//		System.out.println(tripAreaVO2.getTripid() + ",");
//		System.out.println(tripAreaVO2.getRegioncontent());
//		System.out.println("---------------------");
//		
//		// 查詢全部
//		List<TripAreaVO> list = dao.getAll();
//		for(TripAreaVO aTripArea : list) {
//			System.out.println(aTripArea.getTriplocationid() + ",");
//			System.out.println(aTripArea.getTripid() + ",");
//			System.out.println(aTripArea.getRegioncontent());
//			System.out.println();
//		}
//
//		// 刪除資料
//		dao.delete(4);
		
	} 

}
