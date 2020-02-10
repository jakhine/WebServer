import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
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
    Logger logger = Logger.getLogger(MyServer.class);


    private String rootFolderPath;
    private int localPort;
    private ServerSocket socket;
    private Socket clientSocket;


    public static void Start( ){
        BasicConfigurator.configure();
        MyServer myServer = new MyServer();
        Configuration.ConfigureServer(myServer);
        try {
            myServer.createSocket();
            myServer.listen();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
   void createSocket() throws IOException {
       socket = new ServerSocket(localPort);
    }
    void listen () throws IOException{
        clientSocket = this.socket.accept();
    }


    public void setRootFolderPath(String rootFolderPath) {
        this.rootFolderPath = rootFolderPath;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

}
