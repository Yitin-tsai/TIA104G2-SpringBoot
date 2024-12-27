package chilltrip.triparea.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import chillchip.trip.model.TripVO;

@Entity
@Table(name = "itinerary_area")
public class TripAreaVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_location_id")
	private Integer triplocationid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id", referencedColumnName = "trip_id")
	private TripVO tripid;

	@Column(name = "region_content")
	private String regioncontent;
	
	public Integer getTriplocationid() {
		return triplocationid;
	}
	
	public String getRegioncontent() {
		return regioncontent;
	}
	
	public void setTriplocationid(Integer triplocationid) {
		this.triplocationid = triplocationid;
	}
	
	public void setRegioncontent(String regioncontent) {
		this.regioncontent = regioncontent;
	}

	public TripVO getTripid() {
		return tripid;
	}

	public void setTripid(TripVO tripid) {
		this.tripid = tripid;
	}

	@Override
	public String toString() {
		return "TripAreaVO [triplocationid=" + triplocationid + ", tripid=" + tripid + ", regioncontent="
				+ regioncontent + "]";
	}
	
}
