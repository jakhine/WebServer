import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private static final Logger logger = Logger.getLogger(Cache.class);
    private static Map<String, byte[]> cache = new ConcurrentHashMap<>();
    public static byte[] getFile(File file) throws IOException {
        return cache.computeIfAbsent(file.getPath(), k -> {
            try {
                logger.info("cache added");
                return Files.readAllBytes(file.toPath());

            } catch (IOException e) {
                return null;

            }
        });
    }
}

