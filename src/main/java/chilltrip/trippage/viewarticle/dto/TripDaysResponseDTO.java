package chilltrip.trippage.viewarticle.dto;

import java.util.List;

//返回給前端的完整 DTO
public class TripDaysResponseDTO {
	
    private List<TripDayDTO> days;

	public List<TripDayDTO> getDays() {
		return days;
	}

	public void setDays(List<TripDayDTO> days) {
		this.days = days;
	}
    
   
}
