package chilltrip.announce.model;

import static chilltrip.util.Constants.PAGE_MAX_RESULT;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import chilltrip.admin.model.AdminVO;


@Repository
public class AnnounceDAOimpl implements AnnounceDAO {
	
	private SessionFactory factory;

	@Autowired
	 public AnnounceDAOimpl( @Qualifier("getSessionFactory") SessionFactory factory) {
        this.factory = factory;
    }

	private Session getSession() {
		return factory.getCurrentSession();
	}

	@Override
	public void insert(AnnounceVO annouceVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			Transaction tx =session.beginTransaction();
			session.save(annouceVO);
			tx.commit();

		} catch (Exception e) {
			e.printStackTrace();
			Transaction tx = getSession().beginTransaction();
			tx.rollback();
		}

	}

	@Override
	public void update(AnnounceVO annouceVO) {
		// TODO Auto-generated method stub
		try {
			Transaction tx = getSession().beginTransaction();
			getSession().update(annouceVO);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			Transaction tx = getSession().beginTransaction();
			tx.rollback();
			
		}

	}

	@Override
	public boolean delete(Integer annouceid) {
		// TODO Auto-generated method stub
		AnnounceVO announceVO = null;
	    try {
	        getSession().beginTransaction();  
	        announceVO = getSession().get(AnnounceVO.class, annouceid);
	        
	        if (announceVO != null) {
	            getSession().delete(announceVO);  
	            getSession().getTransaction().commit();  
	            return true;
	        } else {
	            getSession().getTransaction().rollback();  
	            return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        if (getSession().getTransaction() != null) {
	            getSession().getTransaction().rollback();  
	        }
	        return false;
	    }
	}

	@Override
	public List<AnnounceVO> getAll() {
		// TODO Auto-generated method stub

		Transaction tx = getSession().beginTransaction();
		List<AnnounceVO> list = getSession().createQuery("from AnnounceVO order by announceid desc", AnnounceVO.class).list();
		tx.commit();
		return list;
	}

	@Override
	public List<AnnounceVO> getByCompositeQuery(Map<String, String> map) {
		// TODO Auto-generated method stub
		if (map.size() == 0)
			return getAll();
		 
	    Session session = getSession();
	    Transaction tx = session.beginTransaction();
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<AnnounceVO> criteria = builder.createQuery(AnnounceVO.class);
		Root<AnnounceVO> root = criteria.from(AnnounceVO.class);

		List<Predicate> predicates = new ArrayList<>();
		for (Map.Entry<String, String> row : map.entrySet()) {
			if ("title".equals(row.getKey())) {
				predicates.add(builder.like(root.get("title"), "%" + row.getValue() + "%"));
			}

			if ("starttime".equals(row.getKey())) {
				if (map.containsKey("starttime"))
					predicates.add(builder.greaterThanOrEqualTo(root.get("starttime"), Date.valueOf(row.getValue())));
			}

			if ("endtime".equals(row.getKey())) {
				if (map.containsKey("endtime"))
					predicates.add(builder.lessThanOrEqualTo(root.get("endtime"), Date.valueOf(row.getValue())));

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
		Transaction tx = getSession().beginTransaction();
		List<AnnounceVO> list =   getSession().createQuery("from AnnounceVO", AnnounceVO.class).setFirstResult(first)
				.setMaxResults(PAGE_MAX_RESULT).list();
		
		tx.commit();
		return list;
	}
	
	@Override
	public List<AnnounceVO> getByadminid(Integer adminid) {
		// TODO Auto-generated method stub
		Session session = getSession();
		Transaction tx =  session.beginTransaction();
		List<AnnounceVO> list = new ArrayList<AnnounceVO>();
		AdminVO admin = session.get(AdminVO.class, adminid);
		for(AnnounceVO a :admin.getAnnounces()) {
			list.add(a);
		}
		tx.commit();
		return (List)list ;
	}
	
	@Override
	
	public long getTotal() {
		return getSession().createQuery("select count(*) from AnnounceVO", Long.class).uniqueResult();
	}

	@Override
	
	public AnnounceVO getOne(Integer announceid) {
		
		Transaction tx = getSession().beginTransaction();
		AnnounceVO announce = getSession().get(AnnounceVO.class, announceid);
		tx.commit();
		return announce;
	}
	

}
