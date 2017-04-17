package st.pavel.taop.misc;

import java.io.IOException;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import st.pavel.taop.domain.PatronsRecord;

public class SerializerPatronsRecord implements Serializer<PatronsRecord> {

	@Override
	public void serialize(DataOutput2 out, PatronsRecord value) throws IOException {
		out.writeLong(value.getIssueNumber());
		out.writeUTF(value.getPatrons());
	}

	@Override
	public PatronsRecord deserialize(DataInput2 input, int available) throws IOException {
		return new PatronsRecord(input.readLong(), input.readUTF());
	}

}
