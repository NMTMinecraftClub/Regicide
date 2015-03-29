package nmt.minecraft.Regicide.IO;

import java.util.Arrays;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class handles all commands sent to /regicide
 * @author William
 *
 */
public class RegicideCommands implements CommandExecutor{
	private String blueChat = ChatColor.BLUE+"";
	private String goldChat = ChatColor.GOLD+"";
	private String greenChat = ChatColor.GREEN+"";
	private String redChat = ChatColor.RED+"";
	private String boldChat = ChatColor.BOLD+"";
	private String resetChat = ChatColor.RESET+"";
	/**
	 * This method determines how a sent command is to be interpreted.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//All commands must be sent by a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a playah!");
			return false;
		}
		if(args.length == 0){
			sender.sendMessage("Something went wrong... We need more arguments");
			sender.sendMessage("Valid commands are register, start, setSpawn, or setLobby");
			return false;
		}
		//The help menu
		if (args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(greenChat + boldChat + "===Help===" + resetChat);
			sender.sendMessage("Usage: " + redChat + "/regicide " + blueChat + "[command] " + goldChat + "[command arguments]" + resetChat);
			sender.sendMessage(greenChat + boldChat + "===Commands===" + resetChat);
			sender.sendMessage(blueChat + "register " + goldChat + " [game name]" + resetChat + " registers a new instance of Regicide");
			sender.sendMessage(blueChat + "start    " + goldChat + " [game name]" + resetChat + " starts the specified game");
			sender.sendMessage(blueChat + "setSpawn " + goldChat + " [game name]" + resetChat + " sets another spawn point for the specified game");
			sender.sendMessage(blueChat + "setLobby " + goldChat + " [game name]" + resetChat + " sets the lobby location");
			sender.sendMessage(blueChat + "setExit  " + goldChat + " [game name]" + resetChat + " sets the exit location of a game");
			sender.sendMessage(blueChat + "open     " + goldChat + " [game name]" + resetChat + " opens a game for registration");
			sender.sendMessage(blueChat + "leave    " + resetChat + " leave regicide");
			return true;
		}
		
		//Sender must now be a player
		//Register buttons/games
		if (args[0].equalsIgnoreCase("register")) {
			if(args.length != 2){
				sender.sendMessage("Wrong number of arguments: /regicide register [name]");
				return false;
			}
			registerGame(sender, args);
			return true;
		}
		
		//Start games
		if (args[0].equalsIgnoreCase("start")) {
			if(args.length != 2){
				sender.sendMessage("Wrong number of arguments: /regicide start [name]");
				return false;
			}
			startGame(sender, args);
			return true;
		}
		
		//Set a Game Spawn
		if (args[0].equalsIgnoreCase("setSpawn")) {
			if(args.length != 2){
				sender.sendMessage("Wrong number of arguments: /regicide setSpawn [name]");
				return false;
			}
			setSpawn(sender, args);
			return true;
		}
		
		//Set Lobby
		if (args[0].equalsIgnoreCase("setLobby")) {
			if(args.length != 2){
				sender.sendMessage("Wrong number of arguments: /regicide setLobby [name]");
				return false;
			}
			setLobby(sender, args);
			return true;
		}
		
		//Open Game
		if (args[0].equalsIgnoreCase("open")) {
			this.openGame(sender, args);
		}
		
		//Leave Game
		if (args[0].equalsIgnoreCase("leave")) {
			this.leaveGame(sender);
		}
		
		//Set Game Exit
		if (args[0].equalsIgnoreCase("setExit")) {
			if(args.length != 2){
				sender.sendMessage("Wrong number of arguments: /regicide Exit [name]");
				return false;
			}
			setExit(sender, args);
			return true;
		}
		sender.sendMessage("Something went wrong...");
		sender.sendMessage("Valid commands are register, start, setSpawn, or setLobby");
		return false;
	}
	
	public static List<String> getCommandList(){
		String[] commands = {"register", "setLobby", "setSpawn", "setExit", "start", "leave", "open", "help"};
		return Arrays.asList(commands);
	}
	
	/**
	 * A player must register a button, and which in turn registers a game.
	 * @param sender The sender.
	 * @param args The arguments to the command.
	 * @return True if the registration completed, false if otherwise.
	 */
	public boolean registerGame(CommandSender sender, String[] args) {
		
		if (args.length != 2) {
			sender.sendMessage("Not the correct number of arguments: /regicide register <game name>g");
			return false;
		}
		List<RegicideGame> Games = RegicidePlugin.regicidePlugin.getGames();
		//Check to see if a Game with the same name already exists
		for (RegicideGame g : Games) {
			if (g.getName().equals(args[1])) {
				//Alert User that a game with the same name already exists.
				sender.sendMessage("Game with name: \"" + args[1] + "\" already exists!");
				return false;
			}
		}
		//Add new Game to lissssst
		//@Debug
		RegicidePlugin.regicidePlugin.getLogger().info("Adding game: " + args[1]);
		RegicideGame game = new RegicideGame(args[1]);
		Games.add(game);
		//Register Button
		ButtonListener listen = new ButtonListener(((Player) sender).getLocation(), game);
		Bukkit.getPluginManager().registerEvents(listen, RegicidePlugin.regicidePlugin);
		sender.sendMessage("Successfully registered game: " + args[1]);
		return true;
	}
	
	/**
	 * This method starts the specified game.
	 * @param sender The command sender.
	 * @param args The command arguments.
	 * @return
	 */
	public boolean startGame(CommandSender sender, String[] args) {
		if (args.length > 2 || args.length == 0) {
			return false;
		}
		String gameName = args[1];
		List<RegicideGame> Games = RegicidePlugin.regicidePlugin.getGames();
		for (RegicideGame g : Games) {
			if (g.getName().equals(gameName)) {
				//Check to see if game is already running
				if (g.getIsRunning()) {
					sender.sendMessage(redChat + boldChat + "ERROR! " + resetChat + redChat + "Game: " + g.getName() + " is already running!" + resetChat);
					return false;
				}
				
				sender.sendMessage("Started Game Instance: " + g.getName());
				RegicidePlugin.regicidePlugin.getLogger().info("Started Game: " + g.getName());
				g.startGame();
				return true;
			}
		}
		RegicidePlugin.regicidePlugin.getLogger().info("Warning! Could not find: \"" + gameName + "\" in registered games!");
		sender.sendMessage("That game is not registered! Make sure you register with /regicide register <game name>");
		return false;
	}
	
	/**
	 * This method adds a spawn point to the specified game to the location of the sender.
	 * @param sender The sender of the command.
	 * @param args The command arguments.
	 * @return
	 */
	public boolean setSpawn(CommandSender sender, String[] args) {
		List<RegicideGame> games = RegicidePlugin.regicidePlugin.getGames();
		
		for (RegicideGame game : games) {
			if (game.getName().equalsIgnoreCase(args[1])) {
				//Check to see if game is already running
				if (game.getIsRunning()) {
					sender.sendMessage(redChat + boldChat + "ERROR! " + resetChat + redChat + "Game: " + game.getName() + " is already running!" + resetChat);
					return false;
				}
				Location loc = ((Player) sender).getLocation();
				game.addSpawnLocation(loc);
				
				sender.sendMessage("Successfully registered starting position!");
				return true;
			}
		}
		
		sender.sendMessage("Unable to locate instance: " + args[1]);		
		return false;
	}
	
	/**
	 * This method sets the Game lobby to the location of the sender.
	 * @param sender The command sender.
	 * @param args The command arguments.
	 * @return
	 */
	public boolean setLobby(CommandSender sender, String[] args) {
		if (args.length < 2) {
			sender.sendMessage("Please supply the name of the instance to add this spawn point to!");
			return false;
		}
		

		for (RegicideGame game : RegicidePlugin.regicidePlugin.getGames()) {
			if (game.getName().equalsIgnoreCase(args[1])) {
				//Check to see if game is already running
				if (game.getIsRunning()) {
					sender.sendMessage(redChat + boldChat + "ERROR! " + resetChat + redChat + "Game: " + game.getName() + " is already running!" + resetChat);
					return false;
				}
				Location loc = ((Player) sender).getLocation();
				game.setLobbyLocation(loc);
				
				sender.sendMessage("Successfully registered lobby position!");
				return true;
			}
		}
		
		sender.sendMessage("Unable to locate instance: " + args[1]);		
		return false;
		
	}

	/**
	 * This method allows a player to leave all games.
	 * @param sender The sender of the command
	 * @return
	 */
	public boolean leaveGame(CommandSender sender) {
		List<RegicideGame> games = RegicidePlugin.regicidePlugin.getGames();
		for (RegicideGame game : games) {
			game.removePlayer((Player) sender);
			sender.sendMessage("You have left Regicide Game " + game.getName());
		}
		return true;
	}
	
	/**
	 * This method opens a game for player registration.
	 * @param sender The command sender.
	 * @param args The command arguments.
	 * @return
	 */
	public boolean openGame(CommandSender sender, String[] args) {
		List<RegicideGame> games = RegicidePlugin.regicidePlugin.getGames();
		for (RegicideGame game : games) {
			if (game.getName().equalsIgnoreCase(args[1])) {
				//Check to see if the game is running
				if (game.getIsRunning()) {
					sender.sendMessage(redChat + boldChat + "ERROR! " + resetChat + redChat + "Game: " + game.getName() + " is already running!" + resetChat);
					return false;
				}
				//Game is not running
				game.open();
			}
		}
		return true;
	}
	
	/**
	 * This method sets a Game Exit location.
	 * @param sender The command sender.
	 * @param args The command arguments.
	 * @return
	 */
	public boolean setExit(CommandSender sender, String[] args) {
		if (args.length < 2) {
			sender.sendMessage("Please supply the name of the instance to add this exit point to!");
			return false;
		}
		

		for (RegicideGame game : RegicidePlugin.regicidePlugin.getGames()) {
			if (game.getName().equalsIgnoreCase(args[1])) {
				//do the stuff
				Location loc = ((Player) sender).getLocation();
				game.setExitLocation(loc);
				
				sender.sendMessage("Successfully registered Exit position!");
				return true;
			}
		}
		
		sender.sendMessage("Unable to locate instance: " + args[1]);		
		return false;
		
	}
}
