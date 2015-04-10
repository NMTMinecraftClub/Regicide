package nmt.minecraft.Regicide.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nmt.minecraft.Regicide.RegicidePlugin;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class GameConfigManager {
	
	
	private YamlConfiguration config;

	
	public GameConfigManager() {
		config = null;
	}
	
	
	/**
	 * Attempts to load the yaml configuration from the provided file
	 * @param configFile
	 */
	public void loadConfig(File configFile) {
		if (configFile == null || !configFile.exists()) {
			RegicidePlugin.regicidePlugin.getLogger().warning("Unable to load config from file " + configFile);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
