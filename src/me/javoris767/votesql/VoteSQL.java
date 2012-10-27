package me.javoris767.votesql;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteSQL extends JavaPlugin implements Listener {
	Logger log = Bukkit.getLogger();
	public VoteSQL plugin;

	public void onDisable() {
		log.info(this + " is now disabled");
		}

	public void onEnable() {
		log.info(this + " is now enabled");
		Bukkit.getPluginManager().registerEvents(this, this);
		LoadConfiguration();
		getCommand("votesql").setExecutor(new VoteSQLCommand(this));
		findVotifier();
		try {
			MetricsLite metrics;
			metrics = new MetricsLite(this);
			metrics.start();
			} catch (IOException e1) {
				e1.printStackTrace();
				}
		if (getConfig().getBoolean("VoteSQL.MySQL.Enabled")) {
			Connection connection = null;
			Statement st = null;
			int rs = 0;
			try {
				connection = DriverManager.getConnection("jdbc:MySQL://" + getConfig().getString("VoteSQL.MySQL.Server") + "/" + getConfig().getString("VoteSQL.MySQL.Database"), getConfig().getString("VoteSQL.MySQL.User"), getConfig().getString("VoteSQL.MySQL.Password"));
				st = connection.createStatement();
				rs = st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + getConfig().getString("VoteSQL.MySQL.Table_Prefix") + "`( `id` MEDIUMINT NOT NULL AUTO_INCREMENT, `playername` text, `votes` MEDIUMINT(255), PRIMARY KEY (`id`))");
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.print(rs);
				}
		}
	}
	
	private void findVotifier() {
		if (Bukkit.getPluginManager().getPlugin("Votifier") != null) {
			log.info("[" + this + "]" + " Votifier found!");
			} else {
				log.info("[" + this + "]" + " Votifier not found!");
				log.info("[" + this + "]" + " Disabling plugin...");
				Bukkit.getPluginManager().disablePlugin(plugin);
			}
	}
	
	public void LoadConfiguration() { 
		String path0 = "VoteSQL.MySQL.Enabled";
		String path1 = "VoteSQL.MySQL.Server";
		String path2 = "VoteSQL.MySQL.Database";
		String path3 = "VoteSQL.MySQL.User";
		String path4 = "VoteSQL.MySQL.Password";
		String path5 = "VoteSQL.MySQL.Table_Prefix";

		getConfig().addDefault(path0, false);
		getConfig().addDefault(path1, "Server Address eg.Localhost");
        getConfig().addDefault(path2, "Place Database name here");
        getConfig().addDefault(path3, "Place User of MySQL Database here");
        getConfig().addDefault(path4, "Place User password here");
        getConfig().addDefault(path5, "votesql");

        getConfig().options().copyDefaults(true);
        getConfig().options().header("Thanks for choosing VoteSQL! Simply change the info below.");
        saveConfig();
        }

	@EventHandler
	public void onVote(VotifierEvent event) {
		Vote vote = event.getVote();
        String username = vote.getUsername();
        addData(username);
        }

	public void addData(String playername) {
		PreparedStatement pst = null;
        Connection con = null;
        Statement stmt = null;
        int num = 1;
        ResultSet rs = null;
        try
        {
        	con = DriverManager.getConnection("jdbc:MySQL://" + getConfig().getString("VoteSQL.MySQL.Server") + "/" + getConfig().getString("VoteSQL.MySQL.Database"), getConfig().getString("VoteSQL.MySQL.User"), getConfig().getString("VoteSQL.MySQL.Password"));
        	
        	String database = getConfig().getString("VoteSQL.MySQL.Table_Prefix");
        	stmt = con.createStatement();
        	if (stmt.execute("SELECT * FROM " + database + " WHERE playername='" + playername + "';")) {
        		rs = stmt.getResultSet();
        		if (!rs.next()) {
        			pst = con.prepareStatement("INSERT INTO " + database + "(playername, votes) VALUES(?, ?)");
        			pst.setString(1, playername);
        			pst.setInt(2, 1);
        			pst.executeUpdate();
        			System.out.print("inserted");
        			} else {
        				num = rs.getInt("votes");
        				num++;
        				pst = con.prepareStatement("UPDATE " + database + " SET votes=? WHERE playername='" + playername + "';");
        				pst.setInt(1, num);
        				pst.executeUpdate();
        				}
        		}
        	} catch (SQLException ex) {
        		System.out.print(ex);
        	}
	}
}