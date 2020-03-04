/*
Класс который конфигурирует объект сервера из конфиг.файла


 */

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final Logger logger = Logger.getLogger(Configuration.class);


    private static final Properties DEFAULT_PROPERTIES = new Properties();

    static {
        DEFAULT_PROPERTIES.setProperty("rootFolderPath", "c:\\www");
        DEFAULT_PROPERTIES.setProperty("localPort", "8888");
        DEFAULT_PROPERTIES.setProperty("shutdownPort", "8889");
        DEFAULT_PROPERTIES.setProperty("indexFile", "index.html");
        DEFAULT_PROPERTIES.setProperty("content-type", "txt:text/plain");
        DEFAULT_PROPERTIES.setProperty("statisticsFile", "c:\\www\\stats.txt");
        DEFAULT_PROPERTIES.setProperty("maxFileSize", "500");


    }

    private final Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }

    public static Configuration loadProperties(String configFilePath) {
        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            Properties p = new Properties();
            p.load(fis);
            logger.info(String.format("Loaded properties: %s", p));
            return new Configuration(p);
        } catch (IOException e) {
            logger.error(String.format("Файл свойств отсуствует! Будут установлены значения по умолчанию - %s", e));
            return new Configuration(DEFAULT_PROPERTIES);
        }
    }
    //добавляем параметры из файла

    public String getRootFolderPath() {
        return properties.getProperty("rootFolderPath", DEFAULT_PROPERTIES.getProperty("rootFolderPath"));
    }

    public String getLocalPort() {
        return properties.getProperty("localPort", DEFAULT_PROPERTIES.getProperty("localPort"));
    }

    public String getShutdownPort() {
        return properties.getProperty("shutdownPort", DEFAULT_PROPERTIES.getProperty("shutdownPort"));
    }

    public String getIndexFile() {
        return properties.getProperty("indexFile", DEFAULT_PROPERTIES.getProperty("indexFile"));
    }

    public String getMimeMapping() {
        return properties.getProperty("content-type", DEFAULT_PROPERTIES.getProperty("content-type"));
    }

    public String getStatisticsFile() {
        return properties.getProperty("statisticsFile", DEFAULT_PROPERTIES.getProperty("statisticsFile"));
    }

    public String getMaxFileSize() {
        return properties.getProperty("maxFileSize", DEFAULT_PROPERTIES.getProperty("statisticsFile"));
    }


}
