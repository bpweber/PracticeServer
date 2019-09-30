package me.bpweber.practiceserver;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Merchant {

	// public static HashMap<String, ItemStack> scrapping = new HashMap<String,
	// ItemStack>();
	// public static HashMap<String, Integer> scrapamt = new HashMap<String,
	// Integer>();

	public static int getTier(ItemStack is) {
		int tier = 0;
		if (is.getType().name().contains("WOOD_") || is.getType().name().contains("LEATHER_")
				|| is.getType().name().contains("COAL_")
				|| (is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Leather")))
			tier = 1;
		if (is.getType().name().contains("STONE_") || is.getType().name().contains("CHAINMAIL_")
				|| is.getType().name().contains("EMERALD_")
				|| (is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Chainmail")))
			tier = 2;
		if ((is.getType().name().contains("IRON_") && !(is.getType() == Material.IRON_FENCE))
				|| (is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Iron")))
			tier = 3;
		if (is.getType().name().contains("DIAMOND_")
				|| (is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Diamond")))
			tier = 4;
		if (is.getType().name().contains("GOLD_")
				|| (is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Golden")))
			tier = 5;
		return tier;
	}
	/*
	 * public static int getScrapAmt(ItemStack is) { int val = 0; if
	 * (is.getType().name().contains("_SWORD") ||
	 * is.getType().name().contains("_AXE") ||
	 * is.getType().name().contains("_LEGGINGS")) val = 2; if
	 * (is.getType().name().contains("_CHESTPLATE")) val = 3; if
	 * (is.getType().name().contains("_HELMET") ||
	 * is.getType().name().contains("_BOOTS")) val = 1; return val; }
	 * 
	 * public static ItemStack getScrap(ItemStack is) { int amt = 0; int tier =
	 * 0; tier = getTier(is); if (Durability.isArmor(is) &&
	 * !is.getType().name().contains("_PICKAXE")) { amt = getScrapAmt(is); if
	 * (tier == 5) amt *= 2; else tier++; ItemStack s = Durability.scrap(tier);
	 * s.setAmount(amt); return s; } if (is.getType().name().contains("_ORE")) {
	 * amt = is.getAmount(); ItemStack s = Durability.scrap(tier);
	 * s.setAmount(amt); return s; /* if (tier < 3) { amt = is.getAmount() * 2;
	 * ItemStack s = Durability.scrap(tier); s.setAmount(amt); return s; } if
	 * (tier >= 3) { if (is.getAmount() < 2) return null; if (is.getAmount() >
	 * 1) { amt = is.getAmount() / 2; ItemStack s = Durability.scrap(tier);
	 * s.setAmount(amt); return s; } }
	 * 
	 * } if (Durability.isScrap(is)) { if (tier == 5 && is.getAmount() == 20) {
	 * return Items.orb(false); } if (tier == 4 && is.getAmount() == 60) {
	 * return Items.orb(false); } if (is.getAmount() == 32) { return
	 * Items.enchant(tier, 0, false); } if (is.getAmount() == 16) { return
	 * Items.enchant(tier, 1, false); } if (tier == 1) { if (is.getAmount() < 2)
	 * return null; if (is.getAmount() > 1) { tier++; amt = is.getAmount() / 2;
	 * ItemStack s = Durability.scrap(tier); s.setAmount(amt); return s; } } if
	 * (tier == 3) { if (is.getAmount() == 64) { amt = 32; tier++; ItemStack s =
	 * Durability.scrap(tier); s.setAmount(amt); return s; } } if (tier == 5 ||
	 * tier == 3) { amt = is.getAmount() * 3; tier--; ItemStack s =
	 * Durability.scrap(tier); s.setAmount(amt); return s; } if (tier == 4 ||
	 * tier == 2) { amt = is.getAmount() * 2; tier--; ItemStack s =
	 * Durability.scrap(tier); s.setAmount(amt); return s; } } if (is.getType()
	 * == Material.MAGMA_CREAM) { ItemStack s = Durability.scrap(5);
	 * s.setAmount(is.getAmount() * 20); return s; } return null; }
	 * 
	 * @EventHandler public void onPlayerQuit(PlayerQuitEvent e) { Player p =
	 * e.getPlayer(); if (scrapping.containsKey(p.getName())) {
	 * p.getInventory().addItem(scrapping.get(p.getName()));
	 * scrapping.remove(p.getName()); // scrapamt.remove(p.getName()); } }
	 * 
	 * @EventHandler public void onPlayerKick(PlayerKickEvent e) { Player p =
	 * e.getPlayer(); if (scrapping.containsKey(p.getName())) {
	 * p.getInventory().addItem(scrapping.get(p.getName()));
	 * scrapping.remove(p.getName()); // scrapamt.remove(p.getName()); } } /*
	 * 
	 * @SuppressWarnings("deprecation")
	 * 
	 * @EventHandler public void onClick(PlayerInteractEntityEvent e) { Player p
	 * = e.getPlayer(); if (e.getRightClicked() instanceof HumanEntity) { if
	 * (((HumanEntity) e.getRightClicked()).getName().equals("Merchant")) { if
	 * (e.getRightClicked().hasMetadata("NPC")) { e.setCancelled(true); if
	 * (p.getItemInHand() == null || p.getItemInHand().getType() ==
	 * Material.AIR) { p.sendMessage(ChatColor.YELLOW +
	 * "Equip the item to scrap and " + ChatColor.UNDERLINE + "RIGHT CLICK" +
	 * ChatColor.YELLOW + " the Merchant."); } else { if
	 * (scrapping.containsKey(p.getName())) { p.sendMessage(ChatColor.RED +
	 * "You have a pending merchant request. Type 'N' to cancel."); return; } if
	 * (p.getItemInHand().getItemMeta().hasLore()) if
	 * (p.getItemInHand().getItemMeta().getLore().contains(ChatColor.GRAY +
	 * "Untradeable")) { p.sendMessage(ChatColor.RED + "You " +
	 * ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " scrap this item.");
	 * return; } if (getScrap(p.getItemInHand()) != null) { ItemStack scrap =
	 * getScrap(p.getItemInHand()); int amt = scrap.getAmount(); String name =
	 * scrap.getItemMeta().getDisplayName(); p.sendMessage(ChatColor.YELLOW +
	 * "You will recieve " + ChatColor.BOLD + amt + "x " + ChatColor.YELLOW +
	 * "'" + name + ChatColor.YELLOW + "' by scrapping " + ChatColor.BOLD +
	 * p.getItemInHand().getAmount() + "x " + ChatColor.YELLOW + "'" +
	 * ChatColor.RESET + p.getItemInHand().getItemMeta().getDisplayName() +
	 * ChatColor.YELLOW + "'"); p.sendMessage(ChatColor.GRAY + "Type " +
	 * ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY +
	 * " to confirm this scrap. Or type " + ChatColor.RED + ChatColor.BOLD + "N"
	 * + ChatColor.GRAY + " to cancel."); scrapping.put(p.getName(),
	 * p.getItemInHand()); p.setItemInHand(null); p.updateInventory(); } else {
	 * p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" +
	 * ChatColor.RED + " scrap this item."); } } // else { //
	 * p.sendMessage(ChatColor.RED + "You " + // ChatColor.UNDERLINE + "cannot"
	 * + ChatColor.RED // + " scrap this item."); } } } }
	 * 
	 * @EventHandler(priority = EventPriority.LOWEST) public void
	 * onPrompt(AsyncPlayerChatEvent e) { Player p = e.getPlayer(); if
	 * (scrapping.containsKey(p.getName())) { e.setCancelled(true); if
	 * (e.getMessage().equalsIgnoreCase("y")) { ItemStack scrap =
	 * getScrap(scrapping.get(p.getName())); new BukkitRunnable() {
	 * 
	 * @Override public void run() { // for (int i = 0; i < amt; i++)
	 * addToInv(p, scrap); scrapping.remove(p.getName()); }
	 * }.runTaskLater(Main.main, 1L); p.playSound(p.getLocation(),
	 * Sound.ANVIL_USE, 1, 1); p.sendMessage("" + ChatColor.RED + ChatColor.BOLD
	 * + "ITEM SCRAPPED"); return; } else if
	 * (e.getMessage().equalsIgnoreCase("n")) { p.sendMessage("" + ChatColor.RED
	 * + "Item Scrapping - " + ChatColor.BOLD + "CANCELLED"); ItemStack is =
	 * scrapping.get(p.getName()); new BukkitRunnable() {
	 * 
	 * @Override public void run() { addToInv(p, is); }
	 * }.runTaskLater(Main.main, 1L); scrapping.remove(p.getName()); return; }
	 * else { p.sendMessage(ChatColor.RED + "Invalid option.");
	 * p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD
	 * + "Y" + ChatColor.GRAY + " to confirm this scrap. Or type " +
	 * ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
	 * return; } } }
	 * 
	 * public void addToInv(Player p, ItemStack is) { int amt = is.getAmount();
	 * while (amt > 64) { int slot = p.getInventory().firstEmpty(); if (slot !=
	 * -1) { ItemStack clone = is.clone(); clone.setAmount(64);
	 * p.getInventory().setItem(slot, clone); } else { ItemStack clone =
	 * is.clone(); clone.setAmount(64);
	 * p.getWorld().dropItemNaturally(p.getLocation(), clone); } amt -= 64; }
	 * int slot = p.getInventory().firstEmpty(); if (slot != -1) { ItemStack
	 * clone = is.clone(); clone.setAmount(amt); p.getInventory().setItem(slot,
	 * clone); } else { ItemStack clone = is.clone(); clone.setAmount(amt);
	 * p.getWorld().dropItemNaturally(p.getLocation(), clone); } }
	 */
}
