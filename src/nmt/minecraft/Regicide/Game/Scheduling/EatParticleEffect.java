package nmt.minecraft.Regicide.Game.Scheduling;

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
	
	public EatParticleEffect(RegicideGame game, RPlayer player) {
		targetPlayer = player;
		this.game = game;
	}
	
	
	public void run() {

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
			particle.sendPacket(player.getPlayer());
		}
	}
	
	
}
