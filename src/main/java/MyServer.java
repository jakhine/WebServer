import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {


    //  параметры сервера
    private String rootFolderPath;
    private int localPort;
    private String indexFile;
    private ServerSocket socket;
    private Socket clientSocket;


    public MyServer configureFromFile(File configFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(configFile, MyServer.class);          //присваиваем значения
    }


    public MyServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                socket.close();
            } catch (IOException e) {
                //Log Error
            }
        }));
    }


    public void createSocket() throws IOException {
        socket = new ServerSocket(localPort);
    }

    public Socket listen() throws IOException {
        return this.socket.accept();
    }

    public String getRootFolderPath() {
        return rootFolderPath;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getIndexFile() {

        return indexFile;
    }

    @Override
    public String toString() {
        return String.format("rootFolderPath - %s, port - %s, indexFile - %s", rootFolderPath, localPort, indexFile);

    }



}
