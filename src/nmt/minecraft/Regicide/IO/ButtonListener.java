package nmt.minecraft.Regicide.IO;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Events.RegicideGameEndEvent;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * This class listens to a button and adds them to a game.
 * presses the registered button
 * @author William
 *
 */
public class ButtonListener implements Listener{
	
	private Location buttonLocation;
	private RegicideGame gameInstance;
	/**
	 * Main Constructor, requires the button location.
	 * @param buttonLocation The button location.
	 */
	public ButtonListener (Location buttonLocation, RegicideGame gameInstance) {
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
		if (gameInstance == null) {
			return;
		}
		
//		if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
//			System.out.println("parts!");
//			if (e.isCancelled()) {
//				return;
//			}
//			
//
//			System.out.println("parts!");
//			if (e.getItem() != null)
//			if (e.getItem().getType() != Material.COOKED_BEEF || e.getPlayer().getFoodLevel() >= 19.9f) {
//				//not eating OR already full
//				return;
//			}
//
//			System.out.println("parts!");
//			if (gameInstance.getPlayer(e.getPlayer()) == null) {
//				return;
//			}
//			
//			//display food particles
//			WrapperPlayServerWorldParticles particle = new WrapperPlayServerWorldParticles();
//			particle.setLocation(e.getPlayer().getEyeLocation());
//			particle.setNumberOfParticles(5);
//			particle.setParticleEffect(ParticleEffect.ICONCRACK);
//
//			System.out.println("parts!");
//			
//			for (RPlayer p : gameInstance.getPlayers()) {
//				particle.sendPacket(p.getPlayer());
//			}
//		}
		if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		double XCor = e.getClickedBlock().getLocation().getX();
		double YCor = e.getClickedBlock().getLocation().getY();
		double ButtonXCor = Math.floor(this.buttonLocation.getX());
		double ButtonYCor = Math.floor(this.buttonLocation.getY());
		if (XCor == ButtonXCor && YCor == ButtonYCor) {
			RegicidePlugin.regicidePlugin.getLogger().info("Player: " + e.getPlayer().getName() + " requesting registration...");
			//Add player to game and teleport to correct lobby
			this.gameInstance.addPlayer(e.getPlayer());
		}
	
		
	}
	

	@EventHandler
	public void onGameEnd(RegicideGameEndEvent e) {
		gameInstance = null;
	}
}
