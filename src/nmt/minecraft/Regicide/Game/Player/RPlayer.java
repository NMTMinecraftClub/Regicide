package nmt.minecraft.Regicide.Game.Player;

import java.util.UUID;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;

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

