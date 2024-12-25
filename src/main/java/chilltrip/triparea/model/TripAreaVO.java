package chilltrip.triparea.model;

public class TripAreaVO {
	private Integer triplocationid;
	private Integer tripid;
	private String regioncontent;
	
	public Integer getTriplocationid() {
		return triplocationid;
	}
	
	public Integer getTripid() {
		return tripid;
	}
	
	public String getRegioncontent() {
		return regioncontent;
	}
	
	public void setTriplocationid(Integer triplocationid) {
		this.triplocationid = triplocationid;
	}
	
	public void setTripid(Integer tripid) {
		this.tripid = tripid;
	}
	
	public void setRegioncontent(String regioncontent) {
		this.regioncontent = regioncontent;
	}
	
	@Override
	public String toString() {
		return "TripAreaVO [triplocationid=" + triplocationid + ", tripid=" + tripid + ", regioncontent="
				+ regioncontent + "]";
	}
	
}
