package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speedfish implements Listener {

	public void onEnable() {
		Main.log.info("[Speedfish] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[Speedfish] has been disabled.");
	}

	public static ItemStack fish(int tier, boolean inshop) {
		ItemStack is = new ItemStack(Material.RAW_FISH);
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<String>();
		String name = "";
		int price = 0;
		if (tier == 1) {
			price = 100;
			name = ChatColor.WHITE + "Raw Shrimp of Lesser Agility";
			lore.add(ChatColor.RED + "SPEED (I) BUFF " + ChatColor.GRAY + "(15s)");
			lore.add(ChatColor.RED + "-10% HUNGER " + ChatColor.GRAY + "(instant)");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "A pink scaled crustacean.");
		}
		if (tier == 2) {
			price = 200;
			name = ChatColor.GREEN + "Raw Herring of Agility";
			lore.add(ChatColor.RED + "SPEED (I) BUFF " + ChatColor.GRAY + "(20s)");
			lore.add(ChatColor.RED + "-20% HUNGER " + ChatColor.GRAY + "(instant)");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "A colourful and medium sized fish.");
		}
		if (tier == 3) {
			price = 400;
			name = ChatColor.AQUA + "Raw Salmon of Lasting Agility";
			lore.add(ChatColor.RED + "SPEED (I) BUFF " + ChatColor.GRAY + "(30s)");
			lore.add(ChatColor.RED + "-30% HUNGER " + ChatColor.GRAY + "(instant)");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "A beautiful jumping fish.");
		}
		if (tier == 4) {
			price = 800;
			name = ChatColor.LIGHT_PURPLE + "Raw Lobster of Bursting Agility";
			lore.add(ChatColor.RED + "SPEED (II) BUFF " + ChatColor.GRAY + "(15s)");
			lore.add(ChatColor.RED + "-40% HUNGER " + ChatColor.GRAY + "(instant)");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "A large red crustacean.");
		}
		if (tier == 5) {
			price = 1600;
			name = ChatColor.YELLOW + "Raw Swordfish of Godlike Speed";
			lore.add(ChatColor.RED + "SPEED (II) BUFF " + ChatColor.GRAY + "(30s)");
			lore.add(ChatColor.RED + "-50% HUNGER " + ChatColor.GRAY + "(instant)");
			lore.add("" + ChatColor.GRAY + ChatColor.ITALIC + "An elongated fish with a long bill.");
		}
		if (inshop)
			lore.add(ChatColor.GREEN + "Price: " + ChatColor.WHITE + price + "g");
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	int getSpeed(ItemStack is) {
		if (is != null && is.getType() != Material.AIR) {
			if (is.getItemMeta().hasLore()) {
				for (String line : is.getItemMeta().getLore()) {
					if (line.contains("SPEED")) {
						if (line.contains("(I)"))
							return 1;
						if (line.contains("(II)"))
							return 2;
					}
				}
			}
		}
		return 0;
	}

	int getDuration(ItemStack is) {
		if (is != null && is.getType() != Material.AIR) {
			if (is.getItemMeta().hasLore()) {
				for (String line : is.getItemMeta().getLore()) {
					if (line.contains("SPEED")) {
						// Bukkit.getServer().broadcastMessage(line.split("\\(")[2].split("s\\)")[0]);
						try {
							return Integer.parseInt(line.split("\\(")[2].split("s\\)")[0]);
						} catch (Exception e) {
							return 0;
						}
					}
				}
			}
		}
		return 0;
	}

	int getHunger(ItemStack is) {
		if (is != null && is.getType() != Material.AIR) {
			if (is.getItemMeta().hasLore()) {
				for (String line : is.getItemMeta().getLore()) {
					if (line.contains("HUNGER")) {
						try {
							int amt = Integer.parseInt(line.split("-")[1].split("%")[0]);
							return amt / 5;
						} catch (Exception e) {
							return 0;
						}
					}
				}
			}
		}
		return 0;
	}

	@EventHandler
	public void onSpeedFish(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getItemInHand().getType() == Material.COOKED_FISH && p.getItemInHand().getItemMeta().hasDisplayName()
					&& p.getItemInHand().getItemMeta().getDisplayName().contains("Cooked")
					&& p.getItemInHand().getItemMeta().hasLore()) {
				if (e.hasBlock())
					if ((e.getClickedBlock().getType() == Material.FURNACE
							|| e.getClickedBlock().getType() == Material.BURNING_FURNACE
							|| e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 1, 0))
									.getType() == Material.FIRE)
							|| (e.getClickedBlock().getRelative(e.getBlockFace())
									.getType() == Material.STATIONARY_LAVA))
						return;
				if (p.hasPotionEffect(PotionEffectType.SPEED)) {
					e.setCancelled(true);
				} else {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration(p.getItemInHand()) * 20,
							getSpeed(p.getItemInHand())));
					p.playSound(p.getLocation(), Sound.EAT, 1, 1);
					if (p.getFoodLevel() + getHunger(p.getItemInHand()) > 20) {
						p.setFoodLevel(20);
					} else {
						p.setFoodLevel(p.getFoodLevel() + getHunger(p.getItemInHand()));
					}
					if (p.getItemInHand().getAmount() > 1) {
						p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
					} else {
						e.setCancelled(true);
						p.setItemInHand(null);
					}
				}
			} else if (p.getItemInHand().getType() == Material.RAW_FISH
					&& p.getItemInHand().getItemMeta().hasDisplayName()
					&& p.getItemInHand().getItemMeta().getDisplayName().contains("Raw")) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == Material.FURNACE)
					return;
				p.sendMessage(ChatColor.YELLOW + "To cook, " + ChatColor.UNDERLINE + "RIGHT CLICK" + ChatColor.YELLOW
						+ " any heat source.");
				p.sendMessage(ChatColor.GRAY + "Ex. Fire, Lava, Furnace");
			}
		}
	}

	@EventHandler
	public void onFishCook(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getItemInHand().getType() == Material.RAW_FISH && p.getItemInHand().getItemMeta().hasDisplayName()) {
				if ((e.getClickedBlock().getType() == Material.FURNACE
						|| e.getClickedBlock().getType() == Material.BURNING_FURNACE
						|| e.getClickedBlock().getWorld().getBlockAt(e.getClickedBlock().getLocation().add(0, 1, 0))
								.getType() == Material.FIRE)
						|| (e.getClickedBlock().getRelative(e.getBlockFace()).getType() == Material.STATIONARY_LAVA)) {
					e.setCancelled(true);
					p.playSound(p.getLocation(), Sound.FIZZ, 1, 0);
					ItemStack is = p.getItemInHand();
					ItemMeta im = is.getItemMeta();
					String name = im.getDisplayName();
					is.setType(Material.COOKED_FISH);
					name = name.replaceAll("Raw", "Cooked");
					im.setDisplayName(name);
					is.setItemMeta(im);
					p.setItemInHand(is);
				}
			}
		}
	}
}
