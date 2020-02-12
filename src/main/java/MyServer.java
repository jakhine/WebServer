import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

/*
Класс в котором
создается объект сервера ,
далее он передается в конфиг класс,
слушается порт,
создается КлиентСокет, и согласно Реквесту создается Респонс

 */
public class MyServer implements Runnable {
    static Logger logger = Logger.getLogger(MyServer.class);

    private String rootFolderPath;
    private int localPort;
    private int shutdownPort;
    private ServerSocket socket;
    private ServerSocket sDSocket; //shutdownSocket
    private Socket clientSocket;
    private String indexFile;
    boolean isOn = true;

    public static void launch() {
        BasicConfigurator.configure();
        MyServer myServer = new MyServer();
        Configuration.ConfigureServer(myServer);
        try {
            Thread thread = new Thread(myServer);
            myServer.createSocket();
            thread.start();
            while (myServer.isOn) {
                myServer.listen();
                try (InputStream input = myServer.clientSocket.getInputStream()) {
                    try (OutputStream output = myServer.clientSocket.getOutputStream()) {                // try-catch with resources
                        try (PrintWriter writer = new PrintWriter(output, true)) {
                            HttpRequest httpRequest = new HttpRequest(input);
                            String path = "" + httpRequest.getPath();
                            if (path.endsWith("/")) path = path + myServer.indexFile;
                            File file = new File(myServer.rootFolderPath + path);
                            myServer.sendResponseWithFile(file, writer, output);

                        }
                    }
                }

            }
        } catch (Exception e) {
            if (myServer.isOn)
                logger.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
            else logger.info(String.format("Server was shut down at %s", Instant.now()));
        }

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

    public void sendResponseWithFile(File file, PrintWriter writer, OutputStream output) {
        HttpResponse response = new HttpResponse();
        if (file.isDirectory()) {
            file = new File(file.getAbsolutePath() + "/" + indexFile);
        }
        if (!file.exists()) {
            response.setStatusCode("404 NOT FOUND");
            sendResponse(response, writer);
            return;
        }

        if (file.getName().contains(".")) {
            String filename = file.getName();
            String fileExtension = filename.substring(filename.lastIndexOf("."));
            response.setContentType(fileExtension);
        }

        sendResponse(response, writer);

        sendFile(file, output);
    }


    void sendFile(File file, OutputStream output) {
        try (FileInputStream fileInputStream = new FileInputStream(file);
        ) {
            byte[] buf = new byte[250];
            int count = 0;
            while ((count = fileInputStream.read(buf)) != -1) {
                output.write(buf, 0, count);
            }
            logger.info(String.format("File %s sent", file.getPath()));
        } catch (IOException e) {

            logger.error(String.format("Could not send file: %s", e.getMessage()), e);
        }
    }

    void createSocket() throws IOException {
        socket = new ServerSocket(localPort);
        sDSocket = new ServerSocket(shutdownPort);

    }

    @Override
    public void run() {
        try {
            listenForShutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void listen() throws IOException {
        clientSocket = this.socket.accept();
        logger.info(String.format("connection from address+ %s", clientSocket.getInetAddress()));
    }

    void listenForShutdown() throws IOException {

        while (isOn) {
            Socket shdwnSckt = sDSocket.accept();
            logger.info(String.format("shutDownSocket connected  %s", shdwnSckt.getInetAddress()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(shdwnSckt.getInputStream()));
            if (reader.readLine().contains("shutdown")) {
                isOn = false;
                logger.info(String.format("Server was shut down at %s", Instant.now()));
            }
        }
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

    public String getIndexFile() {
        return indexFile;
    }
}
