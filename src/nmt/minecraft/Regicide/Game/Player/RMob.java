package nmt.minecraft.Regicide.Game.Player;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class RMob {
	
	private Entity villager;
	
	
	private RegicideGame game;
	private EntityType mob;
	
	public RMob(RegicideGame game, EntityType mob) {
		
		this.game = game;
		this.mob = mob;
		
		//Create villager entity this RegVil will follow and manipulate
		Location spawnLoc = game.getSpawnLocation().getBlock().getLocation();
		villager = spawnLoc.getWorld().spawnEntity(spawnLoc, mob);
		RegicidePlugin.regicidePlugin.getLogger().info("Spawning Villager!");
	}
	
	public Entity getVillager() {
		return villager;
	}
	
	/**
	 * Recreate this villager with random skin and everything.
	 * This method is safe to call even if a villager still is living.
	 */
	public void rebirth() {
		
		Location spawnLoc = game.getSpawnLocation();
		
		if (!villager.isDead()) {
			villager.remove();
		}

		villager = spawnLoc.getWorld().spawnEntity(spawnLoc, this.mob); 
	}
	
	public void remove() {
		
		villager.remove();
	}
}
