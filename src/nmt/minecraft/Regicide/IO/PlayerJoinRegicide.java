package nmt.minecraft.Regicide.IO;

import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is thrown whenever a Player is attempting to join an
 * active game of Regicide.
 * @author William
 *
 */
public class PlayerJoinRegicide extends Event {

	private final Player player;
	private final RegicideGame gameInstance;
	
	/**
	 * Main constructor of the event.
	 * @param player The Player who is joining.
	 * @param gameInstance The Instance of the Game to associate the player with.
	 */
	public PlayerJoinRegicide(Player player, RegicideGame gameInstance) {
		this.player = player;
		this.gameInstance = gameInstance;
	}
	
	/**
	 * Returns the player who is attempting to join a game.
	 * @return A Player object.
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Returns the Game Instance of the Player who is attempting to join.
	 * @return
	 */
	public RegicideGame getGameInstance() {
		return this.gameInstance;
	}
	/**
	 * Return null, cause it's required.
	 */
	@Override
	public HandlerList getHandlers() {
		return null;
	}
	
}
