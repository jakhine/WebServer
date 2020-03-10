import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/*
Класс в котором
создается объект сервера ,
далее он передается в конфиг класс,
слушается порт,
создается КлиентСокет, и согласно Реквесту создается Респонс

 */
public class MyServer {
    static final Map<String, AtomicInteger> stats = new ConcurrentHashMap<>();
    private static final HashMap<String, String> typeMapping = new HashMap<>();
    public static final Map<String, String> TYPE_MAPPING = Collections.unmodifiableMap(typeMapping);
    public static String rootFolderPath;
    public static String indexFile;
    private Logger logger = Logger.getLogger(MyServer.class);
    private int localPort;
    private int shutdownPort;
    private ServerSocket socket;
    private ShutdownListener shutdownListener;
    private File statistics;

    public MyServer(String configFilePath) {

        Configuration config = Configuration.loadProperties(configFilePath);
        rootFolderPath = config.getRootFolderPath();
        localPort = Integer.parseInt(config.getLocalPort());
        indexFile = config.getIndexFile();
        shutdownPort = Integer.parseInt(config.getShutdownPort());
        setTypeMapping(config.getMimeMapping());
        Cache.setSize(Integer.parseInt(config.getMaxFileSize()));
        statistics = new File(config.getStatisticsFile());

    }

    public void launch() {
        try {
            createSocket();
            startShutdownListener();

        } catch (Exception e) {
            logger.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
            shutdownListener.setOn(false);
        }
        while (shutdownListener.isOn()) {
            listen();
        }
        logger.info(String.format("Server was shut down at %s", Instant.now()));
        logger.info("number of requests: " + stats);
        writeStatistics();


    }

    private void writeStatistics() {
        try {
            if (statistics.createNewFile()) {
                logger.info(String.format("File created: %s", statistics.getName()));
                Files.write(statistics.toPath(),
                        Collections.singleton(String.format("This file shows number of specific requests \n " +
                                "%s number of requests: %s \n", Instant.now(), stats)), StandardCharsets.UTF_8);
            } else {
                Files.write(statistics.toPath(), Collections.singleton(String.format("%s number of requests: %s", Instant.now(), stats)), StandardOpenOption.APPEND);
                logger.info("File already exists.");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startShutdownListener() throws IOException {
        shutdownListener = new ShutdownListener(shutdownPort);
        shutdownListener.run();//слушает порт 8081 для выключения
    }

    private void createSocket() throws IOException {
        socket = new ServerSocket(localPort);
    }

    private void listen() {
        try {
            RequestHandler requestHandler = new RequestHandler(socket.accept());
            requestHandler.run();
        } catch (IOException e) {
            logger.error("Could not connect ", e);
        }

    }

    private void setTypeMapping(String mimeMapping) {
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
