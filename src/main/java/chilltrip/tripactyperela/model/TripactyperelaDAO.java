package chilltrip.tripactyperela.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import chilltrip.trip.model.TripVO;
import chilltrip.tripactype.model.TripactypeVO;
import chilltrip.util.HibernateUtil;


public class TripactyperelaDAO implements TripactyperelaDAO_interface{
	// 每頁最大顯示的結果數量
	// int PAGE_MAX_RESULT = 3;
	
	// Hibernate 的 SessionFactory 負責生成 Session
	// SessionFactory 為 thread-safe，可宣告為屬性讓請求執行緒們共用
	private SessionFactory factory;
	
	// 建構子初始化 SessionFactory
	public TripactyperelaDAO() {
		// 使用 Hibernate 獲取 SessionFactory
		factory = HibernateUtil.getSessionFactory();
	}
	
	// Session 為 not thread-safe，所以此方法在各個增刪改查方法裡呼叫
	// 以避免請求執行緒共用了同個 Session
	// 獲取當前的 Hibernate Session
	private Session getSession() {
		// 獲取當前綁定的 Session
		return factory.getCurrentSession();
	}
	
	
	@Override
	public void insert(TripactyperelaVO tripactyperelaVO) {
		try {
			// 獲取 Hibernate Session
			Session session = getSession();
			// 開始交易
			session.beginTransaction();
			// 儲存物件到資料庫
			session.save(tripactyperelaVO);
			// 提交交易
			session.getTransaction().commit();
			
		}catch(Exception e) {
			// 輸出錯誤資訊
			e.printStackTrace();
			// 如果出錯，回滾交易
			getSession().getTransaction().rollback();
		}
		
	}

	@Override
	public void update(TripactyperelaVO tripactyperelaVO) {
		try {
			// 開始交易
			getSession().beginTransaction();
			// 更新資料
			getSession().update(tripactyperelaVO);
			// 提交交易
			getSession().getTransaction().commit();
			
		} catch(Exception e) {
			// 輸出錯誤資訊
			e.printStackTrace();
			// 如果出錯，回滾交易
			getSession().getTransaction().rollback();
		}
		
	}

	@Override
	public void delete(Integer eventtyperelaid) {
		// 根據主鍵獲取物件
		TripactyperelaVO tripactyperelaVO = getSession().get(TripactyperelaVO.class, eventtyperelaid);
		try {
			// 開始交易
			getSession().beginTransaction();
			// 刪除資料
			getSession().delete(tripactyperelaVO);
			// 提交交易
			getSession().getTransaction().commit();
		}catch(Exception e) {
			// 輸出錯誤資訊
			e.printStackTrace();
			// 如果出錯，回滾交易
			getSession().getTransaction().rollback();
		}
	}

	@Override
	public List<TripactyperelaVO> getAll() {
		// 開始交易
		getSession().beginTransaction();
		// 使用 HQL 查詢所有 AnnounceVO 資料
		return getSession().createQuery("from TripactyperelaVO", TripactyperelaVO.class).list();
	}

//	@Override
//	public List<TripactyperelaVO> getByCompositeQuery(Map<String, String> map) {
//		return null;
//	}

//	@Override
//	public List<TripactyperelaVO> getAll(int currentPage) {
//		
//		return null;
//	}

	@Override
	public List<TripactyperelaVO> getByeventtypeid(Integer eventtypeid) {
		
		// 獲取 Session
		Session session = getSession();
		// 開始交易
		session.beginTransaction();
		// 用於存放結果
		List<TripactyperelaVO> list = new ArrayList<TripactyperelaVO>();
		// 根據主鍵獲取 TripactypeVO
		TripactypeVO tripactype = session.get(TripactypeVO.class, eventtypeid);
		for(TripactyperelaVO t : tripactype.getRelationship()) {
			// 從 tripactype 的 relationship 關聯屬性中提取資料
			list.add(t);
		}
		return (List)list;
	}

	
	public static void main(String[] args) {
		
		TripactyperelaDAO dao = new TripactyperelaDAO();
		TripactyperelaVO tripactyperela = new TripactyperelaVO();
		TripVO trip = new TripVO();
		trip.setTrip_id(1);
		TripactypeVO tripactype = new TripactypeVO();
		tripactype.setEventtypeid(1);
		
		// 新增
		tripactyperela.setTripid(trip);
		tripactyperela.setEventtypeid(tripactype);
		dao.insert(tripactyperela);
		
//		// 修改
//		tripactyperela.setRelaid(4);
//		tripactyperela.setTripid(trip);
//		tripactyperela.setEventtypeid(tripactype);
//		dao.update(tripactyperela);
//		
//		// 查詢
//		for(TripactyperelaVO rela : dao.getAll()) {
//			System.out.println(rela);
//		}
//		
//		// 刪除
//		dao.delete(4);
		
	}
	
}
