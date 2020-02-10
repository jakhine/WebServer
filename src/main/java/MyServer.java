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
    private String rootFolderPath;
    private int localPort;
    private ServerSocket socket;
    private Socket clientSocket;


    public static void Start(String configFilePath){
        MyServer myServer = new MyServer();
        Configuration.ConfigureServer(myServer, configFilePath);
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
    Socket listen () throws IOException{
        return this.socket.accept();
    }

}
