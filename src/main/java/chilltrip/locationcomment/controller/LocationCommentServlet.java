package chilltrip.locationcomment.controller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import chilltrip.location.model.LocationService;
import chilltrip.location.model.LocationVO;
import chilltrip.locationcomment.model.LocationCommentService;
import chilltrip.locationcomment.model.LocationCommentVO;
import chilltrip.member.model.MemberService;
import chilltrip.member.model.MemberVO;

@RestController
@RequestMapping("/locationComment")
public class LocationCommentServlet {
	
	@Autowired
	LocationCommentService commentSvc;
	@Autowired
	MemberService memberSvc;
	

	@GetMapping("getByLocation/{id}")
	private List<LocationCommentVO> getByLocation(@PathVariable("id") Integer locationid) throws IOException {
		// TODO Auto-generated method stub

		List<LocationCommentVO> commentList = commentSvc.getLocationCommentByLocation(locationid);

		return commentList;

	}

	@GetMapping("getByMember/{id}")
	public List<LocationCommentVO> getByMember(@PathVariable("id") Integer memberid) throws IOException {

		List<LocationCommentVO> commentList = commentSvc.getLocationCommentByLocation(memberid);
		return commentList;

	}

	@PostMapping("/update/{mid}/{lid}")
	private ResponseEntity<String> updateLocationComment(@Valid @RequestBody LocationCommentVO locationCommentVO,
			BindingResult result,@PathVariable("mid") Integer memberid ,@PathVariable("lid")Integer locationid) {
		// TODO Auto-generated method stub
		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			for (ObjectError error : result.getAllErrors()) {
				errorMessage.append(error.getDefaultMessage()).append("\n");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed: " + errorMessage.toString());
		}
		LocationService locationSvc= new LocationService();
		MemberVO member = memberSvc.getOneMember(memberid);
		LocationVO location = locationSvc.getLocationById(locationid);
		locationCommentVO.setLocationvo(location);
		locationCommentVO.setMembervo(member);	
		commentSvc.updateLocationComment(locationCommentVO);
		return ResponseEntity.ok("success");
	}

	@PostMapping("/add/{mid}/{lid}")
	private ResponseEntity<String> addLocationComment(@Valid @RequestBody LocationCommentVO locationCommentVO,
			BindingResult result,@PathVariable("mid") Integer memberid ,@PathVariable("lid") Integer locationid) {
		// TODO Auto-generated method stub
		if (result.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder();
			for (ObjectError error : result.getAllErrors()) {
				errorMessage.append(error.getDefaultMessage()).append("\n");
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed: " + errorMessage.toString());
		}
		LocationService locationSvc= new LocationService();
		
		MemberVO member = memberSvc.getOneMember(memberid);
		LocationVO location = locationSvc.getLocationById(locationid);
		locationCommentVO.setLocationvo(location);
		locationCommentVO.setMembervo(member);	
		commentSvc.addLocationComment(locationCommentVO);
		return ResponseEntity.ok("success");
	}

	 @PostMapping("/delete/{id}")
	private String deleteLocationComment(@PathVariable("id") Integer id) {
		// TODO Auto-generated method stub
		if(commentSvc.deleteLocationComment(id)) {
			return "sucess";
		}return "false";
	}

}