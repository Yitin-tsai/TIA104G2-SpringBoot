package chilltrip.tripphoto.model;


import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chilltrip.tripphoto.dao.TripphotoDAO;
import chilltrip.tripphoto.dao.TripphotoDAOImplJDBC;

@Service
public class TripphotoService {

	private TripphotoDAO dao;

	
	@Autowired
	public TripphotoService(TripphotoDAO dao) {
		this.dao = dao;
	}

	public TripphotoVO addTripphoto(TripphotoVO tripphotoVO) {
		 // 檢查檔案大小
        if (tripphotoVO.getPhoto().length > 50 * 1024 * 1024) { // 50MB
            throw new IllegalArgumentException("檔案大小超過限制");
        }    
        // 檢查檔案類型（可選）
        String fileType = determineFileType(tripphotoVO.getPhoto());
        if (!isValidImageType(fileType)) {
            throw new IllegalArgumentException("不支援的檔案類型");
        }    
        dao.insert(tripphotoVO);
        return tripphotoVO;
	}

	public TripphotoVO updateTripphoto(TripphotoVO tripphotoVO) {
		 // 檢查檔案大小
        if (tripphotoVO.getPhoto().length > 50 * 1024 * 1024) { // 50MB
            throw new IllegalArgumentException("檔案大小超過限制");
        }    
        // 檢查檔案類型（可選）
        String fileType = determineFileType(tripphotoVO.getPhoto());
        if (!isValidImageType(fileType)) {
            throw new IllegalArgumentException("不支援的檔案類型");
        } 
		dao.update(tripphotoVO);
		return tripphotoVO;
	}

	public void deleteTripphoto(Integer tripphotoid) {
		dao.delete(tripphotoid);
	}

	public List<Map<String, Object>> getOneTripPhotoByType(Integer tripid, Integer triptype) {
		return dao.getOneTripPhotoByType(tripid, triptype);
	}

	 // 取得圖片並轉為Base64--> 這個方法是為了顯示到前端用的（測資料可以只用上面那個）
    public List<Map<String, Object>> getOneTripPhotoByTypeAsBase64(Integer tripPhotoId,Integer triptype) {
        List<Map<String, Object>> photoslist = dao.getOneTripPhotoByType(tripPhotoId, triptype);
        if (!photoslist.isEmpty()) {
        	Map<String, Object> photoMap = photoslist.get(0);
        	byte[] photoData = (byte[]) photoMap.get("photo");
        	System.out.println("Photo data length: " + photoData.length);
        	String base64Photo = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(photoData);
        	System.out.println("Base64 string length: " + base64Photo.length());
        	photoMap.put("photo", base64Photo);
        }
        return photoslist;
    }
    
    //目前還沒想到用在哪
    public String determineFileType(byte[] data) {
        // 使用檔案的 magic number 判斷檔案類型
        if (data.length >= 2) {
            if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8) {
                return "image/jpeg";
            } else if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50) {
                return "image/png";
            }
            // 可以增加其他類型的判斷
        }
        return null;
    }
    
    private boolean isValidImageType(String fileType) {
        return fileType != null && 
               (fileType.equals("image/jpeg") || 
                fileType.equals("image/png"));
    }
    
    public TripphotoVO getTripphotoById (Integer tripphotoid) {
    	return dao.getTripphotoById(tripphotoid);
    }
    
    

}
