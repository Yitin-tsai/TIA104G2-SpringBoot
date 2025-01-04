package chilltrip.tripactyperela.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.trip.model.TripVO;

import chilltrip.tripactype.model.TripactypeVO;

public class TripactyperelaDAO implements TripactyperelaDAO_interface {

	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
	String userid = "root";
	String password = "123456";

	private static final String INSERT = "INSERT INTO itinerary_activity_type_relationship (trip_id, event_type_id) VALUES (?, ?)";
	private static final String UPDATE = "UPDATE itinerary_activity_type_relationship SET trip_id = ?, event_type_id = ? WHERE itinerary_activity_relationship_id = ?";
	private static final String DELETE = "DELETE FROM itinerary_activity_type_relationship WHERE itinerary_activity_relationship_id = ?";
	private static final String GET_ALL = "SELECT * FROM itinerary_activity_type_relationship";
	private static final String GET_BY_TRIPID = "SELECT itinerary_activity_type_relationship.*, itinerary_activity_type.event_content FROM itinerary_activity_type_relationship JOIN itinerary_activity_type ON itinerary_activity_type_relationship.event_type_id = itinerary_activity_type.event_type_id WHERE itinerary_activity_type_relationship.trip_id = ?";
	private static final String GET_BY_EVENTTYPEID = "SELECT * FROM itinerary_activity_type_relationship WHERE event_type_id = ?";
	private static final String DELETE_BY_TRIPID = "DELETE FROM itinerary_activity_type_relationship WHERE trip_id = ?";
	private static final String DELETE_RELATION = "DELETE FROM itinerary_activity_type_relationship WHERE trip_id = ? AND event_type_id = ?";

	@Override
	public void insert(TripactyperelaVO tripactyperelaVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(INSERT);
			pstmt.setInt(1, tripactyperelaVO.getTripid().getTrip_id());
			pstmt.setInt(2, tripactyperelaVO.getEventtypeid().getEventtypeid());

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
	public void update(TripactyperelaVO tripactyperelaVO) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, tripactyperelaVO.getTripid().getTrip_id());
			pstmt.setInt(2, tripactyperelaVO.getEventtypeid().getEventtypeid());
			pstmt.setInt(3, tripactyperelaVO.getRelaid());

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
	public void delete(Integer eventtyperelaid) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, eventtyperelaid);

			pstmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("無法載入資料庫驅動程式" + e.getMessage());
		} catch (SQLException se) {
			if (con != null) {
				try {
					// 3.設定於當有exception發生時之catch區塊內
					con.rollback();
				} catch (SQLException excep) {
					throw new RuntimeException("發生 rollback 錯誤" + excep.getMessage());
				}
			}
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
	public List<TripactyperelaVO> getAll() {
	    List<TripactyperelaVO> list = new ArrayList<TripactyperelaVO>();
	    TripactyperelaVO tripactyperelaVO = null;

	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName(driver);
	        con = DriverManager.getConnection(url, userid, password);
	        pstmt = con.prepareStatement(GET_ALL);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            tripactyperelaVO = new TripactyperelaVO();
	            tripactyperelaVO.setRelaid(rs.getInt("itinerary_activity_relationship_id"));

	            // 建立 TripVO 並設置 trip_id
	            TripVO tripVO = new TripVO();
	            tripVO.setTrip_id(rs.getInt("trip_id")); 
	            tripactyperelaVO.setTripid(tripVO);

	            // 建立 TripactypeVO 並設置 event_type_id 和 event_content
	            TripactypeVO tripactypeVO = new TripactypeVO();
	            tripactypeVO.setEventtypeid(rs.getInt("event_type_id")); // 設置 event_type_id

	            // 查詢 event_content，並設置到 TripactypeVO
	            String eventContent = rs.getString("event_content");  // 從資料庫查詢 event_content
	            tripactypeVO.setEventcontent(eventContent); // 設置 event_content

	            tripactyperelaVO.setEventtypeid(tripactypeVO);

	            list.add(tripactyperelaVO);
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

	    return list;
	}


	@Override
	public List<TripactyperelaVO> getByTripId(Integer tripId) {
	    List<TripactyperelaVO> list = new ArrayList<TripactyperelaVO>();
	    TripactyperelaVO tripactyperelaVO = null;

	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName(driver);
	        con = DriverManager.getConnection(url, userid, password);
	        pstmt = con.prepareStatement(GET_BY_TRIPID);
	        
	        pstmt.setInt(1, tripId); 
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            tripactyperelaVO = new TripactyperelaVO();
	            tripactyperelaVO.setRelaid(rs.getInt("itinerary_activity_relationship_id"));

	            // 建立 TripVO 並設置 trip_id
	            TripVO tripVO = new TripVO();
	            tripVO.setTrip_id(rs.getInt("trip_id")); 
	            tripactyperelaVO.setTripid(tripVO);

	            // 建立 TripactypeVO 並設置 event_type_id 和 event_content
	            TripactypeVO tripactypeVO = new TripactypeVO();
	            tripactypeVO.setEventtypeid(rs.getInt("event_type_id")); // 設置 event_type_id
	            
	            // 查詢 event_content，並設置到 TripactypeVO
	            String eventContent = rs.getString("event_content");  // 從資料庫查詢 event_content
	            tripactypeVO.setEventcontent(eventContent); // 設置 event_content

	            tripactyperelaVO.setEventtypeid(tripactypeVO);

	            list.add(tripactyperelaVO);
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

	    return list;
	}


	@Override
	public List<TripactyperelaVO> getByeventtypeid(Integer eventtypeid) {
		List<TripactyperelaVO> list = new ArrayList<TripactyperelaVO>();
		TripactyperelaVO tripactyperelaVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_BY_EVENTTYPEID);
			rs = pstmt.executeQuery();

			pstmt.setInt(1, eventtypeid);

			while (rs.next()) {
				tripactyperelaVO = new TripactyperelaVO();
				tripactyperelaVO.setRelaid(rs.getInt("itinerary_activity_relationship_id"));

				TripVO tripVO = new TripVO();
				tripVO.setTrip_id(rs.getInt("trip_id")); // 設置 TripVO 的 trip_id
				tripactyperelaVO.setTripid(tripVO);

				TripactypeVO tripactypeVO = new TripactypeVO();
				tripactypeVO.setEventtypeid(rs.getInt("event_type_id")); // 設置 TripactypeVO 的 event_type_id
				tripactyperelaVO.setEventtypeid(tripactypeVO);

				list.add(tripactyperelaVO);
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

		return list;
	}

	@Override
	public void deleteByTripId(Integer tripId) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(DELETE_BY_TRIPID);

			pstmt.setInt(1, tripId);

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
	public boolean deleteRelationByTripAndEventType(Integer tripId, Integer eventTypeId) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(DELETE_RELATION);

			pstmt.setInt(1, tripId);
			pstmt.setInt(2, eventTypeId);

			int rowsAffected = pstmt.executeUpdate();

			return rowsAffected > 0;
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

//	// 每頁最大顯示的結果數量
//	// int PAGE_MAX_RESULT = 3;
//	
//	// Hibernate 的 SessionFactory 負責生成 Session
//	// SessionFactory 為 thread-safe，可宣告為屬性讓請求執行緒們共用
//	private SessionFactory factory;
//	
//	// 建構子初始化 SessionFactory
//	public TripactyperelaDAO() {
//		// 使用 Hibernate 獲取 SessionFactory
//		factory = HibernateUtil.getSessionFactory();
//	}
//	
//	// Session 為 not thread-safe，所以此方法在各個增刪改查方法裡呼叫
//	// 以避免請求執行緒共用了同個 Session
//	// 獲取當前的 Hibernate Session
//	private Session getSession() {
//		// 獲取當前綁定的 Session
//		return factory.getCurrentSession();
//	}
//	
//	
//	@Override
//	public void insert(TripactyperelaVO tripactyperelaVO) {
//		try {
//			// 獲取 Hibernate Session
//			Session session = getSession();
//			// 開始交易
//			session.beginTransaction();
//			// 儲存物件到資料庫
//			session.save(tripactyperelaVO);
//			// 提交交易
//			session.getTransaction().commit();
//			
//		}catch(Exception e) {
//			// 輸出錯誤資訊
//			e.printStackTrace();
//			// 如果出錯，回滾交易
//			getSession().getTransaction().rollback();
//		}
//		
//	}
//
//	@Override
//	public void update(TripactyperelaVO tripactyperelaVO) {
//		try {
//			// 開始交易
//			getSession().beginTransaction();
//			// 更新資料
//			getSession().update(tripactyperelaVO);
//			// 提交交易
//			getSession().getTransaction().commit();
//			
//		} catch(Exception e) {
//			// 輸出錯誤資訊
//			e.printStackTrace();
//			// 如果出錯，回滾交易
//			getSession().getTransaction().rollback();
//		}
//		
//	}
//
//	@Override
//	public void delete(Integer eventtyperelaid) {
//		// 根據主鍵獲取物件
//		TripactyperelaVO tripactyperelaVO = getSession().get(TripactyperelaVO.class, eventtyperelaid);
//		try {
//			// 開始交易
//			getSession().beginTransaction();
//			// 刪除資料
//			getSession().delete(tripactyperelaVO);
//			// 提交交易
//			getSession().getTransaction().commit();
//		}catch(Exception e) {
//			// 輸出錯誤資訊
//			e.printStackTrace();
//			// 如果出錯，回滾交易
//			getSession().getTransaction().rollback();
//		}
//	}
//
//	@Override
//	public List<TripactyperelaVO> getAll() {
//		// 開始交易
//		getSession().beginTransaction();
//		// 使用 HQL 查詢所有 AnnounceVO 資料
//		return getSession().createQuery("from TripactyperelaVO", TripactyperelaVO.class).list();
//	}
//
////	@Override
////	public List<TripactyperelaVO> getByCompositeQuery(Map<String, String> map) {
////		return null;
////	}
//
////	@Override
////	public List<TripactyperelaVO> getAll(int currentPage) {
////		
////		return null;
////	}
//
//	@Override
//	public List<TripactyperelaVO> getByeventtypeid(Integer eventtypeid) {
//		
//		// 獲取 Session
//		Session session = getSession();
//		// 開始交易
//		session.beginTransaction();
//		// 用於存放結果
//		List<TripactyperelaVO> list = new ArrayList<TripactyperelaVO>();
//		// 根據主鍵獲取 TripactypeVO
//		TripactypeVO tripactype = session.get(TripactypeVO.class, eventtypeid);
//		for(TripactyperelaVO t : tripactype.getRelationship()) {
//			// 從 tripactype 的 relationship 關聯屬性中提取資料
//			list.add(t);
//		}
//		return (List)list;
//	}
//	
//	@Override
//	public List<TripactyperelaVO> getByTripId(Integer tripId) {
//		// 獲取 Session
//	    Session session = getSession();
//	    
//	    try {
//	        // 開始交易
//	        session.beginTransaction();
//	        
//	        // 使用 HQL 查詢所有與該 tripId 關聯的資料
//	        List<TripactyperelaVO> relations = session.createQuery(
//	            "FROM TripactyperelaVO WHERE tripid.trip_id = :tripId", TripactyperelaVO.class)
//	            .setParameter("tripId", tripId)
//	            .list();
//	        
//	        // 提交交易
//	        session.getTransaction().commit();
//	        
//	        return relations;
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        // 如果出錯，回滾交易
//	        session.getTransaction().rollback();
//	        return new ArrayList<>();  // 返回空列表，表示查詢失敗
//	    }
//	}
//
//	@Override
//	public void deleteByTripId(Integer tripId) {
//		// 獲取 Session
//	    Session session = getSession();
//	    
//	    try {
//	        // 開始交易
//	        session.beginTransaction();
//	        
//	        // 查詢所有與該 tripId 相關的關聯資料
//	        List<TripactyperelaVO> relations = session.createQuery(
//	            "FROM TripactyperelaVO WHERE tripid.trip_id = :tripId", TripactyperelaVO.class)
//	            .setParameter("tripId", tripId)
//	            .list();
//	        
//	        // 刪除所有查到的關聯資料
//	        for (TripactyperelaVO relation : relations) {
//	            session.delete(relation);
//	        }
//
//	        // 提交交易
//	        session.getTransaction().commit();
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        // 如果出錯，回滾交易
//	        session.getTransaction().rollback();
//	    }
//		
//	}
//	
//	
//
//	@Override
//	public boolean deleteRelationByTripAndEventType(Integer tripId, Integer eventTypeId) {
//		Session session = null;
//        try {
//            session = getSession();
//            session.beginTransaction();
//
//            // 使用 HQL 查詢符合條件的關聯資料
//            String hql = "FROM TripactyperelaVO WHERE tripid.trip_id = :tripId AND eventtypeid.eventtypeid = :eventTypeId";
//            Query<TripactyperelaVO> query = session.createQuery(hql, TripactyperelaVO.class);
//            query.setParameter("tripId", tripId);
//            query.setParameter("eventTypeId", eventTypeId);
//
//            // 獲取符合條件的 TripactyperelaVO 實體
//            TripactyperelaVO tripactyperelaVO = query.uniqueResult();
//
//            if (tripactyperelaVO != null) {
//                // 如果找到對應的關聯，則刪除
//                session.delete(tripactyperelaVO);
//                session.getTransaction().commit();
//                return true; // 刪除成功
//            } else {
//                session.getTransaction().rollback(); // 如果沒有找到對應的關聯則回滾
//                return false; // 沒有找到對應的關聯，刪除失敗
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (session != null && session.getTransaction().isActive()) {
//                session.getTransaction().rollback(); // 回滾交易
//            }
//            return false; // 失敗
//        }
//    }

	public static void main(String[] args) {

//		TripactyperelaDAO dao = new TripactyperelaDAO();
//		TripactyperelaVO tripactyperela = new TripactyperelaVO();
//		TripVO trip = new TripVO();
//		trip.setTrip_id(1);
//		TripactypeVO tripactype = new TripactypeVO();
//		tripactype.setEventtypeid(1);
//		
//		// 新增
//		tripactyperela.setTripid(trip);
//		tripactyperela.setEventtypeid(tripactype);
//		dao.insert(tripactyperela);
//		
//		// 修改
//		tripactyperela.setRelaid(4);
//		tripactyperela.setTripid(trip);
//		tripactyperela.setEventtypeid(tripactype);
//		dao.update(tripactyperela);
//		
//		// 查詢
//		for(TripactyperelaVO rela : dao.getAll()) {
//			System.out.println(rela);
//		}
//		
//		// 刪除
//		dao.delete(4);

	}

}
