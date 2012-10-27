package me.javoris767.votesql.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.javoris767.votesql.VoteSQL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class VoteSQLConfigs
{

	private final HashMap<VoteSQLConfFile, YamlConfiguration> _configurations;

	@SuppressWarnings("unused")
	private final VoteSQL _plugin;

	public VoteSQLConfigs(VoteSQL plugin)
	{
		_plugin = plugin;
		_configurations = new HashMap<VoteSQLConfFile, YamlConfiguration>();

		this.loadConfig();
	}

	private void createConfig(VoteSQLConfFile config, File file)
	{
		switch (config)
		{
		case VOTESQLSETTINGS:
			CommentedYamlConfiguration voteSQLSettings = new CommentedYamlConfiguration();
			String path0 = "VoteSQL.MySQL.Enabled";
			String path1 = "VoteSQL.MySQL.Server";
			String path2 = "VoteSQL.MySQL.Database";
			String path3 = "VoteSQL.MySQL.User";
			String path4 = "VoteSQL.MySQL.Password";
			String path5 = "VoteSQL.MySQL.Table_Prefix";
			String path6 = "VoteSQL.onVote.Message";
			voteSQLSettings.addDefault(path0, false);
			voteSQLSettings.addDefault(path1, "Server Address eg.Localhost");
			voteSQLSettings.addDefault(path2, "Place Database name here");
			voteSQLSettings.addDefault(path3,
					"Place User of MySQL Database here");
			voteSQLSettings.addDefault(path4, "Place User password here");
			voteSQLSettings.addDefault(path5, "votesql");
			voteSQLSettings.addDefault(path6,
					"Put the message you want to be broadcasted!");
			voteSQLSettings.options().copyDefaults(true);
			voteSQLSettings
					.options()
					.header("Thanks for choosing VoteSQL! Simply change the info below.");
			try
			{
				voteSQLSettings.save(file);
			}
			catch (IOException e2)
			{
			}

			_configurations.put(config, voteSQLSettings);
			break;
		case PLAYERDATA:
			CommentedYamlConfiguration playerDataConfig = new CommentedYamlConfiguration();
			try
			{
				playerDataConfig.save(file);
			}
			catch (IOException e2)
			{
			}

			_configurations.put(config, playerDataConfig);
			break;
		}
	}

	/**
	 * Gets a value for path in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to search in
	 * @param String
	 *            Path to search for
	 * @return String Value contained by path
	 */
	public String getProperty(VoteSQLConfFile file, String path)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			String prop = conf.getString(path, "NULL");

			if (!prop.equalsIgnoreCase("NULL"))
				return prop;
			conf.set(path, null);
		}

		return null;
	}

	public YamlConfiguration getConfig(VoteSQLConfFile file)
	{
		return _configurations.get(file);

	}

	/**
	 * Gets a value for path in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to search in
	 * @param String
	 *            Path to search for
	 * @return List<String> Value contained by path
	 */
	public List<String> getPropertyList(VoteSQLConfFile file, String path)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			List<String> prop = conf.getStringList(path);
			if (!prop.contains("NULL"))
				return prop;
			conf.set(path, null);
		}

		return null;
	}

	/**
	 * Loads the plugin's configuration files
	 */
	public void loadConfig()
	{
		for (VoteSQLConfFile file : VoteSQLConfFile.values())
		{
			File confFile = new File(file.getPath());

			if (confFile.exists())
			{
				if (_configurations.containsKey(file))
					_configurations.remove(file);

				YamlConfiguration conf = YamlConfiguration
						.loadConfiguration(confFile);
				_configurations.put(file, conf);
			}
			else
			{
				File parentFile = confFile.getParentFile();

				if (!parentFile.exists())
					parentFile.mkdirs();

				this.createConfig(file, confFile);
			}
		}

	}

	/**
	 * Checks if path exists in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to search in
	 * @param String
	 *            Path to search for
	 * @return boolean Property exists
	 */
	public boolean propertyExists(VoteSQLConfFile file, String path)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			if (conf.contains(path))
				return true;
		}

		return false;
	}

	/**
	 * Sets path to null in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set null
	 * @return boolean If completed
	 */
	public boolean removeProperty(VoteSQLConfFile file, String path)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, null);
			return true;
		}

		return false;
	}

	/**
	 * Saves the plugin's configs
	 */
	public void saveConfig()
	{
		for (VoteSQLConfFile file : VoteSQLConfFile.values())
		{
			if (_configurations.containsKey(file))
				try
				{
					_configurations.get(file).save(new File(file.getPath()));
				}
				catch (IOException e)
				{

				}
		}
	}

	/**
	 * Sets path to value in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set
	 * @param boolean Value to set
	 * @return boolean If completed
	 */
	public boolean setProperty(VoteSQLConfFile file, String path, boolean value)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, value);
			try
			{
				conf.save(new File(file.getPath()));
			}
			catch (IOException e)
			{

			}
			return true;
		}

		return false;
	}

	/**
	 * Sets path to value in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set
	 * @param double Value to set
	 * @return boolean If completed
	 */
	public boolean setProperty(VoteSQLConfFile file, String path, Double value)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, value);
			try
			{
				conf.save(new File(file.getPath()));
			}
			catch (IOException e)
			{

			}
			return true;
		}

		return false;
	}

	/**
	 * Sets path to value in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set
	 * @param int Value to set
	 * @return boolean If completed
	 */
	public boolean setProperty(VoteSQLConfFile file, String path, int value)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, value);
			try
			{
				conf.save(new File(file.getPath()));
			}
			catch (IOException e)
			{
			}
			return true;
		}

		return false;
	}

	/**
	 * Sets path to value in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set
	 * @param String
	 *            Value to set
	 * @return boolean If completed
	 */
	public boolean setProperty(VoteSQLConfFile file, String path, String value)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, value);
			try
			{
				conf.save(new File(file.getPath()));
			}
			catch (IOException e)
			{

			}
			return true;
		}

		return false;
	}

	/**
	 * Sets path to list of values in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set
	 * @param ArrayList
	 *            <String> List of values
	 * @return boolean If completed
	 */
	public boolean setPropertyList(VoteSQLConfFile file, String path,
			ArrayList<String> list)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, list);
			try
			{
				conf.save(new File(file.getPath()));
			}
			catch (IOException e)
			{
			}
			return true;
		}

		return false;
	}

	/**
	 * Sets path to list of values in file
	 * 
	 * @param VoteSQLConfFile
	 *            File to set in
	 * @param String
	 *            Path to set
	 * @param List
	 *            <String> List of values to set
	 * @return boolean If completed
	 */
	public boolean setPropertyList(VoteSQLConfFile file, String path,
			List<String> list)
	{
		FileConfiguration conf = _configurations.get(file);

		if (conf != null)
		{
			conf.set(path, list);
			try
			{
				conf.save(new File(file.getPath()));
			}
			catch (IOException e)
			{
			}
			return true;
		}

		return false;
	}
}
