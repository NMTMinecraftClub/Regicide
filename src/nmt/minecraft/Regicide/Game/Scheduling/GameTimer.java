package nmt.minecraft.Regicide.Game.Scheduling;

import java.util.List;

import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Events.RegicideGameEndEvent;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer extends BukkitRunnable {

	private long time;
	
	private final RegicideGame game;
	
	/**
	 * How often should score be incremented? IN SECONDS
	 */
	private long scoreInterval;
	
	private long endTime;
	
	public GameTimer(RegicideGame game, long endTime, long scoreInterval) {
		this.time = 0;
		this.game = game;
		this.endTime = endTime;  
		this.scoreInterval = scoreInterval;
	}
	
	@Override
	public void run() {
		time++;
		long timeRemaining = endTime - time;
		List<RPlayer> Players = game.getPlayers();
		long MinutesRemaining = (long) Math.floor((timeRemaining / 60));
		int minSecondsRemaining = (int) (timeRemaining % 60);
		
		//set time to day periodically
		if(timeRemaining % 30 == 0){
			Players.get(0).getPlayer().getLocation().getWorld().setTime(600);
		}
		
		//Long term count down timer
		if (timeRemaining > 10 && timeRemaining % 30 == 0) {
			String Minutes = ChatColor.GOLD+"" + MinutesRemaining + ChatColor.RESET+"";
			for (RPlayer p : Players) {
				p.getPlayer().sendMessage(Minutes + ":" + ChatColor.GREEN + String.format("%02d", minSecondsRemaining) 
						+ ChatColor.AQUA+"" + " remaining" + ChatColor.RESET);
			}
		}
		//Count down Timer for Last 10 Seconds
		if (timeRemaining < 10 && timeRemaining > 0) {
			for (RPlayer p : Players) {
				if (timeRemaining != 1)
					p.getPlayer().sendMessage((ChatColor.GOLD+"") + timeRemaining + (ChatColor.AQUA+"") + " seconds remaining!" + ChatColor.RESET+"");
				else
					p.getPlayer().sendMessage((ChatColor.GOLD+"") + timeRemaining + (ChatColor.AQUA+"") + " second remaining!" + ChatColor.RESET+"");

				p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1,0);
			}
		}
		if (time == endTime) {
			Bukkit.getPluginManager().callEvent(new RegicideGameEndEvent(game));
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
					player.getPlayer().sendMessage(ChatColor.RED + "You starved to death!" + ChatColor.RESET);
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
