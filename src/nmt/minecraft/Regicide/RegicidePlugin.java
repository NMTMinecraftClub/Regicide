package nmt.minecraft.Regicide;

import nmt.minecraft.Regicide.IO.RegisterButton;

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
	
	/**
	 * Are we gonna have any config options? 
	 */
	@Override
	public void onLoad() {
		
	}
	
	@Override
	public void onEnable() {
		this.getCommand("regicide").setExecutor(new RegisterButton());
		RegicidePlugin.regicidePlugin = this;
	}
	
	@Override
	public void onDisable() {
		
	}
	
	
}
