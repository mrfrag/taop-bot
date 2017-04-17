package st.pavel.taop.repository;

import org.springframework.data.mapdb.repository.MapDbRepository;

import st.pavel.taop.domain.Chat;

public interface ChatRepository extends MapDbRepository<Chat, Long> {

}
