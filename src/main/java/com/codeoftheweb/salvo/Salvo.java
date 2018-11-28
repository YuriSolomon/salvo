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

    public Salvo() {}

    public Salvo(int turn, List<String> location) {
        this.turn = turn;
        this.location = location;
        this.gamePlayer = getGamePlayer();
    }

    public int getHits(GamePlayer gamePlayer, Salvo salvo) {
        List<String> hits = new ArrayList<>();
        salvo.getLocation().forEach(shot -> {
            if (gamePlayer.opponentsShipsList(gamePlayer).contains(shot)) {
                hits.add(shot);
            }
        });
        return hits.size();
    }

    public Salvo opponentsSalvosByTurn(GamePlayer gamePlayer, Salvo salvo){
        Salvo opponentsNewSalvo = new Salvo();
        for (Salvo opponentsSalvo : gamePlayer.getGame().getOpponent(gamePlayer).getOpponentsSalvoes(gamePlayer)) {
            if (opponentsSalvo.getTurn() == salvo.getTurn()) {
                opponentsNewSalvo = opponentsSalvo;
            }
        }
        return opponentsNewSalvo;
    }

    public List<String> getOpponentsTotalLocations(GamePlayer gamePlayer, Salvo salvo) {
        List<String> totalLocations = new ArrayList<>();
        gamePlayer.getGame().getOpponent(gamePlayer).getSalvo().forEach(salvo1 -> {
            if (salvo1.getTurn() <= salvo.getTurn()) {
                salvo1.getLocation().forEach(location -> {
                    if (!totalLocations.contains(location)) {
                        totalLocations.add(location);
                    }
                });
            }
        });
        return totalLocations;
    }

    public List<String> getSunkenShips(GamePlayer gamePlayer, Salvo salvo) {
        List<String> sunkenShips = new ArrayList<>();
        List<String> shipsLoc = new ArrayList<>();
            gamePlayer.getShip().forEach(ship -> {
                shipsLoc.clear();
                getOpponentsTotalLocations(gamePlayer, salvo).forEach(location -> {
                    if (ship.getLocation().contains(location)) {
                        shipsLoc.add(location);
                    }
                });
                if (ship.getLocation().size() == shipsLoc.size()) {
                    sunkenShips.add(ship.getType());
                }
            });
        return sunkenShips;
    }

    public String getGamePlayerId() { return String.valueOf(gamePlayer.getId()); }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public int getTurn() { return turn; }

    public void setTurn(int turn) { this.turn = turn; }

    public List<String> getLocation() { return location; }

    public void setLocation(List<String> location) { this.location = location; }

    public GamePlayer getGamePlayer() { return gamePlayer; }

    public void setGamePlayer(GamePlayer gamePlayer) { this.gamePlayer = gamePlayer; }
}
