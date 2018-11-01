package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
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
        return gamePlayerRepository.findAll().stream().map(game->gpMap(game)).collect(toList());
    }

    @RequestMapping("/salvoes")
    public List<Object> getSalvoes() {
        return salvoRepository.findAll().stream().map(salvo->salvoMap(salvo)).collect(toList());
    }

    @RequestMapping("/game_view/{id}")
    public Map<String, Object> getGameInfo(@PathVariable long id) {
        return gamePMap(gamePlayerRepository.findOne(id));
    }

    @RequestMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> newGames = new LinkedHashMap<>();
        if (!usedIsLogged(authentication)) {
            newGames.put("current", null);
        } else {
            newGames.put("current", currentPlayetMap(currentUser(authentication)));
        }
            newGames.put("games", gameRepository.findAll().stream().map(game->gameMap(game)).collect(toList()));
        return newGames;
    }

    private Map<String, Object> gpMap(GamePlayer gamePlayer) {
        Map<String, Object> gpmap = new LinkedHashMap<String, Object>();
        gpmap.put("player", playerMap(gamePlayer.getPlayer()));
        return gpmap;
    }

    private Map<String, Object> gamesMap(GamePlayer gamePlayer) {
        Map<String, Object> gamesmap = new LinkedHashMap<String, Object>();
//        gamesmap.put("currentPlayer", currentPlayetMap(currentUserEmail(authentication)));
        gamesmap.put("game", gamePlayer.getGame());
        return gamesmap;
    }

    private Map<String, Object> gameMap(Game game) {
        Map<String, Object> gamemap = new LinkedHashMap<String, Object>();
        gamemap.put("gameId", game.getId());
        gamemap.put("created", game.getDate());
        gamemap.put("player", playerSet(game.getPlayer(game.getGamePlayer())));
        gamemap.put("score", scoreSet(game.getScore()));
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
        gamepmap.put("id", gamePlayer.getId());
        gamepmap.put("created", gamePlayer.getDate());
        gamepmap.put("gamePlayers", gameplayerSet(gamePlayer.getGame().gamePlayer));
        gamepmap.put("ships", shipsSet(gamePlayer.getShip()));
        gamepmap.put("salvoes", salvoesSet(gamePlayer.getSalvo()));
        gamepmap.put("hitTheOpponent", hitTheOpponent(gamePlayer));
        gamepmap.put("opponentsSalvoes", salvoesSet(gamePlayer.getOpponentsSalvoes(gamePlayer)));
        return gamepmap;
    }

    private Map<String, Object> shipMap(Ship ship) {
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
        for (String shipLocation: gamePlayer.opponentsShipsList(gamePlayer)) {
            for (String salvoLocation: gamePlayer.salvoesList(gamePlayer)) {
                if (shipLocation == salvoLocation) {
                    if (!hitTheOpponent.contains(shipLocation)) {
                        hitTheOpponent.add(shipLocation);
                    }
                }
            }
        }
        return hitTheOpponent;
    }

    public Player currentUser(Authentication authentication) {
//        System.out.println(authentication.getName());
//        System.out.println(playerRepository.findByEmail(authentication.getName()));
        return playerRepository.findByEmail(authentication.getName());
    }

    public Boolean usedIsLogged (Authentication authentication) {
        if (authentication == null) {
            return false;
        } else {
            return true;
        }
    }

    @RequestMapping(value = "/players", method = RequestMethod.GET)
    public String showRegisterPage(ModelMap model) {
        return "register";
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    public ResponseEntity handleRegisterRequest(ModelMap model,
                                        @RequestParam String email,
                                        @RequestParam String userName,
                                        @RequestParam String password) {

        if (playerRepository.findByEmail(email) == null) {
            if (playerRepository.findByUserName(userName) == null) {
                playerRepository.save(new Player(userName, email, password));
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
