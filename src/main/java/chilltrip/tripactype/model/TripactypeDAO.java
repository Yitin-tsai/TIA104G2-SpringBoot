package chilltrip.tripactype.model;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.tripactyperela.model.TripactyperelaVO;
import chilltrip.util.HibernateUtil;

public class TripactypeDAO implements TripactypeDAO_interface  {

	private SessionFactory factory;
	
	public TripactypeDAO() {
		factory = HibernateUtil.getSessionFactory();
	}

	
	private Session getSession() {
		return factory.getCurrentSession();
	}
	
	@Override
	public void insert(TripactypeVO tripactypeVO) {
		try {
			Session session = getSession();
			session.beginTransaction();
			session.save(tripactypeVO);
			session.getTransaction().commit();
			
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
		
	}

	@Override
	public void update(TripactypeVO tripactypeVO) {
		try {
			Session session = getSession();
			getSession().beginTransaction();
			getSession().update(tripactypeVO);
			getSession().getTransaction().commit();
			
		}catch(Exception e) {
			e.printStackTrace();
			getSession().getTransaction().rollback();
		}
		
	}

	@Override
	public void delete(Integer eventtypeid) {
		try {
			Session session = getSession();
			getSession().beginTransaction();
			
			// 根據 ID 獲取要刪除的實體
			TripactypeVO tripactypeVO = session.get(TripactypeVO.class, eventtypeid);
			
			// 刪除該實體
			if(tripactypeVO != null) {
				session.delete(tripactypeVO);
			}
			session.getTransaction().commit();
			
		}catch(Exception e) {
			e.printStackTrace();
			
			if(getSession().getTransaction() != null && getSession().getTransaction().isActive()) {
				getSession().getTransaction().rollback();
			}
		}	
	}

	@Override
	public List<TripactypeVO> getAll() {
		
		getSession().beginTransaction();
		
		return getSession().createQuery("from TripactypeVO", TripactypeVO.class).list();
	}

	@Override
	public TripactypeVO getByid(Integer eventtypeid) {
		Session session = getSession();
		session.beginTransaction();
		TripactypeVO tripactypeVO = session.get(TripactypeVO.class, eventtypeid);
		session.getTransaction().commit();
		return tripactypeVO;
	}
	
	public static void main(String[] args) {
		
		TripactypeDAO dao = new TripactypeDAO();
		TripactypeVO tripactype = new TripactypeVO();
		TripactyperelaVO rela = new TripactyperelaVO();
		rela.setRelaid(1);
		
//		// 新增
//		tripactype.setEventcontent("健行活動");
//		dao.insert(tripactype);
//		
//		// 修改
//		tripactype.setEventtypeid(7);
//		tripactype.setEventcontent("美食活動");
//		dao.update(tripactype);
//		
//		// 查詢特定行程活動類型
//		tripactype = dao.getByid(7);
//		System.out.println(tripactype.getEventtypeid() + ",");
//		System.out.println(tripactype.getEventcontent());
//		System.out.println("---------------------");
//		
//		// 查詢行程活動類型
//		List<TripactypeVO> list = dao.getAll();
//		for(TripactypeVO type : list) {
//			System.out.print(type.getEventtypeid() + ",");
//			System.out.println(type.getEventcontent());
//			System.out.println("---------------------");
//		}
//		
//		// 查詢活動
//		List<TripactypeVO> list = dao.getAll();
//		for(TripactypeVO type : list) {
//			System.out.print(type.getEventtypeid() + ",");
//			System.out.print(type.getEventcontent());
//			System.out.println();
//		}
//		
//		// 呼叫 DAO 刪除資料
//		dao.delete(8);
		
	}
}
