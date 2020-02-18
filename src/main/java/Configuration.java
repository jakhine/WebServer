
/*
Класс который конфигурирует объект сервера из конфиг.файла


 */

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static Logger logger = Logger.getLogger(Configuration.class);
    public static final Properties property = new Properties();

    //добавляем параметры из файла
    public static void ConfigureServer(MyServer myServer) {

        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            property.load(fis);
            myServer.setRootFolderPath(property.getProperty("rootFolderPath"));
            myServer.setLocalPort(Integer.parseInt(property.getProperty("localPort")));
            myServer.setIndexFile(property.getProperty("indexFile"));
            myServer.setShutdownPort(Integer.parseInt(property.getProperty("shutdownPort")));
        } catch (IOException e) {
            logger.error(String.format("Файл свойств отсуствует! - %s", e));

        }


    }


}
