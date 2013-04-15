package me.javoris767.votesql.listeners;

import java.util.ArrayList;
import java.util.List;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.utils.Functions;
import me.javoris767.votesql.utils.VoteSQLAPI;
import me.javoris767.votesql.utils.VoteSQLChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
	public void onVote(VotifierEvent event)
	{
		Vote vote = event.getVote();
		String siteVotedOn = vote.getServiceName();
		String username = vote.getUsername();
		Player player = Bukkit.getPlayer(username);
		int money = _plugin.getConfig().getInt("VoteSQL.currency.Amount");

		// Broadcast Vote
		if (_plugin.getConfig().getBoolean("VoteSQL.onVote.messageEnabled") == true)
		{
			VoteSQLChat.broadcastVoteMessage(username, siteVotedOn);
		}

		// Currency
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			VoteSQLChat.logSevere("Vault not found!");
		}else
			if(_plugin.getConfig().getBoolean("VoteSQL.currency.Enabled") == true)
			{
				VoteSQLChat.sendCurrencyReveivedMessage(player, username, money);
				Functions.addMoney(player, _plugin.getConfig().getInt("VoteSQL.currency.Amount"));
			}

		// Add to SQL
		if (_plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true)
		{
			if(username == "" || username == null) {
				VoteSQLChat.logInfo("Empty vote string");
			}else{
				Functions.addData(username);
			}
		}

		// Custom Commands
		List<String> commands = new ArrayList<String>();
		if(_plugin.getConfig().getBoolean("VoteSQL.onVote.commandsEnabled") == true) 
		{
			commands = _plugin.getConfig().getStringList("VoteSQL.onVote.Commands");
			for (String command : commands) {
				if(command.contains("%p")) {
					command = command.replace("%p", player.getName());
				}
				command = Functions.formatMessage(command, vote);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}

			// Add to FlatFile
			if (_plugin.getConfig().getBoolean("VoteSQL.FlatFile.Enabled") == true)
			{
				Integer numberOfVotes = VoteSQLAPI.voteMap.get(username
						.toLowerCase());
				if(username == "" || username == null) {

				}else{
					numberOfVotes++;
					VoteSQLAPI.voteMap.put(username.toLowerCase(), numberOfVotes);
					VoteSQLAPI.saveDataFile();
				}
			}
		}
	}
}