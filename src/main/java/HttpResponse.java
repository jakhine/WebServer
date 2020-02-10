import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String protocol = "HTTP/1.1";
    private String statusCode;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse() {
        setHeaders(Configuration.property.getProperty("content-type"));

    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }


    public String getHeadLine() {
        return String.format("%s %s", protocol, statusCode);
    }


    public void setHeaders(String mimeMapping) {
        for (String pair : mimeMapping.split(" ")) {
            String extension = pair.split(":")[0];
            String content_type = pair.split(":")[1];
            headers.put(extension, content_type);


        }
         MyServer.logger.info("headers added " + headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
