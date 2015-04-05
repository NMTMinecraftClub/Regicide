package nmt.minecraft.Regicide.Game.Events;

import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegicideEatEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private RegicideGame game;
	
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public RegicideEatEvent(RegicideGame game) {
		this.game = game;
	}
	
	public RegicideGame getGame() {
		return this.game;
	}
	
}
