package me.javoris767.votesql.utils;

import java.util.HashMap;
import me.javoris767.votesql.VoteSQL;

import org.bukkit.configuration.file.YamlConfiguration;

public class VoteSQLConfigs
{

	private final HashMap<VoteSQLConfFile, YamlConfiguration> _configurations;
	VoteSQL _plugin;

	public VoteSQLConfigs(VoteSQL plugin)
	{
		_configurations = new HashMap<VoteSQLConfFile, YamlConfiguration>();
		_plugin = plugin;
	}
	public YamlConfiguration getConfig(VoteSQLConfFile file)
	{
		return _configurations.get(file);

	}
}