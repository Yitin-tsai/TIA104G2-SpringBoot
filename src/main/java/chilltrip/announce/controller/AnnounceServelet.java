package chilltrip.announce.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.announce.model.AnnounceService;
import chilltrip.announce.model.AnnounceVO;

@RestController
@RequestMapping("/announce")
public class AnnounceServelet  {
	
	@Autowired
	AnnounceService announceSvc;
	@Autowired
	AdminService adminSvc;

	Gson gson = new Gson();

	@GetMapping("getAll")
	private List<AnnounceVO>  getAllAnnounce(String page,HttpServletRequest req) throws IOException {
		
		int currentPage = (page == null) ? 1 : Integer.parseInt(page);

		List<AnnounceVO> announceList = announceSvc.getAllAnnounce(currentPage);
	
		req.getSession().setAttribute("announcePageQty", announceSvc.getPageTotal());
		return announceList;
	    
	}
	@GetMapping("getByAdmin/{id}")
	private List<AnnounceVO> getByAdmin(@PathVariable("id")Integer adminid) throws IOException {
		
		List<AnnounceVO> announceList = announceSvc.getAnnounceByAdminid(adminid);



		return announceList;

		
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
