package chilltrip.trippage.viewarticle.dto;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ArticleBasicDTO {
    private Integer tripId;
    private String title;          
    private String authorName;     
    private Integer authorId;       
    private Timestamp publishTime;  
    private String tripAbstract;   
    private StatisticsDTO stats;
    private TagsDTO tags;

    // Getters
    public Integer getTripId() {
        return tripId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public Timestamp getPublishTime() {
        return publishTime;
    }

    public String getTripAbstract() {  
        return tripAbstract;
    }

    public StatisticsDTO getStats() {
        return stats;
    }

    public TagsDTO getTags() {
        return tags;
    }

    // Setters
    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public void setPublishTime(Timestamp timestamp) {
        this.publishTime = timestamp;
    }

    public void setTripAbstract(String tripAbstract) {  
        this.tripAbstract = tripAbstract;
    }

    public void setStats(StatisticsDTO stats) {
        this.stats = stats;
    }

    public void setTags(TagsDTO tags) {
        this.tags = tags;
    }
}