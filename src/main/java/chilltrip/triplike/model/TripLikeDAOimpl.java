package chilltrip.triplike.model;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import chilltrip.util.HibernateUtil;

@Repository
public class TripLikeDAOimpl implements TripLikeDAO {
	
	private SessionFactory factory;
	@Autowired
	public TripLikeDAOimpl(SessionFactory factory) {
		this.factory = factory;
	}

	private Session getSession() {
		return factory.getCurrentSession();
	}

	
	
	@Override
	public void insert(TripLikeVO tripLikeVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			session.save(tripLikeVO);
			session.getTransaction().commit();
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public void delete(Integer tripLikeId) {
		// TODO Auto-generated method stub
		TripLikeVO tripLikeVO = getSession().get(TripLikeVO.class, tripLikeId);
		try {
			Session session = getSession();
			session.beginTransaction();
			session.delete(tripLikeVO);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public List<TripLikeVO> getByTrip(Integer tripId) {
		Session session = getSession();
		session.beginTransaction();
		List<TripLikeVO> list =  session.createQuery("FROM TripLikeVO tl WHERE tl.tripvo.trip_id = :tripId", TripLikeVO.class)
				.setParameter("tripId", tripId)
				.list();
		session.getTransaction().commit();
		return list;
		
	}

	@Override
	public List<TripLikeVO> getByMember(Integer memberId) {
		Session session = getSession();
		session.beginTransaction();
		List<TripLikeVO> list =  session.createQuery("FROM TripLikeVO tl WHERE tl.membervo.memberId = :memberId", TripLikeVO.class)
				.setParameter("memberId", memberId)
				.list();
		session.getTransaction().commit();
		return list;
	}
	
	
	
}
