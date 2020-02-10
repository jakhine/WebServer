
    /*
    Класс который конфигурирует объект сервера из конфиг.файла


     */

    import org.apache.log4j.BasicConfigurator;
    import org.apache.log4j.Logger;

    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.net.ServerSocket;
    import java.net.Socket;
    import java.util.Properties;

    public class Configuration {
        static Logger logger = Logger.getLogger(Configuration.class);





    //превращаем конфиг файл в класс с полями всех конфигов?

    //добавляем параметры из файла
    public static void ConfigureServer (MyServer myServer){
        BasicConfigurator.configure();
        Properties property = new Properties();
        property.getProperty("content-type");
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            property.load(fis);
            myServer.setRootFolderPath(property.getProperty("rootFolderPath"));
            myServer.setLocalPort(Integer.parseInt (property.getProperty("localPort")));


        } catch (IOException e) {
            logger.error(String.format ("Файл свойств отсуствует! - %s",e));
        }


    }


}
