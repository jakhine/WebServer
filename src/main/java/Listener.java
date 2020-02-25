import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;

public class Listener extends Thread {
    private Logger logger = Logger.getLogger(Listener.class);

    private boolean isOn = true;
    private ServerSocket sDSocket; //shutdownSocket

    public Listener(int port) {
        try {
            sDSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            listenForShutdown();
            logger.info("is shutting down");

            logger.info(String.format("Server was shut down at %s", Instant.now()));

        } catch (IOException e) {
            logger.error(e);
        }
    }

    void listenForShutdown() throws IOException {
        while (isOn) {

            Socket shdwnSckt = sDSocket.accept();
            logger.info(String.format("shutDownSocket connected  %s", shdwnSckt.getInetAddress()));
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(shdwnSckt.getInputStream()))) {
                if (reader.readLine().contains("shutdown")) {
                    isOn = false;
                }
            }
        }
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public boolean isOn() {
        return isOn;
    }
}
