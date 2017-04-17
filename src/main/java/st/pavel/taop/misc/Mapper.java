package st.pavel.taop.misc;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import st.pavel.taop.domain.TaopPost;
import st.pavel.taop.domain.blogger.BloggerPost;

public class Mapper extends ConfigurableMapper {

	@Override
	protected void configure(MapperFactory factory) {
		factory.getConverterFactory().registerConverter("urlNumberConverter", new UrlNumberConverter());
		factory.getConverterFactory().registerConverter("offsetDateTimeConverter", new OffsetDateTimeConverter());

		factory.classMap(BloggerPost.class, TaopPost.class)
		       .mapNulls(false)
		       .mapNullsInReverse(false)
		       .fieldMap("updated", "created").converter("offsetDateTimeConverter").add()
		       .fieldMap("url", "number").converter("urlNumberConverter").aToB().add()
		       .fieldMap("url", "number").bToA().exclude().add()
		       .byDefault()
		       .register();
	}

}
