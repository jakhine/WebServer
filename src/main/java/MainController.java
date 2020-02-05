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
            MyServer myServer = new MyServer().configureFromFile(configFile);
            log.info(String.format("Server was created with the following parameters %s ", myServer));
            myServer.createSocket();

            while (true) {
                Socket clientSocket = myServer.listen();
                Request request = new Request(clientSocket);
                if (request.path == null) continue;
                log.info(String.format("Connection from address -  %s , and with request - %s", clientSocket.getInetAddress(), request));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                String path = request.path;

                if (path.endsWith("/")) {
                    path = path + "index.html";
                }
                File file = new File(myServer.getRootFolderPath() + path);
                Response response = new Response();

                if (!file.exists()) {
                    response.setStatusCode("404 NOT FOUND");
                    sendResponse(response, writer);
                }
                response.setStatusCode("200 OK");

                if (file.getPath().endsWith(".html") || file.getPath().endsWith(".txt")) {
                    response.setHeaders("Content-Type: ", "text/html");
                    sendResponse(response, writer);
                    sendTextHtml(file, writer);
                }

                if (file.getPath().endsWith(".jpg")) {
                    response.setHeaders("Content-Type: ", "image/jpeg");
                    sendResponse(response, writer);
                    sendJpg(file, output);
                }

                if (file.isDirectory()) {
                    sendResponse(response, writer);
                    sendList(file, writer);
                }

                output.flush();
                writer.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            log.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
        }
    }

    static void sendResponse(Response response, PrintWriter writer) {
        writer.println(response.getHeadLine());
        writer.println(response.getHeaders().entrySet());      //; charset=utf-8"
        log.info(String.format("headers %s Sent", response.getHeaders()));
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
    }

    static void sendTextHtml(File file, PrintWriter writer) throws IOException {
        FileReader fileReader = new FileReader(file);
        char[] buf = new char[256];
        int count = 0;
        while ((count = fileReader.read(buf)) != -1) { //читает посимвольно из файла и пишет в буферный массив символов, возвращает ол-во символов или  -1 когда файл "кончился"
            writer.write(buf, 0, count);
        }
        log.info(String.format("file %s Sent", file.getPath()));
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

    static void sendList(File file, PrintWriter writer) {
        StringBuilder sb = new StringBuilder();
        if (file.list().length == 0) {
            sb.append("The folder is empty");
        } else {
            for (String s : file.list()) {
                sb.append(String.format("<a href=\"%s\\%s\"> %s </a> <br> ", file.getName(), s, s));
            }
        }
        writer.write(sb.toString());
    }
}
