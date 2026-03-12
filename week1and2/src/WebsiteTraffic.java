import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

class WebsiteTraffic {

    // pageUrl -> total visits
    HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique users
    HashMap<String, HashSet<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    HashMap<String, Integer> sourceCount = new HashMap<>();


    // Process incoming event
    public void processEvent(Event e) {

        // Count page views
        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(e.url, new HashSet<>());
        uniqueVisitors.get(e.url).add(e.userId);

        // Count traffic sources
        sourceCount.put(e.source, sourceCount.getOrDefault(e.source, 0) + 1);
    }


    // Get Top 10 pages
    public void getDashboard() {

        PriorityQueue<Map.Entry<String,Integer>> pq =
                new PriorityQueue<>((a,b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");

        int count = 0;
        while(!pq.isEmpty() && count < 10) {

            Map.Entry<String,Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();

            int unique = uniqueVisitors.get(url).size();

            System.out.println((count+1) + ". " + url +
                    " - " + views + " views (" + unique + " unique)");

            count++;
        }

        System.out.println("\nTraffic Sources:");
        for(String source : sourceCount.keySet()) {
            System.out.println(source + " : " + sourceCount.get(source));
        }
    }


    // Main method for testing
    public static void main(String[] args) {

        WebsiteTraffic traffic = new WebsiteTraffic();

        traffic.processEvent(new Event("/article/breaking-news","user123","google"));
        traffic.processEvent(new Event("/article/breaking-news","user456","facebook"));
        traffic.processEvent(new Event("/sports/championship","user789","google"));
        traffic.processEvent(new Event("/sports/championship","user123","direct"));
        traffic.processEvent(new Event("/article/breaking-news","user789","google"));

        traffic.getDashboard();
    }
}