package chilltrip.announce.model;

import java.sql.Date;
import java.util.Arrays;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@NotNull(message = "必須指定管理員")
	private AdminVO adminvo;

	@Column(name = "title")
	@Lob
	@NotNull(message = "標題不能為空")
	@Size(max = 65535, message = "標題長度不能超過 65535 字元")
	private String title;

	@Column(name = "content")
	@Lob
	@NotNull(message = "內容不能為空")
	private String content;

	@Column(name = "start_time")
	@NotNull(message = "開始時間不能為空")
	private Date starttime;

	@Column(name = "end_time")
	@NotNull(message = "結束時間不能為空")
	private Date endtime;

	@Column(name = "cover_photo")
	@Lob
	private byte[] coverphoto;

	@Transient
	private String coverphotoBase64;

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

	@JsonIgnore
	public String getCoverphotoAsBase64() {
		if (coverphoto == null) {
			return null;
		}
		String base64 = Base64.getEncoder().encodeToString(coverphoto);

		return "data:image/jpeg;base64," + base64;
	}

	public void setCoverphotoFromBase64() {
		if (this.coverphotoBase64 != null && !this.coverphotoBase64.isEmpty()) {
			byte[] photo = Base64.getDecoder().decode(coverphotoBase64);
			this.coverphoto = photo;
		}
	}

	public String getCoverphotoBase64() {
		return coverphotoBase64;
	}

	public void setCoverphotoBase64(String coverphotoAsBase64) {
		this.coverphotoBase64 = coverphotoAsBase64;
		this.coverphoto = null;
	}

	@Override
	public String toString() {
		return "AnnounceVO [announceid=" + announceid + ", adminvo=" + adminvo + ", title=" + title + ", content="
				+ content + ", starttime=" + starttime + ", endtime=" + endtime + ", coverphoto="
				+ Arrays.toString(coverphoto) + "]";
	}

}
