import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class DynamicServer {
    public static final int SERVER_PORT = 5000;
    public static final String ROOT_DIRECTORY = "docs/";

    public static void main(String[] args) {
        try {
            Map<String, IResourceHandler> resources = new HashMap<>();

            resources.put("home", new HomeResourceHandler());
            resources.put("cat", new CatResourceHandler());
            resources.put("example", new ExampleResourceHandler());

            ServerSocket server = new ServerSocket(SERVER_PORT);
            System.out.println("start server");

            Socket clientSocket = server.accept();
            Map<String, String> params = getParams(clientSocket);

            String uri = params.get("uri").substring(1);
            String httpVersion = params.get("httpVersion");


            if (!httpVersion.equals("HTTP/1.1")) {
                errorCode(Code.CODE505, clientSocket);
            } else {
                ResponseContent responseContent = null;
                IResourceHandler handler = resources.get(uri);

                if (handler != null) {
                    responseContent = handler.handle(params);
                } else {
                    responseContent = findFile(uri);
                }
                if (responseContent != null) {
                    success(responseContent, clientSocket);
                } else {
                    errorCode(Code.CODE404, clientSocket);
                }
                server.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ResponseContent findFile(String uri) throws IOException {
        File file = new File(ROOT_DIRECTORY + uri);
        if (!file.exists()) return null;

        byte[] buffer = Files.readAllBytes(file.toPath());
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return new ResponseContent(mimeType, buffer);
    }
    public static void errorCode(Code code, Socket clientSocket) throws IOException {
        String[] response = {"HTTP/1.1 " + code + "\r\n", "Server: NewSuperServer\r\n", "\r\n"};
        for (String responseHeaderLine : response) {
            clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
            clientSocket.getOutputStream().flush();
        }
        clientSocket.close();
    }
    public static void success(ResponseContent responseContent, Socket clientSocket)
            throws IOException {
        String[] response = {
                "HTTP/1.1 200 OK\r\n",
                "Server: NewSuperServer\r\n",
                "Content-Type: " + responseContent.getMimeType() + "\r\n",
                "Content-Length: " + responseContent.getContent().length + "\r\n",
                "\r\n"};

        for (String responseHeaderLine : response) {
            clientSocket.getOutputStream().write(responseHeaderLine.getBytes());
            clientSocket.getOutputStream().flush();
        }

        clientSocket.getOutputStream().write(responseContent.getContent());
        clientSocket.getOutputStream().flush();
        clientSocket.close();
    }
    public static Map<String, String> getParams(Socket clientSocket) throws IOException {
        Map<String, String> params = new HashMap<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String headerLine = bufferedReader.readLine();
        String[] firstLine = headerLine.split("\\s+");

        String method = firstLine[0];
        String uri = firstLine[1];
        String httpVersion = firstLine[2];

        params.put("method", method);
        params.put("uri", uri);
        params.put("httpVersion", httpVersion);

        while (!headerLine.isEmpty()) {
            headerLine = bufferedReader.readLine();
            String[] param = headerLine.split(":");
            params.put(param[0], param.length > 1 ? param[1] : "");
        }

        return params;
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
