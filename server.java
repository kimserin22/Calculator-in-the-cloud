package HW1;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Server is listening on port 8888...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Use a thread from the pool to handle the client
                executorService.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            String input;
            while ((input = reader.readLine()) != null) {
                System.out.println("Received from client: " + input);

                // Perform calculation
                try {
                    String result = performCalculation(input);
                    // Send the result back to the client
                    writer.println(result);
                } catch (CalculationException e) {
                    System.err.println("Calculation error: " + e.getErrorType().getErrorMessage());
                    // Send the error message back to the client
                    writer.println(e.getErrorType().getErrorMessage());
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error reading/writing to client: " + e.getMessage());
        }
    }

    private String performCalculation(String input) throws CalculationException {
        try {
            String[] tokens = input.split(" ");

            // Check if there are too many arguments
            if (tokens.length > 3) {
                throw new CalculationException(CalculationError.TOO_MANY_ARGUMENTS);
            }

            int operand1 = Integer.parseInt(tokens[1]);
            int operand2 = Integer.parseInt(tokens[2]);
            String operator = tokens[0];

            int result = 0;

            switch (operator) {
                case "ADD":
                    result = operand1 + operand2;
                    break;
                case "SUB":
                    result = operand1 - operand2;
                    break;
                case "MUL":
                    result = operand1 * operand2;
                    break;
                case "DIV":
                    if (operand2 == 0) {
                        throw new CalculationException(CalculationError.DIVISION_BY_ZERO);
                    }
                    result = operand1 / operand2;
                    break;
                default:
                    throw new CalculationException(CalculationError.INVALID_OPERATOR);
            }

            return String.valueOf(result);
        } catch (NumberFormatException e) {
            throw new CalculationException(CalculationError.INVALID_EXPRESSION);
        }
    }
}

class CalculationException extends Exception {
    private final CalculationError errorType;

    public CalculationException(CalculationError errorType) {
        this.errorType = errorType;
    }

    public CalculationError getErrorType() {
        return errorType;
    }
}

enum CalculationError {
    INVALID_OPERATOR("error: Invalid operator"),
    INVALID_EXPRESSION("error: Invalid expression format"),
    DIVISION_BY_ZERO("error: Cannot divide by zero"),
    TOO_MANY_ARGUMENTS("error: Too many arguments in the expression");

    private final String errorMessage;

    CalculationError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
