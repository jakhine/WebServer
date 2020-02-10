
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
        public static Properties property = new Properties();

    //добавляем параметры из файла
    public static void ConfigureServer (MyServer myServer){
        BasicConfigurator.configure();

        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            property.load(fis);
            myServer.setRootFolderPath(property.getProperty("rootFolderPath"));
            myServer.setLocalPort(Integer.parseInt (property.getProperty("localPort")));
        } catch (IOException e) {
            MyServer.logger.error(String.format ("Файл свойств отсуствует! - %s",e));
        }


    }


}
