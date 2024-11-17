import java.util.Map;

public interface IResourceHandler {
    ResponseContent handle(Map<String, String> params);
}
