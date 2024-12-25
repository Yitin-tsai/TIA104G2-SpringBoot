package chilltrip.announce.model;

import java.sql.Date;
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

import com.fasterxml.jackson.annotation.JsonManagedReference;

import chilltrip.admin.model.AdminVO;

@Entity
@Table(name = "announcement")
public class AnnounceVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "announcement_id", updatable = false)
	private Integer announceid;
	
	@ManyToOne
	@JoinColumn(name = "admin_id", referencedColumnName = "administrator_id")
	 @JsonManagedReference
	private AdminVO adminvo;
	
	@Column(name = "title")
	@Lob
	private String  title;
	
	@Column(name = "content")
	@Lob
	private String  content;
	
	@Column(name = "start_time")
	private Date starttime;
	
	@Column(name = "end_time")
	private Date endtime;
	
	@Column(name = "cover_photo")
	@Lob
	private byte[] coverphoto;
	
	
	
	public Integer getAnnounceid() {
		return announceid;
	}
	public void setAnnounceid(Integer announceid) {
		this.announceid = announceid;
	}
	
	public AdminVO getAdminvo() {
		return adminvo;
	}
	public void setAdminvo(AdminVO adminvo) {
		this.adminvo = adminvo;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public byte[] getCoverphoto() {
		return coverphoto;
	}
	public void setCoverphoto(byte[] coverphoto) {
		this.coverphoto = coverphoto;
	}
	@Override
	public String toString() {
		return "AnnounceVO [announceid=" + announceid + ", adminvo=" + adminvo + ", title=" + title + ", content="
				+ content + ", starttime=" + starttime + ", endtime=" + endtime + ", coverphoto="
				+ Arrays.toString(coverphoto) + "]";
	}


	

}
