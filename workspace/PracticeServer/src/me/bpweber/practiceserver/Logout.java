package me.bpweber.practiceserver;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Logout implements Listener, CommandExecutor {

	static HashMap<String, Integer> logging = new HashMap<String, Integer>();

	static HashMap<String, Location> loggingloc = new HashMap<String, Location>();

	static HashMap<String, Long> syncing = new HashMap<String, Long>();

	public void onEnable() {
		Main.log.info("[Logout] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (logging.containsKey(p.getName())) {
						if (logging.get(p.getName()) == 0) {
							logging.remove(p.getName());
							loggingloc.remove(p.getName());
							if (Alignments.tagged.containsKey(p.getName()))
								Alignments.tagged.remove(p.getName());
							p.saveData();
							p.kickPlayer(ChatColor.GREEN.toString() + "You have safely logged out." + "\n\n"
									+ ChatColor.GRAY.toString() + "Your player data has been synced.");
						} else {
							p.sendMessage(ChatColor.RED + "Logging out in ... " + ChatColor.BOLD
									+ logging.get(p.getName()) + "s");
							logging.put(p.getName(), logging.get(p.getName()) - 1);
						}
					}
				}
			}
		}.runTaskTimer(Main.plugin, 20, 20);
	}

	public void onDisable() {
		Main.log.info("[Logout] has been disabled.");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("logout")) {
				if (!logging.containsKey(p.getName()))
					if (Alignments.isSafeZone(p.getLocation())) {
						if (Alignments.tagged.containsKey(p.getName()))
							Alignments.tagged.remove(p.getName());
						p.saveData();
						p.kickPlayer(ChatColor.GREEN.toString() + "You have safely logged out." + "\n\n"
								+ ChatColor.GRAY.toString() + "Your player data has been synced.");
					} else {
						if ((Alignments.tagged.containsKey(p.getName())
								&& System.currentTimeMillis() - Alignments.tagged.get(p.getName()) < 10 * 1000)) {
							p.sendMessage(ChatColor.RED + "You will be " + ChatColor.BOLD + "LOGGED OUT" + ChatColor.RED
									+ " of the game world shortly.");
							logging.put(p.getName(), 10);
							loggingloc.put(p.getName(), p.getLocation());
						} else {
							p.sendMessage(ChatColor.RED + "You will be " + ChatColor.BOLD + "LOGGED OUT" + ChatColor.RED
									+ " of the game world shortly.");
							logging.put(p.getName(), 3);
							loggingloc.put(p.getName(), p.getLocation());
						}
					}
			}
			if (cmd.getName().equalsIgnoreCase("sync")) {
				if (syncing.containsKey(p.getName())
						&& System.currentTimeMillis() - syncing.get(p.getName()) < 10 * 1000) {
					p.sendMessage(
							ChatColor.RED + "You already have a recent sync request -- please wait a few seconds.");
				} else {
					p.updateInventory();
					p.teleport(p);
					p.saveData();
					p.sendMessage(ChatColor.GREEN + "Synced player data to " + ChatColor.UNDERLINE + "HIVE"
							+ ChatColor.GREEN + " server.");
					syncing.put(p.getName(), System.currentTimeMillis());
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onCancelDamager(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (logging.containsKey(p.getName())) {
				logging.remove(p.getName());
				loggingloc.remove(p.getName());
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Logout - CANCELLED");
			}
		}
	}

	@EventHandler
	public void onCancelDamage(EntityDamageEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (logging.containsKey(p.getName())) {
				logging.remove(p.getName());
				loggingloc.remove(p.getName());
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Logout - CANCELLED");
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		p.saveData();
		if (logging.containsKey(p.getName())) {
			logging.remove(p.getName());
			loggingloc.remove(p.getName());
		}
	}

	@EventHandler
	public void onCancelMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (logging.containsKey(p.getName())) {
			Location loc = loggingloc.get(p.getName());
			if (loc.distanceSquared(e.getTo()) >= 2) {
				logging.remove(p.getName());
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Logout - CANCELLED");
			}
		}
	}
}
