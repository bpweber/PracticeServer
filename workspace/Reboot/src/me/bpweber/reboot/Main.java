package me.bpweber.reboot;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.bpweber.practiceserver.Alignments;

public class Main extends JavaPlugin {

	HashMap<Boolean, Integer> reboot = new HashMap<Boolean, Integer>();

	Long starttime;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p.isOp()) {
				if (cmd.getName().equalsIgnoreCase("reboot")) {
					reboot.put(true, 60);
				}
			} else {
				long diff = System.currentTimeMillis() - starttime;
				diff = (2 * 60 * 60 * 1000) - diff;
				final String msg = ChatColor.YELLOW.toString() + ChatColor.UNDERLINE + "Next Scheduled Reboot: "
						+ ChatColor.YELLOW + (int) (diff / 3600000 % 24) + ChatColor.BOLD + "h " + ChatColor.YELLOW
						+ (int) (diff / 60000 % 60) + ChatColor.BOLD + "m " + ChatColor.YELLOW
						+ (int) (diff / 1000 % 60) + ChatColor.BOLD + "s";
				sender.sendMessage(msg);
			}
		}
		if (sender instanceof ConsoleCommandSender) {
			if (cmd.getName().equalsIgnoreCase("reboot")) {
				reboot.put(true, 60);
			}
		}
		return false;
	}

	public void onEnable() {
		starttime = System.currentTimeMillis();
		new BukkitRunnable() {
			public void run() {
				reboot.put(true, 5 * 60);
			}
		}.runTaskLater(this, 2 * 20 * 60 * 60);
		new BukkitRunnable() {
			public void run() {
				if (reboot.containsKey(true)) {
					if (reboot.get(true) == 0) {
						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							if (Alignments.tagged.containsKey(p.getName()))
								Alignments.tagged.remove(p.getName());
							p.saveData();
							p.kickPlayer(ChatColor.GREEN.toString() + "You have been safely logged out by the server."
									+ "\n\n" + ChatColor.GRAY.toString() + "Your player data has been synced.");
						}
						Bukkit.shutdown();
					} else {
						if (reboot.get(true) == 60 * 5)
							Bukkit.getServer().broadcastMessage("" + ChatColor.AQUA + ChatColor.BOLD + ">> "
									+ ChatColor.AQUA + "The servers will be rebooting in approx. 5 minutes. *");
						if (reboot.get(true) == 60 || reboot.get(true) == 10 || reboot.get(true) <= 5)
							Bukkit.getServer()
									.broadcastMessage("" + ChatColor.RED + ChatColor.BOLD + ">>" + ChatColor.RED
											+ " The server will be " + ChatColor.RED + ChatColor.UNDERLINE + "REBOOTING"
											+ ChatColor.RED + " in " + ChatColor.RED + ChatColor.BOLD + reboot.get(true)
											+ "s...");
						reboot.put(true, reboot.get(true) - 1);
					}
				}
			}
		}.runTaskTimer(this, 20, 20);
	}
}
