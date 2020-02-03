import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;


public class MySimpleServer {




    //  параметры сервера
    private  String rootFolder;
    private  int localPort;
    private String indexFile;
    private ServerSocket socket ;
    private Socket clientSocket;


    public MySimpleServer() {
    }

    public MySimpleServer(String rootFolder, int localPort, String indexFile) {
        this.rootFolder = rootFolder;
        this.localPort = localPort;
        this.indexFile = indexFile;

    }

    public void createSocket() throws IOException {
            socket = new ServerSocket(localPort);

    }

    public Socket listen() throws IOException {
        return this.socket.accept();

    }
    // Accept() connection
public String getRootFolder() {
         return rootFolder;
}

    public int getLocalPort() {
        return localPort;
    }

    public String getIndexFile() {

        return indexFile;
    }

    @Override
    public String toString() {
        return "rootFolder  - " + rootFolder + " port - " + localPort + " indexFile - " + indexFile;

    }

    public static void configure() {
        //TODO
    }

}
