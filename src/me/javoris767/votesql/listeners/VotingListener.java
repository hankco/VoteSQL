package me.javoris767.votesql.listeners;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.utils.Functions;
import me.javoris767.votesql.utils.VoteSQLAPI;
import me.javoris767.votesql.utils.VoteSQLConfFile;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VotingListener implements Listener
{
	private VoteSQL _plugin;

	public VotingListener(VoteSQL plugin)
	{
		_plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, _plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVote(VotifierEvent event)
	{
		Vote vote = event.getVote();
		String username = vote.getUsername();
		if (VoteSQLAPI.getConfigs().getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
				.getBoolean("VoteSQL.MySQL.Enabled") == true)
		{
			Functions.addData(username);
		}
		if (VoteSQLAPI.getConfigs().getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
				.getBoolean("VoteSQL.FlatFile.Enabled") == true)
		{
			Integer numberOfVotes = VoteSQLAPI.voteMap.get(username
					.toLowerCase());
			if (numberOfVotes == null)
			{
				numberOfVotes = 0;
			}
			VoteSQLAPI.voteMap.put(username.toLowerCase(), numberOfVotes + 1);
			VoteSQLAPI.saveDataFile();
		}
		return;
	}
}
