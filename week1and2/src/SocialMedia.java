import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

    public class SocialMedia {
        private ConcurrentHashMap<String, Integer> usernameMap = new ConcurrentHashMap<>();
        private ConcurrentHashMap<String, AtomicInteger> attemptFrequency = new ConcurrentHashMap<>();

        public SocialMedia() {

            usernameMap.put("john_doe", 1);
            usernameMap.put("admin", 2);
            usernameMap.put("testuser", 3);
        }

        public boolean checkAvailability(String username) {


            attemptFrequency
                    .computeIfAbsent(username, k -> new AtomicInteger(0))
                    .incrementAndGet();

            return !usernameMap.containsKey(username);
        }

        public boolean registerUser(String username, int userId) {

            if (usernameMap.containsKey(username)) {
                return false;
            }

            usernameMap.put(username, userId);
            return true;
        }

        public List<String> suggestAlternatives(String username) {

            List<String> suggestions = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                String suggestion = username + i;
                if (!usernameMap.containsKey(suggestion)) {
                    suggestions.add(suggestion);
                }
            }

            String dotVersion = username.replace("_", ".");
            if (!usernameMap.containsKey(dotVersion)) {
                suggestions.add(dotVersion);
            }

            return suggestions;
        }

        public String getMostAttempted() {

            String result = null;
            int max = 0;

            for (Map.Entry<String, AtomicInteger> entry : attemptFrequency.entrySet()) {

                int count = entry.getValue().get();

                if (count > max) {
                    max = count;
                    result = entry.getKey();
                }
            }

            return result + " (" + max + " attempts)";
        }

        public static void main(String[] args) {

            SocialMedia service = new SocialMedia();

            System.out.println("john_doe available: " +
                    service.checkAvailability("john_doe"));

            System.out.println("jane_smith available: " +
                    service.checkAvailability("jane_smith"));

            System.out.println("Suggestions for john_doe: " +
                    service.suggestAlternatives("john_doe"));

            for (int i = 0; i < 5; i++) {
                service.checkAvailability("admin");
            }

            System.out.println("Most attempted: " +
                    service.getMostAttempted());
        }
    }

