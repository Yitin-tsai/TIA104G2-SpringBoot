package chilltrip.trippage.viewarticle.dto;

import java.util.List;

//天數資訊 DTO
public class TripDayDTO {

	private Integer dayNumber;
	private String dayContent;
	private List<TripSpotDTO> spots;

	public Integer getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(Integer dayNumber) {
		this.dayNumber = dayNumber;
	}

	public String getDayContent() {
		return dayContent;
	}

	public void setDayContent(String dayContent) {
		this.dayContent = dayContent;
	}

	public List<TripSpotDTO> getSpots() {
		return spots;
	}

	public void setSpots(List<TripSpotDTO> spots) {
		this.spots = spots;
	}

}
