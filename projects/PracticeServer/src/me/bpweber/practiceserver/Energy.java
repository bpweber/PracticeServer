package me.bpweber.practiceserver;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Energy implements Listener {

	public void onEnable() {
		Main.log.info("[Energy] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					PlayerInventory i = p.getInventory();
					float amt = 100;
					for (ItemStack is : i.getArmorContents()) {
						if (is != null && is.getType() != Material.AIR && is.hasItemMeta()
								&& is.getItemMeta().hasLore()) {
							int added = Damage.getEnergy(is);

							int intel = 0;
							int addedint = Damage.getElem(is, "INT");
							intel = intel + addedint;
							if (intel > 0) {
								added += Math.round(intel * .016);
							}
							amt = amt + (added * 5);
						}
					}
					if (getEnergy(p) < 100) {
						if (!Energy.cd.containsKey(p.getName()) || (Energy.cd.containsKey(p.getName())
								&& System.currentTimeMillis() - Energy.cd.get(p.getName()) > 2000))
							setEnergy(p, getEnergy(p) + (amt / 100));
					}
					if (getEnergy(p) <= 0) {
						p.setSprinting(false);
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 1, 1);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.isSprinting()) {
						if (!Alignments.isSafeZone(p.getLocation())) {
							float amt = 100;
							amt = amt + (17 * 5);
							amt *= 4;
							if (getEnergy(p) > 0) {
								setEnergy(p, getEnergy(p) - (amt / 100));
							}
							if (getEnergy(p) <= 0) {
								setEnergy(p, 0);
								Energy.cd.put(p.getName(), System.currentTimeMillis());
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
								p.setSprinting(false);
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 4, 4);
	}

	public void onDisable() {
		Main.log.info("[Energy] has been disabled.");
	}

	public static float getEnergy(Player p) {
		float energy = 0;
		energy = p.getExp() * 100;
		return energy;
	}

	public static void setEnergy(Player p, float energy) {
		if (energy > 100)
			energy = 100;
		p.setExp(energy / 100);
		p.setLevel((int) energy);
	}

	public static void removeEnergy(Player p, int amt) {
		if (Alignments.isSafeZone(p.getLocation()))
			return;
		if (p.hasMetadata("lastenergy")) {
			if ((System.currentTimeMillis() - p.getMetadata("lastenergy").get(0).asLong()) < 100) {
				return;
			}
		}
		p.setMetadata("lastenergy", new FixedMetadataValue(Main.plugin, System.currentTimeMillis()));
		setEnergy(p, getEnergy(p) - amt);
	}

	public static HashMap<String, Long> nodamage = new HashMap<String, Long>();
	public static HashMap<String, Long> cd = new HashMap<String, Long>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEnergyUse(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (nodamage.containsKey(p.getName()) && System.currentTimeMillis() - nodamage.get(p.getName()) < 100) {
				e.setUseItemInHand(Result.DENY);
				e.setCancelled(true);
				return;
			}
			if (getEnergy(p) > 0) {
				int amt = 6;
				/*
				 * if (p.getItemInHand().getType() == Material.WOOD_SWORD ||
				 * p.getItemInHand().getType() == Material.WOOD_SPADE) { amt =
				 * 7; } else if (p.getItemInHand().getType() ==
				 * Material.WOOD_AXE || p.getItemInHand().getType() ==
				 * Material.STONE_SWORD || p.getItemInHand().getType() ==
				 * Material.STONE_SPADE) { amt = 8; } else if
				 * (p.getItemInHand().getType() == Material.STONE_AXE ||
				 * p.getItemInHand().getType() == Material.IRON_SWORD ||
				 * p.getItemInHand().getType() == Material.IRON_SPADE) { amt =
				 * 9; } else if (p.getItemInHand().getType() ==
				 * Material.IRON_AXE || p.getItemInHand().getType() ==
				 * Material.DIAMOND_SWORD || p.getItemInHand().getType() ==
				 * Material.DIAMOND_SPADE) { amt = 10; } else if
				 * (p.getItemInHand().getType() == Material.DIAMOND_AXE ||
				 * p.getItemInHand().getType() == Material.GOLD_SWORD ||
				 * p.getItemInHand().getType() == Material.GOLD_SPADE) { amt =
				 * 11; } else if (p.getItemInHand().getType() ==
				 * Material.GOLD_AXE) { amt = 12; }
				 */
				if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
					amt = 7;
				} else if (p.getItemInHand().getType() == Material.WOOD_AXE
						|| p.getItemInHand().getType() == Material.WOOD_SPADE
						|| p.getItemInHand().getType() == Material.STONE_SWORD) {
					amt = 8;
				} else if (p.getItemInHand().getType() == Material.STONE_AXE
						|| p.getItemInHand().getType() == Material.STONE_SPADE
						|| p.getItemInHand().getType() == Material.IRON_SWORD) {
					amt = 9;
				} else if (p.getItemInHand().getType() == Material.IRON_AXE
						|| p.getItemInHand().getType() == Material.IRON_SPADE
						|| p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
					amt = 10;
				} else if (p.getItemInHand().getType() == Material.DIAMOND_AXE
						|| p.getItemInHand().getType() == Material.DIAMOND_SPADE
						|| p.getItemInHand().getType() == Material.GOLD_SWORD) {
					amt = 11;
				} else if (p.getItemInHand().getType() == Material.GOLD_AXE
						|| p.getItemInHand().getType() == Material.GOLD_SPADE) {
					amt = 12;
				}
				removeEnergy(p, amt);
			}
			if (getEnergy(p) <= 0) {
				setEnergy(p, 0);
				cd.put(p.getName(), System.currentTimeMillis());
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
				p.playSound(p.getLocation(), Sound.WOLF_PANT, 10F, 1.5F);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEnergyUseDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (nodamage.containsKey(p.getName()) && System.currentTimeMillis() - nodamage.get(p.getName()) < 100) {
				e.setCancelled(true);
				e.setDamage(0);
				return;
			}
			nodamage.put(p.getName(), System.currentTimeMillis());
			if (getEnergy(p) > 0) {
				if (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
					p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				int amt = 6;
				if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
					amt = 7;
				} else if (p.getItemInHand().getType() == Material.WOOD_AXE
						|| p.getItemInHand().getType() == Material.STONE_SWORD) {
					amt = 8;
				} else if (p.getItemInHand().getType() == Material.STONE_AXE
						|| p.getItemInHand().getType() == Material.IRON_SWORD) {
					amt = 9;
				} else if (p.getItemInHand().getType() == Material.IRON_AXE
						|| p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
					amt = 10;
				} else if (p.getItemInHand().getType() == Material.DIAMOND_AXE
						|| p.getItemInHand().getType() == Material.GOLD_SWORD) {
					amt = 11;
				} else if (p.getItemInHand().getType() == Material.GOLD_AXE) {
					amt = 12;
				}
				removeEnergy(p, amt);
			}
			if (getEnergy(p) <= 0) {
				setEnergy(p, 0);
				cd.put(p.getName(), System.currentTimeMillis());
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
				p.playSound(p.getLocation(), Sound.WOLF_PANT, 10F, 1.5F);
			}
		}
	}
}
