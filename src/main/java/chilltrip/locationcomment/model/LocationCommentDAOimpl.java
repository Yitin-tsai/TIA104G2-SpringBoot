package chilltrip.locationcomment.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.location.model.LocationVO;
import chilltrip.member.model.MemberVO;
import chilltrip.util.HibernateUtil;

public class LocationCommentDAOimpl implements LocationCommentDAO{
	private SessionFactory factory;
	
	public LocationCommentDAOimpl() {
		factory = HibernateUtil.getSessionFactory();
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
	public void delete(Integer locationCommentId) {
		// TODO Auto-generated method stub
		LocationCommentVO locationCommentVO =getSession().get(LocationCommentVO.class, locationCommentId);
		try {
			Session session = getSession();
			session.beginTransaction();
			session.delete(locationCommentVO);
			session.getTransaction().commit();
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
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
	public static void main(String[] args) {
		LocationCommentDAOimpl dao = new  LocationCommentDAOimpl();
//		System.out.println(dao.getByLocation(1));;
		System.out.println(dao.getByMember(2));
	}
}
