package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.confuser.barapi.BarAPI;

public class Damage implements Listener {

	HashMap<Player, Long> playerslow = new HashMap<Player, Long>();

	public static HashMap<Player, Long> lasthit = new HashMap<Player, Long>();
	public static HashMap<Player, Player> lastphit = new HashMap<Player, Player>();

	public void onEnable() {
		Main.log.info("[Damage] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					float pcnt = (float) (p.getHealth() / p.getMaxHealth() * 100);
					BarAPI.setMessage(p,
							"" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "HP " + ChatColor.LIGHT_PURPLE
									+ (int) p.getHealth() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " / "
									+ ChatColor.LIGHT_PURPLE + (int) p.getMaxHealth(),
							pcnt);
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 1, 1);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (playerslow.containsKey(p)) {
						if (System.currentTimeMillis() - playerslow.get(p) > 3000)
							p.setWalkSpeed(.2f);
					} else {
						if (p.getWalkSpeed() != .2f)
							p.setWalkSpeed(.2f);
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 20, 20);
	}

	public void onDisable() {
		Main.log.info("[Damage] has been disabled.");
	}

	public static int getHp(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 1) {
				if (lore.get(1).contains("HP")) {
					try {
						return Integer.parseInt(lore.get(1).split(": +")[1]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static int getArmor(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 0) {
				if (lore.get(0).contains("ARMOR")) {
					try {
						return Integer.parseInt(lore.get(0).split(" - ")[1].split("%")[0]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static int getDps(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 0) {
				if (lore.get(0).contains("DPS")) {
					try {
						return Integer.parseInt(lore.get(0).split(" - ")[1].split("%")[0]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static int getEnergy(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 2) {
				if (lore.get(2).contains("ENERGY REGEN")) {
					try {
						return Integer.parseInt(lore.get(2).split(": +")[1].split("%")[0]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static int getHps(ItemStack is) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 2) {
				if (lore.get(2).contains("HP REGEN")) {
					try {
						return Integer.parseInt(lore.get(2).split(": +")[1].split(" HP/s")[0]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static int getPercent(ItemStack is, String type) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			for (String s : lore) {
				if (s.contains(type)) {
					try {
						return Integer.parseInt(s.split(": ")[1].split("%")[0]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static int getElem(ItemStack is, String type) {
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			for (String s : lore) {
				if (s.contains(type)) {
					try {
						return Integer.parseInt(s.split(": +")[1]);
					} catch (Exception e) {
						return 0;
					}
				}
			}
		}
		return 0;
	}

	public static List<Integer> getDamageRange(ItemStack is) {
		List<Integer> dmg = new ArrayList<Integer>();
		dmg.add(1);
		dmg.add(1);
		if (is != null && is.getType() != Material.AIR && is.getItemMeta().hasLore()) {
			List<String> lore = is.getItemMeta().getLore();
			if (lore.size() > 0) {
				if (lore.get(0).contains("DMG")) {
					try {
						int min = 1;
						int max = 1;
						min = Integer.parseInt(lore.get(0).split("DMG: ")[1].split(" - ")[0]);
						max = Integer.parseInt(lore.get(0).split(" - ")[1]);
						dmg.set(0, min);
						dmg.set(1, max);
					} catch (Exception e) {
						dmg.set(0, 1);
						dmg.set(1, 1);
					}
				}
			}
		}
		return dmg;
	}

	public static int getCrit(Player p) {
		int crit = 0;
		ItemStack wep = p.getItemInHand();
		if (Staffs.staff.containsKey(p))
			wep = Staffs.staff.get(p);
		if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasLore()) {
			List<String> lore = wep.getItemMeta().getLore();
			for (String line : lore) {
				if (line.contains("CRITICAL HIT")) {
					crit = getPercent(wep, "CRITICAL HIT");
				}
			}
			if (wep.getType().name().contains("_AXE"))
				crit = crit + 3;
			int intel = 0;
			for (ItemStack is : p.getInventory().getArmorContents()) {
				if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
					int addint = getElem(is, "INT");
					intel = intel + addint;
				}
			}
			if (intel > 0) {
				crit += Math.round(intel * .014);
			}
		}
		return crit;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNpcDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (p.hasMetadata("NPC") || p.getPlayerListName().equals("")) {
				e.setCancelled(true);
				e.setDamage(0);
			}
			if (p.isOp() || p.getGameMode() == GameMode.CREATIVE || p.isFlying()) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onNoOpDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (p.isOp() || p.getGameMode() == GameMode.CREATIVE || p.isFlying()) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlodge(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDamage() <= 0)
				return;
			Player p = (Player) e.getEntity();
			PlayerInventory i = p.getInventory();
			int block = 0;
			int dodge = 0;
			if (p.getHealth() > 0) {
				for (ItemStack is : i.getArmorContents()) {
					if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
						int addedblock = getPercent(is, "BLOCK");
						block = block + addedblock;
						int addeddodge = getPercent(is, "DODGE");
						dodge = dodge + addeddodge;
					}
				}
				int str = 0;
				for (ItemStack is : p.getInventory().getArmorContents()) {
					if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
						int addstr = getElem(is, "STR");
						str = str + addstr;
					}
				}
				if (str > 0) {
					block += Math.round(str * .015);
				}
				// if (block > 40)
				// block = 40;
				// if (dodge > 40)
				// dodge = 40;
				Random random = new Random();
				int dodger = random.nextInt((100 - 1) + 1) + 1;
				int blockr = random.nextInt((100 - 1) + 1) + 1;
				if (e.getDamager() instanceof Player) {
					Player d = (Player) e.getDamager();
					ItemStack wep = d.getItemInHand();
					if (Staffs.staff.containsKey(d))
						wep = Staffs.staff.get(d);
					int accuracy = getPercent(wep, "ACCURACY");
					if (accuracy > 0) {
						int b4block = block;
						int b4dodge = dodge;
						if (accuracy > 0) {
							block = block - accuracy;
							accuracy = accuracy - b4block;
						}
						if (accuracy > 0) {
							dodge = dodge - accuracy;
							accuracy = accuracy - b4dodge;
						}
					}
					if (blockr <= block) {
						e.setDamage(0);
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1, 1);
						d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT BLOCKED* ("
								+ p.getName() + ")");
						p.sendMessage(
								"          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + d.getName() + ")");
					} else if (dodger <= dodge) {
						e.setDamage(0);
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 1, 1);
						d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT DODGED* ("
								+ p.getName() + ")");
						p.sendMessage(
								"          " + ChatColor.GREEN + ChatColor.BOLD + "*DODGE* (" + d.getName() + ")");
					} else if (blockr <= 80 && p.isBlocking()) {
						e.setDamage((int) e.getDamage() / 2);
						p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1, 1);
						d.sendMessage("          " + ChatColor.RED + ChatColor.BOLD + "*OPPONENT BLOCKED* ("
								+ p.getName() + ")");
						p.sendMessage(
								"          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + d.getName() + ")");
					}
				} else if (e.getDamager() instanceof LivingEntity) {
					LivingEntity li = (LivingEntity) e.getDamager();
					String mname = "";
					if (li.hasMetadata("name"))
						mname = li.getMetadata("name").get(0).asString();
					if (blockr <= block) {
						e.setDamage(0);
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1, 1);
						p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + mname
								+ ChatColor.DARK_GREEN + ")");
					} else if (dodger <= dodge) {
						e.setDamage(0);
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 1, 1);
						p.sendMessage("          " + ChatColor.GREEN + ChatColor.BOLD + "*DODGE* (" + mname
								+ ChatColor.GREEN + ")");
					} else if (blockr <= 80 && p.isBlocking()) {
						e.setDamage((int) e.getDamage() / 2);
						p.playSound(p.getLocation(), Sound.ZOMBIE_METAL, 1, 1);
						p.sendMessage("          " + ChatColor.DARK_GREEN + ChatColor.BOLD + "*BLOCK* (" + mname
								+ ChatColor.DARK_GREEN + ")");
					}
				}
			}
		}
		if (e.getDamage() <= 0)
			return;
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			LivingEntity li = (LivingEntity) e.getEntity();
			ItemStack wep = p.getItemInHand();
			if (Staffs.staff.containsKey(p))
				wep = Staffs.staff.get(p);
			if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasLore()) {

				int min = getDamageRange(wep).get(0);
				int max = getDamageRange(wep).get(1);
				Random random = new Random();
				int dmg = random.nextInt((max - min) + 1) + min;

				int tier = Merchant.getTier(wep);

				List<String> lore = wep.getItemMeta().getLore();
				for (String line : lore) {
					if (line.contains("ICE DMG")) {
						li.getWorld().playEffect(li.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8194);
						int eldmg = getElem(wep, "ICE DMG");
						dmg = dmg + eldmg;
						if (tier == 1)
							li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0));
						if (tier == 2)
							li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0));
						if (tier == 3)
							li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
						if (tier == 4)
							li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
						if (tier == 5)
							li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
					}
					if (line.contains("POISON DMG")) {
						li.getWorld().playEffect(li.getLocation().add(0, 1.3, 0), Effect.POTION_BREAK, 8196);
						int eldmg = getElem(wep, "POISON DMG");
						dmg = dmg + eldmg;
						if (tier == 1)
							li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 15, 0));
						if (tier == 2)
							li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 25, 0));
						if (tier == 3)
							li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 1));
						if (tier == 4)
							li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 35, 1));
						if (tier == 5)
							li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
					}
					if (line.contains("FIRE DMG")) {
						int eldmg = getElem(wep, "FIRE DMG");
						dmg = dmg + eldmg;
						if (tier == 1)
							li.setFireTicks(15);
						if (tier == 2)
							li.setFireTicks(25);
						if (tier == 3)
							li.setFireTicks(30);
						if (tier == 4)
							li.setFireTicks(35);
						if (tier == 5)
							li.setFireTicks(40);
					}
					if (line.contains("PURE DMG")) {
						int eldmg = getElem(wep, "PURE DMG");
						dmg = dmg + eldmg;
					}
				}
				int crit = getCrit(p);
				int drop = random.nextInt((100 - 1) + 1) + 1;
				if (drop <= crit) {
					dmg = dmg * 2;
					p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.5f, 0.5f);
					ParticleEffect.CRIT_MAGIC.display(0, 0, 0, 1, 50, li.getLocation(), 20);
				}
				PlayerInventory i = p.getInventory();
				double dps = 0;
				double vit = 0;
				double str = 0;
				for (ItemStack is : i.getArmorContents()) {
					if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
						int adddps = getDps(is);
						dps = dps + adddps;
						int addvit = getElem(is, "VIT");
						vit = vit + addvit;
						int addstr = getElem(is, "STR");
						str = str + addstr;
					}
				}
				if (vit > 0 && wep.getType().name().contains("_SWORD")) {
					double divide = vit / 5000;
					double pre = dmg * divide;
					int cleaned = (int) (dmg + pre);
					dmg = cleaned;
				}
				if (str > 0 && wep.getType().name().contains("_AXE")) {
					double divide = str / 3800;
					double pre = dmg * divide;
					int cleaned = (int) (dmg + pre);
					dmg = cleaned;
				}
				if (dps > 0) {
					double divide = dps / 100;
					double pre = dmg * divide;
					int cleaned = (int) (dmg + pre);
					dmg = cleaned;
				}
				for (String line : lore) {
					if (line.contains("LIFE STEAL")) {
						li.getWorld().playEffect(li.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
						double base = getPercent(wep, "LIFE STEAL");
						double pcnt = base / 100;
						int life = 1;
						if ((int) (pcnt * dmg) > 0) {
							life = (int) (pcnt * dmg);
						}
						if (p.getHealth() < p.getMaxHealth() - life) {
							p.setHealth(p.getHealth() + life);
							ArrayList<String> toggles = Toggles.getToggles(p.getName());
							if (toggles.contains("debug"))
								p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "            +" + ChatColor.GREEN
										+ life + ChatColor.GREEN + ChatColor.BOLD + " HP " + ChatColor.GRAY + "["
										+ (int) p.getHealth() + "/" + (int) p.getMaxHealth() + "HP]");
						} else if (p.getHealth() >= p.getMaxHealth() - life) {
							p.setHealth(p.getMaxHealth());
							ArrayList<String> toggles = Toggles.getToggles(p.getName());
							if (toggles.contains("debug"))
								p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "            +" + ChatColor.GREEN
										+ life + ChatColor.GREEN + ChatColor.BOLD + " HP " + ChatColor.GRAY + "["
										+ (int) p.getMaxHealth() + "/" + (int) p.getMaxHealth() + "HP]");
						}
					}
				}
				e.setDamage(dmg);
				return;
			}
			e.setDamage(1);
		}
	}
	/*
	 * @EventHandler(priority = EventPriority.LOW) public void
	 * onPlayerDamage(EntityDamageByEntityEvent e) { if (e.getDamage() <= 0)
	 * return; if (e.getDamager() instanceof Player && e.getEntity() instanceof
	 * LivingEntity) { Player p = (Player) e.getDamager(); LivingEntity li =
	 * (LivingEntity) e.getEntity(); ItemStack wep = p.getItemInHand(); if
	 * (Staffs.staff.containsKey(p)) wep = Staffs.staff.get(p); if (wep != null
	 * && wep.getType() != Material.AIR && wep.getItemMeta().hasLore()) {
	 * 
	 * int min = getDamageRange(wep).get(0); int max =
	 * getDamageRange(wep).get(1); Random random = new Random(); int dmg =
	 * random.nextInt((max - min) + 1) + min;
	 * 
	 * int tier = Merchant.getTier(wep);
	 * 
	 * List<String> lore = wep.getItemMeta().getLore(); for (String line : lore)
	 * { if (line.contains("ICE DMG")) {
	 * li.getWorld().playEffect(li.getLocation().add(0, 1.3, 0),
	 * Effect.POTION_BREAK, 8194); int eldmg = getElem(wep, "ICE DMG"); dmg =
	 * dmg + eldmg; if (tier == 1) li.addPotionEffect(new
	 * PotionEffect(PotionEffectType.SLOW, 40, 0)); if (tier == 2)
	 * li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 0)); if
	 * (tier == 3) li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
	 * 40, 1)); if (tier == 4) li.addPotionEffect(new
	 * PotionEffect(PotionEffectType.SLOW, 50, 1)); if (tier == 5)
	 * li.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1)); } if
	 * (line.contains("POISON DMG")) {
	 * li.getWorld().playEffect(li.getLocation().add(0, 1.3, 0),
	 * Effect.POTION_BREAK, 8196); int eldmg = getElem(wep, "POISON DMG"); dmg =
	 * dmg + eldmg; if (tier == 1) li.addPotionEffect(new
	 * PotionEffect(PotionEffectType.POISON, 40, 0)); if (tier == 2)
	 * li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 50, 0)); if
	 * (tier == 3) li.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
	 * 60, 1)); if (tier == 4) li.addPotionEffect(new
	 * PotionEffect(PotionEffectType.POISON, 70, 1)); if (tier == 5)
	 * li.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1)); }
	 * if (line.contains("FIRE DMG")) { int eldmg = getElem(wep, "FIRE DMG");
	 * dmg = dmg + eldmg; if (tier == 1) li.setFireTicks(15); if (tier == 2)
	 * li.setFireTicks(25); if (tier == 3) li.setFireTicks(30); if (tier == 4)
	 * li.setFireTicks(35); if (tier == 5) li.setFireTicks(40); } if
	 * (line.contains("PURE DMG")) { int eldmg = getElem(wep, "PURE DMG"); dmg =
	 * dmg + eldmg; } } int crit = getCrit(p); int drop = random.nextInt((100 -
	 * 1) + 1) + 1; if (drop <= crit) { dmg = dmg * 2;
	 * p.playSound(p.getLocation(), Sound.WOOD_CLICK, 1.5f, 0.5f);
	 * ParticleEffect.CRIT_MAGIC.display(0, 0, 0, 1, 50, li.getLocation(), 20);
	 * } PlayerInventory i = p.getInventory(); double dps = 0; double vit = 0;
	 * double str = 0; for (ItemStack is : i.getArmorContents()) { if (is !=
	 * null && is.getType() != Material.AIR && is.hasItemMeta() &&
	 * is.getItemMeta().hasLore()) { int adddps = getDps(is); dps = dps +
	 * adddps; int addvit = getElem(is, "VIT"); vit = vit + addvit; int addstr =
	 * getElem(is, "STR"); str = str + addstr; } } if (vit > 0 &&
	 * wep.getType().name().contains("_SWORD")) { double divide = vit / 5000;
	 * double pre = dmg * divide; int cleaned = (int) (dmg + pre); dmg =
	 * cleaned; } if (str > 0 && wep.getType().name().contains("_AXE")) { double
	 * divide = str / 5000; double pre = dmg * divide; int cleaned = (int) (dmg
	 * + pre); dmg = cleaned; } if (dps > 0) { double divide = dps / 100; double
	 * pre = dmg * divide; int cleaned = (int) (dmg + pre); dmg = cleaned; }
	 * 
	 * for (String line : lore) { if (line.contains("LIFE STEAL")) {
	 * li.getWorld().playEffect(li.getEyeLocation(), Effect.STEP_SOUND,
	 * Material.REDSTONE_BLOCK); double base = getPercent(wep, "LIFE STEAL");
	 * double pcnt = base / 100; int life = 1; if ((int) (pcnt * dmg) > 0) {
	 * life = (int) (pcnt * dmg); } if (p.getHealth() < p.getMaxHealth() - life)
	 * { p.setHealth(p.getHealth() + life); ArrayList<String> toggles =
	 * Toggles.getToggles(p.getName()); if (toggles.contains("debug"))
	 * p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "            +" +
	 * ChatColor.GREEN + life + ChatColor.GREEN + ChatColor.BOLD + " HP " +
	 * ChatColor.GRAY + "[" + (int) p.getHealth() + "/" + (int) p.getMaxHealth()
	 * + "HP]"); } else if (p.getHealth() >= p.getMaxHealth() - life) {
	 * p.setHealth(p.getMaxHealth()); ArrayList<String> toggles =
	 * Toggles.getToggles(p.getName()); if (toggles.contains("debug"))
	 * p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "            +" +
	 * ChatColor.GREEN + life + ChatColor.GREEN + ChatColor.BOLD + " HP " +
	 * ChatColor.GRAY + "[" + (int) p.getMaxHealth() + "/" + (int)
	 * p.getMaxHealth() + "HP]"); } } } e.setDamage(dmg); return; }
	 * e.setDamage(1); } }
	 */

	@EventHandler(priority = EventPriority.NORMAL)
	public void onArmor(EntityDamageEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			PlayerInventory i = p.getInventory();
			double dmg = e.getDamage();
			double arm = 0;
			for (ItemStack is : i.getArmorContents()) {
				if (is != null && is.getType() != Material.AIR && is.hasItemMeta() && is.getItemMeta().hasLore()) {
					int addarm = getArmor(is);
					arm = arm + addarm;
				}
			}
			if (arm > 0) {
				double divide = arm / 100;
				double pre = dmg * divide;
				int cleaned = (int) (dmg - pre);
				if (cleaned <= 1)
					cleaned = 1;
				dmg = cleaned;
				int health = 0;
				if (p.getHealth() - cleaned > 0) {
					health = (int) (p.getHealth() - cleaned);
				}
				ArrayList<String> toggles = Toggles.getToggles(p.getName());
				if (toggles.contains("debug")) {
					if (health < 0)
						health = 0;
					p.sendMessage(ChatColor.RED + "            -" + cleaned + ChatColor.RED + ChatColor.BOLD + "HP "
							+ ChatColor.GRAY + "[-" + (int) arm + "%A -> -" + (int) pre + ChatColor.BOLD + "DMG"
							+ ChatColor.GRAY + "] " + ChatColor.GREEN + "[" + health + ChatColor.BOLD + "HP"
							+ ChatColor.GREEN + "]");
				}
				e.setDamage(cleaned);
			} else {
				ArrayList<String> toggles = Toggles.getToggles(p.getName());
				if (toggles.contains("debug")) {
					int health = (int) (p.getHealth() - dmg);
					if (health < 0)
						health = 0;
					p.sendMessage(ChatColor.RED + "            -" + (int) dmg + ChatColor.RED + ChatColor.BOLD + "HP "
							+ ChatColor.GRAY + "[-0%A -> -0" + ChatColor.BOLD + "DMG" + ChatColor.GRAY + "] "
							+ ChatColor.GREEN + "[" + health + ChatColor.BOLD + "HP" + ChatColor.GREEN + "]");
				}
				e.setDamage(dmg);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDebug(EntityDamageByEntityEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player d = (Player) e.getDamager();
			int dmg = (int) e.getDamage();
			ArrayList<String> toggles = Toggles.getToggles(d.getName());
			if (toggles.contains("debug"))
				d.sendMessage(ChatColor.RED + "            " + dmg + ChatColor.RED + ChatColor.BOLD + " DMG "
						+ ChatColor.RED + "-> " + p.getName());
			lastphit.put(p, d);
			lasthit.put(p, System.currentTimeMillis());
		} else if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
			LivingEntity p = (LivingEntity) e.getEntity();
			Player d = (Player) e.getDamager();
			int dmg = (int) e.getDamage();
			int health = 0;
			if (p.getHealth() - dmg > 0) {
				health = (int) (p.getHealth() - dmg);
			}
			String name = "";
			if (p.hasMetadata("name"))
				name = p.getMetadata("name").get(0).asString();
			ArrayList<String> toggles = Toggles.getToggles(d.getName());
			if (toggles.contains("debug"))
				d.sendMessage(ChatColor.RED + "            " + dmg + ChatColor.RED + ChatColor.BOLD + " DMG "
						+ ChatColor.RED + "-> " + ChatColor.RESET + name + " [" + health + "HP]");
		}
	}

	HashMap<UUID, Long> kb = new HashMap<UUID, Long>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onKnockback(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
			LivingEntity p = (LivingEntity) e.getEntity();
			LivingEntity d = (LivingEntity) e.getDamager();
			if (e.getDamage() <= 0)
				return;
			if (p instanceof Player) {
				Vector v = p.getLocation().toVector().subtract(d.getLocation().toVector());
				if (!(v.getX() == 0 && v.getY() == 0 && v.getZ() == 0))
					v.normalize();
				p.setVelocity(v.multiply(0.15F));
			} else {
				if (!(kb.containsKey(p.getUniqueId())) || (kb.containsKey(p.getUniqueId())
						&& System.currentTimeMillis() - kb.get(p.getUniqueId()) > 500)) {
					kb.put(p.getUniqueId(), System.currentTimeMillis());
					Vector v = p.getLocation().toVector().subtract(d.getLocation().toVector());
					if (!(v.getX() == 0 && v.getY() == 0 && v.getZ() == 0))
						v.normalize();
					if (d instanceof Player) {
						Player dam = (Player) d;
						if (dam.getItemInHand() != null && dam.getItemInHand().getType().name().contains("_SPADE")) {
							p.setVelocity(v.multiply(1.25F).setY(.4));
						} else {
							p.setVelocity(v.multiply(0.5F).setY(.35));
						}
					} else {
						p.setVelocity(v.multiply(0.5F).setY(.35));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDamageEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity s = (LivingEntity) e.getEntity();
			if (e.getDamage() >= s.getHealth()) {
				if (kb.containsKey(s.getUniqueId()))
					kb.remove(s.getUniqueId());
			}
		}
	}

	ArrayList<String> p_arm = new ArrayList<String>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onPolearmAOE(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {
			LivingEntity p = (LivingEntity) e.getEntity();
			Player d = (Player) e.getDamager();
			if (e.getDamage() <= 0)
				return;
			if (d.getItemInHand() != null && d.getItemInHand().getType().name().contains("_SPADE")
					&& !p_arm.contains(d.getName())) {
				for (Entity near : p.getNearbyEntities(2.5, 3, 2.5)) {
					if (near instanceof LivingEntity) {
						if (near != p && near != d) {
							if (near != null) {
								LivingEntity n = (LivingEntity) near;
								if (Energy.nodamage.containsKey(d.getName()))
									Energy.nodamage.remove(d.getName());
								p_arm.add(d.getName());
								n.damage(1, d);
								p_arm.remove(d.getName());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDamageSound(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			if (e.getDamage() <= 0)
				return;
			Player p = (Player) e.getDamager();
			p.playSound(p.getLocation(), Sound.HURT_FLESH, 1, 1);
			if (e.getEntity() instanceof Player)
				e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.WOOD_CLICK, 1, 1.6f);
		}
		if (e.getEntity() instanceof Player && !(e.getDamager() instanceof Player)
				&& e.getDamager() instanceof LivingEntity) {
			Player p = (Player) e.getEntity();
			p.setWalkSpeed(.165f);
			playerslow.put(p, System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBypassArmor(EntityDamageEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity li = (LivingEntity) e.getEntity();
			if (e.getDamage() <= 0)
				return;
			int dmg = (int) e.getDamage();
			e.setDamage(0);
			e.setCancelled(true);
			li.playEffect(EntityEffect.HURT);
			li.setLastDamageCause(e);
			if (li.getHealth() - dmg <= 0) {
				li.setHealth(0);
			} else {
				li.setHealth(li.getHealth() - dmg);
			}
		}
	}
}
