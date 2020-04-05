public class HttpRequestHandlerImpls {

    //        HttpRequestHandler thePath = HttpRequestHandler.class.getAnnotations().
    @HttpRequestHandler("/hello")
    IHttpRequestHandler helloServlet = request -> {
        HttpResponse httpResponse = new HttpResponse(200, "text/html");
        httpResponse.setBody("<h1>Hello from Servlet</h1>");
        return httpResponse;
    };
}
