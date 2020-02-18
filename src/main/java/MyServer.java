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
public class MyServer   {
    private Logger logger = Logger.getLogger(MyServer.class);
    private String rootFolderPath;
    private int localPort;
    private int shutdownPort;
    private ServerSocket socket;
    private ServerSocket sDSocket; //shutdownSocket
    private Socket clientSocket;
    private String indexFile;
    boolean isOn = true;

    public void launch() { // //TODO all that stuff goes in listener


        Configuration.ConfigureServer(this);
        try {
//            Thread thread = new Thread(myServer);//TODO
            createSocket();
            Listener shutdownListener = new Listener(shutdownPort);
            shutdownListener.start();//слушает порт 8081 для выключения
            while (shutdownListener.isOn()) {
                listen();

                //Create request object
                //Analyze request
                //Create response
                //Write response

                HttpRequest httpRequest = new HttpRequest(clientSocket);
                logger.info(httpRequest);
                File file = new File(rootFolderPath + httpRequest.getPath());
                HttpResponse httpResponse = new HttpResponse(clientSocket);
                httpResponse.setFile(file);


            }
        } catch (Exception e) {
            if (isOn)
                logger.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
            else logger.info(String.format("Server was shut down at %s", Instant.now()));
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

    public void setShutdownPort(int shutdownPort) {
        this.shutdownPort = shutdownPort;
    }

}
