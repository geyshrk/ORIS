package Homeworks.chatbot;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class ChatServerChecker extends Thread{
    private ServerSocket server;

    private boolean serverWasCorrectlyClosed;
    public ChatServerChecker(){
        serverWasCorrectlyClosed = false;
    }
    @Override
    public void run(){
        Scanner scanner = new Scanner(System.in);
        String sc = scanner.nextLine();
        while (!sc.equals("exit")) {
            System.out.println("Неверная команда");
            sc = scanner.nextLine();
        }
        try {
            server.close();
            serverWasCorrectlyClosed = true;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
    public void setServer(ServerSocket server) {
        this.server = server;
    }
    public boolean isServerWasCorrectlyClosed() {
        return serverWasCorrectlyClosed;
    }
}
