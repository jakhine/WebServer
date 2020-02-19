import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/*
Класс в котором
создается объект сервера ,
далее он передается в конфиг класс,
слушается порт,
создается КлиентСокет, и согласно Реквесту создается Респонс

 */
public class MyServer {
    private Logger logger = Logger.getLogger(MyServer.class);
    private String rootFolderPath;
    private int localPort;
    private int shutdownPort;
    private ServerSocket socket;
    private Socket clientSocket;
    private Listener shutdownListener;
    private String indexFile;
    private File file;
    private static final HashMap<String, String> typeMapping = new HashMap<>();
    public static final Map<String, String> TYPE_MAPPING = Collections.unmodifiableMap(typeMapping);


    public MyServer(String configFilePath) {
        if (Configuration.loadProperties(configFilePath)) {
            Properties properties = Configuration.getProperties();
            logger.info(properties);
            rootFolderPath = properties.getProperty("rootFolderPath");
            localPort = Integer.parseInt(properties.getProperty("localPort"));
            indexFile = properties.getProperty("indexFile");
            shutdownPort = Integer.parseInt(properties.getProperty("shutdownPort"));
            setTypeMapping(properties.getProperty("content-type"));
        } else setDefaultValues();

    }

    public void launch() {
        try {
            createSocket();
            startShutdownListener();
        } catch (Exception e) {
            logger.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
            e.printStackTrace();
            shutdownListener.setOn(false);
        }
        while (shutdownListener.isOn()) {
            listen();
            try (InputStream input = clientSocket.getInputStream()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) { // здесь открываю потоки так как закрытие потоков закрывает весь сокет
                    HttpRequest httpRequest = createRequest(reader);                 //Create request object
                    analyzeRequest(httpRequest);                                     //Analyze request
                    HttpResponse httpResponse = createResponse();                     //Create response
                    httpResponse.writeResponse(clientSocket);                        //Write response
                }
            } catch (Exception e) {
                logger.error(String.format("Could not send response  %s", e.getMessage()), e);
                send404Response();
            }
        }


    }

    void analyzeRequest(HttpRequest httpRequest) {
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

    HttpResponse createResponse() {
        HttpResponse httpResponse = new HttpResponse();
        if (file == null) {
            httpResponse.setStatusCode("404 NOT FOUND");
        } else {
            httpResponse.setFile(file);
            httpResponse.setStatusCode("200");
            String fileExtension = getFileExtension(file); // добываем расширение файла
            httpResponse.setContentType(fileExtension);
        }
        logger.info(String.format("httpResponse was created %s ", httpResponse));
        return httpResponse;
    }

    void startShutdownListener() {
        shutdownListener = new Listener(shutdownPort);
//        Runtime.getRuntime().addShutdownHook(shutdownListener);
        shutdownListener.start();//слушает порт 8081 для выключения
    }

    void createSocket() throws IOException {
        socket = new ServerSocket(localPort);
    }

    String getFileExtension(File file) {
        String fileExtension = "";
        if (file.getName().contains(".")) {         // добываем расширение файла
            String filename = file.getName();
            fileExtension = filename.substring(filename.lastIndexOf("."));
        }
        return fileExtension;
    }

    void listen() {
        try {
            clientSocket = this.socket.accept();
            logger.info(String.format("connection from address+ %s", clientSocket.getInetAddress()));
        } catch (IOException e) {
            logger.error(String.format("Could not connect from address+ %s", clientSocket.getInetAddress()), e);
            e.printStackTrace();
        }

    }

    public void setDefaultValues() {
        rootFolderPath = "c:\\www";   //Default values
        localPort = 8888;                //Default values
        shutdownPort = 8889;             //Default values
    }

    public void setTypeMapping(String mimeMapping) {
        if (mimeMapping != null) {
            for (String pair : mimeMapping.split(" ")) {
                String extension = pair.split(":")[0];
                String content_type = pair.split(":")[1];
                typeMapping.put(extension, content_type);
            }
            logger.info("headers were added " + typeMapping);
        } else logger.info("headers were not added " + typeMapping);
    }

    public void send404Response() {
        HttpResponse response = createResponse();
        response.setStatusCode("404");
        response.writeResponse(clientSocket);


    }

    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }


    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setIndexFile(String indexFile) {
        this.indexFile = indexFile;
    }

    public void setShutdownPort(int shutdownPort) {
        this.shutdownPort = shutdownPort;
    }

}
