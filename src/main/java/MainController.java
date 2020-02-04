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


                writer.println("HTTP/1.1 200 OK"); //TODO дописать заполнение заголовков ответа
                writer.println("Content-Type: text/html");      //; charset=utf-8"
                writer.println();                               // пустая строка, сигнализирующая об окончании контента запроса
                //
                if (path.endsWith("/")) {
                    path = path + "index.html";
                    File file = new File(mss.getRootFolderPath() + path);

                    FileReader fileReader = new FileReader(file);
                    char[] buf = new char[256];
                    int count = 0;
                    while ((count = fileReader.read(buf)) != -1) { //читает посимвольно из файла и пишет в буферный массив символов, возвращает ол-во символов или  -1 когда файл "кончился"
                        writer.write(buf, 0, count);
                    }
                }
                writer.close();
                clientSocket.close();


                log.info(String.format("address -  %s ", request.path));
                //TODO
            }


        } catch (IOException e) {
            log.error(String.format("Could not create and start web server: %s", e.getMessage()), e);
        }


//        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port); //создаем соккет
//        server = HttpServer.create(inetSocketAddress, 0); //создаем сервер и привязываем к нему сокет, (вторым аргументом - максимальное кол-во соединений)
//        rootContext = server.createContext(rootFolder); // создаем так называемый контекст, адрес
//        System.out.println(" rootContext.getPath() = " + rootContext.getPath());
//
//        rootContext.setHandler(httpHandler);  // привязываем обработчик запросов по этому адресу
//        server.start(); // запускаем сервер
//        log.info(String.format("Сервер %s запущен", server.getAddress()));
    }

    public static String getRequestAddress(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        return reader.readLine();
    }

}
