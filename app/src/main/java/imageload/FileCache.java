package imageload;

import android.content.Context;

import java.io.File;

public class FileCache {
    private File cacheDir;
    public FileCache() {
    }
    public File getFile(String url) {
        File f = new File(url);
        return f;
    }
    public void clear() {
        File[] files = cacheDir.listFiles();
        for (File f : files)
            f.delete();
    }
}
