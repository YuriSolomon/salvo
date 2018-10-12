package com.codeoftheweb.salvo;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class GamePlayer {

    private Date date;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="player_id")
    private Player player;

//    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name="game_id")
    private Game game;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public GamePlayer() {}

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.date = new Date();
    }

    public Player getGames() {
        return this.player;
    }

    public Game getPlayers() {
        return this.game;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
