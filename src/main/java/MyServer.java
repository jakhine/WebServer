import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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


    public static void launch() {
        BasicConfigurator.configure();
        MyServer myServer = new MyServer();
        Configuration.ConfigureServer(myServer);
        try {
            myServer.createSocket();
            while (true) {
                myServer.listen();
                HttppRequest httppRequest = new HttppRequest(myServer.clientSocket);
                HttpResponse response = new HttpResponse();
                String path = httppRequest.getPath();
                if (path.endsWith("/")) path = path+ "index.html";
                File file = new File(myServer.rootFolderPath + path);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void sendFile(File file, OutputStream output) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            logger.info((file.getPath()));
            byte[] buf = new byte[250];
            int count = 0;
            while ((count = fileInputStream.read(buf)) != -1) {
                output.write(buf, 0, count);
            }
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

}
