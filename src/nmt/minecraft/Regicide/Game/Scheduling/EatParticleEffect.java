package nmt.minecraft.Regicide.Game.Scheduling;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;

/**
 * Creates eating particle effects for the provided player each time it is run.
 * @author Skyler
 *
 */
public class EatParticleEffect extends BukkitRunnable {
	
	private RPlayer targetPlayer;
	
	private RegicideGame game;
	
	private long delay;
	
	private long loopTimes;
	
	private long index;
	
	public EatParticleEffect(RegicideGame game, RPlayer player, long delay, long loopTimes) {
		targetPlayer = player;
		this.game = game;
		this.index = 0;
		
		this.delay = delay;
		this.loopTimes = loopTimes;
		
		this.runTaskTimer(RegicidePlugin.regicidePlugin, (long) 0.1, delay);
	}
	
	
	@SuppressWarnings("deprecation")
	public void run() {
		System.out.println("Sending food particles!");
		//display food particles
		WrapperPlayServerWorldParticles particle = new WrapperPlayServerWorldParticles();
		particle.setNumberOfParticles(10);
		particle.setOffset(targetPlayer.getPlayer().getLocation().getDirection());
		particle.setLocation(targetPlayer.getPlayer().getEyeLocation().add(targetPlayer.getPlayer().getLocation().getDirection()));
		particle.setParticleName("iconcrack_" + Material.COOKED_BEEF.getId());
		
		for (RPlayer player : game.getPlayers()) {
			if (player.getPlayer().getUniqueId().equals(targetPlayer.getPlayer().getUniqueId())) {
				continue;
			}
			
			//else display the particles
			System.out.println("Spam: sending to player: " + player.getPlayer().getName());
			particle.sendPacket(player.getPlayer());
		}
		
		
		index++;
		if (index >= loopTimes) {
			this.cancel();
		}
	}
	
	
}
