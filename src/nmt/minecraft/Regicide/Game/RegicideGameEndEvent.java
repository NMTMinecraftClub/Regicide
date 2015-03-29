package nmt.minecraft.Regicide.Game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegicideGameEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private RegicideGame game;
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public RegicideGameEndEvent(RegicideGame game) {
		this.game = game;
	}
	
	public RegicideGame getGame() {
		return this.game;
	}
	
}
