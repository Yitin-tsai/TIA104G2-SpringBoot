package chilltrip.tripcollection.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import chilltrip.member.model.MemberVO;
import chilltrip.trip.model.TripVO;

@Entity
@Table(name = "trip_collection")
public class TripCollectionVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_collection_id", updatable = false)
	Integer tripCollectionId;
	
	@ManyToOne
	@JoinColumn(name = "trip_id",  referencedColumnName = "trip_id")	
	private TripVO tripvo;
	
	@ManyToOne
	@JoinColumn(name = "member_id",  referencedColumnName = "member_id")
	private MemberVO membervo;

	public Integer getTripCollectionId() {
		return tripCollectionId;
	}

	public void setTripCollectionId(Integer tripCollectionId) {
		this.tripCollectionId = tripCollectionId;
	}

	public TripVO getTripvo() {
		return tripvo;
	}

	public void setTripvo(TripVO tripvo) {
		this.tripvo = tripvo;
	}

	public MemberVO getMembervo() {
		return membervo;
	}

	public void setMembervo(MemberVO membervo) {
		this.membervo = membervo;
	}

	@Override
	public String toString() {
		return "TripCollectionVO [tripCollectionId=" + tripCollectionId + ", tripvo=" + tripvo + ", membervo="
				+ membervo + "]";
	}
	
	
}
