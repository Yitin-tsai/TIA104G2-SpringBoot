package chilltrip.sub_trip.model;

import java.sql.Clob;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import chilltrip.trip.model.TripVO;

@Entity

@Table(name = "sub_trip")
public class SubtripVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sub_trip_id", updatable = false)
	private Integer sub_trip_id;

	@ManyToOne
	@JoinColumn(name = "trip_id", referencedColumnName = "trip_id")
	private TripVO tripVO;

	private Integer tripid;

	@Column(name = "index")
	private Integer index;

	@Column(name = "content")
	@Lob
	private Clob content;

	public Integer getSubtripid() {
		return sub_trip_id;
	}

	public void setSubtripid(Integer sub_trip_id) {
		this.sub_trip_id = sub_trip_id;
	}

	public void setTripid(Integer tripid) {
		this.tripid = tripid;
	}

	public Integer getTripid() {
		return tripid;
	}

	public TripVO getTripVO() {
		return tripVO;
	}

	public void setTripVO(TripVO tripVO) {
		this.tripVO = tripVO;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Clob getContent() {
		 try {
		        return (content != null) ? content : new SerialClob(new char[0]);
		    } catch (SQLException e) {
		        throw new RuntimeException("獲取 content 失敗", e);
		    }
	}

	public void setContent(String content) throws SerialException, SQLException {
		try {
	        this.content = (content != null) ? 
	            new SerialClob(content.toCharArray()) : 
	            new SerialClob(new char[0]);
	    } catch (SQLException e) {
	        throw new RuntimeException("設置 content 失敗", e);
	    }
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder("SubtripVO [").append("subtripid=").append(sub_trip_id).append(",tripVO=").append(tripVO)
				.append(",index=").append(index).append(",content=").append(content).append("]").toString();
	}
}
