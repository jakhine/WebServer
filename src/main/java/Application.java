import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Application {

   private static Logger logger = Logger.getLogger(Application.class);
    /*
    Класс для запуска и остановки сервера
     */

    public static void main(String[] args) {
        BasicConfigurator.configure();
        String configFilePath = "src/main/resources/config.properties";
        MyServer myServer = new MyServer(configFilePath);
        myServer.launch();
   }

}
