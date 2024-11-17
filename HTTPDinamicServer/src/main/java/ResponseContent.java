import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseContent {
    private String mimeType;
    private byte[] content;
}
