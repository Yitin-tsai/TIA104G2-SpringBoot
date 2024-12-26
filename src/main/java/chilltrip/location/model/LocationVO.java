package chilltrip.location.model;

import java.sql.Timestamp;
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

@Entity

@Table(name = "location")
public class LocationVO  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "location_id", updatable = false)
	private Integer locationid;
	
	@Column(name = "address")
	@Lob
	private String address;
	
	@Column(name = "create_time", updatable = false)
	private Timestamp create_time;
	
	@Column(name = "comments_number")
	private Integer comments_number;
	
	@Column(name = "score")
	private Float score;
	
	@Column(name = "location_name")
	private String location_name;
	
	@OneToMany(mappedBy= "locationvo",cascade = CascadeType.ALL)
	@OrderBy("createTime desc")
	@JsonIgnore
	private Set<LocationCommentVO> locationComment;
	
	public Set<LocationCommentVO> getLocationComment() {
		return locationComment;
	}

	public void setLocationComment(Set<LocationCommentVO> locationComment) {
		this.locationComment = locationComment;
	}

	public Integer getLocationid() {
		return locationid;
	}

	public void setLocationid(Integer locationid) {
		this.locationid = locationid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Timestamp getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Timestamp create_time) {
		this.create_time = create_time;
	}

	public Integer getComments_number() {
		return comments_number;
	}

	public void setComments_number(Integer comments_number) {
		this.comments_number = comments_number;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	@Override
	public String toString() {
		return "locationVO [locationid=" + locationid + ", address=" + address + ", create_time=" + create_time
				+ ", comments_number=" + comments_number + ", score=" + score + ", location_name=" + location_name
				+ "]";
	}

}
