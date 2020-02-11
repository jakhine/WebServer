
/*
Класс который конфигурирует объект сервера из конфиг.файла


 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public static final Properties property = new Properties();

    //добавляем параметры из файла
    public static void ConfigureServer(MyServer myServer) {

        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            property.load(fis);
            myServer.setRootFolderPath(property.getProperty("rootFolderPath"));
            myServer.setLocalPort(Integer.parseInt(property.getProperty("localPort")));
            myServer.setIndexFile(property.getProperty("indexFile"));
        } catch (IOException e) {
            MyServer.logger.error(String.format("Файл свойств отсуствует! - %s", e));
        }


    }


}
