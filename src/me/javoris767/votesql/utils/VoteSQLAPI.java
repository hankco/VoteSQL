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
	public static Boolean mySQLSupport;
	private static VoteSQLConfigs cm;

	public VoteSQLAPI(VoteSQL plugin)
	{
		_plugin = plugin;
		VoteSQL.v = plugin.getDescription().getVersion();
		mySQLSupport = cm.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
				.getBoolean("VoteSQL.MySQL.Enabled");

		registerUtils();
		registerListeners();
		registerCommands();
		setUpSQL();
		attemptMetrics();
		findVotifier();
	}

	private void setUpSQL()
	{
		if (mySQLSupport)
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
