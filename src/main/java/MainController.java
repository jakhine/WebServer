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
                HttpRequest request = new HttpRequest(clientSocket);
                if (request.path == null) continue;
                log.info(String.format("Connection from address -  %s , and with request - %s", clientSocket.getInetAddress(), request));
                OutputStream output = clientSocket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                String path = request.path;

                if (path.endsWith("/")) {
                    path = path + "index.html";
                }
                File file = new File(myServer.getRootFolderPath() + path);
                HttpResponse httpResponse = new HttpResponse();

                if (!file.exists()) {
                    httpResponse.setStatusCode("404 NOT FOUND");
                    sendResponse(httpResponse, writer);
                }
                httpResponse.setStatusCode("200 OK");

                if (file.getPath().endsWith(".html") || file.getPath().endsWith(".txt")) {
                    httpResponse.setHeaders("Content-Type: ", "text/html");
                    sendResponse(httpResponse, writer);
                    sendTextHtml(file, writer);
                }

                if (file.getPath().endsWith(".jpg")) {
                    httpResponse.setHeaders("Content-Type: ", "image/jpeg");
                    sendResponse(httpResponse, writer);
                    sendJpg(file, output);
                }

                if (file.isDirectory()) {
                    sendResponse(httpResponse, writer);
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

    static void sendResponse(HttpResponse httpResponse, PrintWriter writer) {
        writer.println(httpResponse.getHeadLine());
        writer.println(httpResponse.getHeaders().entrySet());      //; charset=utf-8"
        log.info(String.format("headers %s Sent", httpResponse.getHeaders()));
        writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
    }

    static void sendTextHtml(File file, PrintWriter writer) throws IOException {
        try (FileReader fileReader = new FileReader(file)) {
            char[] buf = new char[256];
            int count = 0;
            while ((count = fileReader.read(buf)) != -1) { //читает посимвольно из файла и пишет в буферный массив символов, возвращает ол-во символов или  -1 когда файл "кончился"
                writer.write(buf, 0, count);
            }
        }
        log.info(String.format("file %s Sent", file.getPath()));
    }

    static void sendJpg(File file, OutputStream output) throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            System.out.println(file.getPath());
            byte[] buf = new byte[250];
            int count = 0;
            while ((count = fileInputStream.read(buf)) != -1) {
                output.write(buf, 0, count);
            }
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
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
