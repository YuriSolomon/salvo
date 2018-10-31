package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GameRepository extends JpaRepository<Game, Long> {

}