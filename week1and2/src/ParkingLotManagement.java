import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    String status; // EMPTY, OCCUPIED, DELETED

    ParkingSpot() {
        status = "EMPTY";
    }
}

public class ParkingLotManagement {

    private static final int TOTAL_SPOTS = 500;

    ParkingSpot[] table = new ParkingSpot[TOTAL_SPOTS];

    int occupiedSpots = 0;
    int totalProbes = 0;

    public ParkingLotManagement() {
        for (int i = 0; i < TOTAL_SPOTS; i++)
            table[i] = new ParkingSpot();
    }

    // Hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % TOTAL_SPOTS;
    }

    // Park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].status.equals("OCCUPIED")) {
            index = (index + 1) % TOTAL_SPOTS; // linear probing
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupiedSpots++;
        totalProbes += probes;

        System.out.println("Assigned spot #" + index +
                " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (!table[index].status.equals("EMPTY")) {

            if (table[index].status.equals("OCCUPIED") &&
                    table[index].licensePlate.equals(plate)) {

                long exitTime = System.currentTimeMillis();
                long duration = exitTime - table[index].entryTime;

                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5 per hour

                table[index].status = "DELETED";
                occupiedSpots--;

                System.out.println("Spot #" + index + " freed");
                System.out.println("Duration: " +
                        String.format("%.2f", hours) +
                        " hours, Fee: $" +
                        String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % TOTAL_SPOTS;
        }

        System.out.println("Vehicle not found.");
    }

    // Statistics
    public void getStatistics() {

        double occupancy =
                (occupiedSpots * 100.0) / TOTAL_SPOTS;

        double avgProbes =
                occupiedSpots == 0 ? 0 :
                        (double) totalProbes / occupiedSpots;

        System.out.println("Occupancy: " +
                String.format("%.2f", occupancy) + "%");

        System.out.println("Average Probes: " +
                String.format("%.2f", avgProbes));

        System.out.println("Total Occupied Spots: " + occupiedSpots);
    }

    // Test
    public static void main(String[] args) {

        ParkingLotManagement lot =
                new ParkingLotManagement();

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}