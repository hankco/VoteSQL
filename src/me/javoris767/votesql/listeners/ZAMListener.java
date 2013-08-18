package me.javoris767.votesql.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.javoris767.votesql.VoteSQL;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.zavteam.plugins.API.MessageBroadcastEvent;

public class ZAMListener implements Listener {
	private VoteSQL plugin;

	public ZAMListener(VoteSQL voteSQL) {
		plugin = voteSQL;
	}

	@EventHandler
	public void onBroadcast(MessageBroadcastEvent event) {
		if(event.getMessage().toString().contains("<topvote>"))
		{
			if(plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true &&
					plugin.getConfig().getBoolean("VoteSQL.ZavAutoMessager.Enable") == true) 
			{

				String database = plugin.getConfig().getString("VoteSQL.MySQL.Table_Prefix");
				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;
				try 
				{
					con = DriverManager.getConnection(
							"jdbc:MySQL://"
									+ plugin.getConfig().getString("VoteSQL.MySQL.Server")
									+ "/"
									+ plugin.getConfig().getString("VoteSQL.MySQL.Database"),
									plugin.getConfig().getString("VoteSQL.MySQL.User"),
									plugin.getConfig().getString("VoteSQL.MySQL.Password"));
					stmt = con.createStatement();

					rs = stmt.executeQuery("SELECT * FROM " + database + " ORDER BY votes DESC LIMIT 1;");

					while(rs.next())
					{
						String topvoter = rs.getString("playername");
						String topvoter1 = topvoter.toString();

						event.getMessage().toString().replace("<topvote>", topvoter1);
					}
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}