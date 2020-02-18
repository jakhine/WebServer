import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private Logger logger = Logger.getLogger(HttpResponse.class);
    private final String protocol = "HTTP/1.1";
    private String statusCode;
    private static Map<String, String> typeMapping = new HashMap<>();
//    private File file;

    private String contentType = "";

    public HttpResponse(Socket clientSocket, File file) {
        try (OutputStream output = clientSocket.getOutputStream()) {                // try-catch with resources
            try (PrintWriter writer = new PrintWriter(output, true)) {
                setTypeMapping(Configuration.getProperties().getProperty("content-type"));
                sendResponseWithFile(this, file,writer,output);
            }
        } catch (IOException e) {
           logger.error(e);
//            e.printStackTrace();
        }
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
        logger.info("headers added " + typeMapping);
    }
    public void sendResponse(HttpResponse response, PrintWriter writer) {
        if (response.getHeadLine().toLowerCase().contains("404 not found")) {
            writer.println(response.getHeadLine());
            writer.println(response.getContentType());
            writer.write("<h4> The file not found  </h4>");
        } else {
            writer.println(response.getHeadLine());
            writer.println(response.getContentType());
        }
        logger.info(String.format("headers %s Sent", response.getHeadLine()));
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
    }

    public void sendResponseWithFile(HttpResponse response, File file, PrintWriter writer, OutputStream output) {

        if (file.isDirectory()) {
            file = new File(file.getAbsolutePath() + "/" + Configuration.getProperties().getProperty("indexFile"));
        }
        if (!file.exists()) {
            response.setStatusCode("404 NOT FOUND");
            sendResponse(response, writer);
            return;
        }

        if (file.getName().contains(".")) {         // добываем расширение файла
            String filename = file.getName();
            String fileExtension = filename.substring(filename.lastIndexOf("."));
            response.setContentType(fileExtension);
        }

        sendResponse(response, writer);

        sendFile(file, output);
    }


    void sendFile(File file, OutputStream output) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buf = new byte[250];
            int count;
            while ((count = fileInputStream.read(buf)) != -1) {
                output.write(buf, 0, count);
            }
            logger.info(String.format("File %s sent", file.getPath()));
        } catch (IOException e) {

            logger.error(String.format("Could not send file: %s", e.getMessage()), e);
        }
    }
    public Map<String, String> getTypeMapping() {
        return typeMapping;
    }

    public void setContentType(String fileExtension) {
        contentType = typeMapping.getOrDefault(fileExtension, "text/plain");
    }

    public  void send404Response(){

    }
    public String getContentType() {
        return contentType;
    }

//    public void setFile(File file) {
//        this.file = file;
//    }

}

