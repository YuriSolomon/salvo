package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/leaderboard")
    public List<Object> getPlayersScoreList() {
        return playerRepository.findAll().stream().map(player->playerMap(player)).collect(toList());
    }

    @RequestMapping("/salvoes")
    public List<Object> getSalvoes() {
        return salvoRepository.findAll().stream().map(salvo->salvoMap(salvo)).collect(toList());
    }

    @RequestMapping("/game_view/{id}")
    public ResponseEntity<Map<String, Object>> getGameInfo(@PathVariable long id, Authentication authentication) {
        GamePlayer gamePlayer = gamePlayerRepository.findById(id);
        if (gamePlayer.getPlayer() == currentUser(authentication)) {
            return new ResponseEntity<>(gamePMap(gamePlayer), HttpStatus.OK);
        }
        return new ResponseEntity<>(makeMap("Error", "You have to login"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Game game, Authentication authentication) {
        Map<String, Object> newGames = new LinkedHashMap<>();
        if (!usedIsLogged(authentication)) {
            newGames.put("current", null);
        } else {
            newGames.put("current", currentPlayetMap(currentUser(authentication)));
        }
            newGames.put("games", gameRepository.findAll().stream().map(games->gameMap(games)).collect(toList()));
        return newGames;
    }

    private Map<String, Object> gpMap(GamePlayer gamePlayer) {
        Map<String, Object> gpmap = new LinkedHashMap<String, Object>();
        gpmap.put("player", playerMap(gamePlayer.getPlayer()));
        return gpmap;
    }

    private Map<String, Object> gamesMap(GamePlayer gamePlayer) {
        Map<String, Object> gamesmap = new LinkedHashMap<String, Object>();
        gamesmap.put("game", gamePlayer.getGame());
        return gamesmap;
    }

    private Map<String, Object> gameMap(Game game) {
        Map<String, Object> gamemap = new LinkedHashMap<String, Object>();
        gamemap.put("gameid", game.getId());
        gamemap.put("created", game.getDate());
        gamemap.put("players", gplayerSet(game.getGamePlayer()));
        return gamemap;
    }

    private Map<String, Object> playerMap(Player player) {
        Map<String, Object> playermap = new LinkedHashMap<String, Object>();
        playermap.put("playerId", player.getId());
        playermap.put("userName", player.getUserName());
        playermap.put("email", player.getEmail());
        playermap.put("score", scoreSet(player.getScore()));
        return playermap;
    }

    private Map<String, Object> gameplayerMap(GamePlayer gamePlayer) {
        Map<String, Object> gameplayermap = new LinkedHashMap<String, Object>();
        gameplayermap.put("id", gamePlayer.getId());
        gameplayermap.put("player", playerMap(gamePlayer.getPlayer()));
        return gameplayermap;
    }

    private Map<String, Object> gamePMap(GamePlayer gamePlayer) {
        Map<String, Object> gamepmap = new LinkedHashMap<String, Object>();
        gamepmap.put("gamePlayerId", gamePlayer.getId());
        gamepmap.put("playerId", gamePlayer.getPlayer().getId());
        gamepmap.put("created", gamePlayer.getDate());
        gamepmap.put("gamePlayers", gameplayerSet(gamePlayer.getGame().gamePlayer));
        gamepmap.put("ships", shipsSet(gamePlayer.getShip()));
        gamepmap.put("salvoes", salvoesSet(gamePlayer.getSalvo()));
        gamepmap.put("hitTheOpponent", hitTheOpponent(gamePlayer));
        gamepmap.put("opponentsSalvoes", gamePlayer.getOpponentsSalvoes(gamePlayer) != null ? salvoesSet(gamePlayer.getOpponentsSalvoes(gamePlayer)) : null);
        return gamepmap;
    }

    private Map<String, Object> shipMap(Ship ship) {
        System.out.println(ship.toString());
        Map<String, Object> shipmap = new LinkedHashMap<String, Object>();
        shipmap.put("type", ship.getType());
        shipmap.put("location", ship.getLocation());
        return shipmap;
    }

    private Map<String, Object> salvoMap(Salvo salvo) {
        Map<String, Object> salvomap = new LinkedHashMap<String, Object>();
        salvomap.put("turn", salvo.getTurn());
        salvomap.put("player", salvo.getGamePlayer().getPlayer().getId());
        salvomap.put("location", salvo.getLocation());
        return salvomap;
    }

    private Map<String, Object> scoreMap(Score score) {
        Map<String, Object> scoremap = new LinkedHashMap<String, Object>();
        scoremap.put("player", score.getPlayer());
        scoremap.put("finishTime", score.getDate());
        scoremap.put("gameScore", score.getGame().getScore(score.getPlayer()));
        return scoremap;
    }

    private Map<String, Object> currentPlayetMap(Player player) {
        Map<String, Object> currentplayetmap = new LinkedHashMap<String, Object>();
        currentplayetmap.put("playerId", player.getId());
        currentplayetmap.put("userName", player.getUserName());
        currentplayetmap.put("email", player.getEmail());
        return currentplayetmap;
    }

    private Map<String, Object> gplayerMap(GamePlayer gamePlayer) {
        Map<String, Object> gplayermap = new LinkedHashMap<String, Object>();
        gplayermap.put("gpid", gamePlayer.getId());
        gplayermap.put("playerid", gamePlayer.getPlayer().getId());
        gplayermap.put("userName", gamePlayer.getPlayer().getUserName());
        gplayermap.put("email", gamePlayer.getPlayer().getEmail());
        return gplayermap;
    }

    private List<Map<String, Object>> gplayerSet (Set<GamePlayer> gamePlayer) {
        return gamePlayer.stream().map(gameplayer-> gplayerMap(gameplayer)).collect(toList());
    }

    private List<Map<String, Object>> gameplayerSet (Set<GamePlayer> gamePlayer) {
        return gamePlayer.stream().map(gameplayer-> gameplayerMap(gameplayer)).collect(toList());
    }

    private List<Map<String, Object>> shipsSet (Set<Ship> ship) {
        return ship.stream().map(ships-> shipMap(ships)).collect(toList());
    }

    private List<Map<String, Object>> playerSet (Set<Player> players) {
        return players.stream().map(player-> playerMap(player)).collect(toList());
    }

    private List<Map<String, Object>> gameSet (Set<Game> games) {
        return games.stream().map(game-> gameMap(game)).collect(toList());
    }

    private List<Map<String, Object>> scoreSet (Set<Score> score) {
        return score.stream().map(scores-> scoreMap(scores)).collect(toList());
    }

    private List<Map<String, Object>> salvoesSet (Set<Salvo> salvo) {
        return salvo.stream().map(salvoes-> salvoMap(salvoes)).collect(toList());
    }

    private List<Map<String, Object>> gpSet (Set<GamePlayer> gamePlayer) {
        return gamePlayer.stream().map(gameplayer-> gpMap(gameplayer)).collect(toList());
    }

    public List<String> hitTheOpponent(GamePlayer gamePlayer) {
        List<String> hitTheOpponent = new ArrayList<>();
        if (gamePlayer.getOpponentsShips(gamePlayer) != null) {
            for (String shipLocation : gamePlayer.opponentsShipsList(gamePlayer)) {
                for (String salvoLocation : gamePlayer.salvoesList(gamePlayer)) {
                    if (shipLocation == salvoLocation) {
                        if (!hitTheOpponent.contains(shipLocation)) {
                            hitTheOpponent.add(shipLocation);
                        }
                    }
                }
            }
            return hitTheOpponent;
        }
        return null;
    }

    public Player currentUser(Authentication authentication) {
        if (usedIsLogged(authentication)) {
            return playerRepository.findByEmail(authentication.getName());
        }
        return null;
    }

    public Boolean usedIsLogged (Authentication authentication) {
        if (authentication == null) {
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> handleRegisterRequest(
                                                    @RequestParam String email,
                                                    @RequestParam String userName,
                                                    @RequestParam String password) {

        if ((email == "") || (userName == "") || (password == "")) {
            return new ResponseEntity<>(makeMap("Error", "All fields must be filled"), HttpStatus.FORBIDDEN);
        } else if (playerRepository.findByEmail(email) == null) {
            if (playerRepository.findByUserName(userName) == null) {
                playerRepository.save(new Player(userName, email, password));
                return new ResponseEntity<>(makeMap("Success", "Registered"), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(makeMap("Error", "User name is already in use"), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(makeMap("Error", "Email is already in use"), HttpStatus.FORBIDDEN);
        }
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", key);
        map.put("message", value);
        return map;
    }

    private Map<String, Object> makeSimpleMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(value = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(
                                                Authentication authentication) {

        if (!usedIsLogged(authentication)) {
            return new ResponseEntity<>(makeMap("Error", "please login"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = new Game();
            gameRepository.save(game);
            GamePlayer gamePlayer = new GamePlayer(game, currentUser(authentication));
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(makeSimpleMap("gpid", gamePlayer.getId()),HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/games/players/{gpid}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeShips(
                                                @PathVariable long gpid,
                                                @PathVariable List<String> location,
                                                @PathVariable String type,
                                                Authentication authentication) {

        if (!usedIsLogged(authentication)) {
            return new ResponseEntity<>(makeMap("Error", "please login"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gpid) == null) {
            return new ResponseEntity<>(makeMap("Error", "game doesn't exist"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gpid).getPlayer() != currentUser(authentication)) {
            return new ResponseEntity<>(makeMap("Error", "you have no permission to edit other player's ships"), HttpStatus.UNAUTHORIZED);
        } else if (shipRepository.findByType(type).getType() == type) {
            return new ResponseEntity<>(makeMap("Error", "ship has already been placed"), HttpStatus.FORBIDDEN);
        } else {
            Ship ship = new Ship(type, location);
            shipRepository.save(ship);
            GamePlayer currentGP = gamePlayerRepository.findById(gpid);
            currentGP.addShip(ship);
            return new ResponseEntity<>(makeMap("Success", "the ship was placed"),HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/game/{id}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(
            @PathVariable long id,
            Authentication authentication) {

        if (!usedIsLogged(authentication)) {
            return new ResponseEntity<>(makeMap("Error", "please login"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameRepository.findById(id);
            System.out.println(game.toString());
            GamePlayer gamePlayer = new GamePlayer(game, currentUser(authentication));
            System.out.println(gamePlayer.toString());
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(makeSimpleMap("gpid", gamePlayer.getId()),HttpStatus.CREATED);
        }
    }
}