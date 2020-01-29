import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class MySimpleServer {


    //  параметры сервера

    private static String rootCatalog = "/";
    private static String hostname = "localhost";
    private static int port = 80;
    private static HttpServer server;
    private static HttpContext rootContext;
    private static HttpHandler httpHandler =  new MyHttpHandlerImpl();


    public static void main(String[] args) {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port); //создаем соккет
            server = HttpServer.create(inetSocketAddress,0); //создаем сервер и привязываем к нему сокет, (вторым аргументом - максимальное кол-во соединений)
            rootContext =  server.createContext(rootCatalog); // создаем так называемый контекст, адрес
            System.out.println(" rootContext.getPath() = " + rootContext.getPath());

            rootContext.setHandler(httpHandler);  // привязываем обработчик запросов по этому адресу
            server.start(); // запускаем сервер
            System.out.println("Сервер " + server.getAddress() + " запущен");

    }
        catch (Exception e ){
                                System.out.println(e);
                             }
    }
    public static void configureServer (String newRootCatalog){
        //TODO
//        server.removeContext(rootCatalog);
//        rootContext = server.createContext(newRootCatalog);

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
