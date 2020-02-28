import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HttpResponse {

    public static final HttpResponse HTTP_404 = new HttpResponse("404", "text/plain");

    private static final Logger logger = Logger.getLogger(HttpResponse.class);
    private final String protocol = "HTTP/1.1";
    private String statusCode;
    private File file;
    private String contentType = "";
    static  Hashtable<String, ArrayList<Byte>> cache = new Hashtable<String, ArrayList<Byte>>();

    public HttpResponse(String statusCode, String contentType) {
        this.statusCode = statusCode;
        this.contentType = contentType;
    }

    public HttpResponse() {

    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }


    public String getHeadLine() {
        return String.format("%s %s", protocol, statusCode);
    }


    private void writeHeaders(PrintWriter writer) {
        writer.println(getHeadLine());
        writer.println(contentType);
        logger.info(String.format("headers %s Sent", getHeadLine()));
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
    }

    public synchronized void writeResponse(Socket clientSocket) {
        try (OutputStream output = clientSocket.getOutputStream()) {                // try-catch with resources
            try (PrintWriter writer = new PrintWriter(output, true)) {
                writeHeaders(writer);
                if (file != null) sendFile(file, output);
            }
        } catch (IOException e) {
            logger.error(e);
//            e.printStackTrace();
        }
    }

    synchronized void sendFile(File file, OutputStream output) {

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fileInputStream)) {

            if (!cache.containsKey(file.getPath())) {
                ArrayList<Byte> list = new ArrayList<>();
                while (bis.available() != 0) {
                    list.add((byte) bis.read());
                }
                cache.put(file.getPath(),list);
                logger.info("cache added");
            }

            output.write(convertByteList(cache.get(file.getPath())));
            logger.info(String.format("File %s sent", file.getPath()));
        } catch (IOException e) {
            logger.error(String.format("Could not send file: %s", e.getMessage()));
        }
        logger.info(cache.keySet());
        for ( String s:cache.keySet()) {
            logger.info(String.format("Key - %s, value - %s", s, cache.get(s)));
        }
    }

    byte[] convertByteList(ArrayList<Byte> byteList) {
        byte[] res = new byte[byteList.size()];
        int i = 0;
        for (byte b : byteList) {
            res[i] = b;
            i++;
        }
        return res;
    }

    public void setContentType(String fileExtension) {
        contentType = MyServer.TYPE_MAPPING.getOrDefault(fileExtension, "text/plain");
    }

    public void setFile(File file) {
        this.file = file;
    }

}

