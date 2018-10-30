package com.codeoftheweb.salvo;

import org.hibernate.validator.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByEmail(String email);
    Player findByUserName(String userName);

}
