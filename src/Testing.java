import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Testing {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(20);
//        buffer.putInt(5);
//        buffer.putInt(1);
//        buffer.flip();
        RandomAccessFile random = new RandomAccessFile(new File("Testing/t.a"), "rw");
        FileChannel fc = random.getChannel();
//        fc.write(buffer);
        fc.read(buffer);
        buffer.flip();
        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());
    }
}
