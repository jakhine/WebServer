public class HttpRequestHandlerImpl implements IHttpRequestHandler {
    @Override
    @HttpRequestHandler ("app/example")
    public HttpResponse process(HttpRequest request) {
        return null;
    }
}
