package chilltrip.locationcomment.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import chilltrip.location.model.LocationVO;
import chilltrip.member.model.MemberVO;
import chilltrip.util.HibernateUtil;
@Repository
public class LocationCommentDAOimpl implements LocationCommentDAO{
	private SessionFactory factory;
	
	@Autowired
	public LocationCommentDAOimpl( @Qualifier("getSessionFactory") SessionFactory factory) {
        this.factory = factory;
    }
	private Session getSession() {
		return factory.getCurrentSession();
	}
	
	@Override
	public void insert(LocationCommentVO locationCommentVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			session.save(locationCommentVO);
			session.getTransaction().commit();
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public void update(LocationCommentVO locationCommentVO) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			session.update(locationCommentVO);
			session.getTransaction().commit();
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public boolean delete(Integer locationCommentId) {
		// TODO Auto-generated method stub
		try {
			Session session = getSession();
			session.beginTransaction();
			LocationCommentVO locationCommentVO =session.get(LocationCommentVO.class, locationCommentId);
			session.delete(locationCommentVO);
			session.getTransaction().commit();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
			return false;
		}
	}

	@Override
	public List<LocationCommentVO> getByLocation(Integer locationId) {
		
		Session session = getSession();
		session.beginTransaction();
		List<LocationCommentVO> list = new ArrayList<LocationCommentVO>();
		LocationVO location = session.get(LocationVO.class, locationId);
		for(LocationCommentVO locaCom: location.getLocationComment()) {
			list.add(locaCom);
		}
		return list;
	}

	@Override
	public List<LocationCommentVO> getByMember(Integer memberId) {
		// TODO Auto-generated method stub
		Session session = getSession();
		session.beginTransaction();
		List<LocationCommentVO> list = new ArrayList<LocationCommentVO>();
		MemberVO member = session.get(MemberVO.class, memberId);
		for(LocationCommentVO memCom : member.getLocationComment()) {
			list.add(memCom);
		}
		return list;
		
	}
	@Override
	public LocationCommentVO getById(Integer id) {
		Session session = getSession();
		session.beginTransaction();
		LocationCommentVO comment =  session.get(LocationCommentVO.class, id);
		session.getTransaction().commit();
		return comment;
	}
	
}
