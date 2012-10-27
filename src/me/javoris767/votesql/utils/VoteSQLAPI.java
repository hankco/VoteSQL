package me.javoris767.votesql.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.commands.VoteSQLCommand;
import me.javoris767.votesql.listeners.VotingListener;

import org.bukkit.Bukkit;

public class VoteSQLAPI
{
	private VoteSQL _plugin;

	public VoteSQLAPI(VoteSQL plugin)
	{
		_plugin = plugin;
		VoteSQL.v = plugin.getDescription().getVersion();
		registerUtils();
		LoadConfiguration();
		registerListeners();
		registerCommands();
		setUpSQL();
		attemptMetrics();
		findVotifier();
	}

	private void setUpSQL()
	{
		if (_plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled"))
		{
			Connection connection = null;
			Statement st = null;
			int rs = 0;
			try
			{
				connection = DriverManager
						.getConnection(
								"jdbc:MySQL://"
										+ _plugin.getConfig().getString(
												"VoteSQL.MySQL.Server")
										+ "/"
										+ _plugin.getConfig().getString(
												"VoteSQL.MySQL.Database"),
								_plugin.getConfig().getString(
										"VoteSQL.MySQL.User"),
								_plugin.getConfig().getString(
										"VoteSQL.MySQL.Password"));
				st = connection.createStatement();
				rs = st.executeUpdate("CREATE TABLE IF NOT EXISTS `"
						+ _plugin.getConfig().getString(
								"VoteSQL.MySQL.Table_Prefix")
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

	private void LoadConfiguration()
	{
		String path0 = "VoteSQL.MySQL.Enabled";
		String path1 = "VoteSQL.MySQL.Server";
		String path2 = "VoteSQL.MySQL.Database";
		String path3 = "VoteSQL.MySQL.User";
		String path4 = "VoteSQL.MySQL.Password";
		String path5 = "VoteSQL.MySQL.Table_Prefix";
		String path6 = "VoteSQL.onVote.Message";

		_plugin.getConfig().addDefault(path0, false);
		_plugin.getConfig().addDefault(path1, "Server Address eg.Localhost");
		_plugin.getConfig().addDefault(path2, "Place Database name here");
		_plugin.getConfig().addDefault(path3,
				"Place User of MySQL Database here");
		_plugin.getConfig().addDefault(path4, "Place User password here");
		_plugin.getConfig().addDefault(path5, "votesql");
		_plugin.getConfig().addDefault(path6,
				"Put the message you want to be broadcasted!");
		_plugin.getConfig().options().copyDefaults(true);
		_plugin.getConfig()
				.options()
				.header("Thanks for choosing VoteSQL! Simply change the info below.");
		_plugin.saveConfig();

	}

	private void registerUtils()
	{
		new Permissions(_plugin);
		new Functions(_plugin);
		new VoteSQLChat(_plugin);
	}

	private void registerListeners()
	{
		new VotingListener(_plugin);
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
