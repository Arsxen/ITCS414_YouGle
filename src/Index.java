

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class Index {

	// Term id -> (d) dictionary
	private static Map<Integer, Pair<Long, Integer>> postingDict 
		= new TreeMap<Integer, Pair<Long, Integer>>();
	// Doc name -> doc id dictionary
	private static Map<String, Integer> docDict
		= new TreeMap<String, Integer>();
	// Term -> term id dictionary
	private static Map<String, Integer> termDict
		= new TreeMap<String, Integer>();
	// Block queue
	private static LinkedList<File> blockQueue
		= new LinkedList<File>();

	// Total file counter
	private static int totalFileCount = 0;
	// Document counter
	private static int docIdCounter = 0;
	// Term counter
	private static int wordIdCounter = 0;
	// Index
	private static BaseIndex index = null;

	
	/* 
	 * Write a posting list to the given file 
	 * You should record the file position of this posting list
	 * so that you can read it back during retrieval
	 * 
	 * */
	private static void writePosting(FileChannel fc, PostingList posting)
			throws IOException {
		/*
		 * TODO: Your code here
		 *	 
		 */
		index.writePosting(fc, posting);
	}
	

	 /**
     * Pop next element if there is one, otherwise return null
     * @param iter an iterator that contains integers
     * @return next element or null
     */
    private static Integer popNextOrNull(Iterator<Integer> iter) {
        if (iter.hasNext()) {
            return iter.next();
        } else {
            return null;
        }
    }
	
    
   
	
	/**
	 * Main method to start the indexing process.
	 * @param method		:Indexing method. "Basic" by default, but extra credit will be given for those
	 * 			who can implement variable byte (VB) or Gamma index compression algorithm
	 * @param dataDirname	:relative path to the dataset root directory. E.g. "./datasets/small"
	 * @param outputDirname	:relative path to the output directory to store index. You must not assume
	 * 			that this directory exist. If it does, you must clear out the content before indexing.
	 */
	public static int runIndexer(String method, String dataDirname, String outputDirname) throws IOException 
	{
		/* Get index */
		String className = method + "Index";
		try {
			Class<?> indexClass = Class.forName(className);
			index = (BaseIndex) indexClass.newInstance();
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}
		
		/* Get root directory */
		File rootdir = new File(dataDirname);
		if (!rootdir.exists() || !rootdir.isDirectory()) {
			System.err.println("Invalid data directory: " + dataDirname);
			return -1;
		}
		
		   
		/* Get output directory*/
		File outdir = new File(outputDirname);
		if (outdir.exists() && !outdir.isDirectory()) {
			System.err.println("Invalid output directory: " + outputDirname);
			return -1;
		}
		
		/*	TODO: delete all the files/sub folder under outdir
		 * 
		 */
		Files.walkFileTree(outdir.toPath(), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (!dir.equals(outdir.toPath()))
					Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
		});


		
		if (!outdir.exists()) {
			if (!outdir.mkdirs()) {
				System.err.println("Create output directory failure");
				return -1;
			}
		}
		
		
		
		
		/* BSBI indexing algorithm */
		File[] dirlist = rootdir.listFiles();
		/* For each block */
		for (File block : dirlist) {
			File blockFile = new File(outputDirname, block.getName());
			//System.out.println("Processing block "+block.getName());
			blockQueue.add(blockFile);

			File blockDir = new File(dataDirname, block.getName());
			File[] filelist = blockDir.listFiles();

			//Term id -> Set of docId
			Map<Integer, Set<Integer>> blockPL = new TreeMap<>();

			/* For each file */
			for (File file : filelist) {
				++totalFileCount;
				String fileName = block.getName() + "/" + file.getName();
				
				 // use pre-increment to ensure docID > 0
                int docId = ++docIdCounter;
                docDict.put(fileName, docId);
				
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.trim().split("\\s+");
					for (String token : tokens) {
						/*
						 * TODO: Your code here
						 *       For each term, build up a list of
						 *       documents in which the term occurs
						 */
						int curTermId;
						if (!termDict.containsKey(token)) {
							termDict.put(token, ++wordIdCounter);
							curTermId = wordIdCounter;
						}
						else {
							curTermId = termDict.get(token);
						}

						if (!blockPL.containsKey(curTermId)) {
							blockPL.put(curTermId, new HashSet<>());
						}
						blockPL.get(curTermId).add(docId);
					}
				}
				reader.close();
			}

			/* Sort and output */
			if (!blockFile.createNewFile()) {
				System.err.println("Create new block failure.");
				return -1;
			}
			
			RandomAccessFile bfc = new RandomAccessFile(blockFile, "rw");
			
			/*
			 * TODO: Your code here
			 *       Write all posting lists for all terms to file (bfc) 
			 */
			FileChannel fc = bfc.getChannel();
			for (Map.Entry<Integer, Set<Integer>> entry : blockPL.entrySet()) {
				PostingList p = new PostingList(entry.getKey(), new ArrayList<>(entry.getValue()));
				Collections.sort(p.getList());
				writePosting(fc, p);
			}


			bfc.close();
		}
		/* Required: output total number of files. */
		//System.out.println("Total Files Indexed: "+totalFileCount);

		/* Merge blocks */
		while (true) {
			if (blockQueue.size() <= 1)
				break;

			File b1 = blockQueue.removeFirst();
			File b2 = blockQueue.removeFirst();
			
			File combfile = new File(outputDirname, b1.getName() + "+" + b2.getName());
			if (!combfile.createNewFile()) {
				System.err.println("Create new block failure.");
				return -1;
			}

			RandomAccessFile bf1 = new RandomAccessFile(b1, "r");
			RandomAccessFile bf2 = new RandomAccessFile(b2, "r");
			RandomAccessFile mf = new RandomAccessFile(combfile, "rw");
			 
			/*
			 * TODO: Your code here
			 *       Combine blocks bf1 and bf2 into our combined file, mf
			 *       You will want to consider in what order to merge
			 *       the two blocks (based on term ID, perhaps?).
			 *       
			 */
			FileChannel bf1FC = bf1.getChannel();
			FileChannel bf2FC = bf2.getChannel();
			FileChannel mfFC = mf.getChannel();

			PostingList bf1PostingList = index.readPosting(bf1FC);
			PostingList bf2PostingList = index.readPosting(bf2FC);
			//Merge 2 blocks using merge algorithm of merge sort
			while (bf1PostingList != null || bf2PostingList != null) {
                PostingList toWritePosting;
			    if (bf1PostingList != null && bf2PostingList != null) {

			    	//Merge 2 posting lists if term id are equal.
                    if (bf1PostingList.getTermId() == bf2PostingList.getTermId()) {
                    	/*
                    	* If doc id in posting list no.1 less than doc id in posting list no.2
                    	* then add all doc id of posting list no.2 to posting list no.1
                    	* else add all doc id of posting list no.1 to posting list no.2
                    	* */
                        if (bf1PostingList.getList().get(0) < bf2PostingList.getList().get(0)) {
                        	bf1PostingList.getList().addAll(bf2PostingList.getList());
                        	toWritePosting = bf1PostingList;
						}
                        else {
							bf2PostingList.getList().addAll(bf1PostingList.getList());
							toWritePosting = bf2PostingList;
						}
                        //Read next posting list of block 1 and block 2
						bf1PostingList = index.readPosting(bf1FC);
						bf2PostingList = index.readPosting(bf2FC);
                    }
                    /*
                    * if term id of posting list no.1 < term id of posting list no.2
                    * then write posting list no.1 to combined fine and read next posting list of block1
                    * else write posting list no.2 to combined fine and read next posting list of block2
                    * */
                    else if (bf1PostingList.getTermId() < bf2PostingList.getTermId()) {
                        toWritePosting = bf1PostingList;
                        bf1PostingList = index.readPosting(bf1FC);
                    }
                    else {
                        toWritePosting = bf2PostingList;
                        bf2PostingList = index.readPosting(bf2FC);
                    }
                }
			    /*
			    * If there are no posting list left in block1
			    * then writing all remain posting list of block 2 to combined file
			    * */
			    else if (bf1PostingList == null) {
			    	toWritePosting = bf2PostingList;
					bf2PostingList = index.readPosting(bf2FC);
                }
				/*
				 * If there are no posting list left in block 2
				 * then writing all remain posting list of block 1 to combined file
				 * */
			    else {
			    	toWritePosting = bf1PostingList;
					bf1PostingList = index.readPosting(bf1FC);
                }
			    //Update <position in index file, doc frequency> of postingDict
				if (!postingDict.containsKey(toWritePosting.getTermId())) {
					postingDict.put(toWritePosting.getTermId(), new Pair<>(mfFC.position(), toWritePosting.getList().size()));
				}
				else {
					postingDict.get(toWritePosting.getTermId()).setFirst(mfFC.position());
					postingDict.get(toWritePosting.getTermId()).setSecond(toWritePosting.getList().size());
				}
				//Write posting list to combined file
			    writePosting(mfFC, toWritePosting);
             }
			
			bf1.close();
			bf2.close();
			mf.close();
			b1.delete();
			b2.delete();
			blockQueue.add(combfile);
		}


		/* Dump constructed index back into file system */
		File indexFile = blockQueue.removeFirst();
		indexFile.renameTo(new File(outputDirname, "corpus.index"));

		BufferedWriter termWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "term.dict")));
		for (String term : termDict.keySet()) {
			termWriter.write(term + "\t" + termDict.get(term) + "\n");
		}
		termWriter.close();

		BufferedWriter docWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "doc.dict")));
		for (String doc : docDict.keySet()) {
			docWriter.write(doc + "\t" + docDict.get(doc) + "\n");
		}
		docWriter.close();

		BufferedWriter postWriter = new BufferedWriter(new FileWriter(new File(
				outputDirname, "posting.dict")));
		for (Integer termId : postingDict.keySet()) {
			postWriter.write(termId + "\t" + postingDict.get(termId).getFirst()
					+ "\t" + postingDict.get(termId).getSecond() + "\n");
		}
		postWriter.close();

		return totalFileCount;
	}

	public static void main(String[] args) throws IOException {
		/* Parse command line */
		if (args.length != 3) {
			System.err
					.println("Usage: java Index [Basic|VB|Gamma] data_dir output_dir");
			return;
		}

		/* Get index */
		String className = "";
		try {
			className = args[0];
		} catch (Exception e) {
			System.err
					.println("Index method must be \"Basic\", \"VB\", or \"Gamma\"");
			throw new RuntimeException(e);
		}

		/* Get root directory */
		String root = args[1];
		

		/* Get output directory */
		String output = args[2];
		runIndexer(className, root, output);
	}

}
