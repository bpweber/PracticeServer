package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {

	public static ItemStack orb(boolean inshop) {
		ItemStack orb = new ItemStack(Material.MAGMA_CREAM);
		ItemMeta orbmeta = orb.getItemMeta();
		List<String> lore = new ArrayList<String>();
		orbmeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Orb of Alteration");
		lore.add(ChatColor.GRAY + "Randomizes stats of selected equipment.");
		if (inshop)
			lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + "2000g");
		orbmeta.setLore(lore);
		orb.setItemMeta(orbmeta);
		return orb;
	}

	public static ItemStack enchant(int tier, int type, boolean inshop) {
		ItemStack is = new ItemStack(Material.EMPTY_MAP);
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		String name = "";
		int price = 0;
		if (tier == 1) {
			price = 100;
			name = ChatColor.WHITE + " Enchant ";
			if (type == 0)
				name = name + "Wooden";
			if (type == 1)
				name = name + "Leather";
		}
		if (tier == 2) {
			price = 200;
			name = ChatColor.GREEN + " Enchant ";
			if (type == 0)
				name = name + "Stone";
			if (type == 1)
				name = name + "Chainmail";
		}
		if (tier == 3) {
			price = 400;
			name = ChatColor.AQUA + " Enchant Iron";
		}
		if (tier == 4) {
			price = 800;
			name = ChatColor.LIGHT_PURPLE + " Enchant Diamond";
		}
		if (tier == 5) {
			price = 1600;
			name = ChatColor.YELLOW + " Enchant Gold";
		}
		if (type == 0) {
			price *= 1.5;
			name = name + " Weapon";
			lore.add(ChatColor.RED + "+5% DMG");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "Weapon will VANISH if enchant above +3 FAILS.");
		}
		if (type == 1) {
			name = name + " Armor";
			lore.add(ChatColor.RED + "+5% HP");
			lore.add(ChatColor.RED + "+5% HP REGEN");
			lore.add(ChatColor.GRAY + "   - OR -");
			lore.add(ChatColor.RED + "+1% ENERGY REGEN");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "Armor will VANISH if enchant above +3 FAILS.");
		}
		if (inshop)
			lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g");
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Scroll:" + name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}
