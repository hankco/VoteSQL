package me.javoris767.votesql.listeners;

import me.javoris767.votesql.VoteSQL;
import me.javoris767.votesql.utils.VoteSQLChat;
import me.javoris767.votesql.utils.VoteSQLUpdate;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
	
	private VoteSQLUpdate votesqlUpdate;
	
	private VoteSQL plugin;
	public PlayerListener(VoteSQL voteSQL) {
		plugin = voteSQL;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(player.isOp() && votesqlUpdate.updateNeeded()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                	VoteSQLChat.broadcast("New Update Found: v" + votesqlUpdate.getVersion() + ". You have " + VoteSQL.v + "!");
                }
            }, 40L);
        }
	}
}
