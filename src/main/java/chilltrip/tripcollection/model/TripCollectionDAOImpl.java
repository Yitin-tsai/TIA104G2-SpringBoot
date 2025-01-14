package chilltrip.tripcollection.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import chilltrip.triplike.model.TripLikeVO;
import chilltrip.util.HibernateUtil;

@Repository
public class TripCollectionDAOImpl implements TripCollectionDAO {

	private SessionFactory factory;

	@Autowired
	public TripCollectionDAOImpl(@Qualifier("getSessionFactory") SessionFactory factory) {
		this.factory = factory;
	}

	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public void insert(TripCollectionVO tripCollectionVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			session.save(tripCollectionVO);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}

	}

	@Override
	public void delete(Integer tripCollectionId) {
		// TODO Auto-generated method stub
		TripCollectionVO tripCollectionVO = getSession().get(TripCollectionVO.class, tripCollectionId);
		try {
			Session session = getSession();
			session.beginTransaction();
			session.delete(tripCollectionVO);
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public List<TripCollectionVO> getByTrip(Integer tripId) {
		Session session = getSession();
		session.beginTransaction();
		List<TripCollectionVO> list = session
				.createQuery("FROM TripCollectionVO tc WHERE tc.tripvo.trip_id = :tripId", TripCollectionVO.class)
				.setParameter("tripId", tripId).list();
		session.getTransaction().commit();
		return list;
	}

	@Override
	public List<TripCollectionVO> getByMember(Integer memberId, Integer currentPage) {
		int first = (currentPage - 1) * PAGE_MAX_RESULT;
		Session session = getSession();
		session.beginTransaction();
		List<TripCollectionVO> list = session
				.createQuery("FROM TripCollectionVO tc WHERE tc.membervo.memberId = :memberId", TripCollectionVO.class)
				.setParameter("memberId", memberId).setFirstResult(first).setMaxResults(PAGE_MAX_RESULT).list();
		session.getTransaction().commit();
		return list;
		// TODO Auto-generated method stub

	}

	@Override
	public long getTotalByMember(Integer memberId) {
		return getSession()
				.createQuery("select count(*) FROM TripCollectionVO tc WHERE tc.membervo.memberId = :memberId",
						Long.class)
				.setParameter("memberId", memberId)
				.uniqueResult();

	}
	
	public boolean getOne(Integer memberId, Integer tripId ) {
		Session session = getSession();
		session.beginTransaction();
		
		try{TripCollectionVO collection = session.createQuery("FROM TripCollectionVO tc where tc.membervo.memberId = :memberId and tc.tripvo.trip_id = :tripId", TripCollectionVO.class)
							.setParameter("memberId", memberId)
							.setParameter("tripId", tripId)
							.getSingleResult();
			if(collection != null)
				return true;
		}catch(NoResultException e) {
			return false;
		}
		return false;							
	}
	

}
