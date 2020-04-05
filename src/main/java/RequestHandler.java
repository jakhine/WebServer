import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestHandler implements Runnable {
    private static final Map<String, IHttpRequestHandler> reqHandlers = new ConcurrentHashMap<>();
    private static final HttpRequestHandlerImpls iReqImpls = new HttpRequestHandlerImpls();
    private static Logger logger = Logger.getLogger(RequestHandler.class);
    private Socket clientSocket;
    private String rootFolderPath = MyServer.rootFolderPath;
    private String indexFile = MyServer.indexFile;
    private File file;

    static {
        for (Field f :
                iReqImpls.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(HttpRequestHandler.class)) {
                HttpRequestHandler httpReqHandlerAnnotation = f.getAnnotation(HttpRequestHandler.class);
                try {
                    reqHandlers.put(httpReqHandlerAnnotation.value(), (IHttpRequestHandler) f.get(iReqImpls));
                } catch (IllegalAccessException e) {
                    logger.error("Could not find any instances with the annotation", e);
                }

            }
        }
    }


    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        logger.info(String.format("connection from address+ %s", clientSocket.getInetAddress()));
//        this.run();
    }


    @Override
    public void run() {


        // здесь открываю потоки так как закрытие потоков закрывает весь сокет
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            HttpRequest httpRequest = createRequest(reader);                //Create request object
            if (reqHandlers.containsKey(httpRequest.getPath())) {
                reqHandlers.get(httpRequest.getPath()).process(httpRequest).writeResponse(clientSocket);
            } else {
                analyzeRequest(httpRequest);                                     //Analyze request
                createSendResponse(clientSocket);                               //Create response
            }

        } catch (Exception e) {
            logger.error(String.format("Could not send response  %s", e.getMessage()));
            HttpResponse.HTTP_404.writeResponse(clientSocket);
        }
    }


    private void analyzeRequest(HttpRequest httpRequest) {
        MyServer.stats.computeIfAbsent((String.format("%s %s", httpRequest.getHttpMethod(), httpRequest.getPath())), k -> new AtomicInteger(0));
        MyServer.stats.get(String.format("%s %s", httpRequest.getHttpMethod(), httpRequest.getPath())).incrementAndGet();
        logger.info(String.format("%s %s %s", httpRequest.getHttpMethod(), httpRequest.getPath(), MyServer.stats));


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

}