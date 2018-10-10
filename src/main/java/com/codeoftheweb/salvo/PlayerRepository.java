package com.codeoftheweb.salvo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public class PlayerRepository {
    public interface playerRepository extends JpaRepository<Player, Long> {
        List<Player> findByUserName(String userName);
    }
}
