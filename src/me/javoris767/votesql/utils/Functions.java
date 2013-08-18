package me.javoris767.votesql.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



import me.javoris767.votesql.VoteSQL;

import org.bukkit.entity.Player;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.vexsoftware.votifier.model.Vote;

public class Functions implements DataSource
{
	//private static ConnectionPool pool;
	private Connection connection = null;
	private static VoteSQL plugin;
	private static MiniConnectionPoolManager pool;
	public Functions(VoteSQL voteSQL)
	{
		plugin = voteSQL;
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
	public static void addData(String playername) throws ClassNotFoundException
	{
		PreparedStatement pst = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int num = 1;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			VoteSQLChat.logInfo("MySQL driver loaded");
			MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
			dataSource.setDatabaseName(plugin.getConfig().getString("VoteSQL.MySQL.Database"));
			dataSource.setServerName(plugin.getConfig().getString("VoteSQL.MySQL.Server"));
			dataSource.setPort(3306);
			dataSource.setUser(plugin.getConfig().getString("VoteSQL.MySQL.User"));
			dataSource.setPassword(plugin.getConfig().getString("VoteSQL.MySQL.Password"));
			
			pool = new MiniConnectionPoolManager(dataSource, 10);
			
			con = pool.getValidConnection();
			/*con = DriverManager.getConnection(
					"jdbc:MySQL://"
							+ plugin.getConfig().getString("VoteSQL.MySQL.Server")
							+ "/"
							+ plugin.getConfig().getString("VoteSQL.MySQL.Database"),
							plugin.getConfig().getString("VoteSQL.MySQL.User"),
							plugin.getConfig().getString("VoteSQL.MySQL.Password"));*/

			String database = plugin.getConfig().getString("VoteSQL.MySQL.Table_Prefix");
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
		} finally {
			close(rs);
			close(stmt);
			close(con);
		}
	}
    public synchronized void close() {
        try {
            pool.dispose();
        } catch (SQLException ex) {
        	VoteSQLChat.logSevere(ex.getMessage());
        }
    }

    public void reload() {
    }

    private static void close(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
            	VoteSQLChat.logSevere(ex.getMessage());
            }
        }
    }

    private static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
            	VoteSQLChat.logSevere(ex.getMessage());
            }
        }
    }

    private static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                VoteSQLChat.logSevere(ex.getMessage());
            }
        }
    }
}