import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Logger logger = Logger.getLogger(HttpRequest.class);
    private String httpMethod;
    private String path;
    private String protocol;
    private Map<String, String> headers;

    public HttpRequest(BufferedReader reader ) throws IOException {


                String line = reader.readLine();
        logger.info(line);
                Map<String, String> headers = new HashMap<>();
                if (!line.isEmpty()) {

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
                logger.info("headLine is null");



    }

    @Override
    public String toString() {

        return String.format("httpMethod - %s, path - %s, protocol - %s", httpMethod, path, protocol);
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
