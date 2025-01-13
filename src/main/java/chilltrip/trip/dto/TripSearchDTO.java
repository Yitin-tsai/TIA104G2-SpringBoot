package chilltrip.trip.dto;

public class TripSearchDTO {

	private String eventContent;
	private String regionContent;
	private String keyword;
	private int page = 0;
	private int size = 12;

	// 構造函數
	public TripSearchDTO() {
	}

	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

	public String getRegionContent() {
		return regionContent;
	}

	public void setRegionContent(String regionContent) {
		this.regionContent = regionContent;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "TripSearchDTO{" + "eventContent='" + eventContent + '\'' + ", regionContent='" + regionContent + '\''
				+ ", keyword='" + keyword + '\'' + ", page=" + page + ", size=" + size + '}';

	}
}