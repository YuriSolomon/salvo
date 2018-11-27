package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int turn;

    @ElementCollection
    @Column(name = "location")
    private List<String> location = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo() {};

    public Salvo(int turn, List<String> location) {
        this.turn = turn;
        this.location = location;
        this.gamePlayer = getGamePlayer();
    }

    public int getHits(GamePlayer gamePlayer, List<String> loc) {
        List<String> hits = new ArrayList<>();
        loc.forEach(shot -> {
            if (gamePlayer.opponentsShipsList(gamePlayer).contains(shot)) {
                hits.add(shot);
            }
        });
        return hits.size();
    }

//    public int getOpponentsHits(GamePlayer gamePlayer, Salvo salvo) {
////        Salvo opponentsSalvo = opponentsSalvoByTurn(gamePlayer, salvo);
//        List<String> hits = new ArrayList<>();
//        opponentsSalvoByTurn(gamePlayer, salvo).location.forEach(shot -> {
//            if (gamePlayer.shipsList(gamePlayer).contains(shot)) {
//                hits.add(shot);
//            }
//        });
//        return hits.size();
//    }

    public List<String> opponentsSalvoLocationsByTurn(GamePlayer gamePlayer, Salvo salvo){
        List<String> oppLocList = new ArrayList<>();
        for (Salvo opponentsSalvo : gamePlayer.getGame().getOpponent(gamePlayer).salvo) {
            if (opponentsSalvo.getTurn() == salvo.getTurn()) {
                opponentsSalvo.getLocation().forEach(loc -> {
                    oppLocList.add(loc);
                });
            }
        }
        return oppLocList;
    }

    public String getGamePlayerId() {
        return String.valueOf(gamePlayer.getId());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTurn() { return turn; }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
