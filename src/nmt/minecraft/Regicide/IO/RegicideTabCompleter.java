package nmt.minecraft.Regicide.IO;

import java.util.ArrayList;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class RegicideTabCompleter implements TabCompleter{
	//public static RegicidePlugin plugin = RegicidePlugin.plugin; //not sure if this is needed
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if(cmd.getName().equalsIgnoreCase("regicide")){
			List<String> list=null;
			if(args.length == 1){
				 list = RegicideCommands.getCommandList();//get the list of commands
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
