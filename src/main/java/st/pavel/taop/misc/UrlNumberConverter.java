package st.pavel.taop.misc;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBefore;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class UrlNumberConverter extends CustomConverter<String, Long> {

	@Override
	public Long convert(String source, Type<? extends Long> destinationType, MappingContext mappingContext) {
		return Long.parseLong(substringBefore(substringAfterLast(source, "/"), "-"));
	}

}
