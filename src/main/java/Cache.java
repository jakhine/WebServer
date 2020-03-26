import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private static int size;
    private static final Logger logger = Logger.getLogger(Cache.class);
    private static final Map<String, byte[]> cache = new ConcurrentHashMap<>();

    public synchronized static byte[] getFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        if (bytes.length > size)
            return bytes;
        else return cache.computeIfAbsent(file.getPath(), k -> {
            logger.info("cache added");
            return bytes;
        });
    }

    public static void setSize(int size) {
        Cache.size = size * 1000;
    }
}

