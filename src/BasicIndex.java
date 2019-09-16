import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public class BasicIndex implements BaseIndex {

	@Override
	public PostingList readPosting(FileChannel fc) {
		/*
		 * TODO: Your code here
		 *       Read and return the postings list from the given file.
		 */
		
		return null;
	}

	@Override
	public void writePosting(FileChannel fc, PostingList p) {
		/*
		 * TODO: Your code here
		 *       Write the given postings list to the given file.
		 */
		ByteBuffer buffer = ByteBuffer.allocate(4);
		try {
			//Write term id to fc
			int termId = p.getTermId();
			buffer.putInt(termId);
			fc.write(buffer);

			//Write doc freq to fc
			int docFreq = p.getList().size();
			buffer.putInt(docFreq);



			for (Integer docId: p.getList()) {

			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}

