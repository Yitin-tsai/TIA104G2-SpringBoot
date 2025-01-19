package chilltrip.trippage.editor.dto;

import java.util.Base64;
import java.util.List;

public class TripCreateRequest {
	private Integer memberId;
	private String articleTitle;
	private String abstractContent; // 因為 abstract 是保留字
	private Integer status;
	private String activityType;
	private String region;
	private List<DaySchedule> daySchedules;
	private byte[] coverPhoto; // 直接使用 byte[]

	// Getters and Setters
	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	public String getAbstractContent() {
		return abstractContent;
	}

	public void setAbstractContent(String abstractContent) {
		this.abstractContent = abstractContent;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<DaySchedule> getDaySchedules() {
		return daySchedules;
	}

	public void setDaySchedules(List<DaySchedule> daySchedules) {
		this.daySchedules = daySchedules;
	}

	public byte[] getCoverPhoto() {
		return coverPhoto;
	}

	public void setCoverPhoto(byte[] coverPhoto) {
		this.coverPhoto = coverPhoto;
	}

}
