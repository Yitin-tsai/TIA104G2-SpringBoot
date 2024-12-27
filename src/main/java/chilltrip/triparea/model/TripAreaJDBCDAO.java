package chilltrip.triparea.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chilltrip.trip.model.TripVO;

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
	private static final String UPDATE_TRIP_AREA = "UPDATE itinerary_area SET region_content=? WHERE trip_id = ? AND region_content=?";
	private static final String GET_TRIP_BY_TRIPAREA = "SELECT itinerary_area.trip_location_id,itinerary_area.region_content,trip.trip_id, trip.article_title, trip.abstract AS trip_abstract, trip.visitors_number, trip.likes FROM itinerary_area JOIN trip ON itinerary_area.trip_id = trip.trip_id WHERE itinerary_area.region_content = ?";
	private static final String DELETE_TRIPAREA_FROM_TRIP = "DELETE FROM itinerary_area WHERE trip_id=? AND region_content=?";
	
	
	@Override
	public void insert(TripAreaVO tripAreaVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT_STMT);

			// 插入 TripVO 的 trip_id (這裡傳入 tripVO.getTrip_id()，而不是 trip_id)
			pstmt.setInt(1, tripAreaVO.getTripid().getTrip_id());
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

			pstmt.setInt(1, tripAreaVO.getTripid().getTrip_id());
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
				// 查詢 trip_id，並設置為 TripVO
				Integer tripId = rs.getInt("trip_id");
				tripAreaVO = new TripAreaVO();
				TripVO tripVO = new TripVO();
				
				tripVO.setTrip_id(tripId);  // 透過 TripVO 查找或設置 trip_id
				tripAreaVO.setTripid(tripVO);  // 設定 TripVO 對象到 TripAreaVO
				tripAreaVO.setTriplocationid(rs.getInt("trip_location_id"));
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
				// 查詢 trip_id，並設置為 TripVO
				Integer tripId = rs.getInt("trip_id");
				TripVO tripVO = new TripVO();
				tripVO.setTrip_id(tripId); // 設定 TripVO 的 trip_id
				tripAreaVO.setTripid(tripVO);  // 設定 TripVO 到 TripAreaVO
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
	
	@Override
	public List<TripAreaVO> getTripBytripArea(String regioncontent) {
		List<TripAreaVO> list = new ArrayList<TripAreaVO>();
		TripAreaVO tripAreaVO = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_TRIP_BY_TRIPAREA);
			pstmt.setString(1, regioncontent);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				tripAreaVO = new TripAreaVO();
				// 設定 TripAreaVO 的屬性
//				tripAreaVO.setTriplocationid(rs.getInt("trip_location_id"));
				tripAreaVO.setRegioncontent(rs.getString("region_content"));
				
				TripVO tripVO = new TripVO();
//				tripVO.setTrip_id(rs.getInt("trip_id"));
	            tripVO.setArticle_title(rs.getString("article_title"));
	            tripVO.setTrip_abstract(rs.getString("trip_abstract"));
	            tripVO.setVisitors_number(rs.getInt("visitors_number"));
	            tripVO.setLikes(rs.getInt("likes"));
	            
	            // 關聯 TripVO 到 TripAreaVO
	            tripAreaVO.setTripid(tripVO);
	            
	            // 將結果加入集合
	            list.add(tripAreaVO);
			}
			
			
		}catch(ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		}catch(SQLException se) {
			throw new RuntimeException("發生資料庫錯誤" + se.getMessage());
		}finally {
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
	public void addTripAreaToTrip(TripVO tripId, String regionContent) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT_STMT);

			TripVO tripVO = new TripVO();
			pstmt.setInt(1, tripVO.getTrip_id());  // 使用 tripVO 的 tripid
			pstmt.setString(2, regionContent);

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
	public void removeTripAreaFromTrip(TripVO tripId, String regionContent) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(DELETE_TRIPAREA_FROM_TRIP);

			TripVO tripVO = new TripVO();
			pstmt.setInt(1, tripVO.getTrip_id());  // 使用 tripVO 的 tripid
			pstmt.setString(2, regionContent);   // 設定地區標註
			
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
	public void updateTripArea(Integer tripId, String oldRegionContent, String newRegionContent) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(UPDATE_TRIP_AREA);

			pstmt.setString(1, newRegionContent);  // 新的地區標註
			pstmt.setInt(2, tripId);   // 行程 ID
			pstmt.setString(3, oldRegionContent);  // 舊的地區標註

			// 執行更新
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("未找到對應的行程和地區標註，更新失敗");
            }

            System.out.println("更新成功！更新了 " + rowsUpdated + " 條記錄");

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

	public static void main(String[] args) {

		TripAreaJDBCDAO dao = new TripAreaJDBCDAO();
		TripAreaVO tripAreaVO = new TripAreaVO();
		
		// 新增資料: 需要先建立 TripVO 實例，並設置 trip_id
	    TripVO tripVO = new TripVO();
	    tripVO.setTrip_id(1); // 設定 TripVO 的 trip_id
		
//		// 新增
//		tripAreaVO.setTripid(tripVO);
//		tripAreaVO.setRegioncontent("台中");
//		
//		dao.insert(tripAreaVO);
//
//		// 修改
//		tripAreaVO.setTriplocationid(5);
//		tripAreaVO.setTripid(tripVO);
//		tripAreaVO.setRegioncontent("高雄");
//		
//		dao.update(tripAreaVO);
//		
//		// 用主鍵查詢單筆行程地區
//		TripAreaVO tripAreaVO2 = dao.findByPrimaryKey(5);
//		System.out.println(tripAreaVO2.getTriplocationid() + ",");
//		System.out.println(tripAreaVO2.getTripid().getTrip_id() + ",");
//		System.out.println(tripAreaVO2.getRegioncontent());
//		System.out.println("---------------------");
//		
//		// 查詢全部
//		List<TripAreaVO> list = dao.getAll();
//		for(TripAreaVO aTripArea : list) {
//			System.out.println(aTripArea.getTriplocationid() + ",");
//			System.out.println(aTripArea.getTripid().getTrip_id() + ",");
//			System.out.println(aTripArea.getRegioncontent());
//			System.out.println();
//		}
//	    
//	    // 測試參數
//        String regionContent = "東京"; // 測試地區名稱
//
//        List<TripAreaVO> resultSet = dao.getTripBytripArea(regionContent);
//
//        // 輸出測試結果
//        if (resultSet != null && !resultSet.isEmpty()) {
//            System.out.println("查詢成功！結果如下：");
//            for (TripAreaVO tripAreaVO2 : resultSet) {
////                System.out.println("行程地區標籤 ID：" + tripAreaVO2.getTriplocationid());
//                System.out.println("行程地區名稱：" + tripAreaVO2.getRegioncontent());
//                TripVO tripVO2 = tripAreaVO2.getTripid();
//                if (tripVO2 != null) {
////                    System.out.println("行程 ID：" + tripVO2.getTrip_id());
//                    System.out.println("行程標題：" + tripVO2.getArticle_title());
//                    System.out.println("行程摘要：" + tripVO2.getTrip_abstract());
//                    System.out.println("訪客數量：" + tripVO2.getVisitors_number());
//                    System.out.println("喜歡數量：" + tripVO2.getLikes());
//                }
//                System.out.println("------------------------------------");
//            }
//        } else {
//            System.out.println("查無符合條件的行程！");
//        }
//	    
//
//		// 刪除資料
//		dao.delete(5);
		
	} 

}
