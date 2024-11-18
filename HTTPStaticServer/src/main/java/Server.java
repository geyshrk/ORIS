import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class Server {
    public static final int SERVER_PORT = 5000;
    public static final String ROOT_DIRECTORY = "html/";

    public static void main (String[] args){
        try {
            ServerSocket server = new ServerSocket(SERVER_PORT);
            // wait client connection
            //System.out.println("accept");
            System.out.println("start server");

            Socket clientSocket = server.accept();

            System.out.println("connected " + clientSocket.getInetAddress() +
                    ":" + clientSocket.getPort());

            InputStream inputStream = clientSocket.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String headerLine = bufferedReader.readLine();

            String[] firstLine = headerLine.split("\\s+");

            String method = firstLine[0];
            String uri = firstLine[1].substring(1);
            String httpVers = firstLine[2];

            System.out.println(method + " " + uri + " " + httpVers);
            while (headerLine != null && !headerLine.equals("")) {
                headerLine = bufferedReader.readLine();
                System.out.println(headerLine);
            }

            if (!httpVers.equals("HTTP/1.1")) {
                String[] response = {"HTTP/1.1 505 HTTP Version Not Supported\r\n","Server: NewSuperServer\r\n","\r\n"};

                for (String responseHeaderLine : response) {
                    clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
                    clientSocket.getOutputStream().flush();
                }
                clientSocket.close();
            } else {

                File file = new File(ROOT_DIRECTORY + uri);
                if (!file.exists()) {
                    String[] response = {"HTTP/1.1 404 Not Found\r\n", "Server: NewSuperServer\r\n", "\r\n"};

                    for (String responseHeaderLine : response) {
                        clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
                        clientSocket.getOutputStream().flush();
                    }
                    clientSocket.close();
                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {

                        byte[] buffer = Files.readAllBytes(file.toPath());

                        String[] response = {
                                "HTTP/1.1 200 OK\r\n",
                                "Server: NewSuperServer\r\n",
                                "Content-Type: text/html; charset=utf-8\r\n",
                                "Content-Length: " + buffer.length + "\r\n",
                                "\r\n"};

                        for (String responseHeaderLine : response) {
                            clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
                            clientSocket.getOutputStream().flush();
                        }

                        clientSocket.getOutputStream().write(buffer);
                        clientSocket.getOutputStream().flush();

                        clientSocket.close();
                    } catch (IOException e) {
                        // внутренняя ошибка сервера
                    }
                }
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
