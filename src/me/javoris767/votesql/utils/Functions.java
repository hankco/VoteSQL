package me.javoris767.votesql.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import me.javoris767.votesql.VoteSQL;

import org.bukkit.entity.Player;

import com.vexsoftware.votifier.model.Vote;

public class Functions
{
	private Connection connection = null;
	public static List<String> items;

	private static VoteSQL _plugin;

	public Functions(VoteSQL plugin)
	{
		_plugin = plugin;
	}
	public Connection getConnection() {
		return connection;
	}	
	public static String formatMessage(String message, Vote vote) {
		if ((message == null) || (vote == null))
			return "";
		if (message.indexOf("/") == 0) {
			message = message.substring(1);
		}
		return message;
	}

	public static String colorize(String string)
	{
		return string.replaceAll("(?i)&([a-k0-9])", "\u00A7$1");
	}
	public static void addMoney(Player player, int money) 
	{
		VoteSQLAPI.econ.depositPlayer(player.getName(), money);
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
							+ _plugin.getConfig().getString("VoteSQL.MySQL.Server")
							+ "/"
							+ _plugin.getConfig().getString("VoteSQL.MySQL.Database"),
							_plugin.getConfig().getString("VoteSQL.MySQL.User"),
							_plugin.getConfig().getString("VoteSQL.MySQL.Password"));

			String database = _plugin.getConfig().getString("VoteSQL.MySQL.Table_Prefix");
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
