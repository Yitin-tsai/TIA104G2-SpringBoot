package chilltrip.tripcomment.model;
import java.sql.Timestamp;
import java.util.Arrays;

public class TripCommentVO implements java.io.Serializable{
	private Integer tripCommentId;
	private Integer memberId;
	private Integer tripId;
	private Integer score;
	private byte[] photo;
	private Timestamp createTime;
	private String content;
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
