package chilltrip.trip.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import chilltrip.member.model.MemberVO;
import chilltrip.tripactyperela.model.TripactyperelaVO;
import chilltrip.tripcollection.model.TripCollectionVO;
import chilltrip.triplike.model.TripLikeVO;

@Entity
@Table(name = "trip")
public class TripVO implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_id", updatable = false)
	private Integer trip_id;
	
	@Transient
	private Integer memberId; //這個格式與yuki對應
	
	@ManyToOne
	@JoinColumn(name = "member_id",  referencedColumnName = "member_id")  //hibernate 關聯 et留
	private MemberVO membervo; 
	
	@OneToMany(mappedBy= "tripvo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TripCollectionVO> tripCollectionvo ;
	
	@OneToMany(mappedBy= "tripvo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<TripLikeVO> tripLikevo ;
	
	@OneToMany(mappedBy = "tripid", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
    private List<TripactyperelaVO> tripactyperelas;
	
	@Column(name = "abstract")
	@Lob
	private String trip_abstract; //abstract是保留字，所以做了修改
	
	@Column(name = "create_time")
	private Timestamp create_time;
	
	@Column(name = "collections")
	private Integer collections;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "overall_score")
	private Integer overall_score=0;
	
	@Column(name = "overall_scored_people")
	private Integer overall_scored_people=0;
	
	@Column(name = "location_number")
	private Integer location_number;
	
	@Column(name = "article_title")
	private String article_title;
	
	@Column(name = "visitors_number")
	private Integer visitors_number;
	
	@Column(name = "likes")
	private Integer likes;
	
	public Set<TripLikeVO> getTripLikevo() {
		return tripLikevo;
	}
	public List<TripactyperelaVO> getTripactyperelas() {
		return tripactyperelas;
	}
	public void setTripLikevo(Set<TripLikeVO> tripLikevo) {
		this.tripLikevo = tripLikevo;
	}
	public void setTripactyperelas(List<TripactyperelaVO> tripactyperelas) {
		this.tripactyperelas = tripactyperelas;
	}
	public Integer getTrip_id() {
		return trip_id;
	}
	public void setTrip_id(Integer trip_id) {
		this.trip_id = trip_id;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public String getTrip_abstract() {
		return trip_abstract;
	}
	public void setTrip_abstract(String trip_abstract) {
		this.trip_abstract = trip_abstract;
	}
	public Timestamp getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}
	public Integer getCollections() {
		return collections;
	}
	public void setCollections(Integer collections) {
		this.collections = collections;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getOverall_score() {
		return overall_score;
	}
	public void setOverall_score(Integer overall_score) {
		this.overall_score = overall_score;
	}
	public Integer getOverall_scored_people() {
		return overall_scored_people;
	}
	public void setOverall_scored_people(Integer overall_scored_people) {
		this.overall_scored_people = overall_scored_people;
	}
	public Integer getLocation_number() {
		return location_number;
	}
	public void setLocation_number(Integer location_number) {
		this.location_number = location_number;
	}
	public String getArticle_title() {
		return article_title;
	}
	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}
	public Integer getVisitors_number() {
		return visitors_number;
	}
	public void setVisitors_number(Integer visitors_number) {
		this.visitors_number = visitors_number;
	}
	public Integer getLikes() {
		return likes;
	}
	public void setLikes(Integer likes) {
		this.likes = likes;
	}
	public MemberVO getMembervo() {
		return membervo;
	}
	public void setMembervo(MemberVO membervo) {
		this.membervo = membervo;
	}
	public Set<TripCollectionVO> getTripCollectionvo() {
		return tripCollectionvo;
	}
	public void setTripCollectionvo(Set<TripCollectionVO> tripCollectionvo) {
		this.tripCollectionvo = tripCollectionvo;
	}
	
	@Override
	public String toString() {
	    return new StringBuilder("tripVO [")
	        .append("tripid=").append(trip_id)
	        .append(", memberid=").append(memberId)
	        .append(", abstract=").append(trip_abstract)
	        .append(", create_time=").append(create_time)
	        .append(", collections=").append(collections)
	        .append(", status=").append(status)
	        .append(", overall_score=").append(overall_score)
	        .append(", overall_scored_people=").append(overall_scored_people)
	        .append(", location_number=").append(location_number)
	        .append(", article_title=").append(article_title)
	        .append(", visitors_number=").append(visitors_number)
	        .append(", likes=").append(likes)
	        .append("]")
	        .toString();
	}
	

}
