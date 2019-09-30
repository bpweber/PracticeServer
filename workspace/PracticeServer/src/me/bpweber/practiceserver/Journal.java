package me.bpweber.practiceserver;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Journal {

	public static ItemStack journal() {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setDisplayName("" + ChatColor.GREEN + ChatColor.BOLD + "Character Journal");
		bm.setLore(Arrays.asList(ChatColor.GRAY + "A book that displays", ChatColor.GRAY + "your character's stats"));
		book.setItemMeta(bm);
		return book;
	}
}
