package me.javoris767.votesql.utils;

public enum VoteSQLConfFile
{
	PLAYERDATA("plugins/VoteSQL/playerdata.yml");

	public static VoteSQLConfFile fromName(String name)
	{
		for (VoteSQLConfFile id : VoteSQLConfFile.values())
		{
			if (id.name().equalsIgnoreCase(name))
				return id;
		}

		return null;
	}

	private final String _path;

	private VoteSQLConfFile(final String path)
	{
		_path = path;
	}

	public String getPath()
	{
		return _path;
	}

}