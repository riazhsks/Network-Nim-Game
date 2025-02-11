package nim;

import java.io.*;
import java.net.*;

public class Nim {
    private static final int PORT = 123; // Port to listen on
    private static int pileSize = 21; // Initial number of rocks
    private static Socket player1Socket, player2Socket;
    private static BufferedWriter out1, out2;
    private static BufferedReader in1, in2;
    private static boolean isGameOver = false;
    private static char currentPlayer = '1'; // Start with Player 1

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started, waiting for players...");

            // Accept Player 1
            player1Socket = serverSocket.accept();
            out1 = new BufferedWriter(new OutputStreamWriter(player1Socket.getOutputStream()));
            in1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
            out1.write("You are Player 1. Waiting for Player 2...");
            out1.newLine();
            out1.flush();
            System.out.println("Player 1 connected.");

            // Accept Player 2
            player2Socket = serverSocket.accept();
            out2 = new BufferedWriter(new OutputStreamWriter(player2Socket.getOutputStream()));
            in2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
            out2.write("You are Player 2.");
            out2.newLine();
            out2.flush();
            System.out.println("Player 2 connected.");

            out1.write("Player 2 has joined. The game begins!");
            out1.newLine();
            out1.flush();
            out2.write("The game begins!");
            out2.newLine();
            out2.flush();

            // Game loop
            while (!isGameOver) {
                printPileSize();
                handlePlayerTurn(currentPlayer == '1' ? in1 : in2, currentPlayer == '1' ? out1 : out2);
                currentPlayer = (currentPlayer == '1') ? '2' : '1'; // Switch turn
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    private static void printPileSize() throws IOException {
        String message = "Current pile size: " + pileSize;
        out1.write(message);
        out1.newLine();
        out1.flush();
        out2.write(message);
        out2.newLine();
        out2.flush();
    }

    private static void handlePlayerTurn(BufferedReader in, BufferedWriter out) {
        boolean validMove = false;
        while (!validMove) {
            try {
                out.write("Your turn! Enter the number of rocks to take (1-3):");
                out.newLine();
                out.flush();

                int rocksToTake = Integer.parseInt(in.readLine()); // Wait for player's input
                if (rocksToTake >= 1 && rocksToTake <= 3 && rocksToTake <= pileSize) {
                    pileSize -= rocksToTake;
                    validMove = true;
                    checkGameOver();
                } else {
                    out.write("Invalid move. You can take 1 to 3 rocks, and the remaining pile size is " + pileSize + ".");
                    out.newLine();
                    out.flush();
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkGameOver() throws IOException {
        if (pileSize <= 0) {
            isGameOver = true;
            if (currentPlayer == '1') {
                out1.write("You win!");
                out1.newLine();
                out1.flush();
                out2.write("You lose.");
                out2.newLine();
                out2.flush();
            } else {
                out1.write("You lose.");
                out1.newLine();
                out1.flush();
                out2.write("You win!");
                out2.newLine();
                out2.flush();
            }
        }
    }

    private static void closeConnections() {
        try {
            if (in1 != null) in1.close();
            if (in2 != null) in2.close();
            if (out1 != null) out1.close();
            if (out2 != null) out2.close();
            if (player1Socket != null) player1Socket.close();
            if (player2Socket != null) player2Socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
