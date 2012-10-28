package me.javoris767.votesql.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.commands.VoteSQLCommand;
import me.javoris767.votesql.listeners.VotingListener;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class VoteSQLAPI
{
	private VoteSQL _plugin;

	private static VoteSQLConfigs cm;
	public static HashMap<String, Integer> voteMap;

	public VoteSQLAPI(VoteSQL plugin)
	{
		_plugin = plugin;
		VoteSQL.v = _plugin.getDescription().getVersion();
		registerUtils();
		registerListeners();
		registerCommands();
		setUpSQL();
		attemptMetrics();
		findVotifier();
		if (cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS).getBoolean(
				"VoteSQL.FlatFile.Enabled") == true)
		{
			voteMap = new HashMap<String, Integer>();
			loadDataFile();
		}
	}

	private void setUpSQL()
	{
		if (cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS).getBoolean(
				"VoteSQL.MySQL.Enabled"))
		{
			Connection connection = null;
			Statement st = null;
			int rs = 0;
			try
			{
				connection = DriverManager.getConnection("jdbc:MySQL://"
						+ cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
								.getString("VoteSQL.MySQL.Server")
						+ "/"
						+ cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
								.getString("VoteSQL.MySQL.Database"),
						cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
								.getString("VoteSQL.MySQL.User"),
						cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
								.getString("VoteSQL.MySQL.Password"));
				st = connection.createStatement();
				rs = st.executeUpdate("CREATE TABLE IF NOT EXISTS `"
						+ cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
								.getString("VoteSQL.MySQL.Table_Prefix")
						+ "`( `id` MEDIUMINT NOT NULL AUTO_INCREMENT, `playername` text, `votes` MEDIUMINT(255), PRIMARY KEY (`id`))");
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				VoteSQLChat.logSevere(" Error:" + rs);
			}
		}
		return;
	}

	public static void saveDataFile()
	{
		YamlConfiguration config = cm.getConfig(VoteSQLConfFile.PLAYERDATA);
		File df = new File("plugins" + File.separator + "VoteSQL"
				+ File.separator + "playerdata.yml");
		try
		{
			config.save(df);
		}
		catch (IOException ex)
		{
			VoteSQLChat.logInfo("Could not save the data!");
		}
		for (String name : voteMap.keySet())
		{
			if (name != null || voteMap != null)
			{
				config.set("Voter." + name.toLowerCase() + ".amountOfVotes",
						voteMap.get(name.toLowerCase()).intValue());
				return;
			}
			VoteSQLChat.logInfo("Could not save the data!");
			return;
		}
		return;
	}

	public static void loadDataFile()
	{
		YamlConfiguration config = VoteSQLAPI.getConfigs().getConfig(
				VoteSQLConfFile.PLAYERDATA);

		ConfigurationSection voteSection = config
				.getConfigurationSection("Voter");
		if (voteSection == null)
		{
			config.createSection("Voter");
			return;
		}
		if (voteMap == null)
		{
			VoteSQLChat.logSevere(" HashMap 'voteMap' did not save correctly!");
			return;
		}
		for (String key : voteSection.getKeys(false))
		{
			voteMap.put(
					key.toLowerCase(),
					config.getInt("Voter." + key.toLowerCase()
							+ ".amountOfVotes"));
		}
		return;
	}

	private void findVotifier()
	{
		if (Bukkit.getPluginManager().getPlugin("Votifier") != null)
		{
			VoteSQLChat.logInfo(" Votifier has been found!");
		}
		else
		{
			VoteSQLChat.logInfo(" Votifier can not be found!");
			VoteSQLChat.logInfo(" Disabling the plugin!");
			Bukkit.getPluginManager().disablePlugin(_plugin);
		}

	}

	private void registerCommands()
	{

		VoteSQLCommand vC = new VoteSQLCommand(_plugin);
		_plugin.getCommand("votesql").setExecutor(vC);
	}

	private void registerUtils()
	{
		new Permissions(_plugin);
		new Functions(_plugin);
		new VoteSQLChat(_plugin);
		cm = new VoteSQLConfigs(_plugin);
	}

	private void registerListeners()
	{
		new VotingListener(_plugin);
	}

	public static VoteSQLConfigs getConfigs()
	{

		return cm;

	}

	private void attemptMetrics()
	{
		try
		{
			MetricsLite metrics;
			metrics = new MetricsLite(_plugin);
			metrics.start();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		return;
	}
}
