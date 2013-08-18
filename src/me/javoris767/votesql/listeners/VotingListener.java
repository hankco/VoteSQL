package me.javoris767.votesql.listeners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.utils.Functions;
import me.javoris767.votesql.utils.VoteSQLAPI;
import me.javoris767.votesql.utils.VoteSQLChat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VotingListener implements Listener
{
	private VoteSQL plugin;

	public VotingListener(VoteSQL voteSQL)
	{
		plugin = voteSQL;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onVote(VotifierEvent event) throws ClassNotFoundException
	{
		Vote vote = event.getVote();
		String siteVotedOn = vote.getServiceName();
		String username = vote.getUsername();
		Player player = Bukkit.getPlayer(username);
		int money = plugin.getConfig().getInt("VoteSQL.currency.Amount");

		// Broadcast Vote
		if (plugin.getConfig().getBoolean("VoteSQL.onVote.messageEnabled") == true)
		{
			VoteSQLChat.broadcastVoteMessage(username, siteVotedOn);
			VoteSQLChat.logInfo("Calling broadcast");
		}

		// Currency
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			VoteSQLChat.logSevere("Vault not found!");
		}else
			if(plugin.getConfig().getBoolean("VoteSQL.currency.Enabled") == true)
			{
				if(player.isOnline()) {
					VoteSQLChat.sendCurrencyReveivedMessage(player, username, money);
				}
				Functions.addMoney(player, plugin.getConfig().getInt("VoteSQL.currency.Amount"));
				VoteSQLChat.logInfo("Calling Currency");
			}

		// Add to SQL
		if (plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true)
		{
			if(username != "" || username != null) {
				Functions.addData(username);
				VoteSQLChat.logInfo("Calling MySQL");
			}
		}

		// Custom Commands
		List<String> commands = new ArrayList<String>();
		if(plugin.getConfig().getBoolean("VoteSQL.onVote.commandsEnabled") == true)
		{
			commands = plugin.getConfig().getStringList("VoteSQL.onVote.Commands");
			for (String command : commands) {
				if(command.contains("%p")) {
					command = command.replace("%p", player.getName());
				}
				command = Functions.formatMessage(command, vote);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				VoteSQLChat.logInfo("Calling Commands");
			}

			// Add to FlatFile
			if (plugin.getConfig().getBoolean("VoteSQL.FlatFile.Enabled") == true)
			{     
				if(username != "" || username != null) {
					VoteSQLAPI.voteMap.put(username.toLowerCase(), 0);
					Integer numberOfVotes = VoteSQLAPI.voteMap.get(username.toLowerCase());

					numberOfVotes++;

					VoteSQLAPI.voteMap.put(username.toLowerCase(), numberOfVotes);
					try {
						VoteSQLAPI.saveDataFile();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InvalidConfigurationException e) {
						e.printStackTrace();
					}
					VoteSQLChat.logInfo("Calling Flatfile");
				}
			}
		}
	}
}