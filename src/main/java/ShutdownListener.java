import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ShutdownListener implements Runnable {
    private static Logger logger = Logger.getLogger(ShutdownListener.class);

    private boolean isOn = false;
    private ServerSocket sDSocket; //shutdownSocket

    public ShutdownListener(int port) throws IOException {
        try {
            sDSocket = new ServerSocket(port);
            isOn = true;
//            this.run();
        } catch (IOException e) {
            logger.error("Could not create ShutdownListener", e);
            throw new IOException();
        }
    }

    @Override
    public void run() {
        try {
            listenForShutdown();
            logger.info("is shutting down");


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

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
