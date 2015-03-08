package nmt.minecraft.Regicide.IO;

import nmt.minecraft.Regicide.RegicidePlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This class listens to a button and triggers a PlayerJoinRegide event when a player
 * presses the registered button
 * @author William
 *
 */
public class ButtonListener implements Listener{
	
	private Location buttonLocation;
	private String gameInstance;
	/**
	 * Main Constructor, requires the button location.
	 * @param buttonLocation The button location.
	 */
	public ButtonListener (Location buttonLocation, String gameInstance) {
		this.buttonLocation = buttonLocation;
		this.gameInstance = gameInstance;
		RegicidePlugin.regicidePlugin.getLogger().info("Added ButtonListener at: " + buttonLocation.toString());
	}
	
	/**
	 * If the registered button is pressed, throw event.
	 * @param e The Interact event.
	 */
	@EventHandler
	public void blockActivated(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		double XCor = e.getClickedBlock().getLocation().getX();
		double YCor = e.getClickedBlock().getLocation().getY();
		double ButtonXCor = Math.floor(this.buttonLocation.getX());
		double ButtonYCor = Math.floor(this.buttonLocation.getY());
		if (XCor == ButtonXCor && YCor == ButtonYCor) {
			//Throw event
			Bukkit.getPluginManager().callEvent(new PlayerJoinRegicide(e.getPlayer(), this.gameInstance));
			RegicidePlugin.regicidePlugin.getLogger().info("Player: " + e.getPlayer().getName() + " requesting registration...");
		}
	
		
	}
}
