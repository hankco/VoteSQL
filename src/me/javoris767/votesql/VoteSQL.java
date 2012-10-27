package me.javoris767.votesql;

import me.javoris767.votesql.utils.VoteSQLAPI;
import me.javoris767.votesql.utils.VoteSQLChat;

import org.bukkit.plugin.java.JavaPlugin;

public class VoteSQL extends JavaPlugin
{
	public static String v;

	public void onDisable()
	{
		VoteSQLChat.disableMessage();
	}

	public void onEnable()
	{
		new VoteSQLAPI(this);
		VoteSQLChat.enableMessage();
	}
}
