package nmt.minecraft.Regicide.Game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.Player.RPlayer;
import nmt.minecraft.Regicide.ScoreBoard.ScoreBoard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A running instance of a regicide game.
 * <p>Responsible for keeping track of players and scores, starting and stopping the game, etc.</p>
 * <p>Regicide games should run independently of other regicide game isntances</p>
 * @author smanzana
 *
 */
public class RegicideGame implements Listener {
	
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
	 * Name of the match
	 */
	private String name;
	
	private RPlayer king;
	
	private GameTimer timer;
	
	private long endTime = 600;
	
	private ScoreBoard board;
	
	
	/**
	 * Create a blank regicide game.
	 */
	public RegicideGame(String name) {
		this.name = name;
		this.isRunning = false;
		players = new HashMap<UUID, RPlayer>();
		spawnLocations = new LinkedList<Location>();
		lobbyLocation = null;
				
		board = new ScoreBoard();
		
		Bukkit.getPluginManager().registerEvents(this, RegicidePlugin.regicidePlugin);
	}

	
	/**
	 * Set the spawn location of this games lobby
	 * @param loc
	 */
	public void setLobbyLocation(Location loc) {
		this.lobbyLocation = loc;
	}
	
	/**
	 * Adds a possible spawn location
	 * @param spawnLocation
	 */
	public void addSpawnLocation(Location spawnLocation) {
		this.spawnLocations.add(spawnLocation);
	}
	
	
	public void startGame() {
		if (isRunning) {
			return;
			//can't start a game that's already running
		}
		
		if (players.size() <= 0) {
			RegicidePlugin.regicidePlugin.getLogger().severe("Unable to start game because there are no registered players!");
			return;
		}
		
		isRunning = true;
		
		//make players invis 
		makePlayersInvisible();
		
		//get all players and teleport them to a spawn location, and set them to an initial state
		for (RPlayer player : players.values()) {
			player.teleport(getSpawnLocation());
			player.setInitialState();
		}
		
		makeRandomKing();//make someone the king
		
		timer = new GameTimer(this, endTime);
		timer.runTaskTimer(RegicidePlugin.regicidePlugin, 20, 20);
		
		board.displayScoreboard(players.values());
		board.updateKing(king);
		
	}
	
	private void makeRandomKing(){
		int kingIndex;
		Random rand = new Random();
		kingIndex = rand.nextInt(players.size());
		
		king = new LinkedList<RPlayer>(players.values()).get(kingIndex);
		king.makeKing();
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Removes all added spawn locations and works with none.
	 */
	public void resetSpawnLocations() {
		this.spawnLocations = new LinkedList<Location>();
	}
	
	/**
	 * Tries to remove the passed location from the possible spawn locations
	 * @param loc
	 * @return
	 */
	public boolean removeSpawnLocation(Location loc) {
		return this.spawnLocations.remove(loc);
	}
	
	/**
	 * Return a list of involved RPlayers
	 * @return
	 */
	public List<RPlayer> getPlayers() {
		return new LinkedList<RPlayer>(players.values());
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
		if (players.containsKey(player)) {
			return;
		}
		
		players.put(player, new RPlayer(player));
	}
	
	/**
	 * Disguise all players that are part of this fight
	 */
	private void makePlayersInvisible(){
		for(RPlayer player : players.values()){
			player.disguise();
		}
	}
	
	/**
	 * Makes all the players visable
	 */
	private void makePlayersVisable(){
		for(RPlayer player : players.values()){
			player.unDisguise();
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
	
	public ScoreBoard getBoard() {
		return this.board;
	}
	
	/**
	 * Score a point for the current king
	 */
	public void scorePoint() {
		king.addPoint();
		board.updateScore(king, king.getPoints());
	}
	
	public void endGame() {
		this.isRunning = false;
		RegicidePlugin.regicidePlugin.getLogger().info("Game [" + name + "] now stopping!");
		
		makePlayersVisable();
		for (RPlayer player : players.values()) {
			player.getPlayer().sendMessage("Game now ending. This is lame put more fancy ending!");
		}
		
		players.clear();
		
		//TODO PUT FINISHING STUFF
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
			if(e.getEntity() instanceof Villager && e.getDamager() instanceof Player){
				//TODO check if villager, if so nauseua
				Player player = (Player)e.getDamager();
				if(getPlayer(player) != null){
					player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,  200, 1));//find nauseua
				}
				e.setCancelled(true);
			}
			return;
		}
		
		Player player = (Player) e.getEntity();
		
		if (e.getDamage() >= player.getHealth()) {
			//player gonna die!
			e.setCancelled(true);
			player.setHealth(player.getMaxHealth());
			
			//check if they were the king
			RPlayer rplay = getPlayer(player);
			if (rplay.isKing()) {
				//register new king!
				rplay.die();
				this.king = getPlayer((Player) e.getDamager());
				king.makeKing();
				board.updateKing(king);
			}
			
			//teleport needs to come after the fireworks in the die call
			player.teleport(getSpawnLocation());
		}
	}
	
	/**
	 * Gives the king bread when he needs it
	 */
	@EventHandler(priority=EventPriority.HIGH)
	public void onKingEat(PlayerItemConsumeEvent e) {
		Player player = (Player) e.getPlayer();
		RPlayer rplayer = getPlayer(player);
		if(rplayer != null && rplayer.isKing()){
			player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 1));
		}
		
	}
	
	@EventHandler
	public void onEatFood(PlayerInteractEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getItem() != null)
		if (e.getItem().getType() != Material.COOKED_BEEF || e.getPlayer().getFoodLevel() >= 19.9f) {
			//not eating OR already full
			return;
		}
		
		//display food particles
		//e.getPlayer().playEffect(e.getPlayer().getLocation(), Effect., arg2);;
		//WrapperPlayerServerWorldParticles particle = new WrapperPlayerServerWorldParticles();
		
		
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			//if the player is gonna die teleport them and fill they're health
			Player player = (Player) e.getEntity();

			if(getPlayer(player) != null && e.getDamage() >= player.getHealth()){
				e.setCancelled(true);
				player.setHealth(player.getMaxHealth());
				e.getEntity().teleport(getSpawnLocation());
				
				//TODO if the player is on fire put them out
				player.getActivePotionEffects().clear();
				player.setFireTicks(0);
				
				//lose king if you die
				RPlayer rplayer = getPlayer(player);
				if(rplayer.isKing()){
					rplayer.die();
					makeRandomKing();
				}
			}
		}
	}
	
	@EventHandler
	public void onThingGettingSetOnFireEvent(EntityCombustEvent e){
		if(e.getEntity() instanceof Player){
			Player player = (Player)e.getEntity();//if the thing on fire is a player in the game, don't allow it to burn
			if(getPlayer(player) != null){
				e.setCancelled(true);
				player.setFireTicks(0);
			}
		}else if(e.getEntity() instanceof Villager){
			e.setCancelled(true);
		}
	}
}
