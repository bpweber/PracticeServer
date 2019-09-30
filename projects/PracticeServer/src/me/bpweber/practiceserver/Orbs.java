package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Orbs implements Listener {

	public void onEnable() {
		Main.log.info("[Orbs] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[Orbs] has been disabled.");
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (!e.getInventory().getName().equalsIgnoreCase("container.crafting"))
			return;
		if (e.getSlotType() == SlotType.ARMOR)
			return;
		if (e.getCursor() != null && e.getCursor().getType() == Material.MAGMA_CREAM
				&& e.getCursor().getItemMeta().hasDisplayName()
				&& e.getCursor().getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Orb of Alteration")) {
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
				ItemStack is = e.getCurrentItem();
				if (is.getItemMeta().hasLore() && (getItemTier(is) > 0 && getItemTier(is) < 6)
						&& (getItemType(is) > -1 && getItemType(is) <= 7)) {
					e.setCancelled(true);
					if (e.getCursor().getAmount() > 1) {
						e.getCursor().setAmount(e.getCursor().getAmount() - 1);
					} else if (e.getCursor().getAmount() == 1) {
						e.setCursor(null);
					}
					int oldsize = is.getItemMeta().getLore().size();
					ItemStack newis = randomizeStats(is);
					int newsize = newis.getItemMeta().getLore().size();
					// Bukkit.getServer().broadcastMessage(oldsize + " -> " +
					// newsize);
					if (newsize > oldsize) {
						p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
						Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
						FireworkMeta fwm = fw.getFireworkMeta();
						FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW)
								.withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
						fwm.addEffect(effect);
						fwm.setPower(0);
						fw.setFireworkMeta(fwm);
					} else {
						p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2, 1.25f);
						ParticleEffect.LAVA.display(0, 0, 0, 5, 10, p.getEyeLocation(), 20);
					}
					newis.setDurability((short) 0);
					e.setCurrentItem(newis);
				}
			}
		}
	}

	public static int getItemTier(ItemStack is) {
		if (is.getType().name().contains("WOOD_") || is.getType().name().contains("LEATHER_"))
			return 1;
		if (is.getType().name().contains("STONE_") || is.getType().name().contains("CHAINMAIL_"))
			return 2;
		if (is.getType().name().contains("IRON_"))
			return 3;
		if (is.getType().name().contains("DIAMOND_"))
			return 4;
		if (is.getType().name().contains("GOLD_"))
			return 5;
		return 0;
	}

	public static int getItemType(ItemStack is) {
		if (is.getType().name().contains("_HOE"))
			return 0;
		if (is.getType().name().contains("_SPADE"))
			return 1;
		if (is.getType().name().contains("_SWORD"))
			return 2;
		if (is.getType().name().contains("_AXE"))
			return 3;
		if (is.getType().name().contains("_HELMET"))
			return 4;
		if (is.getType().name().contains("_CHESTPLATE"))
			return 5;
		if (is.getType().name().contains("_LEGGINGS"))
			return 6;
		if (is.getType().name().contains("_BOOTS"))
			return 7;
		return -1;
	}

	public static ItemStack randomizeStats(ItemStack is) {
		String name = "";
		String rare = "";
		int tier = getItemTier(is);
		int item = getItemType(is);
		List<String> oldlore = is.getItemMeta().getLore();
		List<String> lore = new ArrayList<String>();
		Random random = new Random();
		int elem = random.nextInt((9 - 1) + 1) + 1;
		int pure = random.nextInt((3 - 1) + 1) + 1;
		int life = random.nextInt((3 - 1) + 1) + 1;
		int crit = random.nextInt((3 - 1) + 1) + 1;
		int acc = random.nextInt((3 - 1) + 1) + 1;
		int dodge = random.nextInt((3 - 1) + 1) + 1;
		int block = random.nextInt((3 - 1) + 1) + 1;
		int vit = random.nextInt((3 - 1) + 1) + 1;
		int str = random.nextInt((3 - 1) + 1) + 1;
		int intel = random.nextInt((3 - 1) + 1) + 1;
		int dodgeamt = 0;
		int blockamt = 0;
		int vitamt = 0;
		int stramt = 0;
		int intamt = 0;	
		int elemamt = 0;
		int pureamt = 0;
		int lifeamt = 0;
		int critamt = 0;
		int accamt = 0;
		rare = oldlore.get(oldlore.size() - 1);
		if (tier == 1) {
			tier = 1;
			dodgeamt = random.nextInt((5 - 1) + 1) + 1;
			blockamt = random.nextInt((5 - 1) + 1) + 1;
			vitamt = random.nextInt((15 - 1) + 1) + 1;
			stramt = random.nextInt((15 - 1) + 1) + 1;
			intamt = random.nextInt((15 - 1) + 1) + 1;
			elemamt = random.nextInt((4 - 1) + 1) + 1;
			pureamt = random.nextInt((4 - 1) + 1) + 1;
			lifeamt = random.nextInt((30 - 1) + 1) + 1;
			critamt = random.nextInt((3 - 1) + 1) + 1;
			accamt = random.nextInt((10 - 1) + 1) + 1;
			if (item == 0) {
				name = "Staff";
				is.setType(Material.WOOD_HOE);
			}
			if (item == 1) {
				name = "Spear";
				is.setType(Material.WOOD_SPADE);
			}
			if (item == 2) {
				name = "Shortsword";
				is.setType(Material.WOOD_SWORD);
			}
			if (item == 3) {
				name = "Hatchet";
				is.setType(Material.WOOD_AXE);
			}
			if (item == 4) {
				name = "Leather Coif";
				is.setType(Material.LEATHER_HELMET);
			}
			if (item == 5) {
				name = "Leather Chestplate";
				is.setType(Material.LEATHER_CHESTPLATE);
			}
			if (item == 6) {
				name = "Leather Leggings";
				is.setType(Material.LEATHER_LEGGINGS);
			}
			if (item == 7) {
				name = "Leather Boots";
				is.setType(Material.LEATHER_BOOTS);
			}
		}
		if (tier == 2) {
			tier = 2;
			dodgeamt = random.nextInt((8 - 1) + 1) + 1;
			blockamt = random.nextInt((8 - 1) + 1) + 1;
			vitamt = random.nextInt((35 - 1) + 1) + 1;
			stramt = random.nextInt((35 - 1) + 1) + 1;
			intamt = random.nextInt((35 - 1) + 1) + 1;
			elemamt = random.nextInt((9 - 1) + 1) + 1;
			pureamt = random.nextInt((9 - 1) + 1) + 1;
			lifeamt = random.nextInt((15 - 1) + 1) + 1;
			critamt = random.nextInt((6 - 1) + 1) + 1;
			accamt = random.nextInt((12 - 1) + 1) + 1;
			if (item == 0) {
				name = "Battletaff";
				is.setType(Material.STONE_HOE);
			}
			if (item == 1) {
				name = "Halberd";
				is.setType(Material.STONE_SPADE);
			}
			if (item == 2) {
				name = "Broadsword";
				is.setType(Material.STONE_SWORD);
			}
			if (item == 3) {
				name = "Great Axe";
				is.setType(Material.STONE_AXE);
			}
			if (item == 4) {
				name = "Medium Helmet";
				is.setType(Material.CHAINMAIL_HELMET);
			}
			if (item == 5) {
				name = "Chainmail";
				is.setType(Material.CHAINMAIL_CHESTPLATE);
			}
			if (item == 6) {
				name = "Chainmail Leggings";
				is.setType(Material.CHAINMAIL_LEGGINGS);
			}
			if (item == 7) {
				name = "Chainmail Boots";
				is.setType(Material.CHAINMAIL_BOOTS);
			}
		}
		if (tier == 3) {
			tier = 3;
			dodgeamt = random.nextInt((10 - 1) + 1) + 1;
			blockamt = random.nextInt((10 - 1) + 1) + 1;
			vitamt = random.nextInt((75 - 1) + 1) + 1;
			stramt = random.nextInt((75 - 1) + 1) + 1;
			intamt = random.nextInt((75 - 1) + 1) + 1;
			elemamt = random.nextInt((15 - 1) + 1) + 1;
			pureamt = random.nextInt((15 - 1) + 1) + 1;
			lifeamt = random.nextInt((12 - 1) + 1) + 1;
			critamt = random.nextInt((8 - 1) + 1) + 1;
			accamt = random.nextInt((25 - 1) + 1) + 1;
			if (item == 0) {
				name = "Wizard Staff";
				is.setType(Material.IRON_HOE);
			}
			if (item == 1) {
				name = "Magic Polearm";
				is.setType(Material.IRON_SPADE);
			}
			if (item == 2) {
				name = "Magic Sword";
				is.setType(Material.IRON_SWORD);
			}
			if (item == 3) {
				name = "War Axe";
				is.setType(Material.IRON_AXE);
			}
			if (item == 4) {
				name = "Full Helmet";
				is.setType(Material.IRON_HELMET);
			}
			if (item == 5) {
				name = "Platemail";
				is.setType(Material.IRON_CHESTPLATE);
			}
			if (item == 6) {
				name = "Platemail Leggings";
				is.setType(Material.IRON_LEGGINGS);
			}
			if (item == 7) {
				name = "Platemail Boots";
				is.setType(Material.IRON_BOOTS);
			}
		}
		if (tier == 4) {
			tier = 4;
			dodgeamt = random.nextInt((12 - 1) + 1) + 1;
			blockamt = random.nextInt((12 - 1) + 1) + 1;
			vitamt = random.nextInt((115 - 1) + 1) + 1;
			stramt = random.nextInt((115 - 1) + 1) + 1;
			intamt = random.nextInt((115 - 1) + 1) + 1;
			elemamt = random.nextInt((25 - 1) + 1) + 1;
			pureamt = random.nextInt((25 - 1) + 1) + 1;
			lifeamt = random.nextInt((10 - 1) + 1) + 1;
			critamt = random.nextInt((10 - 1) + 1) + 1;
			accamt = random.nextInt((28 - 1) + 1) + 1;
			if (item == 0) {
				name = "Ancient Staff";
				is.setType(Material.DIAMOND_HOE);
			}
			if (item == 1) {
				name = "Ancient Polearm";
				is.setType(Material.DIAMOND_SPADE);
			}
			if (item == 2) {
				name = "Ancient Sword";
				is.setType(Material.DIAMOND_SWORD);
			}
			if (item == 3) {
				name = "Ancient Axe";
				is.setType(Material.DIAMOND_AXE);
			}
			if (item == 4) {
				name = "Ancient Full Helmet";
				is.setType(Material.DIAMOND_HELMET);
			}
			if (item == 5) {
				name = "Magic Platemail";
				is.setType(Material.DIAMOND_CHESTPLATE);
			}
			if (item == 6) {
				name = "Magic Platemail Leggings";
				is.setType(Material.DIAMOND_LEGGINGS);
			}
			if (item == 7) {
				name = "Magic Platemail Boots";
				is.setType(Material.DIAMOND_BOOTS);
			}
		}
		if (tier == 5) {
			tier = 5;
			dodgeamt = random.nextInt((12 - 1) + 1) + 1;
			blockamt = random.nextInt((12 - 1) + 1) + 1;
			vitamt = random.nextInt((315 - 1) + 1) + 1;
			stramt = random.nextInt((315 - 1) + 1) + 1;
			intamt = random.nextInt((315 - 1) + 1) + 1;
			elemamt = random.nextInt((55 - 1) + 1) + 1;
			pureamt = random.nextInt((55 - 1) + 1) + 1;
			lifeamt = random.nextInt((8 - 1) + 1) + 1;
			critamt = random.nextInt((11 - 1) + 1) + 1;
			accamt = random.nextInt((35 - 1) + 1) + 1;
			if (item == 0) {
				name = "Legendary Staff";
				is.setType(Material.GOLD_HOE);
			}
			if (item == 1) {
				name = "Legendary Polearm";
				is.setType(Material.GOLD_SPADE);
			}
			if (item == 2) {
				name = "Legendary Sword";
				is.setType(Material.GOLD_SWORD);
			}
			if (item == 3) {
				name = "Legendary Axe";
				is.setType(Material.GOLD_AXE);
			}
			if (item == 4) {
				name = "Legendary Full Helmet";
				is.setType(Material.GOLD_HELMET);
			}
			if (item == 5) {
				name = "Legendary Platemail";
				is.setType(Material.GOLD_CHESTPLATE);
			}
			if (item == 6) {
				name = "Legendary Platemail Leggings";
				is.setType(Material.GOLD_LEGGINGS);
			}
			if (item == 7) {
				name = "Legendary Platemail Boots";
				is.setType(Material.GOLD_BOOTS);
			}
		}

		if (item == 0 || item == 1 || item == 2 || item == 3) {
			lore.add(oldlore.get(0));
			if (item == 3)
				if (pure == 1) {
					lore.add(ChatColor.RED + "PURE DMG: +" + pureamt);
					name = "Pure " + name;
				}
			if (item == 2)
				if (acc == 1) {
					lore.add(ChatColor.RED + "ACCURACY: " + accamt + "%");
					name = "Accurate " + name;
				}
			if (life == 1) {
				lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
				name = "Vampyric " + name;
			}
			if (crit == 1) {
				lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
				name = "Deadly " + name;
			}
			if (elem == 3) {
				lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
				name = name + " of Ice";
			}
			if (elem == 2) {
				lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
				name = name + " of Poison";
			}
			if (elem == 1) {
				lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
				name = name + " of Fire";
			}
		}
		if (item == 4 || item == 5 || item == 6 || item == 7) {
			lore.add(oldlore.get(0));
			lore.add(oldlore.get(1));
			lore.add(oldlore.get(2));
			if (intel == 1)
				lore.add(ChatColor.RED + "INT: +" + intamt);
			if (str == 1)
				lore.add(ChatColor.RED + "STR: +" + stramt);
			if (vit == 1)
				lore.add(ChatColor.RED + "VIT: +" + vitamt);
			if (dodge == 1) {
				lore.add(ChatColor.RED + "DODGE: " + dodgeamt + "%");
				name = "Agile " + name;
			}
			if (oldlore.get(2).contains("HP REGEN:")) {
				name = "Mending " + name;
			}
			if (block == 1) {
				lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
				name = "Protective " + name;
			}
			if (oldlore.get(2).contains("ENERGY REGEN:")) {
				name = name + " of Fortitude";
			}
		}
		lore.add(rare);
		if (tier == 1)
			name = ChatColor.WHITE + name;
		if (tier == 2)
			name = ChatColor.GREEN + name;
		if (tier == 3)
			name = ChatColor.AQUA + name;
		if (tier == 4)
			name = ChatColor.LIGHT_PURPLE + name;
		if (tier == 5)
			name = ChatColor.YELLOW + name;
		if (Enchants.getPlus(is) > 0)
			name = ChatColor.RED + "[+" + Enchants.getPlus(is) + "] " + name;
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}
