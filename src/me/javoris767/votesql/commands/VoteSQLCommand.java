package me.javoris767.votesql.commands;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.utils.Functions;
import me.javoris767.votesql.utils.Permissions;
import me.javoris767.votesql.utils.VoteSQLChat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteSQLCommand implements CommandExecutor
{

	public VoteSQL _plugin;

	public VoteSQLCommand(VoteSQL plugin)
	{
		_plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args)
	{
		if (command.getName().equalsIgnoreCase("votesql"))
			return handleVoteMySQL(sender, args);
		return true;
	}

	private boolean handleVoteMySQL(CommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.YELLOW + "/votesql reload -"
					+ ChatColor.BLUE + " Reloads the config.");
			sender.sendMessage(ChatColor.YELLOW + "/votesql check <string> -"
					+ ChatColor.BLUE
					+ " Adds a string and 1 vote to the database.");
			VoteSQLChat.broadcastVoteMessage();
		}
		else if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("reload"))
			{
				if (!sender.hasPermission(Permissions.MAINCOMMAND_RELOAD))
				{
					VoteSQLChat.dontHavePermission(sender);
					return true;
				}
				_plugin.reloadConfig();
				sender.sendMessage(ChatColor.GREEN + "[" + "VoteSQL "
						+ _plugin.getDescription().getVersion()
						+ "]: Config reloaded!");
			}
			else if (args[0].equalsIgnoreCase("check"))
			{
				if (!sender.hasPermission(Permissions.MAINCOMMAND_CHECK))
				{
					VoteSQLChat.dontHavePermission(sender);
					return true;
				}
				sender.sendMessage(ChatColor.YELLOW
						+ "/votesql check <string> -" + ChatColor.BLUE
						+ " Adds a string and 1 vote to the database.");
			}
			else if (args.length == 2)
			{
				if (args[0].equalsIgnoreCase("check"))
				{
					if (!sender.hasPermission(Permissions.MAINCOMMAND_CHECK))
					{
						VoteSQLChat.dontHavePermission(sender);
						return true;
					}
					Functions.addData(args[1]);
					sender.sendMessage(ChatColor.GREEN + "[" + "VoteSQL "
							+ _plugin.getDescription().getVersion()
							+ "]: Vote Passed!");
				}
			}
			return true;
		}
		return true;
	}
}
