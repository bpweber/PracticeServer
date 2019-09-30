package me.bpweber.practiceserver;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Money {

	public static boolean hasEnoughGems(Player p, int amt) {
		int gems = 0;
		for (ItemStack is : p.getInventory().getContents()) {
			if (isGem(is)) {
				gems += is.getAmount();
			}
			if (isBankNote(is)) {
				gems += getGems(is);
			}
			if (GemPouches.isGemPouch(is)) {
				gems += GemPouches.getCurrentValue(is);
			}
		}
		if (gems >= amt)
			return true;
		return false;
	}

	public static int getGems(Player p) {
		int gems = 0;
		for (ItemStack is : p.getInventory().getContents()) {
			if (isGem(is)) {
				gems += is.getAmount();
			}
			if (isBankNote(is)) {
				gems += getGems(is);
			}
			if (GemPouches.isGemPouch(is)) {
				gems += GemPouches.getCurrentValue(is);
			}
		}
		return gems;
	}

	public static void takeGems(Player p, int amt) {
		if (hasEnoughGems(p, amt)) {
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack is = p.getInventory().getItem(i);
				if (amt > 0) {
					if (isGem(is)) {
						if (amt >= is.getAmount()) {
							amt -= is.getAmount();
							p.getInventory().setItem(i, null);
						} else {
							is.setAmount(is.getAmount() - amt);
							amt = 0;
						}
					}
					if (isBankNote(is)) {
						int val = getGems(is);
						if (amt >= val) {
							amt -= val;
							p.getInventory().setItem(i, null);
						} else {
							ItemMeta im = is.getItemMeta();
							im.setLore(
									Arrays.asList(
											"" + ChatColor.WHITE + ChatColor.BOLD + "Value: " + ChatColor.WHITE
													+ (val - amt) + " Gems",
											ChatColor.GRAY + "Exchange at any bank for GEM(s)"));
							is.setItemMeta(im);
							amt = 0;
						}
					}
					if (GemPouches.isGemPouch(is)) {
						int val = GemPouches.getCurrentValue(is);
						if (amt >= val) {
							amt -= val;
							GemPouches.setPouchBal(is, 0);
						} else {
							GemPouches.setPouchBal(is, val - amt);
							amt = 0;
						}
					}
				}
			}
		}

	}

	public static boolean isGem(ItemStack is) {
		if (is != null && is.getType() != Material.AIR)
			if (is.getType() == Material.EMERALD)
				if (is.getItemMeta().hasDisplayName()
						&& is.getItemMeta().getDisplayName().toLowerCase().contains("gem"))
					return true;
		return false;
	}

	public static boolean isBankNote(ItemStack is) {
		if (is != null && is.getType() != Material.AIR)
			if (is.getType() == Material.PAPER)
				if (is.getItemMeta().hasDisplayName()
						&& is.getItemMeta().getDisplayName().toLowerCase().contains("bank note"))
					return true;
		return false;
	}

	public static ItemStack createBankNote(int amt) {
		ItemStack is = new ItemStack(Material.PAPER);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Bank Note");
		im.setLore(Arrays.asList("" + ChatColor.WHITE + ChatColor.BOLD + "Value: " + ChatColor.WHITE + amt + " Gems",
				ChatColor.GRAY + "Exchange at any bank for GEM(s)"));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack makeGems(int amt) {
		ItemStack is = new ItemStack(Material.EMERALD, amt);
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList(ChatColor.GRAY.toString() + "The currency of Andalucia"));
		im.setDisplayName(ChatColor.WHITE.toString() + "Gem");
		is.setItemMeta(im);
		is.setAmount(amt);
		return is;
	}

	static int getGems(ItemStack is) {
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
}
