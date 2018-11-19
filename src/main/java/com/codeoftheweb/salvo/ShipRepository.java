package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ShipRepository extends JpaRepository<Ship, Long> {
    Ship findByType(@Param("type") String type);
    Set<Ship> findByGamePlayer(@Param("gameplayer") GamePlayer gamePlayer);

}