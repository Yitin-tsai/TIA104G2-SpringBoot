package chilltrip.member.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

import chilltrip.locationcomment.model.LocationCommentVO;
import chilltrip.trackmember.model.TrackMemberVO;
import chilltrip.trip.model.TripVO;
import chilltrip.tripcollection.model.TripCollectionVO;
import chilltrip.triplike.model.TripLikeVO;

@Entity

@Table(name = "member")
public class MemberVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", updatable = false)
	private Integer memberId;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "account")
	private String account;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@Column(name = "nick_name")
	private String nickName;
	
	@Column(name = "gender")
	private Integer gender;
	
	@Column(name = "birthday")
	private Date birthday;
	
	@Column(name = "company_id")
	private String companyId;
	
	@Column(name = "E_receipt_carrier")
	private String ereceiptCarrier;
	
	@Column(name = "credit_card")
	private String creditCard;
	
	@Column(name = "tracking_number")
	private Integer trackingNumber;
	
	@Column(name = "fans_number")
	private Integer fansNumber;
	
	@Column(name = "photo")
	@Lob
	private byte[] photo;
	
//	private String photo_base64;  ET: 為了轉base64更動vo增加一個String photo  以及set方法不是太好，現在看來只是想要把byte[]的屬性轉成base64格式傳給前端才會多出這個屬性
//									這樣造成hibernate對應問題，還會有重複檔案問題，我覺得在member.daojdbc中的getone方法中應該專注處理資料庫操作，
//									將資料庫中的byte[]轉成base64 應該在controller中得到member物件後 在req.setattribute這邊在來處理轉型的問題 
//									會像是 membervo = membersvc.getOneMember(Integer memberId) 之後 我們在將byte[] photo = membervo.getphoto
//									然後才會是String photoBase64 = Base64.getEncoder().encodeToString(photo) , 在將photoBase64回傳給前端。
//                                  這樣操作才是mvc分層架構的意義。
	
	
	@OneToMany(mappedBy= "membervo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<LocationCommentVO> locationComment;
	
	
	@OneToMany(mappedBy= "membervo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TripCollectionVO> tripCollectionvo ;
	
	@OneToMany(mappedBy= "membervo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TripLikeVO> tripLikevo ;
	
	@OneToMany(mappedBy= "membervo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TripVO> tripvo ;
	
	
	@OneToMany(mappedBy= "fans",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TrackMemberVO> fans;
	
	@OneToMany(mappedBy= "trackedMember",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TrackMemberVO> trackedMember;
	
	
//	
//	public String getPhoto_base64() {
//		return photo_base64;
//	}
//	
//	public void setPhoto_base64(String photo_base64) {
//		this.photo_base64 = photo_base64;
//	}
	
	
	public Integer getMemberId() {
		return memberId;
	}
	public String getEmail() {
		return email;
	}
	public String getAccount() {
		return account;
	}
	public String getPassword() {
		return password;
	}
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phone;
	}
	public Integer getStatus() {
		return status;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public String getNickName() {
		return nickName;
	}
	public Integer getGender() {
		return gender;
	}
	public Date getBirthday() {
		return birthday;
	}
	public String getCompanyId() {
		return companyId;
	}
	public String getEreceiptCarrier() {
		return ereceiptCarrier;
	}
	public String getCreditCard() {
		return creditCard;
	}
	public Integer getTrackingNumber() {
		return trackingNumber;
	}
	public Integer getFansNumber() {
		return fansNumber;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public void setEreceiptCarrier(String ereceiptCarrier) {
		this.ereceiptCarrier = ereceiptCarrier;
	}
	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
	public void setTrackingNumber(Integer trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	public void setFansNumber(Integer fansNumber) {
		this.fansNumber = fansNumber;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public Set<LocationCommentVO> getLocationComment() {
		return locationComment;
	}
	public void setLocationComment(Set<LocationCommentVO> locationComment) {
		this.locationComment = locationComment;
	}
	
	
	
	
	
	public Set<TripCollectionVO> getTripCollectionvo() {
		return tripCollectionvo;
	}

	public void setTripCollectionvo(Set<TripCollectionVO> tripCollectionvo) {
		this.tripCollectionvo = tripCollectionvo;
	}

	public Set<TripVO> getTripvo() {
		return tripvo;
	}

	public void setTripvo(Set<TripVO> tripvo) {
		this.tripvo = tripvo;
	}

	@Override
	public String toString() {
		return "MemberVO [memberId=" + memberId + ", email=" + email + ", account=" + account + ", password=" + password
				+ ", name=" + name + ", phone=" + phone + ", status=" + status + ", createTime=" + createTime
				+ ", nickName=" + nickName + ", gender=" + gender + ", birthday=" + birthday + ", companyId="
				+ companyId + ", ereceiptCarrier=" + ereceiptCarrier + ", creditCard=" + creditCard
				+ ", trackingNumber=" + trackingNumber + ", fansNumber=" + fansNumber + ", photo="
				+ Arrays.toString(photo) + "]";
	}
	
}
