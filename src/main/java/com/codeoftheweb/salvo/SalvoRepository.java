package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface SalvoRepository extends JpaRepository<Salvo, Long> {
    Set<Salvo> findByGamePlayer(GamePlayer gameplayer);
    Salvo findByTurn(int turn);
}