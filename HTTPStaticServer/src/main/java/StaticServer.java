
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;

public class StaticServer {
    public static final int SERVER_PORT = 5000;
    public static final String ROOT_DIRECTORY = "docs/";

    public static void main(String[] args){
        try {
            ServerSocket server = new ServerSocket(SERVER_PORT);

            System.out.println("start server");

            Socket clientSocket = server.accept();

            System.out.println("connected " + clientSocket.getInetAddress() +
                    ":" + clientSocket.getPort());
            InputStream inputStream = clientSocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String headerLine = bufferedReader.readLine();
            String[] firstLine = headerLine.split("\\s+");

            String uri = firstLine[1].substring(1);
            String httpVersion = firstLine[2];
            System.out.println(headerLine);
            while (headerLine != null && !headerLine.isEmpty()) {
                headerLine = bufferedReader.readLine();
                System.out.println(headerLine);
            }

            if (!httpVersion.equals("HTTP/1.1")) {
                errorCode(Code.CODE505, clientSocket);
            } else {
                File file = new File(ROOT_DIRECTORY + uri);
                if (!file.exists()) {
                    errorCode(Code.CODE404, clientSocket);
                } else {
                    try {
                        successfulFileRead(file, clientSocket);
                    } catch (IOException e) {
                        errorCode(Code.CODE500, clientSocket);
                    }
                }
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void successfulFileRead(File file, Socket clientSocket) throws IOException {
        byte[] buffer = Files.readAllBytes(file.toPath());

        String[] response = {
                "HTTP/1.1 200 OK\r\n",
                "Server: NewSuperServer\r\n",
                "Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n",
                "Content-Length: " + buffer.length + "\r\n",
                "\r\n"};

        for (String responseHeaderLine : response) {
            clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
            clientSocket.getOutputStream().flush();
        }

        clientSocket.getOutputStream().write(buffer);
        clientSocket.getOutputStream().flush();

        clientSocket.close();
    }
    public static void errorCode(Code code, Socket clientSocket) throws IOException {
        String[] response = {"HTTP/1.1 " + code + "\r\n", "Server: NewSuperServer\r\n", "\r\n"};
        for (String responseHeaderLine : response) {
            clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
            clientSocket.getOutputStream().flush();
        }
        clientSocket.close();
    }
    private enum Code {
        CODE404, CODE500, CODE505;

        @Override
        public String toString() {
            return switch (this) {
                case CODE404 -> "404 Not Found";
                case CODE505 -> "505 HTTP Version Not Supported";
                default -> "500 Internal Server Error";
            };
        }
    }
}
