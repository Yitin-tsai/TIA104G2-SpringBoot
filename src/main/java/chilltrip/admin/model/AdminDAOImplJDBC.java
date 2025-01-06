package chilltrip.admin.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImplJDBC implements AdminDAO {
	
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/tia104g2?serverTimezone=Asia/Taipei";
	String userid = "root";
	String passwd = "123456";
	
	private static final String INSERT_STMT=
			"INSERT INTO administrator (email, admin_account, admin_password ,admin_name, phone,account_status,nick_name) VALUES(?, ?, ?, ?, ?, ?,?) ";
	private static final String GET_ALL_STMT = 
			"SELECT administrator_id,email, admin_account, admin_password ,admin_name, phone,account_status,create_time,nick_name FROM administrator order by administrator_id";
	private static final String GET_ONE_STMT = 
			"SELECT administrator_id,email, admin_account, admin_password ,admin_name, phone,account_status,create_time,nick_name  FROM administrator WHERE administrator_id=?";
	private static final String DELETE = 
			"DELETE FROM administrator where administrator_id=?";
	private static final String UPDATE = 
			"UPDATE administrator set email=?, admin_account=?, admin_password=?, admin_name=?, phone=?, account_status=?, nick_name=? where administrator_id = ?";
	private static final String GET_ONE_BY_account = "SELECT * FROM administrator WHERE admin_account = ?";
	private static final String GET_ONE_BY_PHONE = "SELECT * FROM administrator WHERE phone = ?";
	@Override
	public void insert(AdminVO adminVO) {
		
		
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setString(1, adminVO.getEmail());
			pstmt.setString(2, adminVO.getAdminaccount());
			pstmt.setString(3, adminVO.getAdminpassword());
			pstmt.setString(4, adminVO.getAdminname());
			pstmt.setString(5, adminVO.getPhone());
			pstmt.setInt(6, adminVO.getStatus());
			pstmt.setString(7, adminVO.getAdminnickname());


			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
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
	public void update(AdminVO adminVO) {
		// TODO Auto-generated method stub

			Connection con = null;
			PreparedStatement pstmt = null;

			try {

				Class.forName(driver);
				con = DriverManager.getConnection(url, userid, passwd);
				pstmt = con.prepareStatement(UPDATE);

				pstmt.setString(1, adminVO.getEmail());
				pstmt.setString(2, adminVO.getAdminaccount());
				pstmt.setString(3, adminVO.getAdminpassword());
				pstmt.setString(4, adminVO.getAdminname());
				pstmt.setString(5, adminVO.getPhone());
				pstmt.setInt(6, adminVO.getStatus());
				pstmt.setString(7, adminVO.getAdminnickname());
				pstmt.setInt(8, adminVO.getAdminid());
				pstmt.executeUpdate();

				// Handle any driver errors
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Couldn't load database driver. "
						+ e.getMessage());
				// Handle any SQL errors
			} catch (SQLException se) {
				throw new RuntimeException("A database error occured. "
						+ se.getMessage());
				// Clean up JDBC resources
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
	public void delete(Integer  adminid) {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, adminid);

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
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
	public List<AdminVO> getAll() {
		List<AdminVO> list = new ArrayList<AdminVO>();
		AdminVO adminVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				
				adminVO = new AdminVO();
				adminVO.setAdminid(rs.getInt("administrator_id"));
				adminVO.setEmail(rs.getString("email"));
				adminVO.setAdminaccount(rs.getString("admin_account"));
				adminVO.setAdminpassword(rs.getString("admin_password"));
				adminVO.setAdminname(rs.getString("admin_name"));
				adminVO.setPhone(rs.getString("phone"));
				adminVO.setStatus(rs.getInt("account_status"));
				adminVO.setAdminnickname(rs.getString("nick_name"));
				list.add(adminVO); // Store the row in the list
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
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
	public AdminVO getById(Integer adminid) {
		
		AdminVO adminVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1,adminid);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// adminVO 也稱為 Domain objects
				adminVO = new AdminVO();
				adminVO.setAdminid(rs.getInt("administrator_id"));
				adminVO.setEmail(rs.getString("email"));
				adminVO.setAdminaccount(rs.getString("admin_account"));
				adminVO.setAdminpassword(rs.getString("admin_password"));
				adminVO.setAdminname(rs.getString("admin_name"));
				adminVO.setPhone(rs.getString("phone"));
				adminVO.setStatus(rs.getInt("account_status"));
				adminVO.setAdminnickname(rs.getString("nick_name"));
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
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
		return adminVO;
	}

	@Override
	public AdminVO getByAccount(String account) {
		AdminVO adminVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_BY_account);

			pstmt.setString(1,account);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// adminVO 也稱為 Domain objects
				adminVO = new AdminVO();
				adminVO.setAdminid(rs.getInt("administrator_id"));
				adminVO.setEmail(rs.getString("email"));
				adminVO.setAdminaccount(rs.getString("admin_account"));
				adminVO.setAdminpassword(rs.getString("admin_password"));
				adminVO.setAdminname(rs.getString("admin_name"));
				adminVO.setPhone(rs.getString("phone"));
				adminVO.setStatus(rs.getInt("account_status"));
				adminVO.setAdminnickname(rs.getString("nick_name"));
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
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
		
		return adminVO;
		
	}


	@Override
	public AdminVO getByPhone(String phone) {
		AdminVO adminVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		try {

			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, passwd);
			pstmt = con.prepareStatement(GET_ONE_BY_PHONE);

			pstmt.setString(1,phone);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// adminVO 也稱為 Domain objects
				adminVO = new AdminVO();
				adminVO.setAdminid(rs.getInt("administrator_id"));
				adminVO.setEmail(rs.getString("email"));
				adminVO.setAdminaccount(rs.getString("admin_account"));
				adminVO.setAdminpassword(rs.getString("admin_password"));
				adminVO.setAdminname(rs.getString("admin_name"));
				adminVO.setPhone(rs.getString("phone"));
				adminVO.setStatus(rs.getInt("account_status"));
				adminVO.setAdminnickname(rs.getString("nick_name"));
			}

			// Handle any driver errors
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Couldn't load database driver. "
					+ e.getMessage());
			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. "
					+ se.getMessage());
			// Clean up JDBC resources
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
		
			return adminVO;
	}
	public static void main(String[] args) {
		AdminDAOImplJDBC dao = new AdminDAOImplJDBC();
		System.out.println(dao.getByAccount("seal123"));
		String account = "seal123";
		System.out.println(account.equals(dao.getByAccount("seal123").getAdminaccount()));
	}
}
	