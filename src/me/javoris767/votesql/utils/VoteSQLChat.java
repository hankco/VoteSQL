package me.javoris767.votesql.utils;

import java.util.logging.Logger;

import me.javoris767.votesql.VoteSQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class VoteSQLChat
{
	private static VoteSQL _plugin;

	private static String pluginName;
	private static String logName;
	private static String prefix;

	private static final Logger log = Logger.getLogger("Minecraft");

	public VoteSQLChat(VoteSQL plugin)
	{
		_plugin = plugin;
		pluginName = _plugin.getName();
		logName = "[" + pluginName + "]";
		prefix = ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + pluginName
				+ ChatColor.GRAY + "]";
	}

	public static void logInfo(String message)
	{
		log.info(logName + message);
	}

	public static void logSevere(String message)
	{
		log.severe(logName + message);
	}

	public static void logWarning(String message)
	{
		log.warning(logName + message);
	}

	public static void enableMessage()
	{
		logInfo(" v" + VoteSQL.v + " enabled.");
	}

	public static void disableMessage()
	{
		logInfo(" v" + VoteSQL.v + " disabled.");
	}

	public static void debugMessage()
	{
		logInfo(" DEBUG: We got here!");
	}

	public void sendMessage(CommandSender sender, String message)
	{
		sender.sendMessage(prefix + message);
	}

	public static void broadcast(String message)
	{
		Bukkit.getServer().broadcastMessage(prefix + message);
	}

	public static void broadcastVoteMessage()
	{
		String message = VoteSQLAPI.getConfigs()
				.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
				.get("VoteSQL.onVote.Message").toString();
		String newMessage = Functions.colorize(message);
		Bukkit.getServer().broadcastMessage(prefix + newMessage);
	}

	public static void dontHavePermission(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_RED + "You do not have permission!");
	}
}
