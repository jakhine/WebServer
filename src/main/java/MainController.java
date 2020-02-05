import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


import java.io.*;
import java.net.*;

public class MainController {

    public static Logger log = Logger.getLogger(MainController.class);
    public static File configFile = new File("C:\\Users\\A650322\\IdeaProjects\\WebServer\\src\\main\\resources\\config.yml"); //определяем файл с конфигами сервера

    public static void main(String[] args) {
        BasicConfigurator.configure(); //TODO донастроить log4j
        try {
            MyServer mss = new MyServer().configureFromFile(configFile);
            log.info(String.format("Server was created with the following parameters %s ", mss));
            mss.createSocket();

            while (true) {
                Socket clientSocket = mss.listen();
                Request request = new Request(clientSocket);
                if (request.path == null) continue;
                log.info(String.format("Connection successful. from address -  %s , and with request - %s", clientSocket.getInetAddress(), request));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                String path = request.path;
                if (path.endsWith("/")) {
                    path = path + "index.html";
                    File file = new File(mss.getRootFolderPath() + path);
                    sendResponse(new Response(request), writer);
                    sendTextHtml(file, writer);
                }
                else if (path.endsWith(".jpg")){
                    sendResponse(new Response(request), writer);
                    File file = new File(mss.getRootFolderPath() + path);
                    sendJpg(file, output);
                }




                output.flush();
                writer.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            log.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
        }
    }

    static void sendTextHtml(File file, PrintWriter writer) throws IOException {
        FileReader fileReader = new FileReader(file);
        log.info(file.getPath());
        char[] buf = new char[256];
        int count = 0;
        while ((count = fileReader.read(buf)) != -1) { //читает посимвольно из файла и пишет в буферный массив символов, возвращает ол-во символов или  -1 когда файл "кончился"
            writer.write(buf, 0, count);
        }

        log.info("file Sent");
    }

    static void sendResponse(Response response, PrintWriter writer) {
        writer.println("HTTP/1.1 200"); //TODO дописать заполнение заголовков ответа
        writer.println("Content-Type: text/html");      //; charset=utf-8"
        log.info("headers Sent");
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
    }

    static void sendJpg(File file, OutputStream output) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        System.out.println(file.getPath());
        byte[] buf = new byte[250];
        int count = 0;
        while ((count = fileInputStream.read(buf)) != -1) {
            output.write(buf, 0, count);
        }

    }
}
