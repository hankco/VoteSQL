package me.javoris767.votesql;

import java.io.IOException;
import java.sql.SQLException;

import me.javoris767.votesql.utils.*;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteSQL extends JavaPlugin
{
	public static String v;
	public static VoteSQL plugin;
	
	public void onDisable()
	{
		VoteSQLChat.disableMessage();
		if (getConfig().getBoolean("VoteSQL.FlatFile.Enabled") == true)
		{
			try {
				VoteSQLAPI.saveDataFile();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEnable()
	{
		try {
			new VoteSQLAPI(this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		VoteSQLChat.enableMessage();
	}
}
