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
		int termId;
		int docFreq;
		ByteBuffer bb = ByteBuffer.allocate(8);
		return null;
	}

	@Override
	public void writePosting(FileChannel fc, PostingList p) {
		/*
		 * TODO: Your code here
		 *       Write the given postings list to the given file.
		 */
		int dataLength = p.getList().size() + 2;
		ByteBuffer bb = ByteBuffer.allocate(dataLength*4);

		bb.putInt(p.getTermId());
		bb.putInt(p.getList().size());

		for (Integer docId : p.getList()) {
			bb.putInt(docId);
		}

		bb.flip();

		try {
			fc.write(bb);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}

