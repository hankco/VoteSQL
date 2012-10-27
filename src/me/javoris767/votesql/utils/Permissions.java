package me.javoris767.votesql.utils;

import me.javoris767.votesql.VoteSQL;

public class Permissions
{
	@SuppressWarnings("unused")
	private VoteSQL _plugin;

	public Permissions(VoteSQL plugin)
	{
		_plugin = plugin;
	}

	public static final String MAINCOMMAND_CHECK = "votesql.check";
	public static final String MAINCOMMAND_RELOAD = "votesql.reload";
	public static final String MAINCOMMAND = "votesql.command.main";
}
