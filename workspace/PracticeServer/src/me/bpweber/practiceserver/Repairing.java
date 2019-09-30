package me.bpweber.practiceserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class Repairing implements Listener {

	static HashMap<String, ItemStack> repairing = new HashMap<String, ItemStack>();
	static HashMap<String, Integer> repaircost = new HashMap<String, Integer>();
	static HashMap<String, Item> ghostitem = new HashMap<String, Item>();

	public void onEnable() {
		Main.log.info("[Repairing] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[Repairing] has been disabled.");
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (repairing.containsKey(p.getName())) {
				p.getInventory().addItem(repairing.get(p.getName()));
				repairing.remove(p.getName());
				repaircost.remove(p.getName());
				ghostitem.get(p.getName()).remove();
				ghostitem.remove(p.getName());
			}
		}
	}

	public static int getTier(ItemStack is) {
		int tier = 0;
		if (is.getType().name().contains("WOOD_") || is.getType().name().contains("LEATHER_"))
			tier = 1;
		if (is.getType().name().contains("STONE_") || is.getType().name().contains("CHAINMAIL_"))
			tier = 2;
		if (is.getType().name().contains("IRON_"))
			tier = 3;
		if (is.getType().name().contains("DIAMOND_"))
			tier = 4;
		if (is.getType().name().contains("GOLD_"))
			tier = 5;
		return tier;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (repairing.containsKey(p.getName())) {
			p.getInventory().addItem(repairing.get(p.getName()));
			repairing.remove(p.getName());
			repairing.remove(p.getName());
			ghostitem.get(p.getName()).remove();
			ghostitem.remove(p.getName());
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		Player p = e.getPlayer();
		if (repairing.containsKey(p.getName())) {
			p.getInventory().addItem(repairing.get(p.getName()));
			repairing.remove(p.getName());
			repairing.remove(p.getName());
			ghostitem.get(p.getName()).remove();
			ghostitem.remove(p.getName());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
			if (e.getClickedBlock().getType() == Material.ANVIL) {
				e.setCancelled(true);
				if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) {
					p.sendMessage(ChatColor.YELLOW + "Equip the item to repair and " + ChatColor.UNDERLINE
							+ "RIGHT CLICK" + ChatColor.YELLOW + " the ANVIL.");
				} else {
					if (repairing.containsKey(p.getName()) && repaircost.containsKey(p.getName())) {
						p.sendMessage(ChatColor.RED + "You have a pending repair request. Type 'N' to cancel.");
						return;
					}
					if (p.getItemInHand().getItemMeta().hasLore()) {
						List<String> lore = p.getItemInHand().getItemMeta().getLore();
						if (lore.contains(ChatColor.GRAY + "Untradeable")) {
							p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED
									+ " repair this item.");
							return;
						}
						if (!(p.getItemInHand().getDurability() == 0)) {
							float percent = (float) p.getItemInHand().getDurability()
									/ (float) p.getItemInHand().getType().getMaxDurability();
							if (lore.size() > 0 && lore.get(0).contains("DMG:")) {
								int mindmg = Damage.getDamageRange(p.getItemInHand()).get(0);
								int maxdmg = Damage.getDamageRange(p.getItemInHand()).get(1);
								int dmg = (maxdmg + mindmg) / 2;
								float price = (getTier(p.getItemInHand()) * dmg * percent);
								int gemamt = (int) price; //* 2;
								if (gemamt < 1)
									gemamt = 1;
								if (Money.hasEnoughGems(p, gemamt)) {
									p.sendMessage(ChatColor.YELLOW + "It will cost " + ChatColor.GREEN + ChatColor.BOLD
											+ gemamt + "G" + ChatColor.YELLOW + " to repair '" + ChatColor.RESET
											+ p.getItemInHand().getItemMeta().getDisplayName() + ChatColor.YELLOW
											+ "'");
									p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD + "Y"
											+ ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED
											+ ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
									repairing.put(p.getName(), p.getItemInHand());
									repaircost.put(p.getName(), gemamt);

									Item itm = (Item) p.getWorld().dropItem(
											e.getClickedBlock().getLocation().add(.5, 1.125, .5),
											makeFakeItem(p.getItemInHand().getType()));
									itm.setVelocity(new Vector(0, 0.1, 0));
									ghostitem.put(p.getName(), itm);

									p.setItemInHand(null);
									p.updateInventory();
								} else {
									p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)"
											+ ChatColor.RED + " to repair this item.");
									p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED
											+ gemamt + ChatColor.BOLD + "G");
								}
							} else if (lore.size() > 0
									&& (lore.get(0).contains("DPS") || lore.get(0).contains("ARMOR"))) {
								int arm = 0;
								if (lore.get(0).contains("DPS"))
									arm = Damage.getDps(p.getItemInHand());
								if (lore.get(0).contains("ARMOR"))
									arm = Damage.getArmor(p.getItemInHand());
								float price = (getTier(p.getItemInHand()) * (arm * 10) * percent);
								int gemamt = (int) price;
								if (gemamt < 1)
									gemamt = 1;
								if (Money.hasEnoughGems(p, gemamt)) {
									p.sendMessage(ChatColor.YELLOW + "It will cost " + ChatColor.GREEN + ChatColor.BOLD
											+ gemamt + "G" + ChatColor.YELLOW + " to repair '" + ChatColor.RESET
											+ p.getItemInHand().getItemMeta().getDisplayName() + ChatColor.YELLOW
											+ "'");
									p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD + "Y"
											+ ChatColor.GRAY + " to confirm this repair. Or type " + ChatColor.RED
											+ ChatColor.BOLD + "N" + ChatColor.GRAY + " to cancel.");
									repairing.put(p.getName(), p.getItemInHand());
									repaircost.put(p.getName(), gemamt);

									Item itm = (Item) p.getWorld().dropItem(
											e.getClickedBlock().getLocation().add(.5, 1.125, .5),
											makeFakeItem(p.getItemInHand().getType()));
									itm.setVelocity(new Vector(0, 0.1, 0));
									ghostitem.put(p.getName(), itm);

									p.setItemInHand(null);
									p.updateInventory();
								} else {
									p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)"
											+ ChatColor.RED + " to repair this item.");
									p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED
											+ gemamt + ChatColor.BOLD + "G");
								}
							} else {
								p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED
										+ " repair this item.");
							}
						}
					} else {
						p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED
								+ " repair this item.");
					}
				}
			}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPrompt(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (repairing.containsKey(p.getName()) && repaircost.containsKey(p.getName())) {
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("y")) {
				if (p.getInventory().firstEmpty() == -1) {
					p.sendMessage(ChatColor.RED
							+ "You don't have enough room in your inventory to confirm this repair request.");
					return;
				}
				if (Money.hasEnoughGems(p, repaircost.get(p.getName()))) {
					ItemStack is = repairing.get(p.getName());
					is.setDurability((short) 0);
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().setItem(p.getInventory().firstEmpty(), is);
					}
					p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
					p.sendMessage(ChatColor.RED + "-" + repaircost.get(p.getName()) + ChatColor.BOLD + "G");
					p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "ITEM REPAIRED");
					Money.takeGems(p, repaircost.get(p.getName()));
					repairing.remove(p.getName());
					repaircost.remove(p.getName());

					if (ghostitem.containsKey(p.getName())) {
						if (ghostitem.get(p.getName()) != null)
							ghostitem.get(p.getName()).remove();
						ghostitem.remove(p.getName());
					}

					return;
				} else {
					p.sendMessage(ChatColor.RED + "You do not have enough " + ChatColor.BOLD + "GEM(s)" + ChatColor.RED
							+ " to repair this item.");
					p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "COST: " + ChatColor.RED
							+ repaircost.get(p.getName()) + ChatColor.BOLD + "G");
					ItemStack is = repairing.get(p.getName());
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().setItem(p.getInventory().firstEmpty(), is);
					}
					repairing.remove(p.getName());
					repaircost.remove(p.getName());
					if (ghostitem.containsKey(p.getName())) {
						if (ghostitem.get(p.getName()) != null)
							ghostitem.get(p.getName()).remove();
						ghostitem.remove(p.getName());
					}
					return;
				}
			} else if (e.getMessage().equalsIgnoreCase("n")) {
				if (p.getInventory().firstEmpty() == -1) {
					p.sendMessage(ChatColor.RED
							+ "You don't have enough room in your inventory to cancel this repair request.");
					return;
				}
				p.sendMessage("" + ChatColor.RED + "Item Repair - " + ChatColor.BOLD + "CANCELLED");
				ItemStack is = repairing.get(p.getName());
				if (p.getInventory().firstEmpty() != -1) {
					p.getInventory().setItem(p.getInventory().firstEmpty(), is);
				}
				repairing.remove(p.getName());
				repaircost.remove(p.getName());
				if (ghostitem.containsKey(p.getName())) {
					if (ghostitem.get(p.getName()) != null)
						ghostitem.get(p.getName()).remove();
					ghostitem.remove(p.getName());
				}
				return;
			} else {
				p.sendMessage(ChatColor.RED + "Invalid option.");
				p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + ChatColor.BOLD + "Y" + ChatColor.GRAY
						+ " to confirm this repair. Or type " + ChatColor.RED + ChatColor.BOLD + "N" + ChatColor.GRAY
						+ " to cancel.");
				return;
			}
		}
	}

	ItemStack makeFakeItem(Material m) {
		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList("notarealitem"));
		is.setItemMeta(im);
		return is;
	}
}
