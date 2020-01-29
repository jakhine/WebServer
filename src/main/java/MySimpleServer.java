import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class MySimpleServer {


    //  параметры сервера

    private static String rootCatalog = "/";
    private static String hostname = "localhost";
    private static int port = 80;


    public static void main(String[] args) {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port); //создаем соккет
            HttpServer server = HttpServer.create(inetSocketAddress,0); //создаем сервер и привязываем к нему сокет, (вторым аргументом - максимальное кол-во соединений)
            HttpContext rootContext =  server.createContext(rootCatalog); // создаем так называемый контекст, адрес
            rootContext.setHandler(new HttpHandlerImpl());  // привязываем обработчик запросов по этому адресу



            server.start(); // запускаем сервер
    }
        catch (Exception e ){
                                System.out.println(e);
                             }
    }



    public static void setRootCatalog(String rootCatalog) {
        MySimpleServer.rootCatalog = rootCatalog;
    }

    public static void setHostname(String hostname) {
        MySimpleServer.hostname = hostname;
    }

    public static void setPort(int port) {
        MySimpleServer.port = port;
    }

}
