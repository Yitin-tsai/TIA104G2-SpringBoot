package chilltrip.admin.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;

@WebServlet("/admin/admin.login")
public class AdminLoginServlet extends HttpServlet {
	private AdminService adminSvc;

	public void init() {
		adminSvc = new AdminService();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");
		if ("login".equals(action)) {
			login(req, res);
		}

	}

	private void login(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String account = req.getParameter("account");
		String password = req.getParameter("password");
		boolean checkAccount = false;
		boolean checklogin = false;

		List<AdminVO> list = adminSvc.getAll();
		for (AdminVO admin : list) {
			if (account.equals(admin.getAdminaccount())) {
				checkAccount = true;
				if (password.equals(admin.getAdminpassword())) {
					req.getSession().setAttribute("adminid", admin.getAdminid());
					checklogin =true;
					System.out.println("login success");
					String url = "/admin/listOneAdmin.jsp";
					RequestDispatcher succesView = req.getRequestDispatcher(url);
					succesView.forward(req, res);
					break;
				}
			}
		}
		if (!checkAccount) {
			System.out.println("not found account");
		}
		if (checkAccount && !checklogin) {
			System.out.println("password error");
		}
	}
}
