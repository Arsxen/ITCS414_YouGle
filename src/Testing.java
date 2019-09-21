import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Testing {
    public static void main(String[] args) throws IOException {
//        String poo = readCorpusToText("./Testing/corpus_poo.index");
//        String ton = readCorpusToText("./Testing/corpus_ton.index");
//        System.out.println(poo.compareTo(ton));
        StringBuilder diff = new StringBuilder();
        List<PostingList> ton = readCorpus("./Testing/corpus_ton.index");
        List<PostingList> poo = readCorpus("./Testing/corpus_pooh.index");

        for (int i = 0; i < ton.size(); i++) {
            if (!ton.get(i).getList().equals(poo.get(i).getList())) {
                diff.append("Ton -> TermID: ").append(ton.get(i).getTermId()).append(" -> ").append(ton.get(i).getList()).append("\n");
                diff.append("Pooh -> TermID: ").append(poo.get(i).getTermId()).append(" -> ").append(poo.get(i).getList()).append("\n\n");
            }
        }
        Files.writeString(Paths.get("./Testing/result_com.txt"), diff.toString());
    }

    public static String readCorpusToText(String pathToCorpusFile) throws FileNotFoundException {
        BasicIndex index = new BasicIndex();
        StringBuilder result = new StringBuilder();
        RandomAccessFile corpus = new RandomAccessFile(new File(pathToCorpusFile), "r");
        FileChannel fc = corpus.getChannel();
        result.append("Reading Corpus...\n");
        PostingList pl;
        while ((pl = index.readPosting(fc)) != null) {
            result.append("TermID: ").append(pl.getTermId()).append(" -> ").append(pl.getList()).append("\n");
        }
        return result.toString();
    }

    public static List<PostingList> readCorpus(String pathToCorpusFile) throws FileNotFoundException {
        BasicIndex index = new BasicIndex();
        RandomAccessFile corpus = new RandomAccessFile(new File(pathToCorpusFile), "r");
        FileChannel fc = corpus.getChannel();
        List<PostingList> result = new ArrayList<>();
        PostingList pl;
        while ((pl = index.readPosting(fc)) != null) {
            result.add(pl);
        }
        return result;
    }
}
