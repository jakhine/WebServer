import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Logger logger = Logger.getLogger(HttpRequest.class);
    private String httpMethod;
    private String path;
    private String protocol;
    private Map<String, String> headers;
    private String parameters;

    public HttpRequest(BufferedReader reader) {


        try {
            while (reader.ready()) {
                String line = reader.readLine();
                logger.info(line);
                Map<String, String> headers = new HashMap<>();

                if (line.contains("?")) parameters = line.substring(line.indexOf("?"));
                logger.info(String.format("params = %s", parameters));
                while (reader.ready()) {
                    String[] pair = reader.readLine().split(": ");
                    if (pair.length == 2) headers.put(pair[0], pair[1]);
                }
                String[] lines = line.split(" ");
                this.httpMethod = lines[0];
                this.path = lines[1].replace("%20", " ");
                this.protocol = lines[2];
                this.headers = Collections.unmodifiableMap(headers);
            }

        } catch (Exception e) {
            logger.error("Could not create request ", e);
        }

    }

    @Override
    public String toString() {

        return String.format("httpMethod - %s, path - %s, protocol - %s", httpMethod, path, protocol);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return "" + path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

}
