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
		if(cmd.getName().equalsIgnoreCase("regicide")){
			List<String> list=new ArrayList<String>();
			if(args.length == 1){
				System.out.println("ARGs[0]: " + args[0]);
				List<String> tmpList;
				 tmpList = RegicideCommands.getCommandList();//get the list of commands
				 //only put the ones that start with the given
				 if(args[0].isEmpty()){
					 return tmpList;
				 }
				 
				 for(String tmpString : tmpList){
					 if(tmpString.startsWith(args[0])){
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

}
