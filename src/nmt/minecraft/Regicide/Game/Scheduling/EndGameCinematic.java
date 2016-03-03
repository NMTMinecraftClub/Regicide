package nmt.minecraft.Regicide.Game.Scheduling;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

public class EndGameCinematic extends BukkitRunnable {
	
	private List<Player> players;
	
	private RegicideGame game;
	
	
	private class KeepStill extends BukkitRunnable {
		
		private long index;
		
		private static final long heldTime = 200;
		
		private static final int fireworkInterval = 30;
		
		public KeepStill() {
			index = 0;
			
			if (players.size() > 3)
			for (int i = 3; i < players.size(); i++) {
				int j = 3;
				for (; j < players.size(); j++) {
					if (i == j) {
						continue;
					}
					players.get(i).hidePlayer(players.get(j));
				}
				
			}
			
			this.runTaskTimer(RegicidePlugin.regicidePlugin, 0, 1);
		}
		
		public void run() {
			
			if (players.isEmpty()) {
				return;
			}
			
//			PotionEffect inst = new PotionEffect(PotionEffectType.SLOW, 10, 100);
//			players.get(0).addPotionEffect(inst);
//			if (players.size() > 1)
//				players.get(1).addPotionEffect(inst);
//			if (players.size() > 2)
//				players.get(2).addPotionEffect(inst);
			
			Player cache;
			Location locc;
			
			//Teleport 1st Place winner
			cache = players.get(0);
			locc = cache.getLocation().clone();
			locc.setX(game.getFirstPlace().getX());
			locc.setY(game.getFirstPlace().getY());
			locc.setZ(game.getFirstPlace().getZ());
			cache.teleport(locc);
			
			//If there is a second place winner, teleport the player.
			if (players.size() >= 2) {
				cache = players.get(1);
				locc = cache.getLocation().clone();
				locc.setX(game.getSecondPlace().getX());
				locc.setY(game.getSecondPlace().getY());
				locc.setZ(game.getSecondPlace().getZ());
				cache.teleport(locc);
			}
			//If there is a third place winner, teleport the player.
			if (players.size() >= 3)
			{
				cache = players.get(2);
				locc = cache.getLocation().clone();
				locc.setX(game.getThirdPlace().getX());
				locc.setY(game.getThirdPlace().getY());
				locc.setZ(game.getThirdPlace().getZ());
				cache.teleport(locc);
				
			}
			
			this.index++;
			if (index >= heldTime) {
				
				
				new FinalPositions().run();
				
				this.cancel();
				return;
			}
			
			if (index % fireworkInterval == 0) {
				launchFireworks(game.getFirstPlace());
				launchFireworks(game.getSecondPlace());
				launchFireworks(game.getThirdPlace());
			}
		}
		
		private void launchFireworks(Location locPoint) {
			
			Firework firework = locPoint.getWorld().spawn(locPoint, Firework.class);
			FireworkMeta fm = firework.getFireworkMeta();
	        fm.addEffect(FireworkEffect.builder()
	            .flicker(false)
	            .trail(true)
	            .with(Type.BALL)
	            .with(Type.BALL_LARGE)
	            .with(Type.STAR)
	            .withColor(Color.YELLOW)
	            .withColor(Color.ORANGE)
	            .withFade(Color.RED)
	            .withFade(Color.PURPLE)
	            .build());
	        fm.setPower(1);
	        firework.setFireworkMeta(fm);
		}
	}
	
	private class FinalPositions extends BukkitRunnable {
		
		public void run() {
			if (players.isEmpty()) {
				return;
			}
			
			if (players.size() > 3)
			for (int i = 3; i < players.size(); i++) {
					int j = 3;
					for (; j < players.size(); j++) {
						if (i == j) {
							continue;
						}
						players.get(i).showPlayer(players.get(j));
					}
					
			}
			
			for (Player player : players) {
				player.teleport(game.getExitLocation());
				player.getPlayer().getActivePotionEffects().clear();
			}
 		}
	}
	
	
	
	/**
	 * Created the cinematic and runs it. The palyer list should be sorted!!
	 * @param participants
	 */
	public EndGameCinematic(RegicideGame game, List<RPlayer> participants) {
		this.players = new LinkedList<Player>();
		this.game = game;
		
		if (participants.isEmpty()) {
			return;
		}
		
		for (RPlayer p : participants) {
			players.add(p.getPlayer());
		}
	}
	
	public void run() {
		//teleport first, second, and third places 
		if (players.isEmpty()) {
			return;
		}
		
		players.get(0).teleport(game.getFirstPlace());
		if (players.size() >= 2) {
			players.get(1).teleport(game.getSecondPlace());
		}
		if (players.size() >= 3)
		{
			players.get(2).teleport(game.getThirdPlace());
			
		}
		
		//teleport rest of people to other location
		if (players.size() >= 4) {
			for(int i = 3; i < players.size(); i++) {
				players.get(i).teleport(game.getOtherPlace());
			}
		}
		
		new KeepStill();
		
		
	}
	
}
