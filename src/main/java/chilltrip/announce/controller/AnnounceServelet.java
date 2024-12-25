package chilltrip.announce.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONObject;

import chilltrip.admin.model.AdminDAO;
import chilltrip.admin.model.AdminDAOImplJDBC;
import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.announce.model.AnnounceService;
import chilltrip.announce.model.AnnounceVO;

@WebServlet("/announce/announce.do")
public class AnnounceServelet extends HttpServlet {

	private AnnounceService announceSvc;
	private AdminService adminSvc;

	public void init() {
		announceSvc = new AnnounceService();
		adminSvc = new AdminService();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

//	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//		req.setCharacterEncoding("UTF-8");
//		res.setContentType("text/html; charset=UTF-8");
//		String action = req.getParameter("action");
//
//		switch (action) {
//		case "getAll":
//			getAllAnnounce(req, res);
//			break;
//		case "getAnnounceByAdmin":
//			getByAdmin(req, res);
//			break;
//		case "delete":
//			deleteAnnounce(req, res);
//			break;
//		case "addAnnounce":
//			addAnnounce(req, res);
//			break;
//		case "compositeQuery":
//			getCompositeQuery(req, res);
//			break;
//		case "upadate":
//			update(req, res);
//			break;
//
//		}

//	}

	private void getAllAnnounce(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String page = req.getParameter("page");
		int currentPage = (page == null) ? 1 : Integer.parseInt(page);

		List<AnnounceVO> announceList = announceSvc.getAllAnnounce(currentPage);

		if (req.getSession().getAttribute("announcePageQty") == null) {
			int announcePageQty = announceSvc.getPageTotal();
			req.getSession().setAttribute("announcePageQty", announcePageQty);
		}
		JSONArray jsonArray = new JSONArray();

		for (AnnounceVO announce : announceList) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("announceid", announce.getAnnounceid());
			jsonRes.put("title", announce.getTitle());
			jsonRes.put("content", announce.getContent());
			jsonRes.put("starttime", announce.getStarttime());
			jsonRes.put("endtime", announce.getEndtime());
			jsonRes.put("coverphoto", announce.getCoverphoto());
			jsonRes.put("admin", announce.getAdminvo());

			jsonArray.put(jsonRes);
		}
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());
	}

	private void getByAdmin(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String admin = req.getParameter("adminid");
		int adminid = Integer.parseInt(admin);
		List<AnnounceVO> announceList = announceSvc.getAnnounceByAdminid(adminid);

		JSONArray jsonArray = new JSONArray();

		for (AnnounceVO announce : announceList) {
			JSONObject jsonRes = new JSONObject();
			jsonRes.put("announceid", announce.getAnnounceid());
			jsonRes.put("title", announce.getTitle());
			jsonRes.put("content", announce.getContent());
			jsonRes.put("starttime", announce.getStarttime());
			jsonRes.put("endtime", announce.getEndtime());
			jsonRes.put("coverphoto", announce.getCoverphoto());
			jsonRes.put("admin", announce.getAdminvo());

			jsonArray.put(jsonRes);
		}

		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(jsonArray.toString());

		
	}

	private void deleteAnnounce(HttpServletRequest req, HttpServletResponse res) throws IOException {

		Integer announceid = Integer.valueOf(req.getParameter("announceid"));
		announceSvc.deleteannounce(announceid);
		
		   // 設置回應的內容類型為純文字 (text/plain)
	    res.setContentType("text/plain");
	    res.setCharacterEncoding("UTF-8");
	    
	    // 回傳成功訊息給前端
	    res.getWriter().write("success");
	}

	private String addAnnounce(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		Integer adminid = Integer.valueOf(req.getParameter("adminid"));
		String title = req.getParameter("title");
		String content = req.getParameter("content");
		Date starttime = Date.valueOf(req.getParameter("starttime").trim());
		Date endtime = Date.valueOf(req.getParameter("endtime").trim());

		byte[] photo = null; // 初始化圖片資料為 null
		Part part = req.getPart("photo");
		InputStream in = part.getInputStream();
		photo = new byte[in.available()]; // byte[] buf = in.readAllBytes(); // Java 9 的新方法
		in.read(photo);
		in.close();

		AdminVO adminvo = adminSvc.getOneAdmin(adminid);

		AnnounceVO announce = new AnnounceVO();

		announce.setAdminvo(adminvo);
		announce.setContent(content);
		announce.setTitle(title);
		announce.setStarttime(starttime);
		announce.setEndtime(endtime);
		announce.setCoverphoto(photo);
		announceSvc.addannounce(announce);
		
		return "add success";
	}

	private String getCompositeQuery(HttpServletRequest req, HttpServletResponse res) {
		Map<String, String[]> map = req.getParameterMap();

		if (map != null) {
			List<AnnounceVO> announceList = announceSvc.getAnnounceByCompositeQuery(map);
			req.setAttribute("anounceList", announceList);
		} else {
			return "/frontend/index.html";
		}

		return "/announce/listCompositeQueryAnnounce.jsp";
	}

	private String update(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		Integer adminid = Integer.valueOf(req.getParameter("adminid"));
		Integer announceid = Integer.valueOf(req.getParameter("announceid"));
		String title = req.getParameter("title");
		String content = req.getParameter("content");
		Date starttime = Date.valueOf(req.getParameter("starttime").trim());
		Date endtime = Date.valueOf(req.getParameter("endtime").trim());

		byte[] photo = null; // 初始化圖片資料為 null
		Part part = req.getPart("photo");
		InputStream in = part.getInputStream();
		photo = new byte[in.available()];
		in.read(photo);
		in.close();

		
		AdminVO adminvo = adminSvc.getOneAdmin(adminid);
		AnnounceVO announce = new AnnounceVO();
		announce.setAnnounceid(announceid);
		announce.setAdminvo(adminvo);
		announce.setContent(content);
		announce.setTitle(title);
		announce.setStarttime(starttime);
		announce.setEndtime(endtime);
		announce.setCoverphoto(photo);
		announceSvc.updateannounce(announce);

		return "/announce/listAllAnnounce.jsp";
	}
}
