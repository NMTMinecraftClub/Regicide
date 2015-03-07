package nmt.minecraft.Regicide.Game;

import java.util.HashMap;
import java.util.LinkedList;
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
	 * Is this game running? find out by querying the isRunning variable!<br /.
	 * A running game is one that is open to players joining -- it hasn't started yet.<br />
	 * TODO include option for players to join after a match has started?
	 */
	private boolean isRunning;
	
	/**
	 * Each game has a lobby. This is the spawning location for this games lobby
	 */
	private Location lobbyLocation;
	
	
	/**
	 * Create a blank regicide game.
	 */
	public RegicideGame() {
		this.isRunning = false;
		players = new HashMap<UUID, RPlayer>();
		spawnLocations = new LinkedList<Location>();
		lobbyLocation = null;
	}
	
	
	
	
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
	
	/**
	 * Simply returns the whole list of spawn locatiosn associated with this game instance
	 * @return The list of spawn locations
	 */
	public List<Location> getSpawnLocations() {
		return this.spawnLocations;
	}
	
	
	public Location getLobbyLocation() {
		return this.lobbyLocation;
	}
	
	/**
	 * Returns whether or not this regicide game is currently running, or is open for players to join
	 * @return whether it is running.
	 */
	public boolean getIsRunning() {
		return this.isRunning;
	}
	
	/**
	 * Adds the passed {@link org.bukkit.Player.Player Player} passed to the game as an active participant
	 * @param player
	 */
	public void addPlayer(Player player) {
		addPlayer(player.getUniqueId());
	}
	
	/**
	 * Adds the player to the game as an active participant
	 * @param player
	 */
	public void addPlayer(UUID player) {
		//TODO add check against already existing player
		players.put(player, new RPlayer(player));
	}
	
	/**
	 * Makes all the players invisible
	 */
	private void makePlayersInvisible(){
		for(RPlayer player : players.values()){
			Player tmp = player.getPlayer();
			for (RPlayer players : players.values()) {
			    players.getPlayer().hidePlayer(tmp);
			}
		}
	}
	
	/**
	 * Makes all the players visable
	 */
	private void makePlayersVisable(){
		for(RPlayer player : players.values()){
			Player tmp = player.getPlayer();
			for (RPlayer players : players.values()) {
			    players.getPlayer().showPlayer(tmp);
			}
		}
	}
	
	public boolean removePlayer(Player player) {
		return removePlayer(player.getUniqueId());
	}
	
	/**
	 * Attempts to remove the passed player and return whether successfull
	 * @param player
	 * @return
	 */
	public boolean removePlayer(UUID player) {
		return removePlayer(getPlayer(player));
	}
	
	public boolean removePlayer(RPlayer player) {
		return (players.remove(player) != null);
	}
	
	/**
	 * Looks up the player in cache and returns the associated RPlayer
	 * @param player
	 * @return
	 */
	public RPlayer getPlayer(Player player) {
		return getPlayer(player.getUniqueId());
	}
	
	/**
	 * Looks up the player and returns the corresponding RPlayer
	 * @param player
	 * @return
	 */
	public RPlayer getPlayer(UUID player) {
		return players.get(player);
	}
	
}
