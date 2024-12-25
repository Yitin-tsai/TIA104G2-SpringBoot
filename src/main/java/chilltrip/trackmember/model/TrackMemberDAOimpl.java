package chilltrip.trackmember.model;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.member.model.MemberVO;
import chilltrip.tripcollection.model.TripCollectionVO;
import chilltrip.util.HibernateUtil;

public class TrackMemberDAOimpl  implements TrackMemberDAO{
	
	private SessionFactory factory;

	public TrackMemberDAOimpl() {
		factory =HibernateUtil.getSessionFactory();
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
		return session.createQuery("SELECT tm.fans FROM TrackMemberVO tm WHERE tm.trackedMember.memberId = :memberId", MemberVO.class)
				.setParameter("memberId", memberId)
				.list();
		
	}

	@Override
	public List<TrackMemberVO> getAllTracks(Integer fansId) {
		Session session = getSession();
		session.beginTransaction();
		return session.createQuery("FROM TrackMemberVO tm WHERE tm.fans.memberId = :memberId", TrackMemberVO.class)
				.setParameter("fansId", fansId)
				.list();
	}

	@Override
	public long getFansQty(Integer memberId) {
		getSession().beginTransaction();
		return getSession().createQuery("select count(*) FROM TrackMemberVO tm WHERE tm.trackedMember.memberId = :memberId", Long.class)
				.setParameter("memberId", memberId)
				.uniqueResult();
	}

	@Override
	public long getTracksQty(Integer fansId) {
		getSession().beginTransaction();
		return getSession().createQuery("select count(*) FROM TrackMemberVO tm WHERE tm.fans.memberId = :memberId", Long.class)
				.setParameter("memberId", fansId)
				.uniqueResult();
		
	}
	public static void main(String[] args) {
		TrackMemberDAO dao = new TrackMemberDAOimpl();
		System.out.println(dao.getAllfans(2));
	}
}
