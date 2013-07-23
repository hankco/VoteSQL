package me.javoris767.votesql;

import java.io.IOException;

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
		new VoteSQLAPI(this);
		VoteSQLChat.enableMessage();
	}
}
