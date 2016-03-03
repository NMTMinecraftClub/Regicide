package nmt.minecraft.Regicide.Game.Scheduling;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

//import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

/**
 * Creates eating particle effects for the provided player each time it is run.
 * @author Skyler
 *
 */
public class EatParticleEffect extends BukkitRunnable {
	
	private RPlayer targetPlayer;
	
	private long loopTimes;
	
	private long index;
	
	private Random rand;
	
	public EatParticleEffect(RegicideGame game, RPlayer player, long delay, long loopTimes) {
		targetPlayer = player;
		this.index = 0;
		
		this.loopTimes = loopTimes;
		
		rand = new Random();
		
		this.runTaskTimer(RegicidePlugin.regicidePlugin, (long) 0.1, delay);
	}
	
	
	public void run() {
		
		Location loc = targetPlayer.getPlayer().getEyeLocation().add(targetPlayer.getPlayer().getLocation().getDirection().multiply(.3));
		
		loc.getWorld().spigot().playEffect(loc, Effect.TILE_BREAK, 170, 0, rand.nextFloat() * 0.5f, 
											rand.nextFloat() * 0.25f, rand.nextFloat() * 0.5f, 0, 
											20, 20);
		
		//display food particlesg
		//WrapperPlayServerWorldParticles particle = new WrapperPlayServerWorldParticles();
//		particle.setNumberOfParticles(10);
//		particle.setParticleData(rand.nextFloat() * .05f);
//		particle.setX((float) loc.getX());
//		particle.setY((float) loc.getY());
//		particle.setZ((float) loc.getZ());
//		int[] data = new int[2];
//		data[0] = Material.COOKED_BEEF.getId();
//		data[1] = 0;
//		particle.setData(data);
//		
//		for (RPlayer player : game.getPlayers()) {
//			if (player.getPlayer().getUniqueId().equals(targetPlayer.getPlayer().getUniqueId())) {
//				continue;
//			}
//			
//			//else display the particles
//			particle.sendPacket(player.getPlayer());
//		}
		
		
		index++;
		if (index >= loopTimes) {
			this.cancel();
		}
	}
	
	
}
