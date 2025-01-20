package chilltrip.trippage.editor.dto;

import java.util.List;

public class DaySchedule {
    private Integer index;
    private String content;
    private List<LocationDetails> spots;

    // Getters and Setters
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<LocationDetails> getSpots() {
        return spots;
    }

    public void setSpots(List<LocationDetails> spots) {
        this.spots = spots;
    }
}