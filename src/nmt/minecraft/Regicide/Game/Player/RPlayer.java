package nmt.minecraft.Regicide.Game.Player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
		//TODO: Find some way to make the villager invisible to the player
	}
	
	/**
	 * moves the villager to sync with the controlling player
	 */
	public void syncVillager(){
		if(villager != null){
			villager.teleport(player.getLocation());
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

