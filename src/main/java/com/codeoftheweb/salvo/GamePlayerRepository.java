package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    Set<GamePlayer> findByPlayer(@Param("player") Player player);
    GamePlayer findById(@Param("id") long id);
}
