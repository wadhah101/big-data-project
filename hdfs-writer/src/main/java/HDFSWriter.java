import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HDFSWriter {
    private final FileSystem fs;
    private final String targetPath;

    public HDFSWriter(URI uri, String targetPath) throws URISyntaxException, IOException {
        this.fs = FileSystem.get(uri, new Configuration());
        this.targetPath = targetPath;
    }

    public void write(String value) throws IOException {
        FSDataOutputStream out = fs.create(new Path(targetPath));
        out.writeUTF(value);
        out.close();
    }

    public void close() throws Exception{
        fs.close();
    }

}
