package chilltrip.trippage.viewarticle.dto;

public class StatisticsDTO {
    private Integer viewCount;
    private Integer favoriteCount;
    private Double rating;
    private Boolean isCollected;

    // Getters
    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public Double getRating() {
        return rating;
    }

    public Boolean getIsCollected() {
        return isCollected;
    }

    // Setters
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setIsCollected(Boolean isCollected) {
        this.isCollected = isCollected;
    }
}