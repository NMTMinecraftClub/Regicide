package nmt.minecraft.Regicide.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class GameConfigManager {
	
	
	private YamlConfiguration config;
	
	private RegicideGame game;

	
	public GameConfigManager(RegicideGame game) {
		config = null;
		this.game = game;
	}
	
	
	/**
	 * Attempts to load the yaml configuration from the provided file
	 * @param configFile
	 */
	public void loadConfig(File configFile) {
		if (configFile == null || !configFile.exists()) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to load config from file " + configFile);
			return;
		}
		
		config = new YamlConfiguration();
		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Error when loading yaml! Invalid Configuration from file: " + configFile);
			return;
		}
		
	}
	
	public List<Location> getSpawnLocations() {
		
		if (config == null) {
			return null;
		}
		
		List<Location> locs = new LinkedList<Location>();
		Location loc;
		
		ConfigurationSection spawnPoints = config.getConfigurationSection("spawnPoints");
		for (String key : spawnPoints.getKeys(false)) {
			loc = (Location) spawnPoints.get(key, null);
			if (loc != null) {
				locs.add(loc);
			}
		}
		
		return locs;
	}
	
	public Location getExit() {
		if (config == null) {
			return null;
		}
		
		return (Location) config.get("exitLocation", null);
	}
	
	public Location getOthers() {
		if (config == null) {
			return null;
		}
		
		return (Location) config.get("otherLocation", null);
	}
	
	public Location getFirst() {
		if (config == null) {
			return null;
		}
		
		return (Location) config.get("firstLocation", null);
	}
	
	public Location getSecond() {
		if (config == null) {
			return null;
		}
		
		return (Location) config.get("secondLocation", null);
	}
	
	public Location getThird() {
		if (config == null) {
			return null;
		}
		
		return (Location) config.get("thirdLocation", null);
	}
	
	public Location getLobby() {
		if (config == null) {
			return null;
		}
		
		return (Location) config.get("lobbyLocation", null);
	}
	
	
	public void save(File configFile) {
		if (configFile == null) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to save config to file: " + configFile);
			return;	
		}
		
		if (config == null) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to save config, because the config is null!");
			return;		
		}
		
		update();
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to save config to file: " + configFile);
			e.printStackTrace();
		}
	}
	
	//Sync config with game
	private void update() {
		if (config == null) {
			config = new YamlConfiguration();
		}
		
		config.set("exitLocation", game.getExitLocation());
		config.set("otherLocation", game.getOtherPlace());
		config.set("firstLocation", game.getFirstPlace());
		config.set("secondLocation", game.getSecondPlace());
		config.set("thirdLocation", game.getThirdPlace());
		config.set("lobbyLocation", game.getLobbyLocation());
		
		List<Location> spawnPoints = game.getSpawnLocations();
		//reset spawn points
		config.set("spawnPoints", null);
		
		int index = 0;
		ConfigurationSection pointsSec = config.getConfigurationSection("spawnPoints");
		if (!(spawnPoints == null) && !spawnPoints.isEmpty()) {
			for (Location loc : spawnPoints) {
				pointsSec.set("loc" + index, loc);
			}
		}
		
	}
	
	
}
