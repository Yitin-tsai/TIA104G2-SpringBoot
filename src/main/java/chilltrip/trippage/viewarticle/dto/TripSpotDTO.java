package chilltrip.trippage.viewarticle.dto;

import java.math.BigDecimal;

//景點信息 DTO
public class TripSpotDTO {
	private String name = "";    // 設定預設空字串
    private String address = "";         // 設定預設空字串
    private String googlePlaceId = "";   // 設定預設空字串
    private BigDecimal latitude = BigDecimal.ZERO;  // 設定預設0
    private BigDecimal longitude = BigDecimal.ZERO; // 設定預設0
    private String startTime = "";       // 設定預設空字串
    private String endTime = "";         // 設定預設空字串
    private Integer spotOrder = 0;       // 設定預設0
    
	
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getGooglePlaceId() {
		return googlePlaceId;
	}
	public void setGooglePlaceId(String googlePlaceId) {
		this.googlePlaceId = googlePlaceId;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
	public Integer getSpotOrder() {
		return spotOrder;
	}
	public void setSpotOrder(Integer spotOrder) {
		this.spotOrder = spotOrder;
	}
    
    
}

