package chilltrip.tripcomment.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripCommentJDBCDAO implements TripCommentDAO_interface {
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "123456";

	private static final String INSERT_STMT = "INSERT INTO trip_comment (member_id,trip_id,score,photo,create_time,content) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String GET_ALL_STMT = "SELECT trip_comment_id,member_id,trip_id,score,photo,create_time,content FROM trip_comment order by trip_comment_id";
	private static final String GET_ONE_STMT = "SELECT trip_comment_id,member_id,trip_id,score,photo,create_time,content FROM trip_comment where trip_comment_id = ?";
	private static final String DELETE = "DELETE FROM trip_comment where trip_comment_id = ?";
	private static final String UPDATE = "UPDATE trip_comment set member_id=?, trip_id=?, score=?, photo=?, create_time=?, content=?  where trip_comment_id = ?";
	private static final String GET_BY_TRIPID = "SELECT * FROM trip_comment WHERE trip_id = ?";
	private static final String GET_COMMENT_WITH_MEMBERINFO = "SELECT trip_comment.trip_comment_id, trip_comment.member_id, trip_comment.trip_id, trip_comment.score, trip_comment.create_time, trip_comment.content, trip_comment.photo AS comment_photo, member.nick_name, member.photo AS member_photo FROM trip_comment JOIN member ON trip_comment.member_id = member.member_id WHERE trip_comment.trip_id = ?";

	@Override
	public void insert(TripCommentVO tripCommentVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setInt(1, tripCommentVO.getMemberId());
			pstmt.setInt(2, tripCommentVO.getTripId());
			pstmt.setInt(3, tripCommentVO.getScore());
			pstmt.setBytes(4, tripCommentVO.getPhoto()); // 圖片資料以 byte[] 儲存
			pstmt.setTimestamp(5, tripCommentVO.getCreateTime());
			pstmt.setString(6, tripCommentVO.getContent());

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
	public void update(TripCommentVO tripCommentVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, tripCommentVO.getMemberId());
			pstmt.setInt(2, tripCommentVO.getTripId());
			pstmt.setInt(3, tripCommentVO.getScore());
			pstmt.setBytes(4, tripCommentVO.getPhoto());
			pstmt.setTimestamp(5, tripCommentVO.getCreateTime());
			pstmt.setString(6, tripCommentVO.getContent());
			pstmt.setInt(7, tripCommentVO.getTripCommentId());

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
	public void delete(Integer tripCommentId) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, tripCommentId);

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
	public TripCommentVO findByPrimaryKey(Integer tripCommentId) {

		TripCommentVO tripCommentVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, tripCommentId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				tripCommentVO = new TripCommentVO();
				tripCommentVO.setTripCommentId(rs.getInt("trip_comment_id"));
				tripCommentVO.setMemberId(rs.getInt("member_id"));
				tripCommentVO.setTripId(rs.getInt("trip_id"));
				tripCommentVO.setScore(rs.getInt("score"));
				tripCommentVO.setPhoto(rs.getBytes("photo"));
				tripCommentVO.setCreateTime(rs.getTimestamp("create_time"));
				tripCommentVO.setContent(rs.getString("content"));
				if(rs.getBytes("photo") != null) {
					tripCommentVO.setPhoto_base64(new String(Base64.getEncoder().encodeToString(rs.getBytes("photo"))));	
				}
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

		return tripCommentVO;
	}

	@Override
	public List<TripCommentVO> getAll() {

		List<TripCommentVO> list = new ArrayList<TripCommentVO>();
		TripCommentVO tripCommentVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				tripCommentVO = new TripCommentVO();
				tripCommentVO.setTripCommentId(rs.getInt("trip_comment_id"));
				tripCommentVO.setMemberId(rs.getInt("member_id"));
				tripCommentVO.setTripId(rs.getInt("trip_id"));
				tripCommentVO.setScore(rs.getInt("score"));
				tripCommentVO.setPhoto(rs.getBytes("photo"));
				tripCommentVO.setCreateTime(rs.getTimestamp("create_time"));
				tripCommentVO.setContent(rs.getString("content"));
				if(rs.getBytes("photo") != null) {
					tripCommentVO.setPhoto_base64(new String(Base64.getEncoder().encodeToString(rs.getBytes("photo"))));	
				}

				list.add(tripCommentVO); // 將該行資料儲存在 list 集合中
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
	
	public List<TripCommentVO> findByTripId(Integer tripId) {
		List<TripCommentVO> list = new ArrayList<TripCommentVO>();
		
		TripCommentVO tripCommentVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_BY_TRIPID);
			
	        pstmt.setInt(1, tripId);
	        
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            tripCommentVO = new TripCommentVO();
	            tripCommentVO.setTripCommentId(rs.getInt("trip_comment_id"));
	            tripCommentVO.setMemberId(rs.getInt("member_id"));
	            tripCommentVO.setTripId(rs.getInt("trip_id"));
	            tripCommentVO.setScore(rs.getInt("score"));
	            tripCommentVO.setPhoto(rs.getBytes("photo"));
	            tripCommentVO.setCreateTime(rs.getTimestamp("create_time"));
	            tripCommentVO.setContent(rs.getString("content"));
	            list.add(tripCommentVO);
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

	public List<Map<String, Object>> getCommentsWithMemberInfo(Integer tripId) {
	    List<Map<String, Object>> list = new ArrayList<>();
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName(driver);
	        con = DriverManager.getConnection(url, userid, passwd);
	        pstmt = con.prepareStatement(GET_COMMENT_WITH_MEMBERINFO);
	        pstmt.setInt(1, tripId);
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Map<String, Object> commentMap = new HashMap<>();
	            commentMap.put("tripCommentId", rs.getInt("trip_comment_id"));
	            commentMap.put("memberId", rs.getInt("member_id"));
	            commentMap.put("tripId", rs.getInt("trip_id"));
	            commentMap.put("score", rs.getInt("score"));
	            commentMap.put("createTime", rs.getTimestamp("create_time"));
	            commentMap.put("content", rs.getString("content"));
	            commentMap.put("nickName", rs.getString("nick_name"));
	            commentMap.put("memberPhoto", rs.getBytes("member_photo"));
	            commentMap.put("commentPhoto", rs.getBytes("comment_photo"));
	            list.add(commentMap);
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

	public static void main(String[] args) {

		TripCommentJDBCDAO dao = new TripCommentJDBCDAO();

//		// 新增
//		TripCommentVO tripcom1 = new TripCommentVO();
//		tripcom1.setMemberId(new Integer(1));
//		tripcom1.setTripId(new Integer(1));
//		tripcom1.setScore(new Integer(5));
//
//		// 讀取圖片並轉換為 byte[]
//		byte[] photoBytes = null;
//		BufferedInputStream bis = null;
//		try {
//			FileInputStream fis = new FileInputStream("src/main/webapp/resource/images/tokyo.jpg");
//			bis = new BufferedInputStream(fis);
//			photoBytes = bis.readAllBytes(); // 讀取檔案並轉換為 byte[]
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (bis != null) {
//				try {
//					bis.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		tripcom1.setPhoto(photoBytes); // 將圖片設置到 tripcom1 物件的 photo 屬性
//		tripcom1.setCreateTime(java.sql.Timestamp.valueOf("2024-12-01 15:30:45"));
//		tripcom1.setContent("這個行程太讚了吧!馬上照著出發去玩超開心~");
//
//		// 呼叫 DAO 方法插入資料
//		dao.insert(tripcom1);

//		// 修改
//		TripCommentVO tripcom2 = new TripCommentVO();
//		tripcom2.setTripCommentId(new Integer(4));
//		tripcom2.setMemberId(new Integer(1));
//		tripcom2.setTripId(new Integer(1));
//		tripcom2.setScore(new Integer(5));
//		// 讀取圖片並轉換為 byte[]
//		byte[] photoBytes = null;
//		BufferedInputStream bis = null;
//		try {
//			FileInputStream fis = new FileInputStream("src/main/webapp/resource/images/tomcat.png");
//			bis = new BufferedInputStream(fis);
//			photoBytes = bis.readAllBytes(); // 讀取檔案並轉換為 byte[]
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (bis != null) {
//				try {
//					bis.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		tripcom2.setPhoto(photoBytes); // 將圖片設置到 tripcom1 物件的 photo 屬性
//		tripcom2.setCreateTime(java.sql.Timestamp.valueOf("2024-12-05 18:30:45"));
//		tripcom2.setContent("這個行程太讚了吧!馬上照著出發去玩超開心~YAAAAAAAAAAAAA");
//		
//		// 呼叫 DAO 方法更新資料
//		dao.update(tripcom2);

//		// 用主鍵查詢單筆行程留言
//		TripCommentVO tripcom3 = dao.findByPrimaryKey(4);
//		System.out.println(tripcom3.getTripCommentId() + ",");
//		System.out.println(tripcom3.getMemberId() + ",");
//		System.out.println(tripcom3.getTripId() + ",");
//		System.out.println(tripcom3.getScore() + ",");
//		System.out.println(tripcom3.getPhoto() + ",");
//		System.out.println(tripcom3.getCreateTime() + ",");
//		System.out.println(tripcom3.getContent());
//		System.out.println("---------------------");

		// 查詢全部
//		List<TripCommentVO> list = dao.getAll();
//		for (TripCommentVO aTripcom : list) {
//			System.out.println(aTripcom.getTripCommentId() + ",");
//			System.out.println(aTripcom.getMemberId() + ",");
//			System.out.println(aTripcom.getTripId() + ",");
//			System.out.println(aTripcom.getScore() + ",");
//			System.out.println(aTripcom.getPhoto() + ",");
//			System.out.println(aTripcom.getCreateTime() + ",");
//			System.out.println(aTripcom.getContent());
//			System.out.println();
//		}

//		// 刪除資料
//		dao.delete(4);
	}
}