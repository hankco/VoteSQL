package me.javoris767.votesql.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.commands.VoteSQLCommand;
import me.javoris767.votesql.listeners.PlayerListener;
import me.javoris767.votesql.listeners.VotingListener;
import me.javoris767.votesql.listeners.ZAMListener;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class VoteSQLAPI
{
	private static VoteSQL plugin;
	public static Economy econ = null;

	private static VoteSQLConfigs cm;
	private VoteSQLUpdate votesqlUpdate;
	private MiniConnectionPoolManager pool;
	public static HashMap<String, Integer> voteMap = new HashMap<String, Integer>();

	public VoteSQLAPI(VoteSQL voteSQL) throws ClassNotFoundException, SQLException	
	{
		plugin = voteSQL;
		VoteSQL.v = plugin.getDescription().getVersion();
		this.votesqlUpdate = new VoteSQLUpdate(plugin, "http://dev.bukkit.org/server-mods/votesql/files.rss");

		registerUtils();
		updateCheck();
		findPlugins();
		registerListeners();
		registerCommands();
		loadConfig();
		connect();
		setUpSQL();
		//poolConnection();

		//attemptMetrics();
		if (plugin.getConfig().getBoolean("VoteSQL.FlatFile.Enabled") == true)
		{
			voteMap = new HashMap<String, Integer>();
			loadDataFile();
		}
	}

	private void loadConfig() {
		String path1 = "VoteSQL.FlatFile.Enabled";
		String path2 = "VoteSQL.MySQL.Enabled";
		String path3 = "VoteSQL.MySQL.Server";
		String path4 = "VoteSQL.MySQL.Database";
		String path5 = "VoteSQL.MySQL.User";
		String path6 = "VoteSQL.MySQL.Password";
		String path7 = "VoteSQL.MySQL.Table_Prefix";

		String path8 = "VoteSQL.onVote.messageEnabled";
		String path9 = "VoteSQL.onVote.Message";
		String path10 = "VoteSQL.onVote.commandsEnabled";

		String path12 = "VoteSQL.currency.Enabled";
		String path13 = "VoteSQL.currency.Amount";
		String path14 = "VoteSQL.currency.Message";

		String path15 = "VoteSQL.ZavAutoMessager.Enable";

		plugin.getConfig().addDefault(path1, false);
		plugin.getConfig().addDefault(path2, false);
		plugin.getConfig().addDefault(path3, "Server Address eg.Localhost");
		plugin.getConfig().addDefault(path4, "Place Database name here");
		plugin.getConfig().addDefault(path5, "Place User of MySQL Database here");
		plugin.getConfig().addDefault(path6, "Place User password here");
		plugin.getConfig().addDefault(path7, "votesql");


		plugin.getConfig().addDefault(path8, true);
		plugin.getConfig().addDefault(path9, "&2Thank you for voting %P from %S!");
		plugin.getConfig().addDefault(path10, false);

		plugin.getConfig().addDefault(path12, false);
		plugin.getConfig().addDefault(path13, 150);
		plugin.getConfig().addDefault(path14, "&2%P, You received %M dollars!");

		plugin.getConfig().addDefault(path15, false);

		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	public synchronized void connect() throws ClassNotFoundException, SQLException {
		if (plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true) {
			Class.forName("com.mysql.jdbc.Driver");
			VoteSQLChat.logInfo("MySQL driver loaded");
			MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
			dataSource.setDatabaseName(plugin.getConfig().getString("VoteSQL.MySQL.Database"));
			dataSource.setServerName(plugin.getConfig().getString("VoteSQL.MySQL.Server"));
			dataSource.setPort(3306);
			dataSource.setUser(plugin.getConfig().getString("VoteSQL.MySQL.User"));
			dataSource.setPassword(plugin.getConfig().getString("VoteSQL.MySQL.Password"));
			pool = new MiniConnectionPoolManager(dataSource, 1);
			VoteSQLChat.logInfo("Connection pool ready");
		}
	}

	private void setUpSQL()
	{
		if (plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true) {
			VoteSQLChat.logInfo("Connecting to SQL database!");
			{
				Connection connection = null;
				Statement st = null;
				int rs = 0;
				try
				{
					connection = pool.getValidConnection();
                    /*connection = 
							DriverManager.getConnection("jdbc:MySQL://" + plugin.getConfig().getString("VoteSQL.MySQL.Server") + "/" + plugin.getConfig().getString("VoteSQL.MySQL.Database"), plugin.getConfig().getString("VoteSQL.MySQL.User"), plugin.getConfig().getString("VoteSQL.MySQL.Password"));*/					st = connection.createStatement(); 
					rs = st.executeUpdate("CREATE TABLE IF NOT EXISTS `"
							+ plugin.getConfig().getString("VoteSQL.MySQL.Table_Prefix")
							+ "`( `id` MEDIUMINT NOT NULL AUTO_INCREMENT, `playername` text, `votes` MEDIUMINT(255), PRIMARY KEY (`id`))");
					VoteSQLChat.logInfo("SQL database connected!");
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					VoteSQLChat.logSevere(" Error:" + rs);
				}
			}
			return;
		}
	}

	public static void saveDataFile() throws IOException, InvalidConfigurationException
	{
		final CommentedYamlConfiguration config;
		File dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
		if (!dataFile.exists()) {
			VoteSQLChat.logInfo("Created playerdata.yml");
			dataFile.createNewFile();
		}

		// Load the configuration file into memory
		config = new CommentedYamlConfiguration();
		config.load(dataFile);

		config.options().header("VoteSQL PlayerData FlatFile");

		// Saves the configuration from memory to file
		config.save(dataFile);

		for (String name : voteMap.keySet())
		{
			if (name != null || voteMap != null)
			{
				config.set("Voter." + name.toLowerCase() + ".amountOfVotes",
						voteMap.get(name.toLowerCase()).intValue());
				return;
			}
		}
	}

	public static void loadDataFile()
	{
		YamlConfiguration config = VoteSQLAPI.getConfigs().getConfig(
				VoteSQLConfFile.PLAYERDATA);

		ConfigurationSection voteSection = config
				.getConfigurationSection("Voter");
		if (voteSection == null)
		{
			config.createSection("Voter");
			return;
		}
		if (voteMap == null)
		{
			VoteSQLChat.logSevere(" HashMap 'voteMap' did not save correctly!");
			return;
		}
		for (String key : voteSection.getKeys(false))
		{
			voteMap.put(
					key.toLowerCase(),
					config.getInt("Voter." + key.toLowerCase()
							+ ".amountOfVotes"));
		}
		return;
	}
	private boolean findPlugins()
	{
		// Votifier
		if (Bukkit.getPluginManager().getPlugin("Votifier") != null)
		{
			VoteSQLChat.logInfo(" Votifier has been found!");
		}
		else
		{
			VoteSQLChat.logInfo(" Votifier can not be found!");
			VoteSQLChat.logInfo(" Disabling the plugin!");
			Bukkit.getPluginManager().disablePlugin(plugin);
		}

		// ZavAutoMessager
		if (Bukkit.getPluginManager().getPlugin("ZavAutoMessager") != null)
		{
			VoteSQLChat.logInfo(" ZavAutoMessager has been found!");
		}
		else
		{
			VoteSQLChat.logInfo(" ZavAutoMessager can not be found!");
		}

		// Vault
		if (Bukkit.getPluginManager().getPlugin("Vault") != null)
		{
			VoteSQLChat.logInfo(" Vault has been found!");
			RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp == null) {
				return false;
			}
			econ = ((Economy)rsp.getProvider());
			return econ != null;
		}
		else
		{
			VoteSQLChat.logInfo(" Vault can not be found!");
		}
		return false;
	}
	private void registerCommands()
	{

		VoteSQLCommand vC = new VoteSQLCommand(plugin);
		plugin.getCommand("votesql").setExecutor(vC);
	}

	private void registerUtils()
	{
		new Permissions(plugin);
		new Functions(plugin);
		new VoteSQLChat(plugin);
		cm = new VoteSQLConfigs(plugin);
	}

	private void registerListeners()
	{
		new VotingListener(plugin);
		new PlayerListener(plugin);
		new ZAMListener(plugin);
	}

	public static VoteSQLConfigs getConfigs()
	{

		return cm;

	}
	public void updateCheck() {
		if (this.votesqlUpdate.updateNeeded()) {
			VoteSQLChat.logInfo(" Version: " + this.votesqlUpdate.getVersion() + " is now available!");
		}else{
			VoteSQLChat.logInfo(" is up to date!");
		}
	}
	/*	public void poolConnection() {
		if (plugin.getConfig().getBoolean("VoteSQL.MySQL.Enabled") == true) {
			String url = 
					"jdbc:mysql://" + plugin.getConfig().getString("VoteSQL.MySQL.Server") + ":3306/" + plugin.getConfig().getString("VoteSQL.MySQL.DataBase") + "?useUnicode=true&characterEncoding=utf-8"; 
			String user =
					plugin.getConfig().getString("VoteSQL.MySQL.User");
			String password =
					plugin.getConfig().getString("VoteSQL.MySQL.Password");
			try {
				VoteSQLChat.logInfo("Connecting to " + user + "@" + url+ "...");
				pool2 = new ConnectionPool(url, user, password);
				Connection conn = getConnection();
				if(conn == null) {
					return;
				}
				conn.close();
			} catch (final NullPointerException ex) {
				VoteSQLChat.logSevere("Error while loading: " + ex);
			} catch (final Exception ex) {
				VoteSQLChat.logSevere("Error while loading: " + ex.getMessage());
				return;
			}
		}
	}*/

	/*	private Connection getConnection() {
		Connection conn = null;
		return conn;
	}*/
}