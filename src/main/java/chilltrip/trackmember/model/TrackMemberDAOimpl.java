package chilltrip.trackmember.model;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import chilltrip.member.model.MemberVO;
import chilltrip.tripcollection.model.TripCollectionVO;
import chilltrip.util.HibernateUtil;

@Repository
public class TrackMemberDAOimpl  implements TrackMemberDAO{
	
	private SessionFactory factory;
	
	@Autowired
	public TrackMemberDAOimpl(SessionFactory factory) {
		this.factory = factory;
	}
	
	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public void insert(TrackMemberVO trackMemberVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			session.save(trackMemberVO);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public void delete(Integer trackMemberId) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			session.delete(trackMemberId);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public List<MemberVO> getAllfans(Integer memberId) {
		Session session = getSession();
		session.beginTransaction();
		 List<MemberVO> list =  session.createQuery("SELECT tm.fans FROM TrackMemberVO tm WHERE tm.trackedMember.memberId = :memberId", MemberVO.class)
				.setParameter("memberId", memberId)
				.list();
		 session.getTransaction().commit();
			return list;
		
	}

	@Override
	public List<MemberVO> getAllTracks(Integer fansId) {
		Session session = getSession();
		session.beginTransaction();
		List<MemberVO> list = session.createQuery("SELECT tm.trackedMember FROM TrackMemberVO tm WHERE tm.fans.memberId = :fansId", MemberVO.class)
				.setParameter("fansId", fansId)
				.list();
		session.getTransaction().commit();
		return list;
	}

	@Override
	public Integer getFansQty(Integer memberId) {
		getSession().beginTransaction();
		Long fans =getSession().createQuery("select count(*) FROM TrackMemberVO tm WHERE tm.trackedMember.memberId = :memberId", Long.class)
				.setParameter("memberId", memberId)
				.uniqueResult();
		getSession().getTransaction().commit();
		return fans != null ?fans.intValue():0;
	}

	@Override
	public Integer getTracksQty(Integer fansId) {
		getSession().beginTransaction();
		Long tracks =  getSession().createQuery("select count(*) FROM TrackMemberVO tm WHERE tm.fans.memberId = :memberId", Long.class)
				.setParameter("memberId", fansId)
				.uniqueResult();
		getSession().getTransaction().commit();
		 return tracks != null ? tracks.intValue() : 0; 
	}
	
}
