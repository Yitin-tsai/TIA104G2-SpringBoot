package chilltrip.navigation;

import java.util.HashMap;
import java.util.Map;

public class NavigationDTO {
	
private Map<String, String> links;
    
    public NavigationDTO() {
        this.links = new HashMap<>();
    }
    
    public void addLink(String key, String path) {
        this.links.put(key, path);
    }
    
    public Map<String, String> getLinks() {
        return links;
    }
    
    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

}
