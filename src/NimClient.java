package cz.cuni.mff.riazhsks.socketmulti;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class NimClient {
    private static final int PORT = 123; // Port number
    private static Socket socket;
    private static BufferedReader in;
    private static BufferedWriter out;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", PORT); // Connect to server
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner scanner = new Scanner(System.in);

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println("Server: " + serverMessage);

                // If it's the player's turn, send input to the server
                if (serverMessage.contains("Your turn")) {
                    System.out.print("Enter number of rocks to take: ");
                    String input = scanner.nextLine();
                    out.write(input);
                    out.newLine();
                    out.flush();
                }

                // Exit if the game ends
                if (serverMessage.contains("win") || serverMessage.contains("lose")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
