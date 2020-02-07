import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

   private final String protocol = "HTTP/1.1";
    private String statusCode;
    private Map<String, String> headers = new HashMap<>();


    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
            }


    public String getHeadLine() {
        return  String.format("%s %s" ,protocol, statusCode);
    }


    public void setHeaders(String key, String value) {
        headers.put(key, value);
           }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
