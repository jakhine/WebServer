
/*
Класс который конфигурирует объект сервера из конфиг.файла


 */

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static Logger logger = Logger.getLogger(Configuration.class);
    private static final Properties property = new Properties();

    public static boolean loadProperties(String configFilePath) {
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            property.load(fis);
            return true;
        } catch (IOException e) {
            logger.error(String.format("Файл свойств отсуствует! Будут установлены значения по умолчанию - %s", e));
            return false;
        }
    }
    //добавляем параметры из файла

    public static Properties getProperties (){
        return  new Properties(property);
    }


}
