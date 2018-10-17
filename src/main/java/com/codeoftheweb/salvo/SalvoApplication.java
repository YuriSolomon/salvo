package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository) {
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

			List<String> loc1 = Arrays.asList("E3","E4","E5","E7","E8");
			List<String> loc2 = Arrays.asList("A5","B5","C5","D5");
			List<String> loc3 = Arrays.asList("H1","H2","H3");
			List<String> loc4 = Arrays.asList("I5","I6","I7");
			List<String> loc5 = Arrays.asList("F5","G5");

			Ship ship1 = new Ship("carrier", loc1);
			Ship ship2 = new Ship("battleship", loc2);
			Ship ship3 = new Ship("destroyer", loc3);
			Ship ship4 = new Ship("Submarine", loc4);
			Ship ship5 = new Ship("portalBoat", loc5);

			shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);

		};
	}
}
