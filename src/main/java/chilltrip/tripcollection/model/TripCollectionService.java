package chilltrip.tripcollection.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.util.List;

import org.hibernate.Session;

import chilltrip.util.HibernateUtil;

public class TripCollectionService {

	private TripCollectionDAO dao;

	public TripCollectionService() {
		dao = new TripCollectionDAOImpl();
	}

	public TripCollectionVO addTripCollection(TripCollectionVO tripCollectionVO) {

		dao.insert(tripCollectionVO);
		return tripCollectionVO;
	}

	public void deleTripCollection(Integer id) {

		dao.delete(id);
	}

	public List<TripCollectionVO> getByTrip(Integer tripId) {
		return dao.getByTrip(tripId);
	}

	public List<TripCollectionVO> getByMember(Integer memberId, Integer currentpage) {
		return dao.getByMember(memberId, currentpage);
	}

	public int getTotalPage(Integer memberId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int pageQty;
		try {
			session.beginTransaction();
			long total = dao.getTotalByMember(memberId);
			pageQty = (int) (total % PAGE_MAX_RESULT == 0 ? (total / PAGE_MAX_RESULT) : (total / PAGE_MAX_RESULT + 1));
			session.getTransaction().commit();
		} catch (Exception e) {
			pageQty = 1;
			session.getTransaction().rollback();
			e.printStackTrace();
		}
		return pageQty;
	}
}
