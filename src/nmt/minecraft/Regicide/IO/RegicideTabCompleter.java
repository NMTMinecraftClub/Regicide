package nmt.minecraft.Regicide.IO;

import java.util.ArrayList;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class RegicideTabCompleter implements TabCompleter{
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		System.out.println("Alias: " + alias);
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
					list.add(game.getName());
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
