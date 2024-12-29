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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
	
	@Email(message = "{member.email.pattern}")
    @NotNull(message = "{member.email.notnull}")
	@Column(name = "email")
	private String email;
	
	@Column(name = "account")
	private String account;
	
	@NotNull(message = "{member.password.notnull}")
    @Size(min = 5, max = 15, message = "{member.password.size}")
	@Column(name = "password")
	private String password;
	
	@NotNull(message = "{member.name.notnull}")
    @Pattern(regexp = "^[\\u4E00-\\u9FFFa-zA-Z\\s]{2,20}$", message = "{member.name.pattern}")
	@Column(name = "name")
	private String name;
	
	@NotNull(message = "{member.phone.notnull}")
    @Pattern(regexp = "^(09[0-9]{8}|0[2-8][0-9]{7,8}|0[2-8]-[0-9]{6,8})$", message = "{member.phone.pattern}")
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "create_time")
	private Timestamp createTime;
	
	@NotNull(message = "{member.nickName.notnull}")
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
