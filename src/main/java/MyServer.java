import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/*
Класс в котором
создается объект сервера ,
далее он передается в конфиг класс,
слушается порт,
создается КлиентСокет, и согласно Реквесту создается Респонс

 */
public class MyServer {
    private Logger logger = Logger.getLogger(MyServer.class);
    public static String rootFolderPath;
    private int localPort;
    private int shutdownPort;
    private ServerSocket socket;
    private Listener shutdownListener;
    public static String indexFile;
    private static final HashMap<String, String> typeMapping = new HashMap<>();
    public static final Map<String, String> TYPE_MAPPING = Collections.unmodifiableMap(typeMapping);
    private File statistics = new File("");

    public MyServer(String configFilePath) {

        Configuration config = Configuration.loadProperties(configFilePath);
        rootFolderPath = config.getRootFolderPath();
        localPort = Integer.parseInt(config.getLocalPort());
        indexFile = config.getIndexFile();
        shutdownPort = Integer.parseInt(config.getShutdownPort());
        setTypeMapping(config.getMimeMapping());
        statistics = new File(config.getStatisticsFile());

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
        }


    }


    void startShutdownListener() {
        shutdownListener = new Listener(shutdownPort);
//        Runtime.getRuntime().addShutdownHook(shutdownListener);
        shutdownListener.start();//слушает порт 8081 для выключения
    }

    void createSocket() throws IOException {
        socket = new ServerSocket(localPort);
    }

    void listen() {
        try {
            RequestHandler requestHandler = new RequestHandler(socket.accept());
            requestHandler.start();
        } catch (IOException e) {
            logger.error("Could not connect ", e);
            e.printStackTrace();
        }

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
}
