import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Testing {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        RandomAccessFile random = new RandomAccessFile(new File("Testing/t.a"), "rw");
        FileChannel fc = random.getChannel();
        fc.read(buffer);
        buffer.flip();
        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());

    }
}
