import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String httpMethod;
    private String path;
    private String protocol;
    private Map<String, String> headers;

    public HttpRequest(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();
            Map<String, String> headers = new HashMap<>();
            if (line != null) {
                while (reader.ready()) {
                    String[] pair = reader.readLine().split(": ");
                    if (pair.length == 2) headers.put(pair[0], pair[1]);
                }
                String[] lines = line.split(" ");
                this.httpMethod = lines[0];
                this.path = lines[1].replace("%20", " ");
                this.protocol = lines[2];
                this.headers = headers;

            }

    }

    @Override
    public String toString() {

        return super.toString() + String.format("httpMethod - %s, path - %s, protocol - %s", httpMethod, path, protocol);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}