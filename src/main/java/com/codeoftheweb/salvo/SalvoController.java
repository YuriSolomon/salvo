package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private SalvoRepository salvoRepository;

    @RequestMapping("gamePlayers")
    public List<GamePlayer> getAll() {
        return gamePlayerRepository.findAll();
    }

    @RequestMapping("/players")
    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    @RequestMapping("/games")
    public List<Object> getGames() {
        return gameRepository.findAll().stream().map(game->gameMap(game)).collect(toList());
    }

    @RequestMapping("/salvoes")
    public List<Object> getSalvoes() {
        return salvoRepository.findAll().stream().map(salvo->salvoMap(salvo)).collect(toList());
    }

    private Map<String, Object> gameMap(Game game) {
        Map<String, Object> gamemap = new LinkedHashMap<String, Object>();
        gamemap.put("id", game.getId());
        gamemap.put("created", game.getDate());
        gamemap.put("gamePlayers", gameplayerSet(game.gamePlayer));
        return gamemap;
    }

    private Map<String, Object> playerMap(Player player) {
        Map<String, Object> playermap = new LinkedHashMap<String, Object>();
        playermap.put("id", player.getId());
        playermap.put("userName", player.getUserName());
        playermap.put("email", player.getEmail());
        return playermap;
    }

    private Map<String, Object> gameplayerMap(GamePlayer gamePlayer) {
        Map<String, Object> gameplayermap = new LinkedHashMap<String, Object>();
        gameplayermap.put("id", gamePlayer.getId());
        gameplayermap.put("player", playerMap(gamePlayer.getPlayer()));
        return gameplayermap;
    }

    private List<Map<String, Object>> gameplayerSet (Set<GamePlayer> gamePlayer) {
        return gamePlayer.stream().map(gameplayer-> gameplayerMap(gameplayer)).collect(toList());
    }

    @RequestMapping("/game_view/{id}")
    public Map<String, Object> getGameInfo(@PathVariable long id) {
        return gamePMap(gamePlayerRepository.findOne(id));
    }

    private Map<String, Object> gamePMap(GamePlayer gamePlayer) {
        Map<String, Object> gamepmap = new LinkedHashMap<String, Object>();
        gamepmap.put("id", gamePlayer.getId());
        gamepmap.put("created", gamePlayer.getDate());
        gamepmap.put("gamePlayers", gameplayerSet(gamePlayer.getGame().gamePlayer));
        gamepmap.put("ships", shipsSet(gamePlayer.getShip()));
        gamepmap.put("salvoes", salvoesSet(gamePlayer.getSalvo()));
        return gamepmap;
    }

    private Map<String, Object> shipMap(Ship ship) {
        Map<String, Object> shipmap = new LinkedHashMap<String, Object>();
        shipmap.put("type", ship.getType());
        shipmap.put("location", ship.getLocation());
        return shipmap;
    }

    private List<Map<String, Object>> shipsSet (Set<Ship> ship) {
        return ship.stream().map(ships-> shipMap(ships)).collect(toList());
    }

    private Map<String, Object> salvoMap(Salvo salvo) {
        Map<String, Object> salvomap = new LinkedHashMap<String, Object>();
        salvomap.put("turn", salvo.getTurn());
        salvomap.put("player", salvo.getGamePlayer().getPlayer().getId());
        salvomap.put("location", salvo.getLocation());
        return salvomap;
    }

    private List<Map<String, Object>> salvoesSet (Set<Salvo> salvo) {
        return salvo.stream().map(salvoes-> salvoMap(salvoes)).collect(toList());
    }

}
