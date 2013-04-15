package me.javoris767.votesql.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			VoteSQLChat.sendMessage(sender, ChatColor.YELLOW + "/votesql reload -"
					+ ChatColor.BLUE + " Reloads the config.");
			VoteSQLChat.sendMessage(sender, ChatColor.YELLOW + "/votesql check <string> -"
					+ ChatColor.BLUE
					+ " Adds 1 vote to the database.");
			VoteSQLChat.sendMessage(sender, ChatColor.YELLOW + "/votesql top -"
					+ ChatColor.BLUE
					+ " Shows the top 5 Voters!");
			return true;
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
				VoteSQLChat.sendMessage(sender, ChatColor.GREEN + "Config reloaded!");
			}
			else if (args[0].equalsIgnoreCase("top"))
			{
				if(!sender.hasPermission(Permissions.MAINCOMMAND_TOP)) 
				{
					VoteSQLChat.dontHavePermission(sender);
					return true;
				}
				if(_plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true) {

					String database = _plugin.getConfig().getString("VoteSQL.MySQL.Table_Prefix");
					Connection con = null;
					Statement stmt = null;
					ResultSet rs = null;
					try {
						con = DriverManager.getConnection(
								"jdbc:MySQL://"
										+ _plugin.getConfig().getString("VoteSQL.MySQL.Server")
										+ "/"
										+ _plugin.getConfig().getString("VoteSQL.MySQL.Database"),
										_plugin.getConfig().getString("VoteSQL.MySQL.User"),
										_plugin.getConfig().getString("VoteSQL.MySQL.Password"));

						stmt = con.createStatement();

						rs = stmt.executeQuery("SELECT * FROM " + database + " ORDER BY votes DESC LIMIT 5;");

						int i = 1;

						VoteSQLChat.sendMessage(sender, ChatColor.GOLD + "-=-=-=-=-=" + ChatColor.DARK_AQUA + "Top 5 Voters" + ChatColor.GOLD + "=-=-=-=-=-");

						while(rs.next()) {
							String q = ChatColor.DARK_AQUA + String.valueOf(i) + ". " + ChatColor.GREEN + rs.getString("playername") + ChatColor.DARK_AQUA + " - " + ChatColor.GREEN + rs.getInt("votes") + " votes";
							VoteSQLChat.sendMessage(sender, q);
							i++;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				else 				if (_plugin.getConfig().getBoolean("VoteSQL.FlatFile.Enabled") == true)  {
					VoteSQLChat.sendMessage(sender, "Command not impemented yet :[");
				}
				else if (args[0].equalsIgnoreCase("check"))
				{
					if (!sender.hasPermission(Permissions.MAINCOMMAND_CHECK))
					{
						VoteSQLChat.dontHavePermission(sender);
						return true;
					}
					VoteSQLChat.sendMessage(sender, ChatColor.YELLOW + "/votesql check <string> -"
							+ ChatColor.BLUE
							+ " Adds a string and 1 vote to the database.");
				} 
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("check")) {
					if (!sender.hasPermission(Permissions.MAINCOMMAND_CHECK))
					{
						VoteSQLChat.dontHavePermission(sender);
						return true;
					}
					Functions.addData(args[1]);
					VoteSQLChat.sendMessage(sender, ChatColor.YELLOW + "Vote Passed!");
				}
			}
		}
		return false;
	}
}