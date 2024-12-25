package chilltrip.triplike.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import chilltrip.member.model.MemberVO;
import chilltrip.trip.entity.TripVO;

@Entity
@Table(name = "trip_like")
public class TripLikeVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_like_id", updatable = false)
	Integer tripLikeId;
	
	@ManyToOne
	@JoinColumn(name = "trip_id",  referencedColumnName = "trip_id")	
	private TripVO tripvo;
	
	@ManyToOne
	@JoinColumn(name = "member_id",  referencedColumnName = "member_id")
	private MemberVO membervo;

	public Integer getTripLikeId() {
		return tripLikeId;
	}

	public void setTripLikeId(Integer tripLikeId) {
		this.tripLikeId = tripLikeId;
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
		return "TripLikeVO [tripLikeId=" + tripLikeId + ", tripvo=" + tripvo + ", membervo=" + membervo + "]";
	}
	
	
	
}
