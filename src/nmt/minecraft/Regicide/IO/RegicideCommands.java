package nmt.minecraft.Regicide.IO;

import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

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
		//Sender must now be a player
		if (label == "register") {
			registerGame(sender, args);
			return true;
		}
		return false;
	}
	
	/**
	 * A player must register a button, and which in turn registers a game.
	 * @param sender The sender.
	 * @param args The arguments to the command.
	 * @return True if the registration completed, false if otherwise.
	 */
	public boolean registerGame(CommandSender sender, String[] args) {
		if (args.length > 2 || args.length == 0) {
			return false;
		}
		List<RegicideGame> Games = RegicidePlugin.regicidePlugin.getGames();
		//Check to see if a Game with the same name already exists
		for (RegicideGame g : Games) {
			if (g.getName() == args[0]) {
				//Alert User that a game with the same name already exists.
				sender.sendMessage("Game with name: \"" + args[0] + "\" already exists!");
				return false;
			}
		}
		//Add new Game to lissssst
		//@Debug
		
		RegicideGame game = new RegicideGame(args[0]);
		Games.add(game);
		//Register Button
		new ButtonListener(((Player) sender).getLocation(), args[0]);
		return true;
	}
	
	public boolean startGame(CommandSender sender, String[] args) {
		if (args.length > 2 || args.length == 0) {
			return false;
		}
		String gameName = args[0];
		List<RegicideGame> Games = RegicidePlugin.regicidePlugin.getGames();
		for (RegicideGame g : Games) {
			if (g.getName() == gameName) {
				sender.sendMessage("Started Game Instance: " + g.getName());
				g.startGame();
			}
		}
		return true;
	}
}
