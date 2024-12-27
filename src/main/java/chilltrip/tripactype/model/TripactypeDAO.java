package chilltrip.tripactype.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chillchip.trip.model.TripVO;
import chilltrip.tripactyperela.model.TripactyperelaVO;

public class TripactypeDAO implements TripactypeDAO_interface  {
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
	String userid = "root";
	String password = "123456";
	
	private static final String INSERT = "INSERT INTO itinerary_activity_type (event_content) VALUES (?)";
    private static final String UPDATE = "UPDATE itinerary_activity_type SET event_content = ? WHERE event_type_id = ?";
    private static final String DELETE = "DELETE FROM itinerary_activity_type WHERE event_type_id = ?";
    private static final String GET_ALL = "SELECT * FROM itinerary_activity_type";
    private static final String GET_BY_ID = "SELECT * FROM itinerary_activity_type WHERE event_type_id = ?";
    private static final String GET_TRIPS_BY_EVENT_TYPE = "SELECT t.* FROM trip t JOIN itinerary_activity_type_relationship r ON t.trip_id = r.trip_id JOIN itinerary_activity_type a ON r.event_type_id = a.event_type_id WHERE a.event_content = ?";
    
    @Override
    public void insert(TripactypeVO tripactypeVO) {
    	Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(INSERT);
            pstmt.setString(1, tripactypeVO.getEventcontent());
            
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
    public void update(TripactypeVO tripactypeVO) {
    	Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(UPDATE);
			
            pstmt.setString(1, tripactypeVO.getEventcontent());
            pstmt.setInt(2, tripactypeVO.getEventtypeid());
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
    public void delete(Integer eventtypeid) {
    	Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行

			// 1.設定於 pstm.executeUpdate()之前
			con.setAutoCommit(false);

			// 先刪除行程留言
			pstmt = con.prepareStatement(DELETE);
			
            pstmt.setInt(1, eventtypeid);
            
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
    public List<TripactypeVO> getAll() {
    	List<TripactypeVO> list = new ArrayList<TripactypeVO>();
    	TripactypeVO tripactypeVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_ALL);
			rs = pstmt.executeQuery();

            while (rs.next()) {
                tripactypeVO = new TripactypeVO();
                tripactypeVO.setEventtypeid(rs.getInt("event_type_id"));
                tripactypeVO.setEventcontent(rs.getString("event_content"));
                list.add(tripactypeVO);
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
    public TripactypeVO getByid(Integer eventtypeid) {
    	
    	TripactypeVO tripactypeVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 加載 MySQL 驅動程式
			Class.forName(driver);
			// 建立連線
			con = DriverManager.getConnection(url, userid, password);
			// 準備 SQL 指令執行
			pstmt = con.prepareStatement(GET_BY_ID);

            pstmt.setInt(1, eventtypeid);
            
            rs = pstmt.executeQuery();
            while (rs.next()) {
            	tripactypeVO = new TripactypeVO();
                tripactypeVO.setEventtypeid(rs.getInt("event_type_id"));
                tripactypeVO.setEventcontent(rs.getString("event_content"));
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

		return tripactypeVO;
	}
    
	@Override
	public List<TripVO> getTripsByEventType(String eventType) {
		List<TripVO> trips = new ArrayList<>();
	    TripVO tripVO = null;

	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        // 加載 MySQL 驅動程式
	        Class.forName(driver);
	        // 建立連線
	        con = DriverManager.getConnection(url, userid, password);
	        // 準備 SQL 指令執行
	        pstmt = con.prepareStatement(GET_TRIPS_BY_EVENT_TYPE);

	        pstmt.setString(1, eventType);

	        rs = pstmt.executeQuery();

	        while (rs.next()) {
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
				tripVO.setArticle_title(rs.getString("article_title"));
				tripVO.setVisitors_number(rs.getInt("visitors_number"));
				tripVO.setLikes(rs.getInt("likes"));
	            trips.add(tripVO);
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

	    return trips;
	}
    
    
    
//	private SessionFactory factory;
//	
//	public TripactypeDAO() {
//		factory = HibernateUtil.getSessionFactory();
//	}
//
//	
//	private Session getSession() {
//		return factory.getCurrentSession();
//	}
//	
//	@Override
//	public void insert(TripactypeVO tripactypeVO) {
//		try {
//			Session session = getSession();
//			session.beginTransaction();
//			session.save(tripactypeVO);
//			session.getTransaction().commit();
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			getSession().getTransaction().rollback();
//		}
//		
//	}
//
//	@Override
//	public void update(TripactypeVO tripactypeVO) {
//		try {
//			Session session = getSession();
//			getSession().beginTransaction();
//			getSession().update(tripactypeVO);
//			getSession().getTransaction().commit();
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			getSession().getTransaction().rollback();
//		}
//		
//	}
//
//	@Override
//	public void delete(Integer eventtypeid) {
//		try {
//			Session session = getSession();
//			getSession().beginTransaction();
//			
//			// 根據 ID 獲取要刪除的實體
//			TripactypeVO tripactypeVO = session.get(TripactypeVO.class, eventtypeid);
//			
//			// 刪除該實體
//			if(tripactypeVO != null) {
//				session.delete(tripactypeVO);
//			}
//			session.getTransaction().commit();
//			
//		}catch(Exception e) {
//			e.printStackTrace();
//			
//			if(getSession().getTransaction() != null && getSession().getTransaction().isActive()) {
//				getSession().getTransaction().rollback();
//			}
//		}	
//	}
//
//	@Override
//	public List<TripactypeVO> getAll() {
//		
//		getSession().beginTransaction();
//		
//		return getSession().createQuery("from TripactypeVO", TripactypeVO.class).list();
//	}
//
//	@Override
//	public TripactypeVO getByid(Integer eventtypeid) {
//		Session session = getSession();
//		session.beginTransaction();
//		TripactypeVO tripactypeVO = session.get(TripactypeVO.class, eventtypeid);
//		session.getTransaction().commit();
//		return tripactypeVO;
//	}
//	
//	@Override
//	public List<TripVO> getTripsByEventType(String eventType) {
//	    
//	    Session session = getSession();
//	    session.beginTransaction();
//	    
//	    // HQL 查詢語句，根據活動類型來查詢相關的行程
//	    String hql = "SELECT t FROM TripVO t " +
//	                 "JOIN TripactyperelaVO r ON t.trip_id = r.trip_id " +
//	                 "JOIN TripactypeVO ta ON r.event_type_id = ta.event_type_id " +
//	                 "WHERE ta.eventcontent = :eventType";
//	    
//	    // 執行查詢，並且設置活動類型參數
//	    List<TripVO> trips = session.createQuery(hql, TripVO.class)
//	                                 .setParameter("eventType", eventType)
//	                                 .getResultList();
//	    
//	    session.getTransaction().commit();
//	    return trips;
//	}
	


	public static void main(String[] args) {
		
		TripactypeDAO dao = new TripactypeDAO();
		TripactypeVO tripactype = new TripactypeVO();
		TripactyperelaVO rela = new TripactyperelaVO();
		rela.setRelaid(1);
		
//		// 新增
//		tripactype.setEventcontent("健行活動");
//		dao.insert(tripactype);
//		
//		// 修改
//		tripactype.setEventtypeid(7);
//		tripactype.setEventcontent("美食活動");
//		dao.update(tripactype);
//		
//		// 查詢特定行程活動類型
//		tripactype = dao.getByid(7);
//		System.out.println(tripactype.getEventtypeid() + ",");
//		System.out.println(tripactype.getEventcontent());
//		System.out.println("---------------------");
//		
//		// 查詢行程活動類型
//		List<TripactypeVO> list = dao.getAll();
//		for(TripactypeVO type : list) {
//			System.out.print(type.getEventtypeid() + ",");
//			System.out.println(type.getEventcontent());
//			System.out.println("---------------------");
//		}
//		
//		// 查詢活動
//		List<TripactypeVO> list = dao.getAll();
//		for(TripactypeVO type : list) {
//			System.out.print(type.getEventtypeid() + ",");
//			System.out.print(type.getEventcontent());
//			System.out.println();
//		}
//		
//		// 呼叫 DAO 刪除資料
//		dao.delete(8);
		
	}
}
