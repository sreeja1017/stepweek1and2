import java.util.*;

public class PlagarismDetection {

    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();

    private int N = 5; // using 5-grams

    public List<String> generateNgrams(String text) {

        String[] words = text.split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        for (String gram : ngrams) {

            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    public void analyzeDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {

            if (ngramIndex.containsKey(gram)) {

                for (String otherDoc : ngramIndex.get(gram)) {

                    if (!otherDoc.equals(docId)) {

                        matchCount.put(otherDoc,
                                matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        System.out.println("Extracted " + ngrams.size() + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            double similarity =
                    (entry.getValue() * 100.0) / ngrams.size();

            System.out.println("Found " + entry.getValue()
                    + " matching n-grams with \"" + entry.getKey() + "\"");

            System.out.println("Similarity: "
                    + String.format("%.2f", similarity) + "%");

            if (similarity > 50) {
                System.out.println("PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        PlagarismDetection detector = new PlagarismDetection();

        String doc1 = "data structures and algorithms are important for computer science students";
        String doc2 = "data structures and algorithms are important topics in computer science";
        String doc3 = "machine learning and artificial intelligence are modern technologies";

        detector.addDocument("essay_089.txt", doc1);
        detector.addDocument("essay_092.txt", doc2);

        detector.analyzeDocument("essay_123.txt", doc1);
    }
}
