import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
Класс в котором
создается объект сервера ,
далее он передается в конфиг класс,
слушается порт,
создается КлиентСокет, и согласно Реквесту создается Респонс

 */
public class MyServer {
    static Logger logger = Logger.getLogger(MyServer.class);

    private String rootFolderPath;
    private int localPort;
    private ServerSocket socket;
    private Socket clientSocket;
    private String indexFile;


    public static void launch() {
        BasicConfigurator.configure();
        MyServer myServer = new MyServer();
        Configuration.ConfigureServer(myServer);
        try {
            myServer.createSocket();
            while (true) {
                myServer.listen();
                try (InputStream input = myServer.clientSocket.getInputStream();
                     OutputStream output = myServer.clientSocket.getOutputStream();
                     PrintWriter writer = new PrintWriter(output, true);
                ) { // try-catch with resources
                    HttpRequest httpRequest = new HttpRequest(input);
                    HttpResponse response = new HttpResponse();
                    String path = httpRequest.getPath();
                    if (path.endsWith("/")) path = path + myServer.indexFile;
                    File file = new File(myServer.rootFolderPath + path);
                    myServer.sendResponseWithFile(response, file, writer, output);
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
        }

    }

    public void sendResponse(HttpResponse response, PrintWriter writer) {

        writer.println(response.getHeadLine());
        writer.println(response.getContentType());
        logger.info(String.format("headers %s Sent", response.getHeadLine()));
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса

//        catch () {
//            logger.error(String.format("Could not send response: %s", e.getMessage()), e);
//        }
    }

    public void sendResponseWithFile(HttpResponse response, File file, PrintWriter writer, OutputStream output) {
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
            logger.info(String.format("File %s sent",file.getPath()));
        } catch (IOException e) {
            logger.error(String.format("Could not send file: %s", e.getMessage()), e);
        }
    }

    void createSocket() throws IOException {
        socket = new ServerSocket(localPort);
    }

    void listen() throws IOException {
        clientSocket = this.socket.accept();
        logger.info(String.format("connection from address+ %s", clientSocket.getInetAddress()));
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

    public String getIndexFile() {
        return indexFile;
    }


}
