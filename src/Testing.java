import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;


public class Testing {
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
