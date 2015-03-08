package nmt.minecraft.Regicide.Game.Player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RPlayer{
	
	private Player player;
	
	private LivingEntity villager;
	
	private boolean isKing;
	
	private int points;
	
	public RPlayer(UUID player) {
		points = 0;
		isKing = false;
		this.player = Bukkit.getPlayer(player);
		spawnVillager();
	}
	
	public void teleport(Location loc) {
		player.teleport(loc);
		villager.teleport(loc);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Spawns a villager at the player's position
	 */
	private void spawnVillager(){
		World world = player.getWorld();
		Location location = player.getLocation();
		villager = (LivingEntity) world.spawnEntity(location,EntityType.VILLAGER);
		villager.setHealth(10000000);
		//TODO: Find some way to make the villager invisible to the player
	}
	
	/**
	 * moves the villager to sync with the controlling player
	 */
	public void syncVillager(){
		if(villager != null){
			villager.teleport(player.getLocation());
			//set the villager target to the player
			((Creature)villager).setTarget(player);
			//make the villager invincible
			villager.setHealth(10000000);
		}
	}
	
	public void setIsKing(boolean isKing) {
		this.isKing = isKing;
	}
	
	public void addPoint() {
		this.points++;
	}
	
	public int getPoints() {
		return this.points;
	}
	
}

