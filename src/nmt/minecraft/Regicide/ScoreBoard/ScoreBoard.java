package nmt.minecraft.Regicide.ScoreBoard;

import java.util.Collection;

import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreBoard {
	
	private org.bukkit.scoreboard.Scoreboard board; 
	
	private Objective scoreObjective;
	
	public ScoreBoard() {
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		scoreObjective = board.registerNewObjective("Points", "dummy");
		
		scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void updateScore(RPlayer player, int score) {
		scoreObjective.getScore(player.getPlayer().getDisplayName()).setScore(score);
	}
	
	public void displayScoreboard(Collection<RPlayer> players) {
		for (RPlayer play : players) {
			updateScore(play, 0);
			play.getPlayer().setScoreboard(board);
		}
	}
}
