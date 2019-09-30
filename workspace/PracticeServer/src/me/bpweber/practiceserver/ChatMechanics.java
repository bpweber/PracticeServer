package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;

public class ChatMechanics implements Listener, CommandExecutor {

	private static HashMap<Player, Player> reply = new HashMap<Player, Player>();

	public void onEnable() {
		Main.log.info("[ChatMechanics] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[ChatMechanics] has been disabled.");
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("gl")) {
				if (ModerationMechanics.muted.containsKey(p.getName().toLowerCase())) {
					int seconds = ModerationMechanics.muted.get(p.getName().toLowerCase());
					int minutes = 0;
					int hours = 0;
					minutes = seconds / 60;
					hours = minutes / 60;
					if (hours >= 1) {
						p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED"
								+ ChatColor.RED + ". You will be unmuted in " + hours + " hours(s).");
						return false;
					} else if (minutes >= 1) {
						p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED"
								+ ChatColor.RED + ". You will be unmuted in " + minutes + " minute(s).");
						return false;
					} else {
						p.sendMessage(ChatColor.RED + "You are currently " + ChatColor.BOLD + "GLOBALLY MUTED"
								+ ChatColor.RED + ". You will be unmuted in " + seconds + " seconds(s).");
					}
				} else {
					if (args.length == 0) {
						p.sendMessage("" + ChatColor.RED + ChatColor.BOLD
								+ "Incorrect Syntax. You must supply a message!" + ChatColor.RED + " /gl <MESSAGE>");
					} else {
						String message = "";
						for (String s : args)
							message += s + " ";
						if (message.toLowerCase().startsWith("wtb") || message.toLowerCase().startsWith("wts")
								|| message.toLowerCase().startsWith("wtt") || message.toLowerCase().startsWith("buying")
								|| message.toLowerCase().startsWith("selling")
								|| message.toLowerCase().startsWith("trading")) {
							if (message.contains("@i@") && p.getItemInHand() != null
									&& p.getItemInHand().getType() != Material.AIR) {
								for (Player pl : Bukkit.getServer().getOnlinePlayers())
									sendShowString(p, p.getItemInHand(), "" + ChatColor.GREEN + "<" + ChatColor.BOLD
											+ "T" + ChatColor.GREEN + "> " + ChatColor.RESET, message, pl);
							} else {
								for (Player pl : Bukkit.getServer().getOnlinePlayers())
									pl.sendMessage("" + ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN
											+ "> " + ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE
											+ message);
							}
							Main.log.info("" + ChatColor.GREEN + "<" + ChatColor.BOLD + "T" + ChatColor.GREEN + "> "
									+ ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE + message);
						} else {
							if (message.contains("@i@") && p.getItemInHand() != null
									&& p.getItemInHand().getType() != Material.AIR) {
								for (Player pl : Bukkit.getServer().getOnlinePlayers())
									sendShowString(p, p.getItemInHand(), "" + ChatColor.AQUA + "<" + ChatColor.BOLD
											+ "G" + ChatColor.AQUA + "> " + ChatColor.RESET, message, pl);
							} else {
								for (Player pl : Bukkit.getServer().getOnlinePlayers())
									pl.sendMessage("" + ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA
											+ "> " + ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE
											+ message);
							}
							Main.log.info("" + ChatColor.AQUA + "<" + ChatColor.BOLD + "G" + ChatColor.AQUA + "> "
									+ ChatColor.RESET + p.getDisplayName() + ": " + ChatColor.WHITE + message);
						}
					}
				}
			}
			if (cmd.getName().equalsIgnoreCase("br")) {
				if (p.isOp()) {
					if (args.length == 0) {
						p.sendMessage("" + ChatColor.RED + ChatColor.BOLD
								+ "Incorrect Syntax. You must supply a message!" + ChatColor.RED + " /br <MESSAGE>");
					} else {
						String message = "";
						for (String s : args)
							message += s + " ";
						for (Player pl : Bukkit.getServer().getOnlinePlayers())
							pl.sendMessage("" + ChatColor.AQUA + ChatColor.BOLD + ">> " + ChatColor.AQUA + message);
						Main.log.info("" + ChatColor.AQUA + ChatColor.BOLD + ">> " + ChatColor.AQUA + message);
					}
				}
			}
			if (cmd.getName().equalsIgnoreCase("message")) {
				if (args.length == 1) {
					Player sendee = Bukkit.getServer().getPlayer(args[0]);
					if (sendee == null || sendee.isOp() && !p.isOp()) {
						p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[0] + ChatColor.RED + " is OFFLINE.");
					} else {
						sendee.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "FROM " + p.getDisplayName()
								+ ": " + ChatColor.WHITE + "/" + label + " " + args[0]);
						p.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "TO " + sendee.getDisplayName() + ": "
								+ ChatColor.WHITE + "/" + label + " " + args[0]);
						sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
						reply.put(sendee, p);
					}
				} else if (args.length >= 2) {
					Player sendee = Bukkit.getServer().getPlayer(args[0]);
					if (sendee == null
							|| ModerationMechanics.vanished.contains(sendee.getName().toLowerCase()) && !p.isOp()) {
						p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + args[0] + ChatColor.RED + " is OFFLINE.");
					} else {
						String message = "";
						for (int i = 1; i < args.length; i++) {
							message += args[i] + " ";
						}
						if (message.contains("@i@") && p.getItemInHand() != null
								&& p.getItemInHand().getType() != Material.AIR) {
							sendShowString(p, p.getItemInHand(), "" + ChatColor.DARK_GRAY + ChatColor.BOLD + "FROM ",
									message, sendee);
							sendShowString(sendee, p.getItemInHand(), "" + ChatColor.DARK_GRAY + ChatColor.BOLD + "TO ",
									message, p);
						} else {
							sendee.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "FROM " + p.getDisplayName()
									+ ": " + ChatColor.WHITE + message);
							p.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "TO " + sendee.getDisplayName()
									+ ": " + ChatColor.WHITE + message);
						}
						sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
						reply.put(sendee, p);
					}
				} else {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect syntax. " + "/" + label
							+ " <PLAYER> <MESSAGE>");
				}
			}
			if (cmd.getName().equalsIgnoreCase("reply")) {
				if (reply.containsKey(p)) {
					Player sendee = reply.get(p);
					if (sendee == null
							|| ModerationMechanics.vanished.contains(sendee.getName().toLowerCase()) && !p.isOp()) {
						p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + reply.get(p).getName() + ChatColor.RED
								+ " is OFFLINE.");
					} else {
						if (args.length == 0) {
							sendee.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "FROM " + p.getDisplayName()
									+ ": " + ChatColor.WHITE + "/" + label + " " + sendee.getName());
							p.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "TO " + sendee.getDisplayName()
									+ ": " + ChatColor.WHITE + "/" + label + " " + sendee.getName());
							sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
							reply.put(sendee, p);
						} else if (args.length >= 1) {
							String message = "";
							for (String s : args)
								message += s + " ";
							if (message.contains("@i@") && p.getItemInHand() != null
									&& p.getItemInHand().getType() != Material.AIR) {
								sendShowString(p, p.getItemInHand(),
										"" + ChatColor.DARK_GRAY + ChatColor.BOLD + "FROM ", message, sendee);
								sendShowString(sendee, p.getItemInHand(),
										"" + ChatColor.DARK_GRAY + ChatColor.BOLD + "TO ", message, p);
							} else {
								sendee.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "FROM "
										+ p.getDisplayName() + ": " + ChatColor.WHITE + message);
								p.sendMessage("" + ChatColor.DARK_GRAY + ChatColor.BOLD + "TO "
										+ sendee.getDisplayName() + ": " + ChatColor.WHITE + message);

								sendee.playSound(sendee.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
								reply.put(sendee, p);
							}
						}
					}
				} else {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RED
							+ "You have no conversation to respond to!");
				}
			}
			if (cmd.getName().equalsIgnoreCase("roll")) {
				if (args.length < 1 || args.length > 1) {
					p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax." + ChatColor.GRAY
							+ " /roll <1 - 10000>");
				} else if (args.length == 1) {
					int max = 0;
					try {
						max = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						p.sendMessage(
								"" + ChatColor.RED + ChatColor.BOLD + "Non-Numeric Max Number. /roll <1 - 10000>");
						return true;
					}
					if (max < 1 || max > 10000) {
						p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Incorrect Syntax." + ChatColor.GRAY
								+ " /roll <1 - 10000>");
					} else {
						Random random = new Random();
						int roll = random.nextInt(max + 1);
						p.sendMessage(p.getDisplayName() + ChatColor.GRAY + " has rolled a " + ChatColor.GRAY
								+ ChatColor.BOLD + ChatColor.UNDERLINE + roll + ChatColor.GRAY + " out of "
								+ ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + max + ".");
						List<Player> to_send = new ArrayList<Player>();
						for (Player pl : Bukkit.getServer().getOnlinePlayers())
							if (pl != null)
								if (pl != p)
									if (pl.getLocation().distance(p.getLocation()) < 50)
										to_send.add(pl);
						if (to_send.size() > 0)
							for (Player pl : to_send)
								pl.sendMessage(p.getDisplayName() + ChatColor.GRAY + " has rolled a " + ChatColor.GRAY
										+ ChatColor.BOLD + ChatColor.UNDERLINE + roll + ChatColor.GRAY + " out of "
										+ ChatColor.GRAY + ChatColor.BOLD + ChatColor.UNDERLINE + max + ".");
					}
				}
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String s = e.getMessage().toLowerCase();
		if (s.startsWith("/"))
			s = s.replace("/", "");
		if (s.contains(" "))
			s = s.split(" ")[0];
		if (s.equals("i") || s.equals("give")) {
			e.setCancelled(true);
			if (p.isOp())
				p.getInventory().addItem(new ItemStack(Material.MOB_SPAWNER));
			p.sendMessage(
					ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
			return;
		}
		if (s.equals("save-all") || s.equals("stop") || s.equals("restart") || s.equals("reload") || s.equals("ban")
				|| s.equals("tpall") || s.equals("kill") || s.equals("vanish") || s.equals("mute")
				|| s.equals("more")) {
			e.setCancelled(true);
			p.sendMessage(
					ChatColor.WHITE + "Unknown command. View your Character Journal's Index for a list of commands.");
			return;
		}
		if (!p.isOp()) {
			String rank = "";
			if (ModerationMechanics.ranks.containsKey(p.getName()))
				rank = ModerationMechanics.ranks.get(p.getName());
			if (rank.equals("pmod")) {
				if (!(s.equals("roll") || s.equals("gl") || s.equals("toggle") || s.equals("toggles")
						|| s.equals("togglepvp") || s.equals("togglechaos") || s.equals("toggledebug")
						|| s.equals("debug") || s.equals("toggleff") || s.equals("add") || s.equals("del")
						|| s.equals("delete") || s.equals("message") || s.equals("msg") || s.equals("m")
						|| s.equals("whisper") || s.equals("w") || s.equals("tell") || s.equals("t")
						|| s.equals("reply") || s.equals("r") || s.equals("logout") || s.equals("sync")
						|| s.equals("reboot") || s.equals("pinvite") || s.equals("paccept") || s.equals("pquit")
						|| s.equals("pkick") || s.equals("pdecline") || s.equals("p") || s.equals("psban")
						|| s.equals("psunban") || s.equals("psmute") || s.equals("psunmute"))) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.WHITE
							+ "Unknown command. View your Character Journal's Index for a list of commands.");
				}
			} else if (ModerationMechanics.isSub(p)) {
				if (!(s.equals("roll") || s.equals("gl") || s.equals("toggle") || s.equals("toggles")
						|| s.equals("togglepvp") || s.equals("togglechaos") || s.equals("toggledebug")
						|| s.equals("debug") || s.equals("toggleff") || s.equals("add") || s.equals("del")
						|| s.equals("delete") || s.equals("message") || s.equals("msg") || s.equals("m")
						|| s.equals("whisper") || s.equals("w") || s.equals("tell") || s.equals("t")
						|| s.equals("reply") || s.equals("r") || s.equals("logout") || s.equals("sync")
						|| s.equals("reboot") || s.equals("pinvite") || s.equals("paccept") || s.equals("pquit")
						|| s.equals("pkick") || s.equals("pdecline") || s.equals("p") || s.equals("toggletrail"))) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.WHITE
							+ "Unknown command. View your Character Journal's Index for a list of commands.");
				}
			} else {
				if (!(s.equals("roll") || s.equals("gl") || s.equals("toggle") || s.equals("toggles")
						|| s.equals("togglepvp") || s.equals("togglechaos") || s.equals("toggledebug")
						|| s.equals("debug") || s.equals("toggleff") || s.equals("add") || s.equals("del")
						|| s.equals("delete") || s.equals("message") || s.equals("msg") || s.equals("m")
						|| s.equals("whisper") || s.equals("w") || s.equals("tell") || s.equals("t")
						|| s.equals("reply") || s.equals("r") || s.equals("logout") || s.equals("sync")
						|| s.equals("reboot") || s.equals("pinvite") || s.equals("paccept") || s.equals("pquit")
						|| s.equals("pkick") || s.equals("pdecline") || s.equals("p"))) {
					e.setCancelled(true);
					p.sendMessage(ChatColor.WHITE
							+ "Unknown command. View your Character Journal's Index for a list of commands.");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatTabComplete(PlayerChatTabCompleteEvent e) {
		Player p = e.getPlayer();
		if (e.getChatMessage() != null) {
			p.closeInventory();
			p.performCommand("gl " + e.getChatMessage());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (!e.isCancelled()) {
			Player p = e.getPlayer();
			e.setCancelled(true);
			String message = e.getMessage();
			if (message.contains("@i@") && p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				sendShowString(p, p.getItemInHand(), "", message, p);
				List<Player> to_send = new ArrayList<Player>();
				for (Player pl : Bukkit.getServer().getOnlinePlayers())
					if (!ModerationMechanics.vanished.contains(pl.getName().toLowerCase()))
						if (pl != null)
							if (pl != p)
								if (pl.getLocation().distance(p.getLocation()) < 50)
									to_send.add(pl);
				if (to_send.size() <= 0) {
					p.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "No one heard you.");
				} else {
					for (Player pl : to_send)
						sendShowString(p, p.getItemInHand(), "", message, pl);
				}
				for (Player op : Bukkit.getServer().getOnlinePlayers())
					if (op.isOp())
						if (ModerationMechanics.vanished.contains(op.getName().toLowerCase()))
							if (op != p)
								sendShowString(p, p.getItemInHand(), "", message, op);
				Main.log.info(p.getDisplayName() + ": " + ChatColor.WHITE + message);
			} else {
				p.sendMessage(p.getDisplayName() + ": " + ChatColor.WHITE + message);
				List<Player> to_send = new ArrayList<Player>();
				for (Player pl : Bukkit.getServer().getOnlinePlayers())
					if (!ModerationMechanics.vanished.contains(pl.getName().toLowerCase()))
						if (pl != null)
							if (pl != p)
								if (pl.getLocation().distance(p.getLocation()) < 50)
									to_send.add(pl);
				if (to_send.size() <= 0) {
					p.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "No one heard you.");
				} else {
					for (Player pl : to_send)
						pl.sendMessage(p.getDisplayName() + ": " + ChatColor.WHITE + message);
				}
				for (Player op : Bukkit.getServer().getOnlinePlayers())
					if (op.isOp())
						if (ModerationMechanics.vanished.contains(op.getName().toLowerCase()))
							if (op != p)
								op.sendMessage(p.getDisplayName() + ": " + ChatColor.WHITE + message);
				Main.log.info(p.getDisplayName() + ": " + ChatColor.WHITE + message);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void sendShowString(Player sender, ItemStack is, String prefix, String message, Player p) {
		if (message.contains("@i@") && is != null && is.getType() != Material.AIR) {
			String before = "";
			try {
				before = message.split("@i@")[0];
			} catch (Exception e) {
				before = "";
			}
			String after = "";
			try {
				after = message.split("@i@")[1];
			} catch (Exception e) {
				after = "";
			}
			before = prefix + sender.getDisplayName() + ": " + ChatColor.WHITE + before;
			after = ChatColor.WHITE + after;
			String item = "{id:" + is.getTypeId() + "}";
			if (is.getItemMeta().hasLore()) {
				String lore = "";
				for (String s : is.getItemMeta().getLore())
					lore += ("\\\"" + s + "\\\", ").replace(":", "|");
				if (lore.endsWith(", "))
					lore = lore.substring(0, lore.length() - 2);
				if (is.getItemMeta().hasDisplayName()) {
					item = "{id:1,tag:{display:{Name:\\\"" + is.getItemMeta().getDisplayName() + "\\\",Lore:[" + lore
							+ "]}}}";
				} else {
					item = "{id:" + is.getTypeId() + ",tag:{display:{Lore:[" + lore + "]}}}";
				}
			} else {
				if (is.getItemMeta().hasDisplayName())
					item = "{id:1,tag:{display:{Name:\\\"" + is.getItemMeta().getDisplayName() + "\\\"}}}";
			}
			String msg = "[\"\",{\"text\":\"" + before
					+ "\"},{\"text\":\"§f§l§nSHOW\",\"hoverEvent\":{\"action\":\"show_item\",\"value\":\"" + item
					+ "\"}},{\"text\":\"" + after + "\"}]";
			PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(msg));
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}
}
