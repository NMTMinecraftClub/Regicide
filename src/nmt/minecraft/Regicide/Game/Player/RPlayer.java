package nmt.minecraft.Regicide.Game.Player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RPlayer {
	
	private Player player;
	
	private LivingEntity villager;
	
	private boolean isKing;
	
	public RPlayer(UUID Player) {
		
	}
	
	public void teleport(Location loc) {
		player.teleport(loc);
		villager.teleport(loc);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	private void spawnVillager(){
		World world = player.getWorld();
		Location location = player.getLocation();
		villager = (LivingEntity) world.spawnEntity(location,EntityType.VILLAGER);
	}
	
	
}

