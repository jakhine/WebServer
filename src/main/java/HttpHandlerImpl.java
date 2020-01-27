import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class HttpHandlerImpl implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        switch (method) {
                case "GET":
                        {
                            System.out.println("Http метод GET");
                            httpExchange.sendResponseHeaders(200,0);
                        }
                        break;

                default :
                        {
                            System.out.println("метод не найден");
                            httpExchange.sendResponseHeaders(501,0);
                        }
        }


    }
}
