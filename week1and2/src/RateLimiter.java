import java.util.*;

class TokenBucket {
    int tokens;
    int maxTokens;
    long lastRefillTime;
    double refillRate; // tokens per second

    TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = ((now - lastRefillTime) / 1000.0) * refillRate;

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
            lastRefillTime = now;
        }
    }
}

public class RateLimiter {

    private HashMap<String, TokenBucket> clientBuckets = new HashMap<>();

    private static final int MAX_REQUESTS = 1000;
    private static final double REFILL_RATE = 1000.0 / 3600; // tokens per second

    public synchronized String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clientBuckets.get(clientId);

        bucket.refill();

        if (bucket.tokens > 0) {
            bucket.tokens--;
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {

            long retryAfter =
                    (long)((1 / REFILL_RATE));

            return "Denied (0 requests remaining, retry after "
                    + retryAfter + " seconds)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            return "Client not found";
        }

        int used = MAX_REQUESTS - bucket.tokens;

        return "{used: " + used +
                ", limit: " + MAX_REQUESTS +
                ", reset: " + (bucket.lastRefillTime + 3600000) + "}";
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));

        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}