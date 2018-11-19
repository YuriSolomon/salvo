package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

    @CreationTimestamp
    private Date date;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch= FetchType.EAGER)
    @OrderBy("location asc")
    Set<Ship> ship = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch= FetchType.EAGER)
    @OrderBy("turn asc")
    Set<Salvo> salvo = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public GamePlayer() {}

    public GamePlayer(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        this.ship.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        this.salvo.add(salvo);
    }

    public Set<Salvo> getOpponentsSalvoes(GamePlayer gamePlayer) {
        if (this.getGame().getOpponent(gamePlayer) != null) {
            return this.getGame().getOpponent(gamePlayer).getSalvo();
        }
        return null;
    }

    public Set<Ship> getOpponentsShips(GamePlayer gamePlayer){
        if (this.getGame().getOpponent(gamePlayer) != null){
            return this.getGame().getOpponent(gamePlayer).getShip();
        }
        return null;
    }

    public List<String> salvoesList(GamePlayer gamePlayer) {
        List<String> salvoesList = new ArrayList<>();
        for (Salvo salvo: getSalvo()) {
            for (String location: salvo.getLocation()) {
                salvoesList.add(location);
            }
        }
        return salvoesList;
    }

    public List<String> opponentsShipsList(GamePlayer gamePlayer) {
        List<String> opponentsShipsList = new ArrayList<>();
        if (getOpponentsShips(this) != null) {
            for (Ship ship : getOpponentsShips(this)) {
                for (String location : ship.getLocation()) {
                    opponentsShipsList.add(location);
                }
            }
            return opponentsShipsList;
        }
        return null;
    }

    public Set<Salvo> getSalvo() {
        return salvo;
    }

    public void setSalvo(Set<Salvo> salvo) {
        this.salvo = salvo;
    }

    public Set<Ship> getShip() {
        return ship;
    }

    public void setShip(Set<Ship> ship) {
        this.ship = ship;
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
