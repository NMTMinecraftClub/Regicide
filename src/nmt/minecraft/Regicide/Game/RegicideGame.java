package nmt.minecraft.Regicide.Game;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * A running instance of a regicide game.
 * <p>Responsible for keeping track of players and scores, starting and stopping the game, etc.</p>
 * <p>Regicide games should run independently of other regicide game isntances</p>
 * @author smanzana
 *
 */
public class RegicideGame {
	
	/**
	 * A map of the players involved in this current instance vs. their actual UUID.<br />
	 * We use a hashmap for cached lookups
	 */
	private HashMap<UUID, RPlayer> players;
	
	/**
	 * List of spawn locations for players
	 */
	private List<Location> spawnLocations;
	
	/**
	 * Is this game running? findout by querying the isRunning variable!<br /.
	 * A running game is one that is open to players joining -- it hasn't started yet.<br />
	 * TODO includle option for players to join after a match has started?
	 */
	private boolean isRunning;
	
	/**
	 * picks a RANDOM spawn location from the current list of spawn locations.
	 * @return A random spawn location
	 */
	public Location getSpawnLocation() {
		
		if (spawnLocations == null || spawnLocations.isEmpty()){
			return null;
		}
		
		Random rand = new Random();
		int rando = rand.nextInt(spawnLocations.size());
		return spawnLocations.get(rando);
	}
	
	public List<Location> getSpawnLocations() {
		return this.spawnLocations;
	}
	
	/**
	 * Returns whether or not this regicide game is currently running, or is open for players to join
	 * @return whether it is running.
	 */
	public boolean getIsRunning() {
		return this.isRunning;
	}
	
	public void addPlayer(Player player) {
		addPlayer(player.getUniqueId());
	}
	
	public void addPlayer(UUID player) {
		
		players.put(player, new RPlayer(player));
	}
	
}
