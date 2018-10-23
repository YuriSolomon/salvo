package com.codeoftheweb.salvo;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
public class Game {

    @CreationTimestamp
    private Date date;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy="game", fetch= FetchType.EAGER)
    @OrderBy("id asc")
    Set<GamePlayer> gamePlayer;

    public Game() {}

    public GamePlayer getGPOpponent(GamePlayer newgamePlayer) {
        for (GamePlayer gamePlayers: this.gamePlayer) {
            if (newgamePlayer.getId() != gamePlayers.getId()) {
                return gamePlayers;
            }
        }
        return null;
    }

    public Set<GamePlayer> getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(Set<GamePlayer> gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
