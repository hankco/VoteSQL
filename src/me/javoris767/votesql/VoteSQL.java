package me.javoris767.votesql;

import me.javoris767.votesql.utils.VoteSQLAPI;
import me.javoris767.votesql.utils.VoteSQLChat;
import me.javoris767.votesql.utils.VoteSQLConfFile;

import org.bukkit.plugin.java.JavaPlugin;

public class VoteSQL extends JavaPlugin
{
	public static String v;

	public void onDisable()
	{
		VoteSQLChat.disableMessage();
		if (VoteSQLAPI.getConfigs().getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
				.getBoolean("VoteSQL.MySQL.Enabled") == true)
		{
			VoteSQLAPI.saveDataFile();
		}
	}

	public void onEnable()
	{
		new VoteSQLAPI(this);
		VoteSQLChat.enableMessage();
	}
}
