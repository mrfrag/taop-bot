package st.pavel.taop.components;

import org.mapdb.Atomic.Var;
import org.mapdb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import st.pavel.taop.domain.PatronsRecord;
import st.pavel.taop.misc.SerializerPatronsRecord;

@Component
public class PatronsRegistry {

	private Var<PatronsRecord> patronsRecord;

	@Autowired
	public PatronsRegistry(DB db) {
		patronsRecord = db.atomicVar("patronsRecord", new SerializerPatronsRecord()).createOrOpen();
	}

	public String get() {
		return patronsRecord.get().getPatrons();
	}

	public void update(Long number, String record) {
		PatronsRecord oldRecord = patronsRecord.get();
		if (oldRecord == null || number > oldRecord.getIssueNumber()) {
			patronsRecord.set(new PatronsRecord(number, record));
		}
	}

}
