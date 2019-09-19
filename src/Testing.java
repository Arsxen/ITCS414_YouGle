import java.io.File;
import java.io.FileNotFoundException;
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

    public static String readCorpus(String pathToCorpusFile) throws FileNotFoundException {
        BasicIndex index = new BasicIndex();
        StringBuilder result = new StringBuilder();
        RandomAccessFile corpus = new RandomAccessFile(new File(pathToCorpusFile), "r");
        FileChannel fc = corpus.getChannel();
        result.append("Reading Corpus...\n");
        PostingList pl;
        while ((pl = index.readPosting(fc)) != null) {
            result.append("TermID: ").append(pl.getTermId()).append(" -> ").append(pl.getList());
        }
        result.append("\n");
        return result.toString();
    }
}
