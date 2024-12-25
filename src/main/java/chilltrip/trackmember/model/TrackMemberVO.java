package chilltrip.trackmember.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import chilltrip.member.model.MemberVO;

@Entity
@Table(name = "track_users")
public class TrackMemberVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "track_users_id", updatable = false)
	private Integer trackMemberId;
	
	@ManyToOne
	@JoinColumn(name = "track_member_id",  referencedColumnName = "member_id")	
	private MemberVO fans;// 使用者
	
	@ManyToOne
	@JoinColumn(name = "being_tracked_member_id",  referencedColumnName = "member_id")		
	private MemberVO trackedMember;// 被追蹤的人

	public Integer getTrackMemberId() {
		return trackMemberId;
	}

	public void setTrackMemberId(Integer trackMemberId) {
		this.trackMemberId = trackMemberId;
	}

	public MemberVO getFans() {
		return fans;
	}

	public void setFans(MemberVO fans) {
		this.fans = fans;
	}

	public MemberVO getTrackedMember() {
		return trackedMember;
	}

	public void setTrackedMember(MemberVO trackedMember) {
		this.trackedMember = trackedMember;
	}

	@Override
	public String toString() {
		return "TrackMemberVO [trackMemberId=" + trackMemberId + ", fans=" + fans + ", trackedMember=" + trackedMember
				+ "]";
	}

	
}
