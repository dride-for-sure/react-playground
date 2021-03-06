package com.dennisjauernig.flashcards.service;

import com.dennisjauernig.flashcards.config.GameConfig;
import com.dennisjauernig.flashcards.controller.model.GameDto;
import com.dennisjauernig.flashcards.controller.model.GameDtoList;
import com.dennisjauernig.flashcards.controller.model.PlayerDto;
import com.dennisjauernig.flashcards.controller.model.QuestionDto;
import com.dennisjauernig.flashcards.model.Game;
import com.dennisjauernig.flashcards.model.GameMaster;
import com.dennisjauernig.flashcards.model.Player;
import com.dennisjauernig.flashcards.model.Question;
import com.dennisjauernig.flashcards.model.enums.Difficulty;
import com.dennisjauernig.flashcards.model.enums.GameStatus;
import com.dennisjauernig.flashcards.repository.GamesDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GamesService {

 private final QuestionsService questionsService;
 private final PlayerService playerService;
 private final GamesDb gamesDb;
 private final GameConfig gameConfig;

 @Autowired
 public GamesService (
         QuestionsService questionsService, PlayerService playerService,
         GamesDb gamesDb, GameConfig gameConfig ) {
  this.questionsService = questionsService;
  this.playerService = playerService;
  this.gamesDb = gamesDb;
  this.gameConfig = gameConfig;
 }

 // √ List all games with status PREPARE
 public List<GameDto> listAvailableGames () {
  return gamesDb.findAllByStatusIs( GameStatus.PREPARE ).stream()
                .map( game -> convertGameToDto( game ) )
                .collect( Collectors.toList() );
 }

 // √ Generate a new game
 public Game generateNewGame (
         PlayerDto playerDto,
         Difficulty difficulty,
         UUID gameId ) {
  List<Question> questionsList = questionsService.generateQuestionList( difficulty );
  List<QuestionDto> questionDtoList = questionsService.convertQuestionListToDto( questionsList );
  Player player = playerService.generateNewPlayer( playerDto, questionDtoList );
  return Game.builder()
             .id( gameId )
             .timestamp( Instant.now().getEpochSecond() )
             .difficulty( difficulty )
             .status( GameStatus.PREPARE )
             .master( GameMaster.builder()
                                .id( playerDto.getId() )
                                .name( playerDto.getName() )
                                .build() )
             .playerList( new ArrayList<>( Collections.singletonList( player ) ) )
             .questionList( questionsList )
             .build();
 }

 // √ Convert the game to a game dto
 public GameDto convertGameToDto ( Game game ) {
  return GameDto.builder()
                .id( game.getId() )
                .difficulty( game.getDifficulty() )
                .status( game.getStatus() )
                .master( game.getMaster() )
                .maxPoints( questionsService.calcMaxPoints( game ) )
                .playerDtoList( game.getPlayerList()
                                    .stream()
                                    .map( player -> playerService.convertPlayerToDto( player ) )
                                    .collect( Collectors.toList() ) )
                .build();
 }

 // √ Add a player to an existing game
 public Game addPlayerToGame ( Game game, Player player ) {
  boolean playerExists = hasPlayer( player.getId(), game );
  if ( playerExists ) {
   return game.toBuilder().build();
  }
  List<Player> updatedPlayerList = new ArrayList<>( game.getPlayerList() );
  updatedPlayerList.add( player );
  return game.toBuilder()
             .playerList( updatedPlayerList )
             .build();
 }

 // √ Promote a random player to gameMaster if he has left
 public Game promoteRandomPlayerToGameMaster ( Game game ) {
  int randomIndex = ( int ) ( Math.random() * game.getPlayerList().size() );
  Player playerToPromote = game.getPlayerList().get( randomIndex );
  GameMaster gameMaster = GameMaster.builder()
                                    .name( playerToPromote.getName() )
                                    .id( playerToPromote.getId() )
                                    .build();
  Game updatedGame = game.toBuilder()
                         .master( gameMaster )
                         .playerList( game.getPlayerList().stream()
                                          .map( player -> player.getId()
                                                                .equals( playerToPromote.getId() )
                                                  ? playerToPromote
                                                  : playerToPromote )
                                          .collect( Collectors.toList() ) )
                         .build();
  saveGame( updatedGame );
  return updatedGame;
 }

 // √ Set game status to FINISH
 public Game setGameStatusToFinish ( Game game ) {
  return game.toBuilder()
             .status( GameStatus.FINISH )
             .build();
 }

 // √ Set game status to PLAY
 public Game setGameStatusToPlay ( Game game ) {
  return game.toBuilder()
             .status( GameStatus.PLAY )
             .build();
 }

 // √ Find player within an existing game
 public boolean hasPlayer ( UUID playerId, Game game ) {
  return game.getPlayerList().stream()
             .anyMatch( player -> player.getId().equals( playerId ) );
 }

 // √ Check if another open game is allowed
 public boolean isMaxOpenGames () {
  return gamesDb.findAll().size() > gameConfig.getMaxOpenGames();
 }

 // √ Check if a specific player is gameMaster for a given game
 public boolean isGameMaster ( Game game, UUID playerId ) {
  return game.getMaster().getId().equals( playerId );
 }

 // √ Convert the List<GameDto> to GameDtoList
 public GameDtoList addTypeToGameDtoList ( List<GameDto> gameDtoList ) {
  return GameDtoList.builder().gameDtoList( gameDtoList ).build();
 }

 // √ Get game by Id
 public Optional<Game> getById ( UUID gameId ) {
  return gamesDb.findById( gameId );
 }

 // √ Check if game has status
 public boolean hasStatus ( GameStatus gameStatus, Game game ) {
  return game.getStatus().equals( gameStatus );
 }

 // √ Save game
 public void saveGame ( Game game ) {
  gamesDb.save( game );
 }

 // √ List all games
 public List<Game> listGames () {
  return gamesDb.findAll();
 }

 // √ Delete game by id
 public void deleteGameById ( UUID gameId ) {
  gamesDb.deleteById( gameId );
 }
}
