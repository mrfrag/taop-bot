package st.pavel.taop.misc;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

public class SerializerDateTime implements Serializer<OffsetDateTime> {

	@Override
	public void serialize(DataOutput2 out, OffsetDateTime value) throws IOException {
		out.writeUTF(value.toString());
	}

	@Override
	public OffsetDateTime deserialize(DataInput2 input, int available) throws IOException {
		return OffsetDateTime.parse(input.readUTF());
	}

}
