package com.codeoftheweb.salvo;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
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

    @OneToMany(mappedBy="game", fetch= FetchType.EAGER)
    Set<Score> score;

    public Game() {}

    public GamePlayer getOpponent(GamePlayer correntGamePlayer) {
        for (GamePlayer otherGamePlayer: this.gamePlayer) {
            if (correntGamePlayer.getId() != otherGamePlayer.getId()) {
                return otherGamePlayer;
            }
        }
        return null;
    }

    public Date getFinishTime(Score score) {
        return score.getDate();
    }

    public Score getScore(Player player) {
        return player.getSingleScore(this);
    }

    public Set<Player> getPlayer(Set<GamePlayer> gamePlayer) {
        Set<Player> playerSet = new HashSet<>();
        for (GamePlayer gp: this.gamePlayer) {
            playerSet.add(gp.getPlayer());
        }
        return playerSet;
    }

    public Set<Score> getScore() { return score; }

    public void setScore(Set<Score> score) { this.score = score; }

    public Set<GamePlayer> getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(Set<GamePlayer> gamePlayer) { this.gamePlayer = gamePlayer; }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

}
