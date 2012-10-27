package me.javoris767.votesql;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteSQLCommand implements CommandExecutor {

	public VoteSQL plugin;
	public VoteSQLCommand(VoteSQL voteSQL) {
		plugin = voteSQL;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.YELLOW + "/votesql reload -" + ChatColor.BLUE + " Reloads the config.");
				sender.sendMessage(ChatColor.YELLOW + "/votesql check <string> -" + ChatColor.BLUE + " Adds a string and 1 vote to the database.");
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("votesql.reload")) {
					plugin.reloadConfig();
					sender.sendMessage(ChatColor.GREEN + "[" + "VoteSQL " + plugin.getDescription().getVersion() + "]: Config reloaded!");
				}
				if(args[0].equalsIgnoreCase("check") && sender.hasPermission("votesql.check")) {
					sender.sendMessage(ChatColor.YELLOW + "/votesql check <string> -" + ChatColor.BLUE + " Adds a string and 1 vote to the database.");
				}
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("check") && sender.hasPermission("votesql.check")) {
						plugin.addData(args[1]);
						sender.sendMessage(ChatColor.GREEN + "[" + "VoteSQL " + plugin.getDescription().getVersion() + "]: Vote Passed!");
					}
				}
			}
			return false;
	}
}