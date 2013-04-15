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
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VoteSQLAPI
{
	private VoteSQL _plugin;
	public static Economy econ = null;

	private static VoteSQLConfigs cm;
	public static HashMap<String, Integer> voteMap = new HashMap<String, Integer>();

	public VoteSQLAPI(VoteSQL plugin)
	{
		_plugin = plugin;
		VoteSQL.v = _plugin.getDescription().getVersion();
		
		registerUtils();
		findVotifier();
		findVault();
		registerListeners();
		registerCommands();
		setUpSQL();
		attemptMetrics();
		loadConfig();
		if (_plugin.getConfig().getBoolean("VoteSQL.FlatFile.Enabled") == true)
		{
			voteMap = new HashMap<String, Integer>();
			loadDataFile();
		}
	}

	private void loadConfig() {
		String path1 = "VoteSQL.FlatFile.Enabled";
		String path2 = "VoteSQL.MySQL.Enabled";
		String path3 = "VoteSQL.MySQL.Server";
		String path4 = "VoteSQL.MySQL.Database";
		String path5 = "VoteSQL.MySQL.User";
		String path6 = "VoteSQL.MySQL.Password";
		String path7 = "VoteSQL.MySQL.Table_Prefix";

		String path8 = "VoteSQL.onVote.messageEnabled";
		String path9 = "VoteSQL.onVote.Message";
		String path10 = "VoteSQL.onVote.commandsEnabled";
		//String path11 = "VoteSQL.onVoteCommands";

		String path12 = "VoteSQL.currency.Enabled";
		String path13 = "VoteSQL.currency.Amount";
		String path14 = "VoteSQL.currency.Message";

		_plugin.getConfig().addDefault(path1, false);
		_plugin.getConfig().addDefault(path2, false);
		_plugin.getConfig().addDefault(path3, "Server Address eg.Localhost");
		_plugin.getConfig().addDefault(path4, "Place Database name here");
		_plugin.getConfig().addDefault(path5, "Place User of MySQL Database here");
		_plugin.getConfig().addDefault(path6, "Place User password here");
		_plugin.getConfig().addDefault(path7, "votesql");


		_plugin.getConfig().addDefault(path8, true);
		_plugin.getConfig().addDefault(path9, "&2Thank you for voting %P from %S!");
		_plugin.getConfig().addDefault(path10, false);

		_plugin.getConfig().addDefault(path12, false);
		_plugin.getConfig().addDefault(path13, 150);
		_plugin.getConfig().addDefault(path14, "&2%P, You received %M dollars!");

		_plugin.getConfig().options().copyDefaults(true);
		_plugin.saveConfig();
	}

	private void setUpSQL()
	{
		if (_plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true) {
			VoteSQLChat.logInfo("Connecting to SQL database!");
			{
				Connection connection = null;
				Statement st = null;
				int rs = 0;
				try
				{
					connection = DriverManager.getConnection("jdbc:MySQL://"
							+ _plugin.getConfig().getString("VoteSQL.MySQL.Server")
							+ "/"
							+ _plugin.getConfig().getString("VoteSQL.MySQL.Database"),
							_plugin.getConfig().getString("VoteSQL.MySQL.User"),
							_plugin.getConfig().getString("VoteSQL.MySQL.Password"));
					st = connection.createStatement();
					rs = st.executeUpdate("CREATE TABLE IF NOT EXISTS `"
							+ _plugin.getConfig().getString("VoteSQL.MySQL.Table_Prefix")
							+ "`( `id` MEDIUMINT NOT NULL AUTO_INCREMENT, `playername` text, `votes` MEDIUMINT(255), PRIMARY KEY (`id`))");
					VoteSQLChat.logInfo("SQL database connected!");
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					VoteSQLChat.logSevere(" Error:" + rs);
				}
			}
			return;
		}
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
	private boolean findVault()
	{
		if (Bukkit.getPluginManager().getPlugin("Vault") != null)
		{
			VoteSQLChat.logInfo(" Vault has been found!");
			RegisteredServiceProvider<Economy> rsp = _plugin.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null) {
				return false;
			}
			econ = ((Economy)rsp.getProvider());
			return econ != null;
		}
		else
		{
			VoteSQLChat.logInfo(" Vault can not be found!");
		}
		return false;
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
		//new JoinListener(_plugin);
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
