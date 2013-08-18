package me.javoris767.votesql.utils;

import me.javoris767.votesql.VoteSQL;

public class Permissions
{
	@SuppressWarnings("unused")
	private VoteSQL plugin;

	public Permissions(VoteSQL voteSQL)
	{
		plugin = voteSQL;
	}

	public static final String MAINCOMMAND_CHECK = "votesql.check";
	public static final String MAINCOMMAND_RELOAD = "votesql.reload";
	public static final String MAINCOMMAND_TOP = "votesql.top";
	public static final String MAINCOMMAND_RANK = "votesql.rank";
	public static final String MAINCOMMAND_VOTES = "votesql.votes";
}
