import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String protocol = "HTTP/1.1";

    private Socket clientSocket;

    private String statusCode;
    private Map<String, String> typeMapping = new HashMap<>();
    private String contentType;

    public HttpResponse() {
        setTypeMapping(Configuration.property.getProperty("content-type"));
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }


    public String getHeadLine() {
        return String.format("%s %s", protocol, statusCode);
    }


    public void setTypeMapping(String mimeMapping) {
        for (String pair : mimeMapping.split(" ")) {
            String extension = pair.split(":")[0];
            String content_type = pair.split(":")[1];
            typeMapping.put(extension, content_type);


        }
        MyServer.logger.info("headers added " + typeMapping);
    }

    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void setContentType(String fileExtension) {
        contentType = typeMapping.getOrDefault(fileExtension, "text/plain");
    }
    public void sendResponse()  {
        try(PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);){
        writer.println(getHeadLine());
        writer.println(response.getTypeMapping().entrySet());
        logger.info(String.format("headers %s Sent", this.getTypeMapping()));
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
        }
       catch (IOException e) {
           MyServer.logger.error(String.format("Could not send file: %s", e.getMessage()),e);
        }
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}

