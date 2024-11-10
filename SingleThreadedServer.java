import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleThreadedServer {

    private static final int PORT = 2200;
    private static final long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on port " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    String request = in.readLine();
                    String response = handleRequest(request);
                    out.println(response);

                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
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

