package me.bpweber.practiceserver;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GemPouches implements Listener {

	public void onEnable() {
		Main.log.info("[GemPouches] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[GemPouches] has been disabled.");
	}

	static ItemStack gemPouch(int tier) {
		String name = "";
		String lore = "";
		if (tier == 1) {
			name = ChatColor.WHITE + "Small Gem Pouch" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
			lore = ChatColor.GRAY + "A small linen pouch that holds " + ChatColor.BOLD + "100g";
		}
		if (tier == 2) {
			name = ChatColor.GREEN + "Medium Gem Sack" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
			lore = ChatColor.GRAY + "A medium wool sack that holds " + ChatColor.BOLD + "150g";
		}
		if (tier == 3) {
			name = ChatColor.AQUA + "Large Gem Satchel" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
			lore = ChatColor.GRAY + "A large leather satchel that holds " + ChatColor.BOLD + "200g";
		}
		if (tier == 4) {
			name = ChatColor.LIGHT_PURPLE + "Gigantic Gem Container" + ChatColor.GREEN + ChatColor.BOLD + " 0g";
			lore = ChatColor.GRAY + "A giant container that holds " + ChatColor.BOLD + "300g";
		}
		ItemStack is = new ItemStack(Material.INK_SACK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return is;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getInventory().getName().equals("container.crafting")) {
			if (e.isLeftClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK
					&& isGemPouch(e.getCurrentItem()) && e.getCursor() != null
					&& e.getCursor().getType() == Material.EMERALD) {
				if (e.getCurrentItem().getAmount() != 1)
					return;
				e.setCancelled(true);
				int amt = getCurrentValue(e.getCurrentItem());
				int max = getMaxValue(e.getCurrentItem());
				int add = e.getCursor().getAmount();
				if (amt < max) {
					if (amt + add > max) {
						e.getCursor().setAmount(add - (max - amt));						
						setPouchBal(e.getCurrentItem(), max);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
					} else {
						e.setCursor(null);
						setPouchBal(e.getCurrentItem(), amt + add);
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
					}
				}
			}
			if (e.isRightClick() && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.INK_SACK
					&& isGemPouch(e.getCurrentItem())
					&& (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
				if (e.getCurrentItem().getAmount() != 1)
					return;
				e.setCancelled(true);
				int amt = getCurrentValue(e.getCurrentItem());
				if (amt <= 0)
					return;
				if (amt > 64) {
					e.setCursor(Money.makeGems(64));
					amt -= 64;
					setPouchBal(e.getCurrentItem(), amt);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				} else {
					e.setCursor(Money.makeGems(amt));
					setPouchBal(e.getCurrentItem(), 0);
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onItemPickup(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if (e.isCancelled())
			return;
		if (!(e.getItem().getItemStack().getType() == Material.EMERALD))
			return;
		int add = e.getItem().getItemStack().getAmount();
		for (ItemStack is : p.getInventory().getContents()) {
			if (is == null)
				continue;
			if (isGemPouch(is)) {
				if (is.getAmount() != 1)
					return;
				int amt = getCurrentValue(is);
				int max = getMaxValue(is);
				if (add > 0) {
					if (amt < max) {
						if (amt + add > max) {
							add -= (max - amt);
							ItemStack newis = e.getItem().getItemStack();
							newis.setAmount(add);
							e.getItem().setItemStack(newis);
							setPouchBal(is, max);
							p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
							e.setCancelled(true);
							int adding = (max - amt);
							p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "                    +" + ChatColor.GREEN
									+ adding + ChatColor.GREEN + ChatColor.BOLD + "G");
						} else {
							e.getItem().remove();
							setPouchBal(is, amt + add);
							p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
							e.setCancelled(true);
							p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "                    +" + ChatColor.GREEN
									+ add + ChatColor.GREEN + ChatColor.BOLD + "G");
							add = 0;
						}
					}
				}
			}
		}
	}

	static int getMaxValue(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getType() == Material.INK_SACK
				&& is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 0) {
				if (lore.get(0).contains("g")) {
					try {
						String line = ChatColor.stripColor(lore.get(0));
						return Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.lastIndexOf("g")));
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	static int getCurrentValue(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getType() == Material.INK_SACK
				&& is.getItemMeta().hasDisplayName()) {
			try {
				String line = ChatColor.stripColor(is.getItemMeta().getDisplayName());
				return Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.lastIndexOf("g")));
			} catch (Exception e) {
				return 0;
			}
		}
		return 0;
	}

	static void setPouchBal(ItemStack is, int bal) {
		if (is.getItemMeta().hasDisplayName()) {
			String name = is.getItemMeta().getDisplayName();
			name = name.substring(0, name.lastIndexOf(" "));
			name += " " + bal + "g";
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(name);
			is.setItemMeta(im);
		}
	}

	static boolean isGemPouch(ItemStack is) {
		if (is == null)
			return false;
		if (is.getType() != Material.INK_SACK)
			return false;
		if (is.getDurability() != 0)
			return false;
		if (!is.getItemMeta().hasDisplayName())
			return false;
		if (!is.getItemMeta().getDisplayName().contains("g"))
			return false;
		if (getMaxValue(is) == 0)
			return false;
		return true;
	}
}
