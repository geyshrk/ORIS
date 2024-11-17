package Homeworks.chatbot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;

public class ChatServer {
    public static final int SERVER_PORT = 50000;

    public static void main (String[] args){
        System.out.println(int.class);
        ChatServerChecker checker = new ChatServerChecker();
        try {
            ServerSocket server = new ServerSocket(SERVER_PORT);
            System.out.println("start server");

            checker.setServer(server);
            checker.start();

            while (true) {
                System.out.println("wait user connection");

                Socket clientSocket = server.accept();
                System.out.println("user " + clientSocket.getInetAddress() + ":" +
                        clientSocket.getPort() + " is connected");
                UserHandler handler = new UserHandler(clientSocket);

                handler.start();
            }
        } catch (IOException e) {
            if (!checker.isServerWasCorrectlyClosed()) throw new RuntimeException();
        }
    }

//
}
