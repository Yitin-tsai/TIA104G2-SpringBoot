package chilltrip.tripphoto.model;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import chilltrip.trip.model.TripVO;

@Entity

@Table (name = "trip_photo")
public class TripphotoVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer trip_photo_id;
	
	@Column(name = "trip_id", insertable = false, updatable = false)
	private Integer trip_id;
	
	@ManyToOne
    @JoinColumn(name = "trip_id")
	private TripVO tripVO;
	
	@Lob
	@Column(name = "photo" ,columnDefinition = "LONGBLOB")
	private byte[] photo;
	
	@Column(name = "photo_type")
	private Integer photo_type;

	public Integer getTrip_photo_id() {
		return trip_photo_id;
	}

	public void setTrip_photo_id(Integer trip_photo_id) {
		this.trip_photo_id = trip_photo_id;
	}

	public Integer getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(Integer trip_id) {
		this.trip_id = trip_id;
	}

	public TripVO getTripVO() {
		return tripVO;
	}

	public void setTripVO(TripVO tripVO) {
		this.tripVO = tripVO;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	 @Transient // 這個欄位不會存入資料庫
	    public String getPhotoBase64() {
	        if (photo != null) {
	            return Base64.getEncoder().encodeToString(photo);
	        }
	        return null;
	    }
	

	public Integer getPhoto_type() {
		return photo_type;
	}
	

	public void setPhoto_type(Integer photo_type) {
		this.photo_type = photo_type;
	}
	
	@Override
	public String toString() {
	    return new StringBuilder("tripphotoVO [")
	        .append("trip_photo_id=").append(trip_photo_id)
	        .append(", trip_id=").append(trip_id)
	        .append(", photo=").append(photo)
	        .append(", photo_type=").append(photo_type)
	        .append("]")
	        .toString();
	}
	

}
