package me.javoris767.votesql.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.javoris767.votesql.VoteSQL;

public class Functions
{
	@SuppressWarnings("unused")
	private static VoteSQL _plugin;

	public Functions(VoteSQL plugin)
	{
		_plugin = plugin;
	}

	public static String colorize(String string)
	{
		return string.replaceAll("(?i)&([a-k0-9])", "\u00A7$1");
	}

	public static void addData(String playername)
	{
		PreparedStatement pst = null;
		Connection con = null;
		Statement stmt = null;
		int num = 1;
		ResultSet rs = null;
		try
		{
			con = DriverManager.getConnection(
					"jdbc:MySQL://"
							+ VoteSQLAPI.getConfigs()
									.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
									.getString("VoteSQL.MySQL.Server")
							+ "/"
							+ VoteSQLAPI.getConfigs()
									.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
									.getString("VoteSQL.MySQL.Database"),
					VoteSQLAPI.getConfigs()
							.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
							.getString("VoteSQL.MySQL.User"),
					VoteSQLAPI.getConfigs()
							.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
							.getString("VoteSQL.MySQL.Password"));

			String database = VoteSQLAPI.getConfigs()
					.getConfig(VoteSQLConfFile.VOTESQLSETTINGS)
					.getString("VoteSQL.MySQL.Table_Prefix");
			stmt = con.createStatement();
			if (stmt.execute("SELECT * FROM " + database
					+ " WHERE playername='" + playername + "';"))
			{
				rs = stmt.getResultSet();
				if (!rs.next())
				{
					pst = con.prepareStatement("INSERT INTO " + database
							+ "(playername, votes) VALUES(?, ?)");
					pst.setString(1, playername);
					pst.setInt(2, 1);
					pst.executeUpdate();
					System.out.print("inserted");
				}
				else
				{
					num = rs.getInt("votes");
					num++;
					pst = con.prepareStatement("UPDATE " + database
							+ " SET votes=? WHERE playername='" + playername
							+ "';");
					pst.setInt(1, num);
					pst.executeUpdate();
				}
			}
		}
		catch (SQLException ex)
		{
			System.out.print(ex);
		}
	}
}
