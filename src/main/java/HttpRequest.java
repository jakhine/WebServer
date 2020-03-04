import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private final String line;

    public HttpRequest(BufferedReader reader) throws Exception {
        try {
            String line;

//            while (reader.ready()) {
                 line = reader.readLine();
                 this.line = line;
            if(line!=null) {
                logger.info(String.format("Head line is %s", line));
                Map<String, String> headers = new HashMap<>();

                while (reader.ready()) {
                    String[] pair = reader.readLine().split(": ");
                    if (pair.length == 2) headers.put(pair[0], pair[1]);
                }
                String[] lines = line.split(" ");
                this.httpMethod = lines[0];

                String path = URLDecoder.decode(lines[1], StandardCharsets.UTF_8.name());
                if (path.contains("?")){ parameters = path.substring(path.indexOf("?"));
                    logger.info(String.format("params = %s", parameters));
                    path = path.substring(0,path.indexOf("?"));}
                this.path = path;


                this.protocol = lines[2];
                this.headers = Collections.unmodifiableMap(headers);
//            }
            }
        } catch (Exception e) {
            logger.error("Could not create request ", e);
            throw new Exception() ;
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
