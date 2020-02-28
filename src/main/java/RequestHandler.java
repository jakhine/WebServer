import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.Socket;


public class RequestHandler extends Thread {
    private Logger logger = Logger.getLogger(RequestHandler.class);
    private Socket clientSocket;
    private String rootFolderPath = MyServer.rootFolderPath;
    private String indexFile = MyServer.indexFile;
    private File file;
    private volatile static  int counter = 0;



    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        logger.info(String.format("connection from address+ %s", clientSocket.getInetAddress()));
    }

    @Override
    public  void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) { // здесь открываю потоки так как закрытие потоков закрывает весь сокет
            HttpRequest httpRequest = createRequest(reader);                 //Create request object
            analyzeRequest(httpRequest);                                     //Analyze request
            HttpResponse httpResponse = createResponse();                     //Create response
            httpResponse.writeResponse(clientSocket);                        //Write response
        } catch (Exception e) {
            logger.error(String.format("Could not send response  %s", e.getMessage()), e);
            HttpResponse.HTTP_404.writeResponse(clientSocket);
        }
    }

    private synchronized void analyzeRequest(HttpRequest httpRequest) {
        counter++;
        logger.info(httpRequest.getHttpMethod() + " " + counter);
        ;

        file = new File(rootFolderPath + httpRequest.getPath());
        if (file.isDirectory()) {
            file = new File(file.getAbsolutePath() + "/" + indexFile);
        }
    }

     HttpRequest createRequest(BufferedReader reader) throws Exception {
        HttpRequest req = new HttpRequest(reader);
        logger.info(String.format("HttpRequest was created %s", req));
        return req;
    }

    private  HttpResponse createResponse() {
        if (file == null) {
            return HttpResponse.HTTP_404;
        } else {
            HttpResponse httpResponse = new HttpResponse();
            httpResponse.setFile(file);
            httpResponse.setStatusCode("200");
            String fileExtension = getFileExtension(file); // добываем расширение файла
            httpResponse.setContentType(fileExtension);
            logger.info(String.format("httpResponse was created %s ", httpResponse));
            return httpResponse;
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