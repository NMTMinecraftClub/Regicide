package nmt.minecraft.Regicide.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.Regicide.RegicidePlugin;
import nmt.minecraft.Regicide.Game.RegicideGame;

import org.bukkit.Bukkit;
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
			RegicidePlugin.regicidePlugin.getLogger().warning(e.getLocalizedMessage());
			return;
		}
		
	}
	
	public List<Location> getSpawnLocations() {
		
		if (config == null) {
			return null;
		}
		if (!config.contains("spawnPoints")) {
			return null;
		}
		
		List<Location> locs = new LinkedList<Location>();
		ConfigurationSection loc;
		Location tmpLoc;
		
		ConfigurationSection spawnPoints = config.getConfigurationSection("spawnPoints");
		
		
		for (String key : spawnPoints.getKeys(false)) {
			loc = spawnPoints.getConfigurationSection(key);
			if (loc != null) {
				tmpLoc = new Location(Bukkit.getWorld(loc.getString("world", "world")), loc.getDouble("x", 0.0),  loc.getDouble("y", 0.0), loc.getDouble("z", 0.0), (float) loc.getDouble("yaw", 0.0), (float) loc.getDouble("pitch", 0.0));
				locs.add(tmpLoc);
			}
		}

		/**
		 * x
		 * y
		 * z
		 * world
		 * pitch
		 * yaw
		 */
		
		return locs;
	}
	
	public Location getExit() {
		if (config == null) {
			return null;
		}
		
		//return (Location) config.get("exitLocation", null);\
		return getLocation("exitLocation");
	}
	
	public Location getOthers() {
		if (config == null) {
			return null;
		}
		
		//return (Location) config.get("otherLocation", null);
		return getLocation("otherLocation");
	}
	
	public Location getFirst() {
		if (config == null) {
			return null;
		}
		
		//return (Location) config.get("firstLocation", null);
		return getLocation("firstLocation");
	}
	
	public Location getSecond() {
		if (config == null) {
			return null;
		}
		
		//return (Location) config.get("secondLocation", null);
		return getLocation("secondLocation");
	}
	
	public Location getThird() {
		if (config == null) {
			return null;
		}
		
		//return (Location) config.get("thirdLocation", null);
		return getLocation("thirdLocation");
	}
	
	public Location getLobby() {
		if (config == null) {
			return null;
		}
		
		//return (Location) config.get("lobbyLocation", null);
		return getLocation("lobbyLocation");
	}
	
	
	public void save(File configFile) {
		if (configFile == null) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to save config to file: " + configFile);
			return;	
		}
		
		update();
		
		if (config == null) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to save config, because the config is null!");
			return;		
		}
		
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
		
		//config.set("exitLocation", game.getExitLocation());
		setLocation("exitLocation", game.getExitLocation());
		//config.set("otherLocation", game.getOtherPlace());
		setLocation("otherLocation", game.getOtherPlace());
		//config.set("firstLocation", game.getFirstPlace());
		setLocation("firstLocation", game.getFirstPlace());
		//config.set("secondLocation", game.getSecondPlace());
		setLocation("secondLocation", game.getSecondPlace());
		//config.set("thirdLocation", game.getThirdPlace());
		setLocation("thirdLocation", game.getThirdPlace());
		//config.set("lobbyLocation", game.getLobbyLocation());
		setLocation("lobbyLocation", game.getLobbyLocation());
		
		List<Location> spawnPoints = game.getSpawnLocations();
		//reset spawn points
		config.set("spawnPoints", null);
		
		int index = 0;
		ConfigurationSection pointsSec = config.getConfigurationSection("spawnPoints");
		ConfigurationSection locSec;
		if (!(spawnPoints == null) && !spawnPoints.isEmpty()) {
			for (Location loc : spawnPoints) {
				locSec = pointsSec.getConfigurationSection("loc" + index);
				
				if (locSec == null) {
					locSec = config.createSection("loc" + index);
				}
				
				locSec.set("x", loc.getX());
				locSec.set("y", loc.getY());
				locSec.set("z", loc.getZ());
				locSec.set("world", loc.getWorld().getName());
				locSec.set("pitch", loc.getPitch());
				locSec.set("yaw", loc.getYaw());
			}
		}
		
	}
	
	private void setLocation(String path, Location loc) {
		
		if (loc == null || path == null) {
			return;
		}
		
		ConfigurationSection configSect = config.getConfigurationSection(path);
		if (configSect == null) {
			configSect = config.createSection(path);
		}
		
		configSect.set("x", loc.getX());
		configSect.set("y", loc.getY());
		configSect.set("z", loc.getZ());
		configSect.set("world", loc.getWorld().getName());
		configSect.set("pitch", loc.getPitch());
		configSect.set("yaw", loc.getYaw());
	}
	
	private Location getLocation(String path) {
		Location loc;
		ConfigurationSection configSect = config.getConfigurationSection(path);
		
		if (configSect == null) {
			return null;
		}
		
		loc = new Location(
				Bukkit.getWorld(configSect.getString("world", "world")),
				
				configSect.getDouble("x", 0.0),
				configSect.getDouble("y", 0.0),
				configSect.getDouble("z", 0.0),
				
				(float) configSect.getDouble("yaw", 0.0),
				(float) configSect.getDouble("pitch", 0.0)
				
				);
		
		return loc;
	}
	
}
