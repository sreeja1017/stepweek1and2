import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCache {

    private LinkedHashMap<String, VideoData> L1 =
            new LinkedHashMap<String, VideoData>(10000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 10000;
                }
            };

    private LinkedHashMap<String, VideoData> L2 =
            new LinkedHashMap<String, VideoData>(100000, 0.75f, true) {
                protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                    return size() > 100000;
                }
            };

    private HashMap<String, VideoData> database = new HashMap<>();

    private HashMap<String, Integer> accessCount = new HashMap<>();

    int L1Hits = 0;
    int L2Hits = 0;
    int L3Hits = 0;

    public VideoData getVideo(String videoId) {

        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT (0.5ms)");
            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");


        if (L2.containsKey(videoId)) {
            L2Hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);

            promoteToL1(video);

            return video;
        }

        System.out.println("L2 Cache MISS");

        if (database.containsKey(videoId)) {
            L3Hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData video = database.get(videoId);

            L2.put(videoId, video);

            accessCount.put(videoId,
                    accessCount.getOrDefault(videoId, 0) + 1);

            return video;
        }

        System.out.println("Video not found");
        return null;
    }

    private void promoteToL1(VideoData video) {

        int count = accessCount.getOrDefault(video.videoId, 0) + 1;
        accessCount.put(video.videoId, count);

        if (count > 2) {
            L1.put(video.videoId, video);
            System.out.println("Promoted to L1");
        }
    }

    public void addVideo(VideoData video) {
        database.put(video.videoId, video);
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        double L1Rate = total == 0 ? 0 : (L1Hits * 100.0) / total;
        double L2Rate = total == 0 ? 0 : (L2Hits * 100.0) / total;
        double L3Rate = total == 0 ? 0 : (L3Hits * 100.0) / total;

        System.out.println("L1 Hit Rate: " + String.format("%.2f", L1Rate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", L2Rate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", L3Rate) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.addVideo(new VideoData("video_123", "Movie Data"));
        cache.addVideo(new VideoData("video_999", "Series Data"));

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}