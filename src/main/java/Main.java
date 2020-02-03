import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


import java.io.*;
import java.net.*;

public class Main {

    public static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        BasicConfigurator.configure(); //TODO донастроить log4j
        try {
            File configFile = new File("C:\\Users\\A650322\\IdeaProjects\\WebServer\\src\\main\\resources\\config.yml"); //определяем файл с конфигами сервера
            MySimpleServer mss = objectMapper.readValue(configFile, MySimpleServer.class);          //присваиваем значения
            File rootDir = new File(mss.getRootFolder()); //определяем корневой каталог
            log.info(String.format("Server was created with the following parameters %s ", mss));
            mss.createSocket();

            while (true) {
                Socket clientSocket = mss.listen();
                log.info(String.format("Connection successful. from address -  %s ", clientSocket.getInetAddress()));
                InputStream input = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = reader.readLine();
                if (line == null) {
                    continue;
                } else {
                    OutputStream output = clientSocket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    // String httpMethod = line.split(" ")[0];

                    String addr = line.split(" ")[1];

                    writer.println("HTTP/1.1 200 OK"); //TODO дописать заполнение заголовков ответа
//                    writer.println("Content-Type: text/html; charset=utf-8");
//                    output.write(new FileOutputStream(new File(mss.getRootFolder()+uriAddr)));
//                    clientSocket.close();
                }
                log.info(String.format("address -  %s ", line));
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
}
