package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Player {

    private String userName;
    private String email;
    private String password;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy="player", fetch= FetchType.EAGER)
    @OrderBy("id asc")
    Set<GamePlayer> gamePlayer;

    @OneToMany(mappedBy="player", fetch= FetchType.EAGER)
    Set<Score> score;

    public Player() {}

    public Player (String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public Score getSingleScore(Game game) {
        for (Score score: score) {
            if (score.getGame() == game) {
                return score;
            }
        }
        return null;
    }

    public Set<Game> getGame(Set<GamePlayer> gamePlayer) {
        Set<Game> gameSet = new HashSet<>();
        for (GamePlayer gp: this.gamePlayer) {
            gameSet.add(gp.getGame());
        }
        return gameSet;
    }

    public Set<GamePlayer> getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(Set<GamePlayer> gamePlayer) { this.gamePlayer = gamePlayer; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public Set<Score> getScore() { return score; }

    public void setScore(Set<Score> score) { this.score = score; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String toString() { return userName; }
}
