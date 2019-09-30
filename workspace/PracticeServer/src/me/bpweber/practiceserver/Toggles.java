package me.bpweber.practiceserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Toggles implements Listener, CommandExecutor {

	public static HashMap<String, ArrayList<String>> toggles = new HashMap<String, ArrayList<String>>();

	public void onEnable() {
		Main.log.info("[Toggles] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		File file = new File(Main.plugin.getDataFolder(), "toggles.yml");
		YamlConfiguration config = new YamlConfiguration();
		if (!(file.exists()))
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String p : config.getKeys(false)) {
			ArrayList<String> toggle = new ArrayList<String>();
			for (String t : config.getStringList(p))
				toggle.add(t);
			toggles.put(p, toggle);
		}
	}

	public void onDisable() {
		Main.log.info("[Toggles] has been disabled.");
		File file = new File(Main.plugin.getDataFolder(), "toggles.yml");
		YamlConfiguration config = new YamlConfiguration();
		for (String s : toggles.keySet())
			config.set(s, toggles.get(s));
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("toggle")) {
				p.openInventory(Toggles.getToggleMenu(p));
			}
			if (cmd.getName().equalsIgnoreCase("toggledebug")) {
				ArrayList<String> toggle = Toggles.getToggles(p.getName());
				if (toggle.contains("debug")) {
					toggle.remove("debug");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.RED + "Debug Messages - " + ChatColor.BOLD + "DISABLED");
				} else {
					toggle.add("debug");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.GREEN + "Debug Messages - " + ChatColor.BOLD + "ENABLED");
				}
			}
			if (cmd.getName().equalsIgnoreCase("togglepvp")) {
				ArrayList<String> toggle = Toggles.getToggles(p.getName());
				if (toggle.contains("pvp")) {
					toggle.remove("pvp");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.GREEN + "Outgoing PVP Damage - " + ChatColor.BOLD + "ENABLED");
				} else {
					toggle.add("pvp");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.RED + "Outgoing PVP Damage - " + ChatColor.BOLD + "DISABLED");
				}
			}
			if (cmd.getName().equalsIgnoreCase("togglechaos")) {
				ArrayList<String> toggle = Toggles.getToggles(p.getName());
				if (toggle.contains("chaos")) {
					toggle.remove("chaos");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.RED + "Anti-Chaotic - " + ChatColor.BOLD + "DISABLED");
				} else {
					toggle.add("chaos");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.GREEN + "Anti-Chaotic - " + ChatColor.BOLD + "ENABLED");
				}
			}
			if (cmd.getName().equalsIgnoreCase("toggleff")) {
				ArrayList<String> toggle = Toggles.getToggles(p.getName());
				if (toggle.contains("ff")) {
					toggle.remove("ff");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.RED + "Friendly Fire - " + ChatColor.BOLD + "DISABLED");
				} else {
					toggle.add("ff");
					toggles.put(p.getName(), toggle);
					p.sendMessage(ChatColor.GREEN + "Friendly Fire - " + ChatColor.BOLD + "ENABLED");
				}
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNoDamageToggle(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Player pp = (Player) e.getEntity();
			if (e.getDamage() <= 0)
				return;
			ArrayList<String> gettoggles = getToggles(p.getName());
			ArrayList<String> buddies = Buddies.getBuddies(p.getName());
			if (buddies.contains(pp.getName().toLowerCase())) {
				if (!gettoggles.contains("ff")) {
					e.setDamage(0);
					e.setCancelled(true);
					return;
				}
			}
			if (Parties.arePartyMembers(p, pp)) {
				e.setDamage(0);
				e.setCancelled(true);
				return;
			}
			if (gettoggles.contains("pvp")) {
				e.setDamage(0);
				e.setCancelled(true);
				return;
			}
			if (!Alignments.neutral.containsKey(pp.getName()) && !Alignments.chaotic.containsKey(pp.getName())) {
				if (gettoggles.contains("chaos")) {
					e.setDamage(0);
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onClickToggle(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (p.getOpenInventory().getTopInventory().getName().equals("Toggle Menu")) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR
					&& e.getCurrentItem().getItemMeta().hasDisplayName()
					&& e.getCurrentItem().getItemMeta().getDisplayName().contains("/toggle")) {
				String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				name = name.substring(1, name.length());
				p.performCommand(name);
				boolean on;
				if (e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.RED.toString())) {
					on = true;
				} else {
					on = false;
				}
				e.setCurrentItem(getToggleButton(name, on));
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 0.5F);
				// Bukkit.getServer().broadcastMessage(Main.toggles.toString());
			}
		}
	}

	public static ArrayList<String> getToggles(String s) {
		if (toggles.containsKey(s))
			return toggles.get(s);
		return new ArrayList<String>();
	}

	public static Inventory getToggleMenu(Player p) {
		ArrayList<String> toggles = getToggles(p.getName());
		Inventory inv = Bukkit.getServer().createInventory(null, 9, "Toggle Menu");
		if (toggles.contains("pvp")) {
			inv.setItem(0, getToggleButton("togglepvp", true));
		} else {
			inv.setItem(0, getToggleButton("togglepvp", false));
		}
		if (toggles.contains("chaos")) {
			inv.setItem(1, getToggleButton("togglechaos", true));
		} else {
			inv.setItem(1, getToggleButton("togglechaos", false));
		}
		if (toggles.contains("ff")) {
			inv.setItem(2, getToggleButton("toggleff", true));
		} else {
			inv.setItem(2, getToggleButton("toggleff", false));
		}
		if (toggles.contains("debug")) {
			inv.setItem(3, getToggleButton("toggledebug", true));
		} else {
			inv.setItem(3, getToggleButton("toggledebug", false));
		}
		return inv;
	}

	public static ItemStack getToggleButton(String s, boolean on) {
		ItemStack is = new ItemStack(Material.INK_SACK);
		ItemMeta im = is.getItemMeta();
		ChatColor cc = null;
		if (on) {
			is.setDurability((short) 10);
			cc = ChatColor.GREEN;
		} else {
			is.setDurability((short) 8);
			cc = ChatColor.RED;
		}
		im.setDisplayName(cc + "/" + s);
		im.setLore(Arrays.asList(getToggleDescription(s)));
		is.setItemMeta(im);
		return is;
	}

	public static String getToggleDescription(String toggle) {
		String desc = ChatColor.GRAY.toString();
		if (toggle.equalsIgnoreCase("toggledebug")) {
			desc += "Toggles displaying combat debug messages.";
		}
		if (toggle.equalsIgnoreCase("toggleff")) {
			desc += "Toggles friendly-fire between buddies.";
		}
		if (toggle.equalsIgnoreCase("togglechaos")) {
			desc += "Toggles killing blows on lawful players (anti-chaotic).";
		}
		if (toggle.equalsIgnoreCase("togglepvp")) {
			desc += "Toggles all outgoing PvP damage (anti-neutral).";
		}
		return desc;
	}
}
