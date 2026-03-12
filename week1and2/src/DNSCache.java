import java.util.*;
import java.util.concurrent.*;

class DNSCache {

    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, int ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int capacity;

    private final Map<String, DNSEntry> cache;

    private int cacheHits = 0;
    private int cacheMisses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        this.cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        startCleanupThread();
    }

    public synchronized String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            cacheHits++;
            System.out.println("Cache HIT → " + entry.ipAddress);
            return entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            System.out.println("Cache EXPIRED");
        }

        cacheMisses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 300));

        System.out.println("Cache MISS → Queried upstream → " + ip);

        return ip;
    }

    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate 100ms DNS lookup
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Random rand = new Random();

        return "172.217.14." + rand.nextInt(255);
    }

    private void startCleanupThread() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {

            synchronized (this) {

                Iterator<Map.Entry<String, DNSEntry>> iterator = cache.entrySet().iterator();

                while (iterator.hasNext()) {

                    Map.Entry<String, DNSEntry> entry = iterator.next();

                    if (entry.getValue().isExpired()) {
                        iterator.remove();
                    }
                }
            }

        }, 10, 10, TimeUnit.SECONDS);
    }

    public void getCacheStats() {

        int total = cacheHits + cacheMisses;

        double hitRate = total == 0 ? 0 : ((double) cacheHits / total) * 100;

        System.out.println("Cache Hits: " + cacheHits);
        System.out.println("Cache Misses: " + cacheMisses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    public static void main(String[] args) {

        DNSCache dnsCache = new DNSCache(5);

        dnsCache.resolve("google.com");
        dnsCache.resolve("google.com");

        dnsCache.resolve("openai.com");
        dnsCache.resolve("github.com");

        dnsCache.resolve("google.com");

        dnsCache.getCacheStats();
    }
}
