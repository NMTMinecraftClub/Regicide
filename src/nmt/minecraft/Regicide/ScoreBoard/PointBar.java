package nmt.minecraft.Regicide.ScoreBoard;

import nmt.minecraft.Regicide.Game.Player.RPlayer;

/**
 * Implementation of bar indicicating how long until another point is encrued.<br />
 * Only the king should see this? //TODO only king, or everyone?
 * @author smanzana
 *
 */
public class PointBar {
	
	/**
	 * The player who is currently king and currently seeing this bar
	 */
	private RPlayer king;
	
	
	public PointBar() {
		
	}
	
	public void setKing(RPlayer king) {
		this.king = king;
	}
	
}
