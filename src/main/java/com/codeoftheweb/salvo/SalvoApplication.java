package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository,
									  SalvoRepository salvoRepository) {
		return (args) -> {

			Player player1 = new Player("Jack", "jack@gmail.com");
			Player player2 = new Player("Chloe", "chloe@gmail.com");
			Player player3 = new Player("Kim", "kim@gmail.com");
			Player player4 = new Player("David", "david@gmail.com");
			Player player5 = new Player("Michelle", "michaelle@gmail.com");

			playerRepository.save(player1);
			playerRepository.save(player2);
			playerRepository.save(player3);
			playerRepository.save(player4);
			playerRepository.save(player5);

			Game game1 = new Game();
			Game game2 = new Game();
			Game game3 = new Game();

			gameRepository.save(game1);
			gameRepository.save(game2);
			gameRepository.save(game3);

			GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
			GamePlayer gamePlayer2 = new GamePlayer(game1, player2);

			gamePlayerRepository.save(gamePlayer1);
			gamePlayerRepository.save(gamePlayer2);

			List<String> loc1 = Arrays.asList("E3","E4","E5","E6","E7");
			List<String> loc2 = Arrays.asList("A9","B9","C9","D9");
			List<String> loc3 = Arrays.asList("H1","H2","H3");
			List<String> loc4 = Arrays.asList("I6","I7","I8");
			List<String> loc5 = Arrays.asList("G5","H5");
			List<String> loc6 = Arrays.asList("I2","H2","G2","F2","E2");
			List<String> loc7 = Arrays.asList("B6","B7","B8","B9");
			List<String> loc8 = Arrays.asList("D5","D6","D7");
			List<String> loc9 = Arrays.asList("E9","F9","G9");
			List<String> loc10 = Arrays.asList("G5","G6");

			Ship ship1 = new Ship("carrier", loc1);
			Ship ship2 = new Ship("battleship", loc2);
			Ship ship3 = new Ship("destroyer", loc3);
			Ship ship4 = new Ship("submarine", loc4);
			Ship ship5 = new Ship("portalBoat", loc5);
			Ship ship6 = new Ship("carrier", loc6);
			Ship ship7 = new Ship("battleship", loc7);
			Ship ship8 = new Ship("destroyer", loc8);
			Ship ship9 = new Ship("submarine", loc9);
			Ship ship10 = new Ship("portalBoat", loc10);

			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer1.addShip(ship3);
			gamePlayer1.addShip(ship4);
			gamePlayer1.addShip(ship5);
			gamePlayer2.addShip(ship6);
			gamePlayer2.addShip(ship7);
			gamePlayer2.addShip(ship8);
			gamePlayer2.addShip(ship9);
			gamePlayer2.addShip(ship10);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);

			List<String> salvoLoc1 = Arrays.asList("E6","C8","A2");
			List<String> salvoLoc2 = Arrays.asList("B4","H9","C3");
			List<String> salvoLoc3 = Arrays.asList("A4","E4","F7");
			List<String> salvoLoc4 = Arrays.asList("I4","A9","D3");
			List<String> salvoLoc5 = Arrays.asList("E2","F2","G2");

			Salvo salvo1 = new Salvo(1, salvoLoc1);
			Salvo salvo2 = new Salvo(1, salvoLoc2);
			Salvo salvo3 = new Salvo(2, salvoLoc3);
			Salvo salvo4 = new Salvo(2, salvoLoc4);
			Salvo salvo5 = new Salvo(3, salvoLoc5);

			gamePlayer1.addSalvo(salvo1);
			gamePlayer2.addSalvo(salvo2);
			gamePlayer1.addSalvo(salvo3);
			gamePlayer2.addSalvo(salvo4);
			gamePlayer1.addSalvo(salvo5);

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);

		};
	}
}
