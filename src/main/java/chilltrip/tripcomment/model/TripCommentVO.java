package chilltrip.tripcomment.model;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "trip_comment")
public class TripCommentVO implements java.io.Serializable{
	
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_comment_id")
	private Integer tripCommentId;
	
	@Column(name = "member_id")
	private Integer memberId;
	
	@Column(name = "trip_id")
	private Integer tripId;
	
	@Column(name = "score")
	private Integer score;
	
	@Column(name = "photo")
	@Lob
	@JsonIgnore
	private byte[] photo;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@Column(name = "content")
	private String content;
	
	@Transient
	private String photo_base64;
	
	public String getPhoto_base64() {
		return photo_base64;
	}
	public void setPhoto_base64(String photo_base64) {
		this.photo_base64 = photo_base64;
	}
	public Integer getTripCommentId() {
		return tripCommentId;
	}
	public void setTripCommentId(Integer tripCommentId) {
		this.tripCommentId = tripCommentId;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Integer getTripId() {
		return tripId;
	}
	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "TripCommentVO [tripCommentId=" + tripCommentId + ", memberId=" + memberId + ", tripId=" + tripId
				+ ", score=" + score + ", photo=" + Arrays.toString(photo) + ", createTime=" + createTime + ", content="
				+ content + "]";
	}
	
}
