package nmt.minecraft.Regicide.Game.Player;

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
		
		disguise = new MobDisguise(DisguiseType.VILLAGER, true);
	}
	
	public void teleport(Location loc) {
		player.teleport(loc);
	}
	
	/**
	 * Disguises the underlying player as his registered disguise -- a villager
	 */
	public void disguise() {
		DisguiseAPI.disguiseToAll(this.player, disguise);
	}
	
	/**
	 * Removes the disguise this player is using to everyone.
	 */
	public void unDisguise() {
		DisguiseAPI.undisguiseToAll(this.player);
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
		player.getInventory().addItem(new ItemStack(Material.WOOD_SWORD,1));
		player.setExp(0);
		player.getActivePotionEffects().clear();
		player.setGameMode(GameMode.SURVIVAL);
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
	}
	
	public void addKill(){
		this.killCount++;
		this.upgrade();
	}
	
	private void upgrade(){
		if(this.upgradeLevel != levels.length-1 && this.isKing == false){
			//if not at the top level, go to the next level
			this.upgradeLevel++;
			this.switchSword(levels[this.upgradeLevel]);
		}
	}
	
	public void downgrade(){
		this.upgradeLevel = 0;
		this.switchSword(levels[this.upgradeLevel]);
	}
	
	private void switchSword(Material sword){
		PlayerInventory inventory = this.player.getInventory();
		for(Material tmp : levels){
			inventory.remove(tmp);
		}
		inventory.remove(Material.GOLD_SWORD);
		
		inventory.addItem(new ItemStack(sword,1));
	}
	
}

