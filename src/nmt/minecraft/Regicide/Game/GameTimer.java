package nmt.minecraft.Regicide.Game;

import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable {

	private long time;
	
	private final RegicideGame game;
	
	/**
	 * How often should score be incremented? IN SECONDS
	 */
	private final static int scoreInterval = 10;
	
	private long endTime;
	
	public GameTimer(RegicideGame game, long endTime) {
		this.time = 0;
		this.game = game;
		this.endTime = endTime;  
	}
	
	@Override
	public void run() {
		time++;
		
		if (time == endTime) {
			game.endGame();
			this.cancel();
			return;
		}
		
		//score every n seconds
		if (time % scoreInterval == 0) {
			game.scorePoint();
		}
		
		game.getBoard().update(((float) (time % scoreInterval)) / (float) scoreInterval);
		
		//update everyone's food level
		for (RPlayer player : game.getPlayers()) {
			Player p = player.getPlayer();
			if (player.isKing()) {
				
				//is their hunger all the way down?
				//if so, DEATH
				if (p.getFoodLevel() < 1) {
					game.killPlayer(player);
				}
				
				p.setSaturation(0);
				p.setExhaustion(p.getExhaustion() + .5f);
				if (p.getExhaustion() >= 3.9f) {
					p.setFoodLevel(Math.max(p.getFoodLevel() - 1, 0));
					p.setExhaustion(0.0f);
				}
			} else {
				p.setSaturation(20);
				p.setFoodLevel(20);
				
			}
		}
		
	}

}
