package chilltrip.triplocationrelation.model;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import chilltrip.location.model.LocationVO;
import chilltrip.sub_trip.model.SubtripVO;

@Entity
@Table(name = "trip_location_relation")
public class TriplocationrelationVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_location_relation_id", updatable = false)
	private Integer trip_location_relation_id;

	@Column(name = "sub_trip_id")
	private Integer sub_trip_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_trip_id", insertable = false, updatable = false)
	private SubtripVO subtripVO;

	@Column(name = "location_id")
	private Integer location_id;

	// 子行程1（東京第一天）：澀谷、巨蛋、澀谷 一個景點對應一個關係
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_id", insertable = false, updatable = false)
	private LocationVO locationVO;

	@Column(name = "index")
	private Integer index;

	@Column(name = "time_start")
	private Timestamp time_start;

	@Column(name = "time_end")
	private Timestamp time_end;

	public Integer getTrip_location_relation_id() {
		return trip_location_relation_id;
	}

	public void setTrip_location_relation_id(Integer trip_location_relation_id) {
		this.trip_location_relation_id = trip_location_relation_id;
	}

	public Integer getSub_trip_id() {
		return sub_trip_id;
	}

	public void setSub_trip_id(Integer sub_trip_id) {
		this.sub_trip_id = sub_trip_id;
	}

	public SubtripVO getSubtripVO() {
		return subtripVO;
	}

	public void setSubtripVO(SubtripVO subtripVO) {
		this.subtripVO = subtripVO;
	}

	public Integer getLocation_id() {
		return location_id;
	}

	public void setLocation_id(Integer location_id) {
		this.location_id = location_id;
	}

	public LocationVO getLocationVO() {
		return locationVO;
	}

	public void setLocationVO(LocationVO locationVO) {
		this.locationVO = locationVO;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Timestamp getTime_start() {
		return time_start;
	}

	public void setTime_start(Timestamp time_start) {
		this.time_start = time_start;
	}

	public Timestamp getTime_end() {
		return time_end;
	}

	public void setTime_end(Timestamp time_end) {
		this.time_end = time_end;
	}

}
