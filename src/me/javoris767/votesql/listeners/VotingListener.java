package me.javoris767.votesql.listeners;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.utils.Functions;
import me.javoris767.votesql.utils.VoteSQLAPI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VotingListener implements Listener
{
	@SuppressWarnings("unused")
	private VoteSQL _plugin;

	public VotingListener(VoteSQL plugin)
	{
		_plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVote(VotifierEvent event)
	{
		Vote vote = event.getVote();
		String username = vote.getUsername();
		if (VoteSQLAPI.mySQLSupport == true)
		{
			Functions.addData(username);
		}
		return;
	}
}
