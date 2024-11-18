import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class ExampleResourceHandler implements IResourceHandler {
    public static final String ROOT_DIRECTORY = "docs/";
    @Override
    public ResponseContent handle(Map<String, String> params) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.setMimeType("text/html; charset=utf-8");

        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(ROOT_DIRECTORY + "example.html"))) {
            byte[] content = is.readAllBytes();
            responseContent.setContent(content);
        } catch (IOException e) {
            return null;
        }
        return responseContent;
    }
}
