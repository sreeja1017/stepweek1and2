import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class InventorySystem {


    private ConcurrentHashMap<String, AtomicInteger> stockMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitingList = new ConcurrentHashMap<>();

    public void addProduct(String productId, int stock) {
        stockMap.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) return 0;
        return stock.get();
    }

    public String purchaseItem(String productId, int userId) {

        AtomicInteger stock = stockMap.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        while (true) {

            int currentStock = stock.get();

            if (currentStock <= 0) {

                // Add user to waiting list
                ConcurrentLinkedQueue<Integer> queue = waitingList.get(productId);
                queue.add(userId);
                int position = queue.size();

                return "Added to waiting list, position #" + position;
            }

            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                return "Success, " + (currentStock - 1) + " units remaining";
            }
        }
    }

    public List<Integer> getWaitingList(String productId) {
        return new ArrayList<>(waitingList.get(productId));
    }

    public static void main(String[] args) throws InterruptedException {

        InventorySystem system = new InventorySystem();

        String product = "IPHONE15_256GB";

        system.addProduct(product, 100);

        System.out.println("Stock available: " + system.checkStock(product));

        ExecutorService executor = Executors.newFixedThreadPool(100);

        for (int i = 1; i <= 200; i++) {

            int userId = i;

            executor.submit(() -> {
                String result = system.purchaseItem(product, userId);
                System.out.println("User " + userId + ": " + result);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\nFinal Stock: " + system.checkStock(product));
        System.out.println("Waiting List Size: " + system.getWaitingList(product).size());
    }
}