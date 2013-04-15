package me.javoris767.votesql;

import me.javoris767.votesql.utils.*;

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
			VoteSQLAPI.saveDataFile();
		}
	}

	@Override
	public void onEnable()
	{
		new VoteSQLAPI(this);
		VoteSQLChat.enableMessage();
	}
}
