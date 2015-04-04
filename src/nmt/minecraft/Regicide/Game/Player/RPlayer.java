package nmt.minecraft.Regicide.Game.Player;

import java.util.ArrayList;
import java.util.UUID;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

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
	
	private int upgradeLevel;
	
	private static Material[] levels = {Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD};
	
	public RPlayer(UUID player) {
		points = 0;
		isKing = false;
		this.killCount = 0;
		this.upgradeLevel = 0;
		this.player = Bukkit.getPlayer(player);
		
		//disguise = new MobDisguise(DisguiseType.VILLAGER, true);
	}
	
	public void teleport(Location loc) {
		player.teleport(loc);
	}
	
	/**
	 * Disguises the underlying player as his registered disguise -- a villager
	 */
	public void disguise() {
		DisguiseAPI.undisguiseToAll(this.player);
		disguise = new MobDisguise(DisguiseType.VILLAGER, true);
		DisguiseAPI.disguiseToAll(this.player, disguise);
	}
	
	/**
	 * Removes the disguise this player is using to everyone.
	 */
	public void unDisguise() {
		DisguiseAPI.undisguiseToAll(this.player);
		disguise = null;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public RPlayer getLastHitBy() {
		return this.lastHitBy;
	}
	
	public void setHitBy(RPlayer play) {
		this.lastHitBy = play;
	}
	
	
	public void setIsKing(boolean isKing) {
		this.isKing = isKing;
	}
	
	public boolean isKing() {
		return isKing;
	}
	
	public void addPoint() {
		this.points++;
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
	}
	
	public int getPoints() {
		return this.points;
	}
	
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
	
	public void makeKing(){
		this.isKing = true;
		if(isKing == true){
			this.switchSword(Material.GOLD_SWORD);
		}
		this.player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 5));
	}
	
	public void setInitialState() {
		this.isKing=false;
		this.killCount = 0;
		player.setHealth(player.getMaxHealth());
		player.closeInventory();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD,1));
		player.setExp(0);
		player.getActivePotionEffects().clear();
		player.setGameMode(GameMode.SURVIVAL);
		this.disguise();
		this.clearPotionEffects();
	}
	
	public void setEndState() {
		this.isKing=false;
		this.killCount = 0;
		player.setHealth(player.getMaxHealth());
		player.closeInventory();
		player.getInventory().clear();
		player.setExp(0);
		player.getActivePotionEffects().clear();
		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);//set max food level
		this.unDisguise();
		this.clearPotionEffects();
	}
	
	public void addKill(){
		this.killCount++;
		this.upgrade();
	}
	
	private void upgrade(){
		//TODO change this to use kill count to judge if the player should upgrade
		if(this.upgradeLevel != levels.length-1 && this.isKing == false){
			//if not at the top level or the king, go to the next level
				this.upgradeLevel++;
				this.switchSword(levels[this.upgradeLevel]);
		}
	}
	
	public void downgrade(){
		this.killCount = 0;
		this.upgradeLevel = 0;
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
		this.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100,1));
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
	
}

