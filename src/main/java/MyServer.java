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
                HttpRequest httpRequest = new HttpRequest(myServer.clientSocket);
                HttpResponse response = new HttpResponse();
                String path = httpRequest.getPath();
                if (path.endsWith("/")) path = path + myServer.indexFile;
                File file = new File(myServer.rootFolderPath + path);
                myServer.sendResponseWithFile(response,file);
            }
        } catch (Exception e) {
            logger.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
        }

    }
    public void sendResponse(HttpResponse response)  {
        try(PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);){
            writer.println(response.getHeadLine());
            writer.println(response.getContentType());
            logger.info(String.format("headers %s Sent", response.getHeadLine()));
            writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
        }
        catch (IOException e) {
            logger.error(String.format("Could not send response: %s", e.getMessage()),e);
        }
    }

    public void sendResponseWithFile( HttpResponse response , File file){
        if (!file.exists()) {
            response.setStatusCode("404 NOT FOUND");
            sendResponse(response);
            return;
        }
        if (file.getName().contains(".")){
            String filename = file.getName();
            String fileExtension = filename.substring(filename.lastIndexOf("."));
            response.setContentType(fileExtension);
        }
        sendResponse(response);
        sendFile(file);
            }


    void sendFile(File file){
        try (FileInputStream fileInputStream = new FileInputStream(file);
             OutputStream output = clientSocket.getOutputStream()) {
            logger.info((file.getPath()));
            byte[] buf = new byte[250];
            int count = 0;
            while ((count = fileInputStream.read(buf)) != -1) {
                output.write(buf, 0, count);
            }
        } catch (IOException e) {
            logger.error(String.format("Could not send file: %s", e.getMessage()),e);
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
