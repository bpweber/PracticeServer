package me.bpweber.practiceserver;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Durability implements Listener {

	public void onEnable() {
		Main.log.info("[Durability] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[Durability] has been disabled.");
	}

	public static ItemStack scrap(int tier) {
		Material m = null;
		ChatColor cc = ChatColor.WHITE;
		String name = "";
		String lore = "";
		short dura = 0;
		if (tier == 1) {
			m = Material.LEATHER;
			name = "Leather";
			lore = "Leather";
		}
		if (tier == 2) {
			m = Material.IRON_FENCE;
			name = "Chainmail";
			lore = "Chainmail";
			cc = ChatColor.GREEN;
		}
		if (tier > 2)
			m = Material.INK_SACK;
		if (tier == 3) {
			name = "Iron";
			lore = "Iron";
			dura = 7;
			cc = ChatColor.AQUA;
		}
		if (tier == 4) {
			name = "Diamond";
			lore = "Diamond";
			dura = 12;
			cc = ChatColor.LIGHT_PURPLE;
		}
		if (tier == 5) {
			name = "Golden";
			lore = "Gold";
			dura = 11;
			cc = ChatColor.YELLOW;
		}

		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(cc + name + " Armor Scrap");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Recovers 3% Durability of " + cc + lore + " Equipment"));
		is.setItemMeta(im);
		is.setDurability(dura);
		return is;
	}

	public static float getDuraPercent(ItemStack is) {
		float max = is.getType().getMaxDurability();
		float curr = is.getType().getMaxDurability() - is.getDurability();
		float dura = (curr / max) * 100;
		return (float) Math.round(dura);

	}

	public static ItemStack addDura(ItemStack is, float amt) {
		float add = amt / 100;
		float max = is.getType().getMaxDurability();
		float adding = max * add;
		if (adding < 1)
			adding = 1;
		adding = Math.round(adding);
		short dura = (short) (is.getDurability() - adding);
		if (dura < 0)
			dura = 0;
		is.setDurability(dura);
		return is;
	}

	public static ItemStack takeDura(ItemStack is, float amt) {
		float add = amt / 100;
		float max = is.getType().getMaxDurability();
		float adding = max * add;
		if (adding < 1)
			adding = 1;
		adding = Math.round(adding);
		short dura = (short) (is.getDurability() + adding);
		if (dura < 0)
			dura = 0;
		is.setDurability(dura);
		return is;
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getDamager() instanceof Player) {
			Player d = (Player) e.getDamager();
			if (d.getItemInHand() == null || d.getItemInHand().getType() == Material.AIR || e.getDamage() == 1)
				return;
		}
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			Random r = new Random();
			int dodura = r.nextInt(1500);
			if (p.getInventory().getHelmet() != null && p.getInventory().getHelmet().getType() != Material.AIR) {
				ItemStack is = p.getInventory().getHelmet();
				if (dodura <= is.getType().getMaxDurability()) {
					if (is.getDurability() >= is.getType().getMaxDurability()) {
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
						p.getInventory().setHelmet(null);
						Listeners.hpCheck(p);
					} else {
						is.setDurability((short) (is.getDurability() + 1));
					}
				}
			}
			if (p.getInventory().getChestplate() != null
					&& p.getInventory().getChestplate().getType() != Material.AIR) {
				ItemStack is = p.getInventory().getChestplate();
				if (dodura <= is.getType().getMaxDurability()) {
					if (is.getDurability() >= is.getType().getMaxDurability()) {
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
						p.getInventory().setChestplate(null);
						Listeners.hpCheck(p);
					} else {
						is.setDurability((short) (is.getDurability() + 1));
					}
				}
			}
			if (p.getInventory().getLeggings() != null && p.getInventory().getLeggings().getType() != Material.AIR) {
				ItemStack is = p.getInventory().getLeggings();
				if (dodura <= is.getType().getMaxDurability()) {
					if (is.getDurability() >= is.getType().getMaxDurability()) {
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
						p.getInventory().setLeggings(null);
						Listeners.hpCheck(p);
					} else {
						is.setDurability((short) (is.getDurability() + 1));
					}
				}
			}
			if (p.getInventory().getBoots() != null && p.getInventory().getBoots().getType() != Material.AIR) {
				ItemStack is = p.getInventory().getBoots();
				if (dodura <= is.getType().getMaxDurability()) {
					if (is.getDurability() >= is.getType().getMaxDurability()) {
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
						p.getInventory().setBoots(null);
						Listeners.hpCheck(p);
					} else {
						is.setDurability((short) (is.getDurability() + 1));
					}
				}
			}
		}
	}

	@EventHandler
	public void onDamager(EntityDamageByEntityEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (p.getItemInHand() != null && (p.getItemInHand().getType().name().contains("_AXE")
					|| p.getItemInHand().getType().name().contains("_SWORD")
					|| p.getItemInHand().getType().name().contains("_SPADE"))) {
				Random r = new Random();
				int dodura = r.nextInt(1500);
				if (dodura <= p.getItemInHand().getType().getMaxDurability()) {
					if (p.getItemInHand().getDurability() >= p.getItemInHand().getType().getMaxDurability()) {
						p.setItemInHand(null);
						p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
					} else {
						p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 1));
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDuraUpdate(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (p.getItemInHand() != null && (p.getItemInHand().getType().name().contains("_AXE")
					|| p.getItemInHand().getType().name().contains("_SWORD")
					|| p.getItemInHand().getType().name().contains("_SPADE"))) {
				p.updateInventory();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onScrapUse(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			if (e.getInventory().getHolder() == p)
				if (isArmor(e.getCurrentItem())) {
					ItemStack is = e.getCurrentItem();
					ItemStack scrap = e.getCursor();
					if (isCorrectScrap(scrap, is)) {
						if (is.getDurability() > 0) {
							// if (is.getType().name().contains("_PICKAXE")
							// && ProfessionMechanics.getPickaxeLevel(is) < 100)
							// {
							e.setCancelled(true);
							if (e.getCursor().getAmount() > 1)
								e.getCursor().setAmount(e.getCursor().getAmount() - 1);
							else if (e.getCursor().getAmount() == 1)
								e.setCursor(null);

							float dura = getDuraPercent(is) + 3;
							if (dura > 100)
								dura = 100;
							p.sendMessage("               " + ChatColor.GREEN + ChatColor.BOLD + "+" + ChatColor.GREEN
									+ "3.0% DURABILITY" + ChatColor.GREEN + ChatColor.BOLD + " -> " + ChatColor.GREEN
									+ dura + "% TOTAL");
							e.setCurrentItem(addDura(is, 3));
							if (Orbs.getItemTier(is) == 1)
								p.getWorld().playEffect(p.getLocation().add(0, 2, 0), Effect.STEP_SOUND,
										Material.JUKEBOX);
							if (Orbs.getItemTier(is) == 2)
								p.getWorld().playEffect(p.getLocation().add(0, 2, 0), Effect.STEP_SOUND, Material.WEB);
							if (Orbs.getItemTier(is) == 3)
								p.getWorld().playEffect(p.getLocation().add(0, 2, 0), Effect.STEP_SOUND,
										Material.IRON_BLOCK);
							if (Orbs.getItemTier(is) == 4)
								p.getWorld().playEffect(p.getLocation().add(0, 2, 0), Effect.STEP_SOUND,
										Material.DIAMOND_BLOCK);
							if (Orbs.getItemTier(is) == 5)
								p.getWorld().playEffect(p.getLocation().add(0, 2, 0), Effect.STEP_SOUND,
										Material.GOLD_BLOCK);
						}
					}
				}
		}
	}

	boolean isCorrectScrap(ItemStack scrap, ItemStack armor) {
		if (scrap == null || armor == null)
			return false;
		if (scrap.getType() == Material.AIR || armor.getType() == Material.AIR)
			return false;
		if (!(isArmor(armor)))
			return false;
		if (!(scrap.getItemMeta().hasDisplayName()))
			return false;
		if (!(scrap.getItemMeta().getDisplayName().contains("Armor Scrap")))
			return false;
		int tier = Orbs.getItemTier(armor);
		if (scrap.getType() == Material.LEATHER)
			if (tier == 1)
				return true;
		if (scrap.getType() == Material.IRON_FENCE)
			if (tier == 2)
				return true;
		if (scrap.getType() == Material.INK_SACK) {
			short dura = scrap.getDurability();
			if (tier == 3 && dura == 7)
				return true;
			if (tier == 4 && dura == 12)
				return true;
			if (tier == 5 && dura == 11)
				return true;
		}
		return false;

	}

	public static boolean isArmor(ItemStack is) {
		if (is == null)
			return false;
		if (is.getType() == Material.AIR)
			return false;
		if (is.getType().name().contains("_HELMET") || is.getType().name().contains("_CHESTPLATE")
				|| is.getType().name().contains("_LEGGINGS") || is.getType().name().contains("_BOOTS")
				|| is.getType().name().contains("_SWORD") || is.getType().name().contains("_AXE")
				|| is.getType().name().contains("_HOE") || is.getType().name().contains("_SPADE")
				|| is.getType().name().contains("_PICKAXE"))
			return true;
		return false;
	}

	public static boolean isScrap(ItemStack is) {
		if (is == null)
			return false;
		if (is.getType() == Material.AIR)
			return false;
		if (!(is.getItemMeta().hasDisplayName()))
			return false;
		if (!(is.getItemMeta().getDisplayName().contains("Armor Scrap")))
			return false;
		if (is.getType() == Material.LEATHER)
			return true;
		if (is.getType() == Material.IRON_FENCE)
			return true;
		if (is.getType() == Material.INK_SACK) {
			short dura = is.getDurability();
			if (dura == 7)
				return true;
			if (dura == 12)
				return true;
			if (dura == 11)
				return true;
		}
		return false;
	}
}
