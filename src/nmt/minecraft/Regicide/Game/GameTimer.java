package nmt.minecraft.Regicide.Game;

import nmt.minecraft.Regicide.Game.Player.RPlayer;

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
			if (player.isKing()) {
				player.getPlayer().setSaturation(0);
			} else {
				player.getPlayer().setSaturation(20);
				player.getPlayer().setFoodLevel(20);
			}
		}
		
	}

}
