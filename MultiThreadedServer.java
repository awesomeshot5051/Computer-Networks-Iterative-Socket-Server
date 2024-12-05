import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedServer {

    private static final int PORT = 2100;
    private static final long startTime = System.currentTimeMillis();
    private static final int THREAD_POOL_SIZE = 10; // Adjust based on server load

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClient(clientSocket)); // Submit each client to a new thread
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            executor.shutdown(); // Cleanly shut down thread pool when server exits
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket;
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String request = in.readLine();
            String response = handleRequest(request);
            out.println(response);

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    private static String handleRequest(String request) {
        switch (request) {
            case "Date and Time":
                return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
            case "Uptime":
                return getUptime();
            case "Memory Use":
                return getMemoryUsage();
            case "Netstat":
                return runSystemCommand("netstat");
            case "Current Users":
                return runSystemCommand("who");
            case "Running Processes":
                return runSystemCommand("ps -e");
            default:
                return "Invalid request";
        }
    }

    private static String getUptime() {
        long currentTime = System.currentTimeMillis();
        long uptime = currentTime - startTime;
        return "Uptime: " + (uptime / 1000) + " seconds";
    }

    private static String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        return "Memory usage: " + memoryUsed + " MB";
    }

    private static String runSystemCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error executing command: " + command;
        }
        return output.toString();
    }
}
