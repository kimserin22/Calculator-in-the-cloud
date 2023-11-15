import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);
            System.out.println("Connected to the server.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                System.out.print("Enter expression (e.g., ADD 2 3): ");
                String input = reader.readLine();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                // Send the expression to the server
                writer.println(input);

                // Receive and print the result from the server
                String result = serverReader.readLine();
                System.out.println("Result from server: " + result);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
