package nmt.minecraft.Regicide.IO;

import java.util.ArrayList;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;
import nmt.minecraft.Regicide.Game.Player.RPlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class RegicideTabCompleter implements TabCompleter{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("regicide")){
			List<String> list=new ArrayList<String>();
			if(args.length == 1){
				List<String> tmpList;
				 tmpList = RegicideCommands.getCommandList();//get the list of commands
				 //only put the ones that start with the given
				 
				 if(args[0].isEmpty()){
					 return tmpList;
				 }
				 
				 for(String tmpString : tmpList){
					 String incomplete = args[0].toLowerCase();
					 if(startsWithIgnoreCase(tmpString,incomplete)){
						 list.add(tmpString);
					 }
				 }
			}else if(args.length == 2 && !args[0].equalsIgnoreCase("register")){
				list = new ArrayList<String>();
				for(RegicideGame game : RegicidePlugin.regicidePlugin.getGames()){
					//should only match games started with what's already been typed in					
					if(args[1].isEmpty() || startsWithIgnoreCase(game.getName(),args[1])){
						list.add(game.getName());
					}
				}
			}else if(args.length == 3 && args[0].equalsIgnoreCase("kick")){
				//regicide kick [game] [player]
				for(RegicideGame game : RegicidePlugin.regicidePlugin.getGames()){
					if(game.getName().equalsIgnoreCase(args[1])){
						//get a list of the players in the game
						for(RPlayer player : game.getPlayers()){
							if(args[2].isEmpty() || startsWithIgnoreCase(player.getPlayer().getName(), args[2])){
								list.add(player.getPlayer().getName());
							}
						}
					}
				}
			}
			return list;
		}
		return null;
	}
	
	private static boolean startsWithIgnoreCase(String string1, String string2){
		string1 = string1.toLowerCase();
		string2 = string2.toLowerCase();
		return string1.startsWith(string2);
	}

}
