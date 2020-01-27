import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class MySimpleServer {

    public static void main(String[] args) {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 80); //создаем соккет
            HttpServer server = HttpServer.create(inetSocketAddress,0); //создаем сервер и привязываем к нему сокет, (вторым аргументом - максимальное кол-во соединений)
            HttpContext context =  server.createContext("/"); // создаем так называемый контекст
            context.setHandler(new HttpHandlerImpl());  //обработчик запросов
            server.start(); // запускаем сервер
    }
        catch (Exception e ){
                                System.out.println(e);
                             }
    }

}
