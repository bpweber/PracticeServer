package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Parties implements CommandExecutor, Listener {

	public static ConcurrentHashMap<Player, ArrayList<Player>> parties = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Player, Player> invite = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Player, Long> invitetime = new ConcurrentHashMap<>();

	public void onEnable() {
		Main.log.info("[Parties] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					refreshScoreboard(p);
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 1, 1);
		new BukkitRunnable() {
			public void run() {
				for (Player p : invite.keySet()) {
					if (invitetime.containsKey(p) && System.currentTimeMillis() - invitetime.get(p) > 30000) {
						if (p.isOnline())
							p.sendMessage(ChatColor.RED + "Party invite from " + ChatColor.BOLD
									+ invite.get(p).getName() + ChatColor.RED + " expired.");
						if (p.isOnline())
							invite.get(p).sendMessage(ChatColor.RED + "Party invite to " + ChatColor.BOLD + p.getName()
									+ ChatColor.RED + " has expired.");
						invite.remove(p);
						invitetime.remove(p);
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 20, 20);
	}

	public void onDisable() {
		Main.log.info("[Parties] has been disabled.");
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("pinvite")) {
				if (args.length == 1) {
					String player = args[0];
					if (Bukkit.getPlayer(player) != null) {
						inviteToParty(Bukkit.getPlayer(player), p);
					} else {
						p.sendMessage(
								ChatColor.RED.toString() + ChatColor.BOLD + player + ChatColor.RED + " is OFFLINE");
					}
				} else {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED
							+ "/pinvite <player>");
					p.sendMessage(ChatColor.GRAY + "You can also " + ChatColor.UNDERLINE + "LEFT CLICK" + ChatColor.GRAY
							+ " players with your " + ChatColor.ITALIC + "Character Journal" + ChatColor.GRAY
							+ " to invite them.");
				}
			}
			if (cmd.getName().equalsIgnoreCase("paccept")) {
				if (args.length == 0) {
					if (!invite.containsKey(p)) {
						p.sendMessage(ChatColor.RED + "No pending party invites.");
						return true;
					}
					Player leader = getParty(invite.get(p));
					if (invite.get(p) == null || leader == null) {
						p.sendMessage(ChatColor.RED + "This party invite is no longer available.");
						invite.remove(p);
						invitetime.remove(p);
						return true;
					}
					if (parties.get(leader).size() == 8) {
						p.sendMessage(ChatColor.RED + "This party is currently full (8/8).");
						invite.remove(p);
						invitetime.remove(p);
						return true;
					}
					ArrayList<Player> mem = parties.get(leader);
					for (Player pl : mem)
						pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P"
								+ ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName()
								+ ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE
								+ "joined" + ChatColor.GRAY + " your party.");
					addPlayer(p, leader);
					p.sendMessage("");
					p.sendMessage(ChatColor.LIGHT_PURPLE + "You have joined " + ChatColor.BOLD + leader.getName() + "'s"
							+ ChatColor.LIGHT_PURPLE + " party.");
					p.sendMessage(ChatColor.GRAY + "To chat with your party, use " + ChatColor.BOLD + "/p"
							+ ChatColor.GRAY + " OR " + ChatColor.BOLD + " /p <message>");
					invite.remove(p);
					invitetime.remove(p);
					return true;
				} else {
					p.sendMessage(
							ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/paccept");
				}
			}
			if (cmd.getName().equalsIgnoreCase("pquit")) {
				if (args.length == 0) {
					if (!isInParty(p)) {
						p.sendMessage(ChatColor.RED + "You are not in a party.");
						return true;
					}
					p.sendMessage(ChatColor.RED.toString() + "You have left the party.");
					removePlayer(p);
				} else {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pquit");
				}
			}
			if (cmd.getName().equalsIgnoreCase("pkick")) {
				if (args.length == 1) {
					String player = args[0];
					if (!isPartyLeader(p)) {
						p.sendMessage(ChatColor.RED.toString() + "You are NOT the leader of your party.");
						p.sendMessage(ChatColor.GRAY.toString() + "Type " + ChatColor.BOLD.toString() + "/pquit"
								+ ChatColor.GRAY + " to quit your current party.");
						return true;
					}
					if (Bukkit.getPlayer(player) != null) {
						if (getParty(Bukkit.getPlayer(player)) != p) {
							p.sendMessage(
									ChatColor.RED.toString() + ChatColor.BOLD + player + " is not in your party.");
							return true;
						}
						Bukkit.getPlayer(player).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD.toString()
								+ "You have been kicked out of the party.");
						removePlayer(Bukkit.getPlayer(player));
					} else {
						p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player + " is not in your party.");
						return true;
					}
				} else {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED
							+ "/pkick <player>");
				}
			}
			if (cmd.getName().equalsIgnoreCase("p")) {
				if (!isInParty(p)) {
					p.sendMessage(ChatColor.RED + "You are not in a party.");
					return true;
				}
				if (args.length == 0) {
					p.sendMessage(
							ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/p <MSG>");
					return true;
				}
				String msg = "";
				for (String s : args)
					msg += s + " ";
				ArrayList<Player> mem = parties.get(getParty(p));
				for (Player pl : mem) {
					pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P"
							+ ChatColor.LIGHT_PURPLE + ">" + " " + p.getDisplayName() + ": " + ChatColor.GRAY + msg);
				}
			}
			if (cmd.getName().equalsIgnoreCase("pdecline")) {
				if (args.length != 0) {
					p.sendMessage(
							ChatColor.RED + "" + ChatColor.BOLD + "Invalid Syntax. " + ChatColor.RED + "/pdecline");
					return true;
				}
				if (!invite.containsKey(p)) {
					p.sendMessage(ChatColor.RED + "No pending party invites.");
					return true;
				}
				p.sendMessage(ChatColor.RED + "Declined " + ChatColor.BOLD + invite.get(p).getName() + "'s"
						+ ChatColor.RED + " party invitation.");
				if (invite.get(p).isOnline()) {
					invite.get(p)
							.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + p.getName()
									+ ChatColor.RED.toString() + " has " + ChatColor.UNDERLINE + "DECLINED"
									+ ChatColor.RED + " your party invitation.");
				}
				invite.remove(p);
				invitetime.remove(p);
			}
		}
		return true;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (isInParty(p)) {
			removePlayer(p);
		}
	}

	public static boolean isPartyLeader(Player p) {
		return parties.containsKey(p);
	}

	public static void refreshScoreboard(Player p) {
		if (isInParty(p)) {
			ArrayList<Player> mem = parties.get(getParty(p));
			Scoreboard sb = Scoreboards.getBoard(p);
			if (sb.getObjective(DisplaySlot.SIDEBAR) != null) {
				Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
				for (Player pl : mem) {
					if (parties.containsKey(pl)) {
						String name = ChatColor.BOLD + pl.getName();
						if (name.length() > 16)
							name = name.substring(0, 16);
						o.getScore(name).setScore((int) pl.getHealth());
					} else {
						String name = pl.getName();
						if (name.length() > 16)
							name = name.substring(0, 16);
						o.getScore(name).setScore((int) pl.getHealth());
					}
				}
				p.setScoreboard(sb);
				Scoreboards.boards.put(p, sb);
			} else {
				updateScoreboard(p);
			}
		}
	}

	public static void updateScoreboard(Player p) {
		if (isInParty(p)) {
			ArrayList<Player> mem = parties.get(getParty(p));
			Scoreboard sb = Scoreboards.getBoard(p);
			if (sb.getObjective(DisplaySlot.SIDEBAR) != null)
				sb.getObjective(DisplaySlot.SIDEBAR).unregister();
			Objective o = sb.registerNewObjective("party_data", "dummy");
			o.setDisplayName("" + ChatColor.RED + ChatColor.BOLD + "Party");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			for (Player pl : mem) {
				if (parties.containsKey(pl)) {
					String name = ChatColor.BOLD + pl.getName();
					if (name.length() > 16)
						name = name.substring(0, 16);
					o.getScore(name).setScore((int) pl.getHealth());
				} else {
					String name = pl.getName();
					if (name.length() > 16)
						name = name.substring(0, 16);
					o.getScore(name).setScore((int) pl.getHealth());
				}
			}
			p.setScoreboard(sb);
			Scoreboards.boards.put(p, sb);
		} else {
			Scoreboard sb = Scoreboards.getBoard(p);
			if (sb.getObjective(DisplaySlot.SIDEBAR) != null)
				sb.getObjective(DisplaySlot.SIDEBAR).unregister();
		}
	}

	public static void createParty(Player p) {
		parties.put(p, new ArrayList<Player>(Arrays.asList(p)));
		updateScoreboard(p);
	}

	public static void addPlayer(Player added, Player leader) {
		if (parties.containsKey(leader)) {
			ArrayList<Player> mem = parties.get(leader);
			if (!mem.contains(added))
				mem.add(added);
			parties.put(leader, mem);
			for (Player p : mem) {
				updateScoreboard(p);
			}
		}
	}

	public static void removePlayer(Player p) {
		if (isInParty(p)) {
			if (isPartyLeader(p)) {
				if (parties.get(p).size() > 1) {
					ArrayList<Player> mem = parties.get(p);
					mem.remove(p);
					Player newleader = mem.get(0);
					parties.put(newleader, mem);
					parties.remove(p);
					newleader.sendMessage(ChatColor.RED + "You have been made the party leader!");
					for (Player pl : mem) {
						pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P"
								+ ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName()
								+ ChatColor.GRAY.toString() + " has " + ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE
								+ "left" + ChatColor.GRAY.toString() + " your party.");
						pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P"
								+ ChatColor.LIGHT_PURPLE + "> " + ChatColor.GRAY + ChatColor.LIGHT_PURPLE.toString()
								+ newleader.getName() + ChatColor.GRAY.toString() + " has been promoted to "
								+ ChatColor.UNDERLINE + "Party Leader");
						updateScoreboard(pl);
					}
				} else {
					parties.remove(p);
				}
			} else {
				for (Player key : parties.keySet()) {
					if (parties.get(key).contains(p)) {
						ArrayList<Player> mem = parties.get(key);
						mem.remove(p);
						parties.put(key, mem);
						for (Player pl : mem) {
							pl.sendMessage(ChatColor.LIGHT_PURPLE.toString() + "<" + ChatColor.BOLD + "P"
									+ ChatColor.LIGHT_PURPLE + ">" + ChatColor.GRAY + " " + p.getName()
									+ ChatColor.GRAY.toString() + " has " + ChatColor.RED + ChatColor.UNDERLINE + "left"
									+ ChatColor.GRAY.toString() + " your party.");
							updateScoreboard(pl);
						}
					}
				}
			}
			updateScoreboard(p);
		}
	}

	public static boolean isInParty(Player p) {
		for (Player key : parties.keySet()) {
			if (parties.get(key).contains(p))
				return true;
		}
		return false;
	}

	public static boolean arePartyMembers(Player p1, Player p2) {
		for (Player key : parties.keySet()) {
			if (parties.get(key).contains(p1) && parties.get(key).contains(p2))
				return true;
		}
		return false;
	}

	public static Player getParty(Player p) {
		if (parties.containsKey(p))
			return p;
		if (isInParty(p)) {
			for (Player key : parties.keySet()) {
				if (parties.get(key).contains(p))
					return key;
			}
		}
		return null;
	}

	public static void inviteToParty(Player p, Player owner) {
		if (p == owner) {
			p.sendMessage(ChatColor.RED + "You cannot invite yourself to your own party.");
			return;
		}
		if (!isPartyLeader(owner) && isInParty(owner)) {
			owner.sendMessage(ChatColor.RED.toString() + "You are NOT the leader of your party.");
			owner.sendMessage(ChatColor.GRAY.toString() + "Type " + ChatColor.BOLD.toString() + "/pquit"
					+ ChatColor.GRAY + " to quit your current party.");
			return;
		}
		if (isInParty(owner) && isPartyLeader(owner) && parties.get(owner).size() == 8) {
			owner.sendMessage(ChatColor.RED + "You cannot have more than " + ChatColor.ITALIC + "8 players"
					+ ChatColor.RED + " in a party.");
			owner.sendMessage(ChatColor.GRAY + "You may use /pkick to kick out unwanted members.");
			return;
		}
		if (isInParty(p)) {
			if (getParty(p) == owner) {
				owner.sendMessage("" + ChatColor.RED + ChatColor.BOLD + p.getName() + ChatColor.RED
						+ " is already in your party.");
				owner.sendMessage(ChatColor.GRAY + "Type /pkick " + p.getName() + " to kick them out.");
			} else {
				owner.sendMessage("" + ChatColor.RED + ChatColor.BOLD + p.getName() + ChatColor.RED
						+ " is already in another party.");
			}
			return;
		}
		if (invite.containsKey(p)) {
			owner.sendMessage(ChatColor.RED + p.getName() + " has a pending party invite.");
			return;
		}
		if (!isInParty(owner)) {
			owner.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Party created.");
			owner.sendMessage(ChatColor.GRAY + "To invite more people to join your party, " + ChatColor.UNDERLINE
					+ "Left Click" + ChatColor.GRAY.toString() + " them with your character journal or use "
					+ ChatColor.BOLD + "/pinvite" + ChatColor.GRAY + ". To kick, use " + ChatColor.BOLD + "/pkick"
					+ ChatColor.GRAY + ".");
			createParty(owner);
		}
		p.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.UNDERLINE + owner.getName() + ChatColor.GRAY
				+ " has invited you to join their party. To accept, type " + ChatColor.LIGHT_PURPLE.toString()
				+ "/paccept" + ChatColor.GRAY + " or to decline, type " + ChatColor.LIGHT_PURPLE.toString()
				+ "/pdecline");
		owner.sendMessage(ChatColor.GRAY + "You have invited " + ChatColor.LIGHT_PURPLE.toString() + p.getName()
				+ ChatColor.GRAY + " to join your party.");
		invite.put(p, owner);
		invitetime.put(p, System.currentTimeMillis());
	}
}
