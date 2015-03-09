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
		king = null;
	}
	
	public void setKing(RPlayer king) {
		//take HUD from old king
		if (king != null) {
			king.getPlayer().setExp(0.0f);
		}
		
		this.king = king;
	}
	
	public void update(float time) {
		king.getPlayer().setLevel(king.getPoints());
		king.getPlayer().setExp(time);
	}
	
}
