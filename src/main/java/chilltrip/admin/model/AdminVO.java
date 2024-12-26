package chilltrip.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import chilltrip.announce.model.AnnounceVO;

@Entity
@Table(name = "administrator")
public class AdminVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "administrator_id", updatable = false)
	private Integer adminid;

	@Column(name = "email")
	private String email;

	@Column(name = "admin_account")
	private String adminaccount;

	@Column(name = "admin_password")
	private String adminpassword;

	@Column(name = "admin_name")
	private String adminname;

	@Column(name = "phone")
	private String phone;

	@Column(name = "account_status")
	private Integer status;

	@Column(name = "create_time", updatable = false)
	private Timestamp createtime;

	@Column(name = "nick_name")
	private String adminnickname;
	
	@OneToMany(mappedBy = "adminvo" , cascade = CascadeType.ALL)
	@OrderBy("announceid asc")
	@JsonIgnore
	private Set<AnnounceVO> announces;

	public Set<AnnounceVO> getAnnounces() {
		return announces;
	}

	public void setAnnounces(Set<AnnounceVO> announces) {
		this.announces = announces;
	}

	public Integer getAdminid() {
		return adminid;
	}

	public void setAdminid(Integer adminid) {
		this.adminid = adminid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdminaccount() {
		return adminaccount;
	}

	public void setAdminaccount(String adminaccount) {
		this.adminaccount = adminaccount;
	}

	public String getAdminpassword() {
		return adminpassword;
	}

	public void setAdminpassword(String adminpassword) {
		this.adminpassword = adminpassword;
	}

	public String getAdminname() {
		return adminname;
	}

	public void setAdminname(String adminname) {
		this.adminname = adminname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Timestamp getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Timestamp createtime) {
		java.util.Date now = new java.util.Date();
		long longnow = now.getTime();
		Timestamp today = new java.sql.Timestamp(longnow);
	     if (createtime == null) {
	            this.createtime = today;
	        } else {
	            this.createtime = createtime;
	        }
	}

	public String getAdminnickname() {
		return adminnickname;
	}

	public void setAdminnickname(String adminnickname) {
		this.adminnickname = adminnickname;
	}

	@Override
	public String toString() {
		return "AdminVO [adminid=" + adminid + ", email=" + email + ", adminaccount=" + adminaccount
				+ ", adminpassword=" + adminpassword + ", adminname=" + adminname + ", phone=" + phone + ", status="
				+ status + ", createtime=" + createtime + ", adminnickname=" + adminnickname + "]";
	}

}
