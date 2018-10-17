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

    @RequestMapping("/games_view/{id}")
    public List<Long> getGameInfo(@PathVariable long id) {
        return gamePlayerRepository.findAll().stream().map(gamePlayer->gamePlayer.getId()).collect(toList());
    }

    private Map<String, Object> gamePMap(GamePlayer gamePlayer) {
        Map<String, Object> gamepmap = new LinkedHashMap<String, Object>();
        gamepmap.put("id", gamePlayer.getId());
        gamepmap.put("created", gamePlayer.getDate());
        gamepmap.put("gamePlayers", playerMap(gamePlayer.getPlayer()));
        gamepmap.put("ships", shipsSet(gamePlayer.getShip()));
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

}
