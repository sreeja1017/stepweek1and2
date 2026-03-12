import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    String account;
    int time; // minutes

    Transaction(int id, int amount, String merchant, String account, int time) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.time = time;
    }
}

public class TwoSumProblem {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<String> findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                Transaction prev = map.get(complement);
                result.add("(" + prev.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public List<String> findTwoSumWithTimeWindow(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction prev = map.get(complement);

                if (Math.abs(t.time - prev.time) <= 60) {
                    result.add("(" + prev.id + ", " + t.id + ")");
                }
            }

            map.put(t.amount, t);
        }

        return result;
    }

    public List<String> detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (String key : map.keySet()) {

            List<Transaction> list = map.get(key);

            if (list.size() > 1) {

                Set<String> accounts = new HashSet<>();

                for (Transaction t : list)
                    accounts.add(t.account);

                result.add(key + " accounts=" + accounts);
            }
        }

        return result;
    }

    public void kSum(int start, int k, int target,
                     List<Integer> current,
                     List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (k == 0 || start >= transactions.size())
            return;

        for (int i = start; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            current.add(t.id);

            kSum(i + 1, k - 1, target - t.amount, current, result);

            current.remove(current.size() - 1);
        }
    }

    public List<List<Integer>> findKSum(int k, int target) {

        List<List<Integer>> result = new ArrayList<>();

        kSum(0, k, target, new ArrayList<>(), result);

        return result;
    }

    public static void main(String[] args) {

        TwoSumProblem system = new TwoSumProblem();

        system.addTransaction(new Transaction(1, 500, "StoreA", "acc1", 600));
        system.addTransaction(new Transaction(2, 300, "StoreB", "acc2", 615));
        system.addTransaction(new Transaction(3, 200, "StoreC", "acc3", 630));

        System.out.println("Two Sum: " + system.findTwoSum(500));
        System.out.println("Two Sum (1 hour window): " + system.findTwoSumWithTimeWindow(500));
        System.out.println("Duplicates: " + system.detectDuplicates());
        System.out.println("K Sum: " + system.findKSum(3, 1000));
    }
}
