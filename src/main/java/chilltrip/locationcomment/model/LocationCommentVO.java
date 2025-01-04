package chilltrip.locationcomment.model;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import chilltrip.location.model.LocationVO;
import chilltrip.member.model.MemberVO;

@Entity

@Table(name = "location_comment")
public class LocationCommentVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "location_comment_id", updatable = false)
	private Integer locationCommitId;

	@ManyToOne
	@JoinColumn(name = "location_id", referencedColumnName = "location_id")
	private LocationVO locationvo;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private MemberVO membervo;

	@Column(name = "content")
	@Lob
	@NotNull(message = "評論內容不能為空")
	@Size(min = 1, message = "評論內容長度必須大於0")
	private String content;

	@Column(name = "photo")
	@Lob
	private byte[] photo;

	@Column(name = "score")
	@NotNull(message = "評分不能為空")
	@Min(value = 1, message = "評分必須在1到5之間")
	@Max(value = 5, message = "評分必須在1到5之間")
	private Integer score;

	@Column(name = "create_time")
	private Timestamp createTime;

	@Override
	public String toString() {
		return "LocationCommentVO [locationCommitId=" + locationCommitId + ", locationvo=" + locationvo + ", membervo="
				+ membervo + ", content=" + content + ", photo=" + Arrays.toString(photo) + ", score=" + score
				+ ", createTime=" + createTime + "]";
	}

	public Integer getLocationCommitId() {
		return locationCommitId;
	}

	public void setLocationCommitId(Integer locationCommitId) {
		this.locationCommitId = locationCommitId;
	}

	public LocationVO getLocationvo() {
		return locationvo;
	}

	public void setLocationvo(LocationVO locationvo) {
		this.locationvo = locationvo;
	}

	public MemberVO getMembervo() {
		return membervo;
	}

	public void setMembervo(MemberVO membervo) {
		this.membervo = membervo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
