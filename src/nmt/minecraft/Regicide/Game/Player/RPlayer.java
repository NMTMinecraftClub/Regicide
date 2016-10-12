package nmt.minecraft.Regicide.Game.Player;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import nmt.minecraft.Regicide.RegicidePlugin;

public class RPlayer{
	
	private Player player;
	
	private boolean isKing;
	
	private int points;
	
	/**
	 * IS THIS GIVING YOU AN ERROR? IF IT IS,
	 * make sure to download the two new libraries we are dependent upon. Namely
	 * Lib's Disguises - http://www.spigotmc.org/resources/libs-disguises.81/
	 * and
	 * ProtocolLib - http://dev.bukkit.org/bukkit-plugins/protocollib/
	 * Make sure to get 3.4.0 of ProtocolLib!!! 
	 */
	private Disguise disguise;
	
	private RPlayer lastHitBy;
	
	private int killCount;
	
	private int killStreakCount;
	
	private int arrowsThisLife;
	
	private int timeAlive;
	
	private int villagerHits;
	
	private int upgradeLevel;
	
	private static Material[] levels = {Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD};
	
	public RPlayer(UUID player) {
		points = 0;
		isKing = false;
		this.killCount = 0;
		this.upgradeLevel = 0;
		this.player = Bukkit.getPlayer(player);
		Bukkit.getLogger().info("added player to game");
	}
	
	public void teleport(Location loc) {
		player.teleport(loc);
	}
	
	/**
	 * Disguises the underlying player as his registered disguise -- a villager
	 */
	/*
	 * TODO: change to this session's disguise type 
	 */
	public void disguise(DisguiseType type) {
		DisguiseAPI.undisguiseToAll(this.player);
		disguise = new MobDisguise(type, true);
		
		DisguiseAPI.disguiseToAll(this.player, disguise);
	}
	
	/**
	 * Removes the disguise this player is using to everyone.
	 */
	public void unDisguise() {
		DisguiseAPI.undisguiseToAll(this.player);
		disguise = null;
	}
	
	/**
	 * Returns the Bukkit Player Object within RPlayer
	 * @return The Bukkit Player Object.
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * This returns the RPlayer that caused damage to this RPlayer Instance last.
	 * @return The RPlayer that last hit this RPlayer instance. Is null if entity <br>
	 * has not been damaged.
	 */
	public RPlayer getLastHitBy() {
		return this.lastHitBy;
	}
	
	/**
	 * This sets the RPlayer who last hit this RPlayer instance.
	 * @param play The RPlayer that last hit this instance.
	 */
	public void setHitBy(RPlayer play) {
		this.lastHitBy = play;
	}
	
	/**
	 * This sets the RPlayer as the King in Regicide.
	 * @param isKing Set True or False.
	 */
	public void setIsKing(boolean isKing) {
		this.isKing = isKing;
	}
	
	/**
	 * Returns true if this RPlayer is the King in a Regicide game instance.<br>
	 * False if otherwise.
	 * @return A boolean.
	 */
	public boolean isKing() {
		return isKing;
	}
	
	/**
	 * This method adds a score point to the RPlayer instance in a Regicide game.
	 */
	public void addPoint() {
		this.points++;
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP , 1.0f, 1.0f);
		if(this.points % 3 == 0 ){
			this.alertPlayers();
		}
	}
	
	/**
	 * This method returns the points this RPlayer currently has.
	 * @return The RPlayer's score.
	 */
	public int getPoints() {
		return this.points;
	}
	
	/**
	 * This method handles a Regicide Death.<br>
	 * Please note that in a Regicide Game, players cannot truly 'die'.
	 */
	public void die(){
		Firework firework = this.player.getWorld().spawn(this.player.getLocation(), Firework.class);
		FireworkMeta fm = firework.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
            .flicker(false)
            .trail(true)
            .with(Type.BALL)
            .with(Type.BALL_LARGE)
            .with(Type.STAR)
            .withColor(Color.YELLOW)
            .withColor(Color.ORANGE)
            .withFade(Color.RED)
            .withFade(Color.PURPLE)
            .build());
        fm.setPower(1);
        firework.setFireworkMeta(fm);
        
        if(this.isKing){
        	player.sendMessage(ChatColor.RED + "You have lost the King!");
        	//remove steak from inventory
        	player.getInventory().remove(Material.COOKED_BEEF);
        }
        this.isKing = false;
	}
	
	/**
	 * This method makes this RPlayer instance the King.
	 */
	public void makeKing(){
		this.isKing = true;
		player.setHealth(player.getMaxHealth());
		
		this.switchSword(Material.GOLD_SWORD);
		
		this.player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
	}
	
	/**
	 * This method sets the initial state of an RPlayer.
	 */
	public void setInitialState(DisguiseType disguise) {
		this.isKing=false;
		this.arrowsThisLife = 1;
		this.killStreakCount = 0;
		this.killCount = 0;
		player.setHealth(player.getMaxHealth());
		player.closeInventory();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD,1));
		
		ItemStack bowItem = new ItemStack(Material.BOW,1);
		ItemMeta bowMeta = bowItem.getItemMeta();
		bowMeta.setDisplayName(ChatColor.DARK_RED+ "Death's " + ChatColor.GOLD +"Bow");
		bowItem.setDurability((short)0);
		bowItem.setItemMeta(bowMeta);
		
		player.getInventory().addItem(bowItem);
		ItemStack arrow = new ItemStack(Material.ARROW,1);
		ItemMeta arrowMeta = arrow.getItemMeta();
		arrowMeta.setDisplayName(ChatColor.DARK_RED + "Death's " + ChatColor.GOLD + "Arrow");
		arrow.setItemMeta(arrowMeta);
		this.player.getInventory().addItem(arrow);
		
		player.setExp(0);
		player.setLevel(0);
		player.getActivePotionEffects().clear();
		player.setGameMode(GameMode.ADVENTURE);
		
		this.getArrow();
		this.disguise(disguise);
		this.clearPotionEffects();
	}
	
	/**
	 * This method sets the end state of an RPlayer.
	 */
	public void setEndState() {
		this.isKing=false;
		this.killCount = 0;
		player.setHealth(player.getMaxHealth());
		player.closeInventory();
		player.getInventory().clear();
		player.setExp(0);
		player.setLevel(0);
		player.getActivePotionEffects().clear();
		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);//set max food level
		this.unDisguise();
		this.clearPotionEffects();
	}
	
	/**
	 * This method increments of an RPlayer's kill count.
	 */
	public void addKill(){
		this.killCount++;
		this.killStreakCount++;
		this.upgrade();
		this.getArrow();
	}
	
	/**
	 * Returns the number of kills this player has achieved
	 * @return
	 */
	public int getKillCount() {
		return this.killCount;
	}
	public void resetKillStreak(){
		this.killStreakCount = 0;
		this.arrowsThisLife = 0;
		for(ItemStack arrow : this.player.getInventory().all(Material.ARROW).values()){
			this.arrowsThisLife+= arrow.getAmount();
		}
		Bukkit.getLogger().info("Had arrows:" + this.arrowsThisLife);
	}
	/**
	 * This method upgrades the weapon of an RPlayer.
	 */
	private void upgrade(){
		//TODO change this to use kill count to judge if the player should upgrade
		if(this.upgradeLevel != levels.length-1 && this.isKing == false){
			//if not at the top level or the king, go to the next level
				this.upgradeLevel++;
				this.switchSword(levels[this.upgradeLevel]);
		}
	}
	
	/**
	 * This method upgrades the weapon of an RPlayer over time.
	 */
	public void timeAliveUpgrade(){
		this.timeAlive++;
		if(this.timeAlive == 2 ){
			upgrade();
			this.timeAlive = 0;
		}
	}
	/**
	 * Gives the player an arrow. 
	 * the rate at which they get an arrow is determined by 2^(number of arrows they have gotten this life)
	 */
	private void getArrow(){
		int killsNeededForNextArrow = (int)Math.pow(2,this.arrowsThisLife);
		Bukkit.getLogger().info("needs: " +killsNeededForNextArrow);
		if(this.killStreakCount >= killsNeededForNextArrow){
			RegicidePlugin.regicidePlugin.getLogger().info("Giving arrow to: " + this.player.getDisplayName());
			this.arrowsThisLife++;
			this.player.sendMessage("You got another " + ChatColor.DARK_RED + "Death's " + ChatColor.GOLD+ "Arrow!");
			this.player.sendMessage("You need "+ChatColor.RED +(int)Math.pow(2, this.arrowsThisLife)+" total kills for the next one!");
			ItemStack arrow = new ItemStack(Material.ARROW,1);
			ItemMeta arrowMeta = arrow.getItemMeta();
			arrowMeta.setDisplayName(ChatColor.DARK_RED + "Death's " + ChatColor.GOLD + "Arrow");
			arrow.setItemMeta(arrowMeta);
			this.player.getInventory().addItem(arrow);
			
		}
	}

	/**
	 * This method downgrades the weapon of an RPlayer.
	 */
	public void downgrade(){
		this.killCount = 0;
		this.villagerHits = 0;
		this.upgradeLevel = upgradeLevel > 0 ? upgradeLevel - 1 : upgradeLevel;
		this.switchSword(levels[this.upgradeLevel]);
	}
	
	private void switchSword(Material sword){
		PlayerInventory inventory = this.player.getInventory();
		for(Material tmp : levels){
			inventory.remove(tmp);
		}
		inventory.remove(Material.GOLD_SWORD);
		ItemStack swordItem = new ItemStack(sword,1);
		
		if(sword == Material.GOLD_SWORD){
			ItemMeta scepter = swordItem.getItemMeta();
			scepter.setDisplayName(ChatColor.GOLD+"Royal Scepter");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("The Royal Scepter");
			lore.add("passed down through the ages");
			lore.add("from "+ChatColor.GREEN+"Eric the green"+ChatColor.DARK_PURPLE+" to you");
			
			scepter.setLore(lore);//just trying this out cuz y not
			swordItem.setItemMeta(scepter);
		}
		
		inventory.addItem(swordItem);
	}
	
	public void clearPotionEffects(){
		for (PotionEffect e : player.getActivePotionEffects()) {
			player.removePotionEffect(e.getType());
		}
	}
	
	public void alertPlayers(){
		//set off firework
		Firework firework = this.player.getWorld().spawn(this.player.getLocation(), Firework.class);
		FireworkMeta fm = firework.getFireworkMeta();
		//this.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100,1));
        fm.addEffect(FireworkEffect.builder()
            .flicker(false)
            .trail(true)
            .with(Type.CREEPER)
            .withColor(Color.GREEN)
            .withFade(Color.RED)
            .withFade(Color.PURPLE)
            .build());
        fm.setPower(1);
        firework.setFireworkMeta(fm);
        //put in a waiting period between fireworks
	}
	
	
	public void doVillagerConcequence(){
		this.player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
		this.villagerHits++;
		if(!this.isKing){ //The king is allowed to bully his citizens apparently...
			this.player.sendMessage(ChatColor.RED +"Hitting non-players has consequences!");
			if(villagerHits == 3){
				this.player.sendMessage(ChatColor.RED +"you hit too many non-players!");
				downgrade();
			}
		}
	}
	
}

