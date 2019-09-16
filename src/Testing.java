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
//        File h = new File("Testing\\a.a");
//        RandomAccessFile a = new RandomAccessFile(h, "rw");
//        FileChannel fc = a.getChannel();
//        ByteBuffer b = ByteBuffer.allocate(4);
//        b.putInt(1500);
//        b.flip();
//        fc.write(b);
//        fc.write(b);
//        Path p = h.toPath();
//        System.out.println(p);
//        byte[] bb = Files.readAllBytes(p);
//        System.out.println(Arrays.toString(bb));

        Set<Pair<Integer, Integer>> a = new TreeSet<>(new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                int firstresult = o1.getFirst().compareTo(o2.getFirst());
                if (firstresult == 0) {
                    return o1.getSecond().compareTo(o2.getSecond());
                }
                return firstresult;
            }
        });

        a.add(new Pair<>(1,2));
        a.add(new Pair<>(1,5));
        a.add(new Pair<>(2,2));
        a.add(new Pair<>(1,1));
        a.add(new Pair<>(1,2));

        System.out.println(a);


    }
}
