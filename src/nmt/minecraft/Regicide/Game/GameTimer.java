package nmt.minecraft.Regicide.Game;

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
		
	}

}
