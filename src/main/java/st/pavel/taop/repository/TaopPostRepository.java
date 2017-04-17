package st.pavel.taop.repository;

import org.springframework.data.mapdb.repository.MapDbRepository;

import st.pavel.taop.domain.TaopPost;

public interface TaopPostRepository extends MapDbRepository<TaopPost, Long> {

}
