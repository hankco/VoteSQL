package me.javoris767.votesql.utils;

import java.util.HashMap;
import me.javoris767.votesql.VoteSQL;

import org.bukkit.configuration.file.YamlConfiguration;

public class VoteSQLConfigs
{

	private final HashMap<VoteSQLConfFile, YamlConfiguration> _configurations;
	VoteSQL plugin;

	public VoteSQLConfigs(VoteSQL voteSQL)
	{
		plugin = voteSQL;
		_configurations = new HashMap<VoteSQLConfFile, YamlConfiguration>();
	}
	public YamlConfiguration getConfig(VoteSQLConfFile file)
	{
		return _configurations.get(file);

	}
}