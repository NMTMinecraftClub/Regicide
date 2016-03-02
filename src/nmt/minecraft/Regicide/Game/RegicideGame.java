package nmt.minecraft.Regicide.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.Events.RegicideGameEndEvent;
import nmt.minecraft.Regicide.Game.Player.RPlayer;
import nmt.minecraft.Regicide.Game.Player.RegicideVillager;
import nmt.minecraft.Regicide.Game.Scheduling.EatParticleEffect;
import nmt.minecraft.Regicide.Game.Scheduling.EndGameCinematic;
import nmt.minecraft.Regicide.Game.Scheduling.GameTimer;
import nmt.minecraft.Regicide.IO.GameAnnouncer;
import nmt.minecraft.Regicide.IO.GameConfigManager;
import nmt.minecraft.Regicide.ScoreBoard.ScoreBoard;


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
	 * Keep track of all villagers related to this game instance
	 */
	private Set<RegicideVillager> villagers;
	
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
	
	private static final long endTime = 900;
	
	private static final long scoreInterval = 10;
	
	private ScoreBoard board;
	
	private boolean isOpen;
	
	private Location exitLocation;
	
	private Location firstPlace, secondPlace, thirdPlace, otherPlace;
	
	private GameConfigManager configManager;
	
	private static Random rand = new Random();
	
	/**
	 * Create a blank regicide game.
	 */
	public RegicideGame(String name) {
		this.name = name;
		this.isRunning = false;
		
		players = new HashMap<UUID, RPlayer>();
		villagers = new HashSet<RegicideVillager>();
		
		
		spawnLocations = new LinkedList<Location>();
		lobbyLocation = null;
		exitLocation = null;
		
		firstPlace = null; 
		secondPlace = null; 
		thirdPlace = null; 
		otherPlace = null;
		
		board = new ScoreBoard();
		
		Bukkit.getPluginManager().registerEvents(this, RegicidePlugin.regicidePlugin);
		isOpen = false;
		
		this.configManager = new GameConfigManager(this);
	}
	
	public void open() {
		this.isOpen = true;
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
		
		if(this.exitLocation == null){
			RegicidePlugin.regicidePlugin.getLogger().severe("Unable to start game because there is no exit Location!");
			return;
		}
		
		isRunning = true;
		isOpen = false;
		
		//make players invis 
		makePlayersInvisible();
		
		//get all players and teleport them to a spawn location, and set them to an initial state
		for (RPlayer player : players.values()) {
			player.teleport(getSpawnLocation());
			player.setInitialState();
		}
		
		makeRandomKing();//make someone the king
		
		timer = new GameTimer(this, endTime, scoreInterval);
		timer.runTaskTimer(RegicidePlugin.regicidePlugin, 20, 20);
		
		board.displayScoreboard(players.values());
		board.updateKing(king);
		
		spawnVillagers(Math.min(players.size() * 5, 100));
		
		//set the time to day
		Iterator<RPlayer> it = players.values().iterator();
		if(it.hasNext()){
			it.next().getPlayer().getLocation().getWorld().setTime(600);
		}
	}
	
	private void makeRandomKing(){
		int kingIndex;
		Random rand = new Random();
		kingIndex = rand.nextInt(players.size());
		
		if (king != null) {
			king.setIsKing(false);
		}
		
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
		if (!isOpen) {
			Bukkit.getPlayer(player).sendMessage(ChatColor.DARK_RED + "ERROR: "+ChatColor.RESET+"Game is not yet open, or has already closed!");
			return;
		}
		
		if (players.containsKey(player)) {
			return;
		}
		
		players.put(player, new RPlayer(player));
		getPlayer(player).teleport(getLobbyLocation());
		GameAnnouncer.GameJoin(this, getPlayer(player));
		getPlayer(player).getPlayer().sendMessage(ChatColor.GREEN+ "You have sucessfully joined "+ ChatColor.GOLD + this.name + ChatColor.RESET);
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
		RPlayer plays = players.remove(player.getPlayer().getUniqueId());
		if (plays == null) {
			return false;
		}
		
		if (exitLocation == null) {
			RegicidePlugin.regicidePlugin.getLogger().warning("No exit location has been set!");
		} else {
			player.teleport(exitLocation);
		}
		
		if (isRunning) {
			if (players.isEmpty() || players.size() < 2) {
				
				Bukkit.getPluginManager().callEvent(new RegicideGameEndEvent(this));
				
			}
			else if (player.isKing()) {
	
				if (plays.getLastHitBy() == null || getPlayer(plays.getLastHitBy().getPlayer()) != null) {
					makeRandomKing();
					board.updateKing(king);
				}
				else {
					this.king = plays.getLastHitBy();
					king.makeKing();
					board.updateKing(king);
				}
			}
		}
		board.removePlayer(player);
		player.setEndState();
		
		return true;
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
		
		//check if game is over
		//if we put point cap (first to n points) put it here
		
		if (king.getPoints() > endTime / scoreInterval) {
			//no way anyone could get as much points to beat it
			endGame();
		}
	}
	
	/**
	 * This method handles the exiting conditions of the game
	 */
	public void endGame() {
		this.isRunning = false;
		RegicidePlugin.regicidePlugin.getLogger().info("Game [" + name + "] now stopping!");
		GameAnnouncer.EndGame(this);
		makePlayersVisable();
		timer.cancel();

		//TODO PUT FINISHING STUFF
		EndGameCinematic cine = new EndGameCinematic(this, this.calculateWinners());
		cine.run();
		
		LinkedList<RPlayer> newList = new LinkedList<RPlayer>(players.values());
		for (RPlayer player : newList) {
			removePlayer(player);
		}
		
		RegicidePlugin.regicidePlugin.endGame(this);
		
		removeVillagers();
		
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerDamagedByEntity(EntityDamageByEntityEvent e) {
		//if the player is being hurt and they are part of the game
		if(e.getEntity() instanceof Player && getPlayer((Player)e.getEntity()) != null){
			//if theplayerif being attacked by a player in the game
			if(e.getDamager() instanceof Player && getPlayer((Player)e.getDamager()) != null){
				RPlayer attacker = getPlayer((Player)e.getDamager());
				RPlayer player = getPlayer((Player)e.getEntity());
				player.setHitBy(attacker);
				
				//if the player is going to die, kill them
				if(e.getDamage() >= player.getPlayer().getHealth()){
					e.setCancelled(true);
					
					//if the game is not running they are waiting in the lobby
					if(this.isRunning == false){
						//TODO add a location to kill player
						player.teleport(lobbyLocation);
						player.getPlayer().setHealth(player.getPlayer().getMaxHealth());
						player.getPlayer().setExhaustion(20);
					}else{
						attacker.addKill();
						killPlayer(player);
					}
					
				}
				
			}else{
				e.setCancelled(true);//prevent the players from being killed by anything other than other players in the game
			}
		}//if a villager who is part of the game is being hurt
		else if(e.getEntity() instanceof Villager && getVillager((Villager)e.getEntity()) != null){
			
			//if they are being hurt by a player in the game, give them wither
			if(e.getDamager() instanceof Player && getPlayer((Player)e.getDamager()) != null){
				RPlayer rplay = getPlayer((Player)e.getDamager());
				rplay.doVillagerConcequence();
			}
			
			//prevent the villager from being hurt no matter what
			e.setCancelled(true);
		}
		/*
		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
		 
			if(e.getEntity() instanceof Villager && e.getDamager() instanceof Player){
				if(getVillager((Villager)e.getEntity()) != null){
					Player player = (Player)e.getDamager();
					if(getPlayer(player) != null){
						//alert other players
						getPlayer(player).alertPlayers();
					}
					e.setCancelled(true);
				}
			}
			return;
		}
		
		Player player = (Player) e.getEntity();
		RPlayer rplay = getPlayer(player);
		
		if (rplay == null) {
			return; 
			//not part of this game!!!!!!
		}
		
		if(e.getDamager() instanceof Player){
			rplay.setHitBy(getPlayer((Player) e.getDamager()));
		}
		
		if (e.getDamage() >= player.getHealth()) {
			//player gonna die!
			getPlayer((Player) e.getDamager()).addKill();
			e.setCancelled(true);
			if(this.isRunning == false && getPlayer(player) != null){
				//means the player is waiting in the lobby
				//so tp them back to the lobby and set health and hunger
				rplay.teleport(lobbyLocation);
				player.setHealth(player.getMaxHealth());
				player.setExhaustion(20);
			}
			killPlayer(rplay);
		}
		//*/
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
			
			//display eat food particles
			EatParticleEffect eff = new EatParticleEffect(this, rplayer, 1, 20);
		}
		
	}
	
	
	@EventHandler
	public void EntityInteract(PlayerInteractEntityEvent e) {
		if (e.isCancelled()) {
			return;
		}
		
		if (e.getRightClicked().getType() == EntityType.VILLAGER)
		if (getPlayer(e.getPlayer()) != null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player && e.getCause() != DamageCause.ENTITY_ATTACK){
		
			//if the player is gonna die teleport them and fill their health
			Player player = (Player) e.getEntity();

			if(getPlayer(player) != null && e.getDamage() >= player.getHealth()){
				e.setCancelled(true);

				this.killPlayer(getPlayer(player));
			}
		}else if(e.getEntity() instanceof Villager){
			Villager villager = (Villager) e.getEntity();
			RegicideVillager vill = getVillager(villager);
			if(vill != null && villager.getHealth() - e.getDamage() <= 0){
				vill.rebirth();
			}
		}
		
	}
	
	@EventHandler
	public void onThingGettingSetOnFireEvent(EntityCombustEvent e) {
		if(e.getEntity() instanceof Player){
			Player player = (Player)e.getEntity();//if the thing on fire is a player in the game, don't allow it to burn
			if(getPlayer(player) != null){
				System.out.println("Cancel combust event for: " + player.getName());
				e.setCancelled(true);
				player.setFireTicks(1);
			}
		}else if(e.getEntity() instanceof Villager){
			e.setCancelled(true);
		}
	}
	
	/**
	 * Catch player logout, and remove the rplayer associated with them
	 * @param e
	 */
	@EventHandler
	public void onLogout(PlayerQuitEvent e) {
		if (getPlayer(e.getPlayer()) != null) {
			board.removePlayer(getPlayer(e.getPlayer()));
			removePlayer(e.getPlayer());
		}
	}
	
	public void setExitLocation(Location exit){
		this.exitLocation = exit;
	}
	
	/**
	 * kills a player by clearing effects, downgrading their weapons, and teleporting them to a spawnpoint 
	 * @param player The player to be killed
	 */
	public void killPlayer(RPlayer player) {
		if (!players.containsValue(player)) {
			//player not in this game!
			return;
		}
		
		Player play = player.getPlayer();
		player.downgrade();
		play.setHealth(play.getMaxHealth());
		
		//display blood effects
		play.getWorld().spigot().playEffect(play.getLocation(), Effect.TILE_BREAK, Material.RED_ROSE.getId(), 0, 0, 0, 0, 0, 20, 20);
		
		//check if they were the king
		if (player.isKing()) {
			player.die();
			//give king to last who hit, or make a random one
			if (player.getLastHitBy() == null) {
				makeRandomKing();
			}
			else {
				this.king = player.getLastHitBy();
				king.makeKing();
				
				if (villagers.isEmpty()) {
					//if we have no other villagers, for any reason
					king.teleport(getSpawnLocation());
				} else {
					//grab a random villager, and swap the two
					int pos = rand.nextInt(villagers.size());
					Iterator<RegicideVillager> it = villagers.iterator();
					RegicideVillager vil = null;
					for (; pos >= 0; pos--) { //grab 'next' pos+1 times (0 means we grab first, etc)
						vil = it.next();
					}
					
					//swap location
					Location oldLoc = king.getPlayer().getLocation();
					king.teleport(vil.getVillager().getLocation());
					vil.getVillager().teleport(oldLoc);
					
					
				}
			}
				
			board.updateKing(king);
		}
		
		//teleport needs to come after the fireworks in the die call
		player.teleport(getSpawnLocation());
		player.clearPotionEffects();
		
		play.setFireTicks(1);
		play.setFoodLevel(20);
		play.setExhaustion(0);
		player.disguise();
		
	}
	
	public Location getExitLocation(){
		return this.exitLocation;
	}
	
	/**
	 * When the game ends, end the game
	 * @param e
	 */
	@EventHandler
	public void onGameEnd(RegicideGameEndEvent e) {
		if (e.getGame().name.equals(name)) {
			endGame();
		}
	}
	
	/**
	 * When the player attempts to drop an item, stop them 
	 * @param e
	 */
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e){
		Player player = e.getPlayer();
		if(getPlayer(player)!= null){
			player.sendMessage(ChatColor.BOLD+""+ChatColor.BLUE+"Naughty Naughty... don't throw away items people give you!!!!"+ChatColor.RESET);
			e.setCancelled(true);
		}
	}
	
	/**
	 * Spawns the given number of villagers into the game
	 * @param count The number of villagers to spawn
	 */
	private void spawnVillagers(int count) {
		for (int i = 0; i < count; i++) {
			villagers.add(new RegicideVillager(this));
		}
	}
	
	/**
	 * removes all villagers from the game
	 */
	private void removeVillagers() {
		for (RegicideVillager vil : villagers) {
			vil.remove();
		}
	}
	
	/**
	 * Returns the regicide villager from the bukkit villager entity
	 * @param v
	 * @return the REgicideVillager wrapper or null if none exist
	 */
	public RegicideVillager getVillager(Villager v) {
		for (RegicideVillager vil : villagers) {
			if (vil.getVillager().equals(v)) {
				return vil;
			}
		}
		
		return null;
	}

	/**
	 * @return the firstPlace
	 */
	public Location getFirstPlace() {
		return firstPlace;
	}

	/**
	 * @param firstPlace the firstPlace to set
	 */
	public void setFirstPlace(Location firstPlace) {
		this.firstPlace = firstPlace;
	}

	/**
	 * @return the secondPlace
	 */
	public Location getSecondPlace() {
		return secondPlace;
	}

	/**
	 * @param secondPlace the secondPlace to set
	 */
	public void setSecondPlace(Location secondPlace) {
		this.secondPlace = secondPlace;
	}

	/**
	 * @return the thirdPlace
	 */
	public Location getThirdPlace() {
		return thirdPlace;
	}

	/**
	 * @param thirdPlace the thirdPlace to set
	 */
	public void setThirdPlace(Location thirdPlace) {
		this.thirdPlace = thirdPlace;
	}

	/**
	 * @return the otherPlace
	 */
	public Location getOtherPlace() {
		return otherPlace;
	}

	/**
	 * @param otherPlace the otherPlace to set
	 */
	public void setOtherPlace(Location otherPlace) {
		this.otherPlace = otherPlace;
	}
	
	/**
	 * calculates the player rankings
	 * @return a sorted list of players based on their points, or <i>null</i> if there are no winners.
	 */
	public ArrayList<RPlayer> calculateWinners(){
		ArrayList<RPlayer> list = new ArrayList<RPlayer>(players.values());
		
		if(list.isEmpty()){
			return null;
		}
		
		//used to compare things in the list
		Comparator<RPlayer> comparator = new Comparator<RPlayer>() {
		    public int compare(RPlayer c1, RPlayer c2) {
		        return c2.getPoints() - c1.getPoints();
		    }
		};

		Collections.sort(list, comparator); // use the comparator to sort the list
		
		return list;
	}
	
	/**
	 * prevent players int the game from picking up items
	 * @param e the event
	 */
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e){
		if(getPlayer(e.getPlayer()) != null){
			e.setCancelled(true);
		}
	}
	
	public void loadConfig(File configFile) {
		configManager.loadConfig(configFile);
		
		spawnLocations = configManager.getSpawnLocations();
		if (spawnLocations == null) {
			spawnLocations = new LinkedList<Location>();
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to fetch spawn data!");
			tellOps("Unable to load " + ChatColor.RED + "spawn" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
		lobbyLocation = configManager.getLobby();
		if (lobbyLocation == null) {
			tellOps("Unable to load " + ChatColor.RED + "lobby" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
		exitLocation = configManager.getExit();
		if (exitLocation == null) {
			tellOps("Unable to load " + ChatColor.RED + "exit" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
		firstPlace = configManager.getFirst();
		if (firstPlace == null) {
			tellOps("Unable to load " + ChatColor.RED + "first place" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
		secondPlace = configManager.getSecond();
		if (secondPlace == null) {
			tellOps("Unable to load " + ChatColor.RED + "second place" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
		thirdPlace = configManager.getThird();
		if (thirdPlace == null) {
			tellOps("Unable to load " + ChatColor.RED + "third place" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
		otherPlace = configManager.getOthers();
		if (otherPlace == null) {
			tellOps("Unable to load " + ChatColor.RED + "others" + ChatColor.BLUE + " location data for Regicide Game " + name);
		}
		
	}
	
	public void saveConfig(File configFile) {
		configManager.save(configFile);
	}
	
	public void printStatus() {
		tellOps(ChatColor.GOLD + "Game Status for [" + name + "] :");
		
		//figure out how many spawn points
		String msg = "";
		if (spawnLocations == null) {
			msg = "" + ChatColor.RED + ChatColor.BOLD + "NULL!";
		} else {
			if (spawnLocations.isEmpty()) {
				msg = "" + ChatColor.YELLOW + "Empty!";
			} else
			{
				//multiple points!
				msg = "" + ChatColor.GREEN + spawnLocations.size() + " points!";
			}
		}
		tellOps("Spawn Points:   " + msg);
		
		//report of lobby
		if (lobbyLocation == null) {
			msg = "" + ChatColor.YELLOW + "Not Set!";
		} else {
			msg = "" + ChatColor.GREEN + "Set!";
		}
		tellOps("Lobby:   " + msg);
		
		//report of exit
		if (exitLocation == null) {
			msg = "" + ChatColor.YELLOW + "Not Set!";
		} else {
			msg = "" + ChatColor.GREEN + "Set!";
		}
		tellOps("Exit:   " + msg);
		
		//report of first place
		if (firstPlace == null) {
			msg = "" + ChatColor.YELLOW + "Not Set!";
		} else {
			msg = "" + ChatColor.GREEN + "Set!";
		}
		tellOps("First Place:   " + msg);
		
		//report of second place
		if (secondPlace == null) {
			msg = "" + ChatColor.YELLOW + "Not Set!";
		} else {
			msg = "" + ChatColor.GREEN + "Set!";
		}
		tellOps("Second Place:   " + msg);
		
		//report of third place
		if (thirdPlace == null) {
			msg = "" + ChatColor.YELLOW + "Not Set!";
		} else {
			msg = "" + ChatColor.GREEN + "Set!";
		}
		tellOps("Third Place:   " + msg);
		
		//report of other place
		if (otherPlace == null) {
			msg = "" + ChatColor.YELLOW + "Not Set!";
		} else {
			msg = "" + ChatColor.GREEN + "Set!";
		}
		tellOps("Other's Place:   " + msg);
	}
	
	private void tellOps(String message) {
		for (Player op : Bukkit.getOnlinePlayers()) {
			if (op.isOp()) {
				op.sendMessage(ChatColor.BOLD + "Regicide: " + ChatColor.RESET + ChatColor.BLUE + message);
			}
		}
	}
}
