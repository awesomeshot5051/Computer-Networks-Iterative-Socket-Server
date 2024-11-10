import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedClient {
    private static long totalTurnaroundTime = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server address: ");
        String serverAddress = scanner.nextLine();

        System.out.print("Enter server port: ");
        int port = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        while (true) {
            System.out.println("Select operation:");
            System.out.println("1. Date and Time");
            System.out.println("2. Uptime");
            System.out.println("3. Memory Use");
            System.out.println("4. Netstat");
            System.out.println("5. Current Users");
            System.out.println("6. Running Processes");
            System.out.println("0. Exit");

            int operationChoice = scanner.nextInt();
            String request = getRequestFromChoice(operationChoice);

            System.out.print("Enter the number of client requests to generate (1, 5, 10, 15, 20, 25): ");
            int numRequests = scanner.nextInt();

            ExecutorService executor = Executors.newFixedThreadPool(numRequests);


            for (int i = 0; i < numRequests; i++) {
                final int requestId = i;
                executor.submit(() -> {
                    long startTime = System.currentTimeMillis();
                    String response = sendRequest(serverAddress, port, request);
                    long turnaroundTime = System.currentTimeMillis() - startTime;
                    System.out.println("Client " + requestId + ": " + response);
                    System.out.println("Turnaround Time for client " + requestId + ": " + turnaroundTime + " ms");
                    synchronized (MultiThreadedClient.class) {
                        totalTurnaroundTime += turnaroundTime;
                    }
                });
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                // Wait for all threads to finish
            }

            double averageTurnaroundTime = (double) totalTurnaroundTime / numRequests;
            System.out.println("Total Turnaround Time: " + totalTurnaroundTime + " ms");
            System.out.println("Average Turnaround Time: " + averageTurnaroundTime + " ms");
        }
    }

    private static String getRequestFromChoice(int choice) {
        switch (choice) {
            case 0:
                System.exit(0);
            case 1:
                return "Date and Time";
            case 2:
                return "Uptime";
            case 3:
                return "Memory Use";
            case 4:
                return "Netstat";
            case 5:
                return "Current Users";
            case 6:
                return "Running Processes";
            default:
                return "Invalid request";
        }
    }

    private static String sendRequest(String serverAddress, int port, String request) {
        try (Socket socket = new Socket(serverAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            return "Error communicating with server: " + e.getMessage();
        }
    }
}

