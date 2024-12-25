package chilltrip.announce.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.sql.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.admin.model.AdminService;
import chilltrip.admin.model.AdminVO;
import chilltrip.util.HibernateUtil;

public class AnnounceDAOimpl implements AnnounceDAO {
	
	private SessionFactory factory;

	public AnnounceDAOimpl() {
		factory = HibernateUtil.getSessionFactory();
	}

	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public void insert(AnnounceVO annouceVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.save(annouceVO);
			session.getTransaction().commit();

		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}

	}

	@Override
	public void update(AnnounceVO annouceVO) {
		// TODO Auto-generated method stub
		try {
			getSession().beginTransaction();
			getSession().update(annouceVO);
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}

	}

	@Override
	public void delete(Integer annouceid) {
		// TODO Auto-generated method stub
		AnnounceVO announceVO = getSession().get(AnnounceVO.class, annouceid);
		try {
//			getSession().beginTransaction();
			getSession().delete(announceVO);
		} catch (Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public List<AnnounceVO> getAll() {
		// TODO Auto-generated method stub
//		getSession().beginTransaction();
		return getSession().createQuery("from AnnounceVO", AnnounceVO.class).list();
		
	}

	@Override
	public List<AnnounceVO> getByCompositeQuery(Map<String, String> map) {
		// TODO Auto-generated method stub
		if (map.size() == 0)
			return getAll();
		getSession().beginTransaction();
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<AnnounceVO> criteria = builder.createQuery(AnnounceVO.class);
		Root<AnnounceVO> root = criteria.from(AnnounceVO.class);

		List<Predicate> predicates = new ArrayList<>();
		for (Map.Entry<String, String> row : map.entrySet()) {
			if ("title".equals(row.getKey())) {
				predicates.add(builder.like(root.get("title"), "%" + row.getValue() + "%"));
			}

			if ("starttime".equals(row.getKey())) {
				if (!map.containsKey("starttime"))
					predicates.add(builder.greaterThanOrEqualTo(root.get("starttime"), Date.valueOf(row.getValue())));
			}

			if ("endtime".equals(row.getKey())) {
				if (!map.containsKey("endtime"))
					predicates.add(builder.lessThanOrEqualTo(root.get("emdtime"), Date.valueOf(row.getValue())));

			}

		}

		criteria.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
		criteria.orderBy(builder.asc(root.get("announceid")));
		TypedQuery<AnnounceVO> query = getSession().createQuery(criteria);

		return query.getResultList();
	}

	@Override
	public List<AnnounceVO> getAll(int currentPage) {
		int first = (currentPage - 1) * PAGE_MAX_RESULT;
		return getSession().createQuery("from AnnounceVO", AnnounceVO.class).setFirstResult(first)
				.setMaxResults(PAGE_MAX_RESULT).list();
	}
	
	@Override
	public List<AnnounceVO> getByadminid(Integer adminid) {
		// TODO Auto-generated method stub
		Session session = getSession();
		session.beginTransaction();
		List<AnnounceVO> list = new ArrayList<AnnounceVO>();
		AdminVO admin = session.get(AdminVO.class, adminid);
		for(AnnounceVO a :admin.getAnnounces()) {
			list.add(a);
		}
		
		return (List)list ;
	}
	
	@Override
	public long getTotal() {
		return getSession().createQuery("select count(*) from AnnounceVO", Long.class).uniqueResult();
	}
	public static void main(String[] args) {
//
		AnnounceDAOimpl dao = new AnnounceDAOimpl();
//		java.util.Date now = new java.util.Date();
//		long long1 = now.getTime();
//		java.sql.Date date1 = new java.sql.Date(long1);
//		AnnounceVO announce = new AnnounceVO();
//		AdminVO et = new AdminVO();
//		AdminService ser = new AdminService();
//		et = ser.getOneAdmin(1);
//		System.out.println(et);
//
//		// 新增
//		announce.setAdminvo(et);
//		announce.setContent("我今天真的好累");
//		announce.setTitle("明天看的到太陽嗎");
//		announce.setStarttime(date1);
//		announce.setEndtime(date1);
//		announce.setCoverphoto(null);
//		System.out.println(announce);
//		dao.insert(announce);
//		
//		System.out.println(dao.getByadminid(1));
//		
		Map<String,String> map  =new HashMap<String, String>();
		map.put("title", "冬季");
		System.out.println(dao.getByCompositeQuery(map));;
		System.out.println(dao.getTotal());
		System.out.println(dao.getAll(1));
		
	}

}
