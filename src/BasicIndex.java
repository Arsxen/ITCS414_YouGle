//Dechapon Tongmak 6088211 Sec 2
//Jarupong Pajakgo 6088107 Sec 2
//Archawat Silachote 6088168 Sec 3

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
		PostingList postingList = null;
		int termId;
		int docFreq;
		ByteBuffer buffer = ByteBuffer.allocate(8);
		try {
			int readStatus = fc.read(buffer);
			if (readStatus != -1) {
				buffer.flip();

				termId = buffer.getInt();
				docFreq = buffer.getInt();

				buffer = ByteBuffer.allocate(docFreq*4);
				fc.read(buffer);
				buffer.flip();

				postingList = new PostingList(termId);
				for (int i = 0; i < docFreq; i++) {
					postingList.getList().add(buffer.getInt());
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return postingList;
	}

	@Override
	public void writePosting(FileChannel fc, PostingList p) {
		/*
		 * TODO: Your code here
		 *       Write the given postings list to the given file.
		 */
		int dataLength = p.getList().size() + 2;
		ByteBuffer buffer = ByteBuffer.allocate(dataLength*4);

		buffer.putInt(p.getTermId());
		buffer.putInt(p.getList().size());

		for (Integer docId : p.getList()) {
			buffer.putInt(docId);
		}

		buffer.flip();

		try {
			fc.write(buffer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}

