package chilltrip.announce.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.announce.model.AnnounceService;
import chilltrip.announce.model.AnnounceVO;

@RestController
@RequestMapping("/announce")
public class AnnounceServelet {

	@Autowired
	AnnounceService announceSvc;
	@Autowired
	AdminService adminSvc;

	@GetMapping("getAll")
	private List<AnnounceVO> getAllAnnounce() throws IOException {

		List<AnnounceVO> announceList = announceSvc.getAllAnnounce();
		return announceList;

	}

	private List<AnnounceVO> getAllAnnounce(String page, HttpServletRequest req) throws IOException {

		int currentPage = (page == null) ? 1 : Integer.parseInt(page);

		List<AnnounceVO> announceList = announceSvc.getAllAnnounce();

		req.getSession().setAttribute("announcePageQty", announceSvc.getPageTotal());
		return announceList;

	}

	@GetMapping("getByAdmin/{id}")
	private List<AnnounceVO> getByAdmin(@PathVariable("id") Integer adminid) throws IOException {

		List<AnnounceVO> announceList = announceSvc.getAnnounceByAdminid(adminid);

		return announceList;
	}
	
	@GetMapping("getByAdminId")
	private List<AnnounceVO> getByAdmin(HttpSession session) throws IOException {
		Integer adminid = (Integer) session.getAttribute("adminId");
		List<AnnounceVO> announceList = announceSvc.getAnnounceByAdminid(adminid);

		return announceList;
	}
	
	@PostMapping("delete/{id}")
	private String deleteAnnounce(@PathVariable("id") Integer announceid) throws IOException {

		if (announceSvc.deleteannounce(announceid)) {
			return "delete success";
		} else {
			return "delete false";
		}
	}

	@PostMapping("add/{id}")
	private ResponseEntity<String> addAnnounce(@Valid @RequestBody AnnounceVO announce, BindingResult result,
			@PathVariable("id") Integer adminid) {
//		Integer adminId = (Integer) req.getSession().getAttribute("adminid");
		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			for (ObjectError error : result.getAllErrors()) {
				errorMessage.append(error.getDefaultMessage()).append("\n");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed: " + errorMessage.toString());
		}
		AdminVO admin = adminSvc.getOneAdmin(adminid);
		announce.setAdminvo(admin);
		announceSvc.addannounce(announce);
		return ResponseEntity.ok("success");
	}

	@GetMapping("CompositeQuery")
	private List<AnnounceVO> getCompositeQuery(@RequestParam Map<String, String> map) {
		if (map != null) {
			List<AnnounceVO> announces =  announceSvc.getAnnounceByCompositeQuery(map);
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    for (AnnounceVO announce : announces) {
			        // 格式化时间字段
			        announce.setStarttimeStr(sdf.format(announce.getStarttime()));
			        announce.setEndtimeStr(sdf.format(announce.getEndtime()));
			    }
			    
			    return announces;
		} else {
			List<AnnounceVO> announces = announceSvc.getAllAnnounce();
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    for (AnnounceVO announce : announces) {
			        // 格式化时间字段
			        announce.setStarttimeStr(sdf.format(announce.getStarttime()));
			        announce.setEndtimeStr(sdf.format(announce.getEndtime()));
			    }
			    
			    return announces;
		}

	}

	@PostMapping("update")
	private ResponseEntity<String> update(@Valid @RequestBody AnnounceVO announce, BindingResult result) {

		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			for (ObjectError error : result.getAllErrors()) {
				errorMessage.append(error.getDefaultMessage()).append("\n");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed: " + errorMessage.toString());
		}

		announceSvc.updateannounce(announce);
		return ResponseEntity.ok("success");
	}

	@GetMapping("getById/{id}")
	private AnnounceVO getAnnounce(@PathVariable("id") Integer announceid) throws IOException {

		return announceSvc.getById(announceid);

	}
}
