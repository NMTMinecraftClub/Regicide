package nmt.minecraft.Regicide.IO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This class listens to a button and triggers a PlayerJoinRegide event when a player
 * presses the registered button
 * @author William
 *
 */
public class ButtonListener{
	
	private Location buttonLocation;
	private String gameInstance;
	/**
	 * Main Constructor, requires the button location.
	 * @param buttonLocation The button location.
	 */
	public ButtonListener (Location buttonLocation, String gameInstance) {
		this.buttonLocation = buttonLocation;
		this.gameInstance = gameInstance;
	}
	
	/**
	 * If the registered button is pressed
	 * @param e
	 */
	@EventHandler
	public void blockActivated(PlayerInteractEvent e) {
		if (e.getClickedBlock().getLocation() == buttonLocation) {
			Bukkit.getPluginManager().callEvent(new PlayerJoinRegicide(e.getPlayer(), this.gameInstance));
		}
	
		
	}
}
