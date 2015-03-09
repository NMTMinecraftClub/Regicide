package nmt.minecraft.Regicide.Game.Player;

import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.Disguise;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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

