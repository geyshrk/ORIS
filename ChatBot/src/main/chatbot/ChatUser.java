package Homeworks.chatbot;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatUser {
    public static void main(String[] args) {
        try {
            Socket userSocket = new Socket("127.0.0.1",50000);

            System.out.println("Сессия с чат-ботом начата. Пожалуйста, задайте свои вопросы: ");

            Scanner messageScanner = new Scanner(System.in);
            String answer = messageScanner.nextLine() + '\n';
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));

            while (true){
                writer.write(answer);
                writer.flush();
                String botMessage = reader.readLine();
                if(botMessage.equals("exit")) break;
                System.out.println(botMessage);
                System.out.println("Есть другие вопросы? Если нет, введите \"exit\"");
                answer = messageScanner.nextLine() + '\n';
            }
            System.out.println("Выход");
            userSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}