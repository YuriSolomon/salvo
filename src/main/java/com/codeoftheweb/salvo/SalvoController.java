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
    @Autowired
    private ScoreRepository scoreRepository;

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
        GamePlayer gp = (GamePlayer) game.gamePlayer.toArray()[game.gamePlayer.size() - 1];
        Map<String, Object> gamemap = new LinkedHashMap<String, Object>();
        gamemap.put("gameid", game.getId());
        gamemap.put("created", game.getDate());
        gamemap.put("players", gplayerSet(game.getGamePlayer()));
        if (gp.getSalvo().size() >= 1) {
            Salvo salvo = (Salvo) gp.getSalvo().toArray()[gp.getSalvo().size() - 1];
            gamemap.put("gameIsOver", gameIsOver(gp, salvo));
        } else {
            gamemap.put("gameIsOver", false);
        }
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
        if (gamePlayer.getSalvo().size() > 0) {
            gamepmap.put("hitTheOpponent", hitTheOpponent(gamePlayer));
            gamepmap.put("opponentsHits", getOpponentsHitsOnMe(gamePlayer));
            gamepmap.put("lastSalvo", getLastSalvo(gamePlayer));
        } else {
            gamepmap.put("hitTheOpponent", null);
            gamepmap.put("opponentsHits", getOpponentsHitsOnMe(gamePlayer));
            gamepmap.put("lastSalvo", null);
        }
        if (gamePlayer.getShip().size() == 5  && gamePlayer.getSalvo().size() >= 1) {
            Salvo lastSalvo = (Salvo) gamePlayer.getSalvo().toArray()[gamePlayer.getSalvo().size() - 1];
            gamepmap.put("turnsHistory", turnsSet(gamePlayer.getSalvo()));
            gamepmap.put("gameState", stateMap(gamePlayer, lastSalvo));
        } else {
            gamepmap.put("turnsHistory", null);
            gamepmap.put("gameState", stateMap(gamePlayer, null));
        }
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

    private Map<String, Object> gplayerMap(GamePlayer gamePlayer) {
        Map<String, Object> gplayermap = new LinkedHashMap<String, Object>();
        gplayermap.put("gpid", gamePlayer.getId());
        gplayermap.put("playerid", gamePlayer.getPlayer().getId());
        gplayermap.put("userName", gamePlayer.getPlayer().getUserName());
        gplayermap.put("email", gamePlayer.getPlayer().getEmail());
        return gplayermap;
    }

    public Map<String, Object> turnsMap(Salvo salvo, GamePlayer gamePlayer) {
        GamePlayer opponent = gamePlayer.getGame().getOpponent(gamePlayer);
        Salvo opponentsSalvo = salvo.opponentsSalvosByTurn(gamePlayer, salvo);
        Map<String, Object> historyMap = new LinkedHashMap<>();
        historyMap.put("turnNumber", salvo.getTurn());
        historyMap.put("hitsOnOppenent", hitsMap(salvo, gamePlayer));
        historyMap.put("hitsOnYou", hitsMap(opponentsSalvo, opponent));
        return historyMap;
    }

    public Map<String, Object> hitsMap(Salvo salvo, GamePlayer gamePlayer) {
        Map<String, Object> historyMap = new LinkedHashMap<>();
        historyMap.put("numberOfFloatingShips", "");
        historyMap.put("numberOfHits", salvo.getHits(gamePlayer, salvo));
        historyMap.put("sunkedShips", salvo.getSunkenShips(gamePlayer, salvo));
        return historyMap;
    }

    public Map<String, Object> stateMap(GamePlayer gamePlayer, Salvo salvo) {
        Map<String, Object> gamesState = new LinkedHashMap<>();
        if (salvo == null) {
            gamesState.put("gameIsOver", false);
            gamesState.put("gamesState", getBeforeGameState(gamePlayer));
            gamesState.put("sunkedShips", null);
        } else {
            gamesState.put("gameIsOver", gameIsOver(gamePlayer, salvo));
            gamesState.put("gamesState", getGameState(gamePlayer, salvo));
            gamesState.put("sunkedShips", salvo.getSunkenShips(gamePlayer, salvo));
        }
        return gamesState;
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

    private List<Map<String, Object>> turnsSet (Set<Salvo> salvos) {
        return salvos.stream().map(salvo-> turnsMap(salvo, salvo.getGamePlayer())).collect(toList());
    }

    public List<String> hitTheOpponent(GamePlayer gamePlayer) {
        List<String> hitTheOpponent = new ArrayList<>();
        List<String> updatedSalvoList = gamePlayer.salvoesList(gamePlayer);
        if ((gamePlayer.getSalvo().size() > gamePlayer.getOpponentsSalvoes(gamePlayer).size())) {
            updatedSalvoList.remove(updatedSalvoList.size()-1);
            updatedSalvoList.remove(updatedSalvoList.size()-1);
            updatedSalvoList.remove(updatedSalvoList.size()-1);
        }
        if (gamePlayer.getOpponentsShips(gamePlayer) != null) {
            for (String shipLocation : gamePlayer.opponentsShipsList(gamePlayer)) {
                for (String salvoLocation : updatedSalvoList) {
                    if (shipLocation.equals(salvoLocation)) {
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

    public List<String> getLastSalvo(GamePlayer gamePlayer) {
        List<String> updatedSalvoList = gamePlayer.salvoesList(gamePlayer);
        if (gamePlayer.getSalvo().size() > 0) {
            if ((gamePlayer.getSalvo().size() > gamePlayer.getOpponentsSalvoes(gamePlayer).size())) {
                updatedSalvoList.remove(updatedSalvoList.size() - 1);
                updatedSalvoList.remove(updatedSalvoList.size() - 1);
                updatedSalvoList.remove(updatedSalvoList.size() - 1);
            }
            List<String> lastSalvoLocations = new ArrayList<>();
            for (Salvo salvo : gamePlayer.getSalvo()) {
                for (String location1 : salvo.getLocation()) {
                    if (!updatedSalvoList.contains(location1)) {
                        lastSalvoLocations.add(location1);
                    }
                }
            }
            return lastSalvoLocations;
        }
        updatedSalvoList.clear();
        return updatedSalvoList;
//        return null;
    }

    public List<String> getOpponentsHitsOnMe(GamePlayer gamePlayer) {
        List<String> opponentsHitsOnMe = new ArrayList<>();
        List<String> opponentsUpdatedSalvoList = gamePlayer.opponentsSalvoesList(gamePlayer);
        if (gamePlayer.getSalvo().size() != 0) {
            if ((gamePlayer.getSalvo().size() < gamePlayer.getOpponentsSalvoes(gamePlayer).size())) {
                opponentsUpdatedSalvoList.remove(opponentsUpdatedSalvoList.size() - 1);
                opponentsUpdatedSalvoList.remove(opponentsUpdatedSalvoList.size() - 1);
                opponentsUpdatedSalvoList.remove(opponentsUpdatedSalvoList.size() - 1);
            }
            if (gamePlayer.getShip() != null) {
                for (String shipLocation : gamePlayer.shipsList(gamePlayer)) {
                    for (String salvoLocation : opponentsUpdatedSalvoList) {
                        if (shipLocation.equals(salvoLocation)) {
                            if (!opponentsHitsOnMe.contains(shipLocation)) {
                                opponentsHitsOnMe.add(shipLocation);
                            }
                        }
                    }
                }
                return opponentsHitsOnMe;
            }
        }
        opponentsHitsOnMe.clear();
        return opponentsHitsOnMe;
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
                                                    @RequestBody List<Ship> newShipsList,
                                                    Authentication authentication) {
        List<Integer> sizeList = new ArrayList<>();
        List<String> locCheck = new ArrayList<>();
        for (Ship ship : newShipsList) {
            sizeList.add(ship.getLocation().size());
        }
        if (!usedIsLogged(authentication)) {
            return new ResponseEntity<>(makeMap("Error", "please login"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gpid) == null) {
            return new ResponseEntity<>(makeMap("Error", "game doesn't exist"), HttpStatus.UNAUTHORIZED);
        } else if (gamePlayerRepository.findById(gpid).getPlayer() != currentUser(authentication)) {
            return new ResponseEntity<>(makeMap("Error", "you have no permission to edit other player's ships"), HttpStatus.UNAUTHORIZED);
        } else if (sizeList.get(0) == 5 && sizeList.get(1) == 4 && sizeList.get(2) == 3 && sizeList.get(3) == 3 && sizeList.get(4) == 2) {
            for (Ship ship : newShipsList) {
                for (String loc : ship.getLocation()) {
                    if (locCheck.contains(loc)) {
                        return new ResponseEntity<>(makeMap("Error", "you cannot place one ship over the other"), HttpStatus.UNAUTHORIZED);
                    } else {
                        locCheck.add(loc);
                    }
                }
            }
            GamePlayer currentGP = gamePlayerRepository.findById(gpid);
            for (Ship newShip : newShipsList) {
                currentGP.addShip(newShip);
                shipRepository.save(newShip);
            }
            return new ResponseEntity<>(makeMap("Success", "the ship was placed"),HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(makeMap("Error", "ships are not in the right size required"), HttpStatus.UNAUTHORIZED);
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
            GamePlayer gamePlayer = new GamePlayer(game, currentUser(authentication));
            gamePlayerRepository.save(gamePlayer);
            return new ResponseEntity<>(makeSimpleMap("gpid", gamePlayer.getId()),HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/games/players/{gpid}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placeSalvoes(
                                                        @PathVariable long gpid,
                                                        @RequestBody Salvo newSalvo,
                                                        Authentication authentication) {
        boolean gameOver = false;
        GamePlayer currentGP = gamePlayerRepository.findById(gpid);
        if (currentGP.getSalvo().size() <= 1) {
            gameOver = false;
        } else {
            Salvo lastSalvo = (Salvo) currentGP.getSalvo().toArray()[currentGP.getSalvo().size() - 1];
            gameOver = gameIsOver(currentGP, lastSalvo);
        }
        if (!gameOver) {
            if (!usedIsLogged(authentication)) {
                return new ResponseEntity<>(makeMap("Error", "please login"), HttpStatus.UNAUTHORIZED);
            } else if (currentGP.getGame().getOpponent(currentGP) == null) {
                return new ResponseEntity<>(makeMap("Error", "wait for opponent to join the game"), HttpStatus.FORBIDDEN);
            } else if (currentGP.getOpponentsShips(currentGP).size() < 5) {
                return new ResponseEntity<>(makeMap("Error", "wait for opponent to place ships"), HttpStatus.FORBIDDEN);
            } else if (currentGP.getSalvo().size() > currentGP.getOpponentsSalvoes(currentGP).size()) {
                return new ResponseEntity<>(makeMap("Error", "please wait for opponent to shoot"), HttpStatus.FORBIDDEN);
            } else if (gamePlayerRepository.findById(gpid) == null) {
                return new ResponseEntity<>(makeMap("Error", "game doesn't exist"), HttpStatus.UNAUTHORIZED);
            } else if (gamePlayerRepository.findById(gpid).getPlayer() != currentUser(authentication)) {
                return new ResponseEntity<>(makeMap("Error", "you have no permission to edit other player's salvos"), HttpStatus.UNAUTHORIZED);
            } else {
                currentGP.addSalvo(newSalvo);
                salvoRepository.save(newSalvo);
                return new ResponseEntity<>(makeMap("Success", "the salvos were shot"), HttpStatus.CREATED);
            }
        }
        return new ResponseEntity<>(makeMap("Error", "game is over"), HttpStatus.UNAUTHORIZED);
    }

    public boolean gameIsOver(GamePlayer gamePlayer, Salvo salvo) {
        GamePlayer opponent = gamePlayer.getGame().getOpponent(gamePlayer);
        Salvo opponentsSalvo = salvo.opponentsSalvosByTurn(gamePlayer, salvo);
        if (gamePlayer.getShip().size() == 5) {
            if ((salvo.getSunkenShips(gamePlayer, salvo).size() == 5) && (salvo.getSunkenShips(opponent, opponentsSalvo).size() == 5)) {
                if (findScore(gamePlayer)) {
                    Score score = new Score(0.5, gamePlayer.getGame(), gamePlayer.getPlayer());
                    scoreRepository.save(score);
                }
                return true;
            } else if (salvo.getSunkenShips(gamePlayer, salvo).size() == 5) {
                if (findScore(gamePlayer)) {
                    Score score = new Score(0, gamePlayer.getGame(), gamePlayer.getPlayer());
                    scoreRepository.save(score);
                }
                return true;
            } else if (salvo.getSunkenShips(opponent, opponentsSalvo).size() == 5) {
                if (findScore(gamePlayer)) {
                    Score score = new Score(1, gamePlayer.getGame(), gamePlayer.getPlayer());
                    scoreRepository.save(score);
                }
                return true;
            }
        }
        return false;
    }

    public boolean findScore(GamePlayer gamePlayer) {
        for (Score score : scoreRepository.findByGame(gamePlayer.getGame())) {
            if (score.getPlayer() == gamePlayer.getPlayer()) {
                return  false;
            }
        }
        return true;
    }

    public String getGameState(GamePlayer gamePlayer, Salvo salvo) {
        if (!gameIsOver(gamePlayer, salvo)) {
            if (gamePlayer.getSalvo().size() == gamePlayer.getOpponentsSalvoes(gamePlayer).size()) {
                return "please shoot a salvo";
            } else if (gamePlayer.getSalvo().size() > gamePlayer.getOpponentsSalvoes(gamePlayer).size()) {
                return "waiting for opponent to shoot a salvo";
            } else {
                return "opponent is waiting for you to shoot a salvo";
            }
        }
        return "game is over";
    }

    public String getBeforeGameState(GamePlayer gamePlayer) {
        if (gamePlayer.getShip().size() < 5) {
            return "please place ships";
        } else if (gamePlayer.getGame().getOpponent(gamePlayer) == null) {
            return "waiting for opponent";
        } else if (gamePlayer.getOpponentsShips(gamePlayer).size() < 5) {
            return "waiting for opponent to place ships";
        } else {
            return "please shoot a salvo";
        }
    }
}