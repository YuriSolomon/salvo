package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByEmail(@Param("email") String email);
    Player findByUserName(@Param("email") String userName);
    Player findById(@Param("id") long id);

}