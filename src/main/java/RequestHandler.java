import org.apache.log4j.Logger;

import javax.xml.ws.RequestWrapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestHandler implements Runnable, IHttpRequestHandler     {
        static Map<String, AtomicInteger> stats = new ConcurrentHashMap<>();
    private Logger logger = Logger.getLogger(RequestHandler.class);
    private Socket clientSocket;
    private String rootFolderPath = MyServer.rootFolderPath;
    private String indexFile = MyServer.indexFile;
    private File file;

    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        logger.info(String.format("connection from address+ %s", clientSocket.getInetAddress()));
//        this.run();
    }


     @Override
    public void run() {
        // здесь открываю потоки так как закрытие потоков закрывает весь сокет
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            HttpRequest httpRequest = createRequest(reader);                 //Create request object
            analyzeRequest(httpRequest);                                     //Analyze request
            createSendResponse(clientSocket);                               //Create response
//            httpResponse.writeResponse(clientSocket);                        //Write response
        } catch (Exception e) {
            logger.error(String.format("Could not send response  %s", e.getMessage()));
            HttpResponse.HTTP_404.writeResponse(clientSocket);
        }
    }


    private void analyzeRequest(HttpRequest httpRequest) {
        MyServer.stats.computeIfAbsent((String.format("%s %s", httpRequest.getHttpMethod(), httpRequest.getPath())), k -> new AtomicInteger(0));
        MyServer.stats.get(String.format("%s %s", httpRequest.getHttpMethod(), httpRequest.getPath())).incrementAndGet();
//        counter.incrementAndGet();
        logger.info(String.format("%s %s %s", httpRequest.getHttpMethod(), httpRequest.getPath(), MyServer.stats));
//        MyServer.cache.get

        file = new File(rootFolderPath + httpRequest.getPath());
        if (file.isDirectory()) {
            file = new File(file.getAbsolutePath() + "/" + indexFile);
        }


    }

    HttpRequest createRequest(BufferedReader reader) {
        HttpRequest req = null;
        try {
            req = new HttpRequest(reader);
        } catch (Exception e) {
            logger.error(String.format("Could not create HttpRequest  %s", e.getMessage()));
        }
        logger.info(String.format("HttpRequest was created %s", req));
        return req;
    }

    private void createSendResponse(Socket clientSocket) {
        if (!file.exists()) {
            HttpResponse.HTTP_404.writeResponse(clientSocket);
        } else {
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setFile(file);
            httpResponse.setStatusCode(200);
            String fileExtension = getFileExtension(file); // добываем расширение файла
            httpResponse.setContentType(fileExtension);
            logger.info(String.format("httpResponse was created %s ", httpResponse));
            httpResponse.writeResponse(clientSocket);
        }
    }

    String getFileExtension(File file) {
        String fileExtension = "";
        if (file.getName().contains(".")) {         // добываем расширение файла
            String filename = file.getName();
            fileExtension = filename.substring(filename.lastIndexOf("."));
        }
        return fileExtension;
    }

    @Override
    public HttpResponse process(HttpRequest httpRequest) {
        return null;
    }
}