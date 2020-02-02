import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;


public class MyHttpHandlerImpl implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        System.out.println(" .getRequestURI()" + httpExchange.getRequestURI());

        switch (method) {
            case "GET": {
                //конфигурирование сервера осущуствляется через параметры, переданные в URI  ?
                // ?rootcatalog="/root"

                System.out.println("Http метод GET");
                httpExchange.sendResponseHeaders(200, 1);
                URI requestURI = httpExchange.getRequestURI();
                String query = requestURI.getQuery();
                String[] params = query.split("&");
//                MySimpleServer.configureServer(params[0].split("=")[1]);
                break;
            }
            default: {
                System.out.println("метод не найден");
                httpExchange.sendResponseHeaders(501, 0);
            }
        }


    }
}
