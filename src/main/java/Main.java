import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {

    public static Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            BasicConfigurator.configure(); //TODO донастроить log4j
        try {

            String configFilePath = "C:\\Users\\A650322\\IdeaProjects\\WebServer\\src\\main\\resources\\config.yml"; // путь к файлу с конфигами сервера
            MySimpleServer mss = objectMapper.readValue(new File(configFilePath), MySimpleServer.class);
            log.info(String.format("Server was created with the following parameters %s ", mss));
            mss.createSocket(8080);
            Socket clientSocket = mss.listen();
            log.info(String.format("Connection successful. from address -  %s ", clientSocket.getInetAddress()));
            //TODO


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
