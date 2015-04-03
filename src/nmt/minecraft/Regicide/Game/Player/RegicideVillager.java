package nmt.minecraft.Regicide.Game.Player;

import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class RegicideVillager {
	
	private Villager villager;
	
	private RegicideGame game;
	
	public RegicideVillager(RegicideGame game) {
		
		this.game = game;
		
		//Create villager entity this RegVil will follow and manipulate
		Location spawnLoc = game.getSpawnLocation();
		villager = (Villager) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VILLAGER);
	}
	
	public Villager getVillager() {
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

		villager = (Villager) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VILLAGER); 
	}
	
	public void remove() {
		
		villager.remove();
	}
}
