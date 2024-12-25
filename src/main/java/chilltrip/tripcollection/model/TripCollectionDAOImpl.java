package chilltrip.tripcollection.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.util.HibernateUtil;

public class TripCollectionDAOImpl implements TripCollectionDAO {

	private SessionFactory factory;

	public TripCollectionDAOImpl() {
		factory = HibernateUtil.getSessionFactory();
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
		return session.createQuery("FROM TripCollectionVO tc WHERE tc.tripvo.trip_id = :tripId", TripCollectionVO.class)
				.setParameter("tripId", tripId)
				.list();
	}

	@Override
	public List<TripCollectionVO> getByMember(Integer memberId, Integer currentPage) {
		int first = (currentPage - 1) * PAGE_MAX_RESULT;
		Session session = getSession();
		session.beginTransaction();
		return session.createQuery("FROM TripCollectionVO tc WHERE tc.membervo.memberId = :memberId" ,TripCollectionVO.class)
				.setParameter("memberId", memberId)
				.setFirstResult(first)
				.setMaxResults(PAGE_MAX_RESULT)
				.list();
		
		// TODO Auto-generated method stub
	
	}

	@Override
	public long getTotalByMember(Integer memberId) {
		return getSession().createQuery("select count(*) FROM TripCollectionVO tc WHERE tc.membervo.memberId = :memberId", Long.class).uniqueResult();
		
	}
	
	public static void main(String[] args) {
		TripCollectionDAO dao = new TripCollectionDAOImpl();
		System.out.println(dao.getByMember(1, 1));
	}
}
