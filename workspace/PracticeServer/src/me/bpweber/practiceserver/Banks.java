package me.bpweber.practiceserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Banks implements Listener {

	public static HashMap<Player, String> banksee = new HashMap<Player, String>();

	public void onEnable() {
		Main.log.info("[Banks] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		File file = new File(Main.plugin.getDataFolder(), "banks");
		if (!file.exists())
			file.mkdirs();
	}

	public void onDisable() {
		Main.log.info("[Banks] has been disabled.");
		File file = new File(Main.plugin.getDataFolder(), "banks");
		if (!file.exists())
			file.mkdirs();
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.ENDER_CHEST) {
				e.setCancelled(true);
				Inventory inv;
				inv = getBank(p);
				if (inv == null) {
					inv = Bukkit.createInventory(null, 63, "Bank Chest (1/1)");
				}
				p.openInventory(inv);
				p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1, 1);
			}
		}
	}

	@EventHandler
	public void onClose(final InventoryCloseEvent e) {
		final Player p = (Player) e.getPlayer();
		if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
			saveBank(e.getInventory(), p);
			new BukkitRunnable() {
				@Override
				public void run() {
					saveBank(e.getInventory(), p);
					if (banksee.containsKey(p))
						banksee.remove(p);
				}
			}.runTaskLater(Main.plugin, 1L);
		}
	}

	@EventHandler
	public void onClickSave(final InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
			saveBank(e.getInventory(), p);
			new BukkitRunnable() {
				@Override
				public void run() {
					saveBank(e.getInventory(), p);
				}
			}.runTaskLater(Main.plugin, 1L);
		}
	}

	public void saveBank(Inventory inv, Player p) {
		String name = p.getName();
		if (banksee.containsKey(p))
			name = banksee.get(p);
		File file = new File(Main.plugin.getDataFolder() + "/banks", name + ".yml");
		YamlConfiguration config = new YamlConfiguration();
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) != null)
				config.set("" + i, inv.getItem(i));
		try {
			config.save(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Inventory getBank(Player p) {
		String name = p.getName();
		if (banksee.containsKey(p))
			name = banksee.get(p);
		File file = new File(Main.plugin.getDataFolder() + "/banks", name + ".yml");
		if (!(file.exists()))
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Inventory inv = Bukkit.createInventory(null, 63, "Bank Chest (1/1)");
		for (int i = 0; i < inv.getSize(); i++) {
			if (config.contains("" + i))
				inv.setItem(i, config.getItemStack("" + i));
		}
		return inv;

	}

	@EventHandler
	public void onInvOpen(InventoryOpenEvent e) {
		if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
			Inventory inv = e.getInventory();
			ItemStack glass = new ItemStack(Material.THIN_GLASS);
			ItemMeta meta = glass.getItemMeta();
			meta.setDisplayName(" ");
			glass.setItemMeta(meta);
			for (int i = 54; i < 63; i++)
				inv.setItem(i, glass);
			ItemStack gem = new ItemStack(Material.EMERALD);
			ItemMeta im = gem.getItemMeta();
			im.setDisplayName("" + ChatColor.GREEN + (int) Main.econ.getBalance((OfflinePlayer) e.getPlayer())
					+ ChatColor.GREEN + ChatColor.BOLD + " GEM(s)");
			im.setLore(Arrays.asList(ChatColor.GRAY + "Right Click to create " + ChatColor.GREEN + "A GEM NOTE"));
			gem.setItemMeta(im);
			inv.setItem(58, gem);
		}
	}

	int getGems(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getType() == Material.PAPER
				&& is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 0) {
				if (lore.get(0).contains("Value")) {
					try {
						String line = ChatColor.stripColor(lore.get(0));
						return Integer.parseInt(line.split(": ")[1].split(" Gems")[0]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	List<String> withdraw = new ArrayList<String>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPromptAmount(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (withdraw.contains(p.getName())) {
			e.setCancelled(true);
			String message = e.getMessage();
			if (e.getMessage().equalsIgnoreCase("cancel")) {
				if (withdraw.contains(p.getName())) {
					for (int i = 0; i < withdraw.size(); i++) {
						withdraw.remove(p.getName());
					}
					p.sendMessage(ChatColor.RED + "Withdrawl operation - " + ChatColor.BOLD + "CANCELLED");
					return;
				}
			}
			int amt = 0;
			try {
				amt = Integer.parseInt(message);
				if (amt > Main.econ.getBalance(p)) {
					p.sendMessage(ChatColor.GRAY + "You cannot withdraw more GEMS than you have stored.");
				} else if (amt <= 0) {
					p.sendMessage(ChatColor.RED + "You must enter a POSIVIVE amount.");
				} else {
					for (int i = 0; i < withdraw.size(); i++) {
						withdraw.remove(p.getName());
					}
					Main.econ.withdrawPlayer(p, amt);
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().setItem(p.getInventory().firstEmpty(), Money.createBankNote(amt));
					}
					p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "New Balance: " + ChatColor.GREEN
							+ (int) Main.econ.getBalance(p) + " GEM(s)");
					p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1.2f);
				}
			} catch (NumberFormatException ex) {
				p.sendMessage(ChatColor.RED
						+ "Please enter a NUMBER, the amount you'd like to WITHDRAW from your bank account. Or type 'cancel' to void the withdrawl.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInvClick(final InventoryClickEvent e) {
		final Player p = (Player) e.getWhoClicked();
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER
				&& e.getCurrentItem().getItemMeta().hasLore() && e.getCursor().getType() == Material.PAPER
				&& e.getCursor().getItemMeta().hasLore()) {
			e.setCancelled(true);
			int first = getGems(e.getCurrentItem());
			int second = getGems(e.getCursor());
			ItemStack gem = new ItemStack(Material.PAPER);
			ItemMeta im = gem.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "Bank Note");
			im.setLore(Arrays.asList(
					"" + ChatColor.WHITE + ChatColor.BOLD + "Value: " + ChatColor.WHITE + (first + second) + " Gems",
					ChatColor.GRAY + "Exchange at any bank for GEM(s)"));
			gem.setItemMeta(im);
			e.setCurrentItem(gem);
			e.setCursor(null);
			p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1.2f);
		}
		if (e.getInventory().getName().equals("Bank Chest (1/1)")) {
			if (e.getClick() == ClickType.RIGHT) {
				if (e.getSlot() == 58) {
					e.setCancelled(true);
					p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Current Balance: " + ChatColor.GREEN
							+ (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
					for (int i = 0; i < withdraw.size(); i++) {
						withdraw.remove(p.getName());
					}
					withdraw.add(p.getName());
					p.sendMessage(ChatColor.GRAY
							+ "Please enter the amount you'd like To CONVERT into a gem note. Alternatively, type "
							+ ChatColor.RED + "'cancel'" + ChatColor.GRAY + " to void this operation.");
					new BukkitRunnable() {
						@Override
						public void run() {
							p.closeInventory();
						}
					}.runTaskLater(Main.plugin, 1L);
				}
			}
			for (int i = 54; i < 63; i++)
				if (e.getSlot() == i) {
					e.setCancelled(true);
				}
			if (e.getSlotType() == SlotType.OUTSIDE)
				return;
			if (e.isShiftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.EMERALD) {
				if (e.getRawSlot() < 63)
					return;
				int amt = e.getCurrentItem().getAmount();
				e.setCancelled(true);
				Main.econ.depositPlayer(p.getName(), amt);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				e.getInventory().setItem(58, gemBalance((int) Main.econ.getBalance(p.getName())));
				p.updateInventory();
				p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + amt + ChatColor.GREEN
						+ ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: "
						+ ChatColor.GREEN + (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
				e.setCurrentItem(null);
			}
			if (e.isShiftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER) {
				if (e.getRawSlot() < 63)
					return;
				int amt = getGems(e.getCurrentItem());
				e.setCancelled(true);
				Main.econ.depositPlayer(p.getName(), amt);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				e.getInventory().setItem(58, gemBalance((int) Main.econ.getBalance(p.getName())));
				p.updateInventory();
				p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + amt + ChatColor.GREEN
						+ ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: "
						+ ChatColor.GREEN + (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
				e.setCurrentItem(null);
			}
			if (e.isShiftClick() && e.getCurrentItem() != null
					&& (e.getCurrentItem().getType() == Material.INK_SACK && e.getCurrentItem().getDurability() == 0)) {
				if (e.getRawSlot() < 63)
					return;
				int amt = GemPouches.getCurrentValue(e.getCurrentItem());
				if (amt < 1) {
					if (e.getInventory().firstEmpty() != -1) {
						e.getInventory().setItem(e.getInventory().firstEmpty(), e.getCurrentItem());
						e.setCurrentItem(null);
					}
					return;
				}
				e.setCancelled(true);
				Main.econ.depositPlayer(p.getName(), amt);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				e.getInventory().setItem(58, gemBalance((int) Main.econ.getBalance(p.getName())));
				p.updateInventory();
				p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + amt + ChatColor.GREEN
						+ ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: "
						+ ChatColor.GREEN + (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
				GemPouches.setPouchBal(e.getCurrentItem(), 0);
			}
			if (!e.isShiftClick() && e.getCursor() != null && e.getCursor().getType() == Material.EMERALD) {
				if (e.getRawSlot() > 53)
					return;
				int amt = e.getCursor().getAmount();
				e.setCancelled(true);
				Main.econ.depositPlayer(p.getName(), amt);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				e.getInventory().setItem(58, gemBalance((int) Main.econ.getBalance(p.getName())));
				p.updateInventory();
				p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + amt + ChatColor.GREEN
						+ ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: "
						+ ChatColor.GREEN + (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
				e.setCursor(null);
			}
			if (!e.isShiftClick() && e.getCursor() != null && e.getCursor().getType() == Material.PAPER) {
				if (e.getRawSlot() > 53)
					return;
				int amt = getGems(e.getCursor());
				e.setCancelled(true);
				Main.econ.depositPlayer(p.getName(), amt);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				e.getInventory().setItem(58, gemBalance((int) Main.econ.getBalance(p.getName())));
				p.updateInventory();
				p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + amt + ChatColor.GREEN
						+ ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: "
						+ ChatColor.GREEN + (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
				e.setCursor(null);
			}
			if (!e.isShiftClick() && e.getCursor() != null
					&& (e.getCursor().getType() == Material.INK_SACK && e.getCursor().getDurability() == 0)) {
				if (e.getRawSlot() > 53)
					return;
				int amt = GemPouches.getCurrentValue(e.getCursor());
				if (amt < 1)
					return;
				e.setCancelled(true);
				Main.econ.depositPlayer(p.getName(), amt);
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				e.getInventory().setItem(58, gemBalance((int) Main.econ.getBalance(p.getName())));
				p.updateInventory();
				p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN + amt + ChatColor.GREEN
						+ ChatColor.BOLD + "G" + ChatColor.GREEN + ", " + ChatColor.BOLD + "New Balance: "
						+ ChatColor.GREEN + (int) Main.econ.getBalance(p.getName()) + " GEM(s)");
				GemPouches.setPouchBal(e.getCursor(), 0);
			}
		}
	}

	ItemStack gemBalance(int amt) {
		ItemStack is = new ItemStack(Material.EMERALD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.GREEN + amt + ChatColor.GREEN + ChatColor.BOLD + " GEM(s)");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Right Click to create " + ChatColor.GREEN + "A GEM NOTE"));
		is.setItemMeta(im);
		return is;
	}
}
