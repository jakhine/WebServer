import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String httpMethod;
    String path;
    String protocol;
    Map<String, String> headers;

    public HttpRequest(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        Map<String, String> headers = new HashMap<>();
        if (line != null) {
            while(reader.ready()){
                String [] pair = reader.readLine().split(": ");
                if (pair.length==2)  headers.put(pair[0],pair[1]);
            }
            System.out.println(headers);
            String[] lines = line.split(" ");
            this.httpMethod = lines[0];
            this.path = lines[1].replace("%20", " ");
            this.protocol = lines[2];
            this.headers = headers;
        } else {
            throw new IllegalArgumentException("Null HTTP Requests are not allowed");
        }
    }


    @Override
    public String toString() {

        return super.toString() + String.format("httpMethod - %s, path - %s, protocol - %s", httpMethod, path, protocol);
    }
}
