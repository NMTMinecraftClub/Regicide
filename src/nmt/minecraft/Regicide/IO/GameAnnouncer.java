package nmt.minecraft.Regicide.IO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

/**
 * This class handles all mass announcements for a game instance.
 * @author William
 *
 */
public class GameAnnouncer {
	private static String aquaChat = ChatColor.AQUA+"";
	//private static String blueChat = ChatColor.BLUE+"";
	private static String goldChat = ChatColor.GOLD+"";
	private static String greenChat = ChatColor.GREEN+"";
	//private static String redChat = ChatColor.RED+"";
	//private static String boldChat = ChatColor.BOLD+"";
	private static String resetChat = ChatColor.RESET+"";
	/**
	 * This method handles announcing a player join event.
	 * @param gameInstance The game instance to alert.
	 * @param joiningPlayer The joining player.
	 */
	public static void GameJoin(RegicideGame gameInstance, RPlayer joiningPlayer) {
		String Message;
		String playerName = joiningPlayer.getPlayer().getName();
		Message = aquaChat + "Welcome " + goldChat + playerName + " to the game!" + resetChat;
		MessageGamePlayers(gameInstance, Message);
	}
	
	/**
	 * This method handles announcing a player leave event
	 * @param gameInstance
	 * @param leavingPlayer
	 */
	public static void GameLeave(RegicideGame gameInstance, Player leavingPlayer) {
		String Message;
		String playerName = leavingPlayer.getName();
		Message = goldChat + playerName + aquaChat + " has left the game.";
		MessageGamePlayers(gameInstance, Message);
	}
	
	/**
	 * This method announces to all players not in a game that a game is open for registration.
	 * @param gameInstance The Game that is now open
	 */
	public static void OpenGame(RegicideGame gameInstance) {
		World world = gameInstance.getLobbyLocation().getWorld();
		//A golden Game Name...
		String GameName = goldChat + gameInstance.getName() + resetChat;
		String Message = aquaChat + "The Regicide Game: " + GameName + aquaChat + " is now " + greenChat + "open!" + resetChat;
		MessageAllNonGamers(world, Message);
	}
	
	/**
	 * This method sends endgame stats to all players in that game instance. 
	 * @param gameInstance The Game Instance that is ending
	 */
	public static void EndGame(RegicideGame gameInstance) {
		ArrayList<RPlayer> TopScorePlayers;
		int maxPadding = 12;
		TopScorePlayers = gameInstance.calculateWinners();
		
		if (TopScorePlayers == null) {
			//There were no players!
			RegicidePlugin.regicidePlugin.getLogger().warning("Game terminating with no players.");
			return;
		}
		
		StringBuilder endScore = new StringBuilder();
		endScore.append(greenChat + "End Game Results:" + resetChat + "\n");
		endScore.append("Top Scoring Players!\n");
		for (int i = 0; i < 3 && i < TopScorePlayers.size(); i++) {
			int PlayerScore = TopScorePlayers.get(i).getPoints();
			String PlayerName = TopScorePlayers.get(i).getPlayer().getName();
			
			endScore.append(goldChat + PlayerName + resetChat);
			//Add padding for name-score alignment
			for (int j = 0; j < maxPadding - PlayerName.length(); j++) {
				endScore.append(".");
			}
			//Add score
			endScore.append(aquaChat + " " + PlayerScore + resetChat + "\n");
		}
		MessageGamePlayers(gameInstance,endScore.toString());
	}
	
	/**
	 * This method sends a string message to all players in a Game Instance
	 * @param gameInstance The game instance for announcements
	 * @param message The message to be sent
	 */
	private static void MessageGamePlayers(RegicideGame gameInstance, String message) {
		for (RPlayer p : gameInstance.getPlayers()) {
			Player member = p.getPlayer();
			member.sendMessage(message);
		}
	}
	
	/**
	 * This method sends a message to all players NOT in currently in a game.
	 * @param message
	 */
	private static void MessageAllNonGamers(World world, String Message) {
		//Obtain all players in a world
		List<Player> allPlayers = world.getPlayers();
		//Find all games
		List<RegicideGame> games = RegicidePlugin.regicidePlugin.getGames();
		List<RPlayer> RegicidePlayers = new ArrayList<RPlayer>();
		//Obtain a list of all players currently in a game
		for (RegicideGame game : games) {
			List<RPlayer> tmp = game.getPlayers();
			RegicidePlayers.addAll(tmp);
		}
		//Remove all players that are currently in a game
		for (RPlayer rp : RegicidePlayers) {
			Player tmp = rp.getPlayer();
			allPlayers.remove(tmp);
		}
		//Message players
		for (Player p : allPlayers) {
			p.sendMessage(Message);
		}
	}
}
