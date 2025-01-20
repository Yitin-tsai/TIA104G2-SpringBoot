package chilltrip.trippage.viewarticle.dto;

import java.util.List;

public class TagsDTO {
    private List<String> areas;
    private List<String> activities;

    // Getters
    public List<String> getAreas() {
        return areas;
    }

    public List<String> getActivities() {
        return activities;
    }

    // Setters
    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
}
