package chilltrip.tripactype.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import chilltrip.tripactyperela.model.TripactyperelaVO;


@Entity
@Table(name = "itinerary_activity_type")
public class TripactypeVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "event_type_id", updatable = false, insertable = false)
	private Integer eventtypeid;
	
	@Lob
	@Column(name = "event_content")
	private String eventcontent;
	
	@OneToMany(mappedBy = "eventtypeid", cascade = CascadeType.ALL)
	@OrderBy("eventtypeid asc")
	private Set<TripactyperelaVO> relationship;

	public Integer getEventtypeid() {
		return eventtypeid;
	}

	public String getEventcontent() {
		return eventcontent;
	}

	public Set<TripactyperelaVO> getRelationship() {
		return relationship;
	}

	public void setEventtypeid(Integer eventtypeid) {
		this.eventtypeid = eventtypeid;
	}

	public void setEventcontent(String eventcontent) {
		this.eventcontent = eventcontent;
	}

	public void setRelationship(Set<TripactyperelaVO> relationship) {
		this.relationship = relationship;
	}

	@Override
	public String toString() {
		return "TripactypeVO [eventtypeid=" + eventtypeid + ", eventcontent=" + eventcontent + ", relationship="
				+ relationship + "]";
	}
	
}
