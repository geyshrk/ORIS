import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Map;

public class CatResourceHandler implements IResourceHandler{
    public static final String ROOT_DIRECTORY = "docs/";
    @Override
    public ResponseContent handle(Map<String, String> params) {
        ResponseContent responseContent = new ResponseContent();

        String userAgent = params.get("User-Agent").toLowerCase();
        String image = "cat.jpg";
        if (userAgent.contains("windows")) image = "winCat.png";
        else if (userAgent.contains("linux")) image = "linuxCat.png";
        else if (userAgent.contains("mac os")) image = "macCat.png";

        responseContent.setMimeType(URLConnection.guessContentTypeFromName(image));

        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(ROOT_DIRECTORY + image))) {
            byte[] content = is.readAllBytes();
            responseContent.setContent(content);
        } catch (IOException e) {
            return null;
        }

        return responseContent;
    }
}
