package nmt.minecraft.Regicide.Game.Scheduling;

import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EndGameCinematic extends BukkitRunnable {
	
	private List<Player> players;
	
	private RegicideGame game;
	
	
	private class KeepStill extends BukkitRunnable {
		
		private long index;
		
		private static final long heldTime = 50;
		
		public KeepStill() {
			index = 0;
			this.runTaskTimer(RegicidePlugin.regicidePlugin, 0, 1);
		}
		
		public void run() {
			
			if (players.isEmpty()) {
				return;
			}
			//Teleport 1st Place winner
			players.get(0).teleport(game.getFirstPlace());
			
			//If there is a second place winner, teleport the player.
			if (players.size() >= 2) {
				players.get(1).teleport(game.getSecondPlace());
			}
			//If there is a third place winner, teleport the player.
			if (players.size() >= 3)
			{
				players.get(2).teleport(game.getThirdPlace());
				
			}
			
			this.index++;
			if (index >= heldTime) {
				
				
				new FinalPositions().run();
				
				this.cancel();
			}
		}
	}
	
	private class FinalPositions extends BukkitRunnable {
		
		public void run() {
			if (players.isEmpty()) {
				return;
			}
			for (Player player : players) {
				player.teleport(game.getExitLocation());
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
