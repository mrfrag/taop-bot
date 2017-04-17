package st.pavel.taop.misc;

import java.time.OffsetDateTime;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class OffsetDateTimeConverter extends CustomConverter<OffsetDateTime, OffsetDateTime> {

	@Override
	public OffsetDateTime convert(OffsetDateTime source, Type<? extends OffsetDateTime> destinationType, MappingContext mappingContext) {
		return source;
	}

}
