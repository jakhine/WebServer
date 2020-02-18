import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Application {

   private Logger logger = Logger.getLogger(Application.class);
    /*
    Класс для запуска и остановки сервера


     */

    public static void main(String[] args) {
        BasicConfigurator.configure();
        MyServer myServer = new MyServer();
        myServer.launch();
   }

}
