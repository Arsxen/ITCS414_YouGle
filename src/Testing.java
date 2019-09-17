import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class Testing {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        RandomAccessFile random = new RandomAccessFile(new File("Testing/t.a"), "rw");
        FileChannel fc = random.getChannel();
        for (int i = 0; i < 3; i++) {
            System.out.println(fc.read(buffer));
            buffer.clear();
        }
    }
}
