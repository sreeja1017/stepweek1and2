import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queries = new HashMap<>();
}

public class AutocompleteSystem {

    TrieNode root = new TrieNode();

    // Global query frequency
    HashMap<String, Integer> frequencyMap = new HashMap<>();


    // Insert query into Trie
    public void insert(String query, int freq) {

        TrieNode node = root;

        for(char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queries.put(query,
                    node.queries.getOrDefault(query, 0) + freq);
        }
    }


    // Update frequency when a user searches
    public void updateFrequency(String query) {

        int freq = frequencyMap.getOrDefault(query, 0) + 1;
        frequencyMap.put(query, freq);

        insert(query, 1);
    }


    // Get top 10 suggestions
    public List<String> search(String prefix) {

        TrieNode node = root;

        for(char c : prefix.toCharArray()) {

            if(!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        PriorityQueue<Map.Entry<String,Integer>> pq =
                new PriorityQueue<>((a,b) -> a.getValue() - b.getValue());

        for(Map.Entry<String,Integer> entry : node.queries.entrySet()) {

            pq.offer(entry);

            if(pq.size() > 10)
                pq.poll();
        }

        List<String> result = new ArrayList<>();

        while(!pq.isEmpty()) {
            result.add(0, pq.poll().getKey()
                    + " (" + pq.peek() + ")");
        }

        return result;
    }


    // Testing
    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.updateFrequency("java tutorial");
        system.updateFrequency("javascript");
        system.updateFrequency("java download");
        system.updateFrequency("java tutorial");

        System.out.println(system.search("jav"));
    }
}
