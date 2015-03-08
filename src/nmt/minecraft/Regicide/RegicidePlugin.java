package nmt.minecraft.Regicide;

import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.IO.RegicideCommands;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin class for the Regicide game mode.
 * <p>
 * This class specifically creates basic game control listeners and a command listener,
 * but doesn't create an instance of a game or anything else.</p>
 * @author Skyler, ADD YOUR NAME HERE
 *
 */
public class RegicidePlugin extends JavaPlugin {
	
	public static RegicidePlugin regicidePlugin;
	private List<RegicideGame> games;
	/**
	 * Are we gonna have any config options? 
	 */
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onEnable() {
		this.getLogger().info("Loading command listener..");
		this.getCommand("regicide").setExecutor(new RegicideCommands());
		RegicidePlugin.regicidePlugin = this;
		this.getLogger().info("Creating Empty Game list...");
		this.games = new LinkedList<RegicideGame>();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	/**
	 * Returns the list of games under the Regicide Plugin
	 * @return The List
	 */
	public List<RegicideGame> getGames() {
		return this.games;
	}
	
	
}
