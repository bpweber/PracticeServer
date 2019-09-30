package me.bpweber.practiceserver;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Mobs implements Listener {

	public static HashMap<LivingEntity, Integer> crit = new HashMap<LivingEntity, Integer>();

	static ConcurrentHashMap<Creature, Player> target = new ConcurrentHashMap<>();

	public void onEnable() {
		Main.log.info("[Mobs] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Entity ent : Bukkit.getWorlds().get(0).getEntities()) {
					if (ent instanceof LivingEntity && !(ent instanceof Player)) {
						LivingEntity l = (LivingEntity) ent;
						if (crit.containsKey(l)) {
							if (isElite(l)) {
								int step = crit.get(l);
								if (step > 0) {
									step--;
									crit.put(l, step);
									l.getWorld().playSound(l.getLocation(), Sound.CREEPER_HISS, 1, 4);
									ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, .3f, 40,
											l.getLocation().add(0, 1, 0), 20);
								}
								if (step == 0) {
									for (Entity e : l.getNearbyEntities(8, 8, 8)) {
										if (e instanceof Player) {
											if (Listeners.mobd.containsKey(l.getUniqueId()))
												Listeners.mobd.remove(l.getUniqueId());
											Player p = (Player) e;
											p.damage(1, l);
											Vector v = p.getLocation().toVector().subtract(l.getLocation().toVector());
											if (!(v.getX() == 0 && v.getY() == 0 && v.getZ() == 0))
												v.normalize();
											p.setVelocity(v.multiply(3));
										}
									}
									l.getWorld().playSound(l.getLocation(), Sound.EXPLODE, 1, .5f);
									ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 1, 40, l.getLocation().add(0, 1, 0),
											20);
									crit.remove(l);
									l.setCustomName(generateOverheadBar(l, l.getHealth(), l.getMaxHealth(),
											getMobTier(l), true));
									l.setCustomNameVisible(true);
									if (l.hasPotionEffect(PotionEffectType.SLOW)) {
										l.removePotionEffect(PotionEffectType.SLOW);
										if (l.getEquipment().getItemInHand() != null
												&& l.getEquipment().getItemInHand().getType().name().contains("_HOE"))
											l.addPotionEffect(
													new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3),
													true);
									}
									if (l.hasPotionEffect(PotionEffectType.JUMP))
										l.removePotionEffect(PotionEffectType.JUMP);
								}
							}
						}
						if (Listeners.named.containsKey(l.getUniqueId())) {
							if (System.currentTimeMillis() - Listeners.named.get(l.getUniqueId()) >= 5 * 1000) {
								Listeners.named.remove(l.getUniqueId());
								String name = "";
								if (l.hasMetadata("name"))
									name = l.getMetadata("name").get(0).asString();
								l.setCustomName(name);
							}
						}

					}
				}
			}
		}.runTaskTimer(Main.plugin, 20, 20);
		new BukkitRunnable() {
			public void run() {
				for (Entity ent : Bukkit.getWorlds().get(0).getEntities()) {
					if (ent instanceof LivingEntity && !(ent instanceof Player)) {
						LivingEntity l = (LivingEntity) ent;
						if (crit.containsKey(l)) {
							if (!isElite(l)) {
								int step = crit.get(l);
								if (step > 0) {
									step--;
									crit.put(l, step);
									l.getWorld().playSound(l.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
								}
								if (step == 0) {
									ParticleEffect.SPELL_WITCH.display(0, 0, 0, .5f, 35, l.getLocation().add(0, 1, 0),
											20);
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(Main.plugin, 20, 10);
		new BukkitRunnable() {
			public void run() {
				for (Entity ent : Bukkit.getWorlds().get(0).getEntities()) {
					if (ent instanceof Creature) {
						Creature c = (Creature) ent;
						if (c.getEquipment().getItemInHand() != null
								&& c.getEquipment().getItemInHand().getType().name().contains("_HOE")) {
							if (isElite(c) && crit.containsKey(c))
								return;
							if (isPlayerNearby(c)) {
								if (c.getTarget() != null) {
									Entity trgt = c.getTarget();
									if (c.getLocation().distanceSquared(trgt.getLocation()) > 9) {
										Projectile pj = null;
										if (getMobTier(c) == 1) {
											pj = c.launchProjectile(Snowball.class);
										}
										if (getMobTier(c) == 2) {
											pj = c.launchProjectile(SmallFireball.class);
										}
										if (getMobTier(c) == 3) {
											pj = c.launchProjectile(EnderPearl.class);
											pj.setVelocity(pj.getVelocity().multiply(1.25));
										}
										if (getMobTier(c) == 4) {
											pj = c.launchProjectile(WitherSkull.class);
										}
										if (getMobTier(c) == 5) {
											pj = c.launchProjectile(LargeFireball.class);
											pj.setVelocity(pj.getVelocity().multiply(2));
										}
									}
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(Main.plugin, 20, 25);
	}

	public void onDisable() {
		Main.log.info("[Mobs] has been disabled.");
	}

	static boolean isPlayerNearby(Creature c) {
		for (Entity ent : c.getNearbyEntities(12, 12, 12)) {
			if (ent != null && ent instanceof Player) {
				if (ent == c.getTarget())
					return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onHit(ProjectileHitEvent e) {
		Projectile pj = e.getEntity();
		if (pj.getShooter() != null && pj.getShooter() instanceof LivingEntity
				&& !(pj.getShooter() instanceof Player)) {
			LivingEntity d = (LivingEntity) pj.getShooter();
			Player target = null;
			for (Entity ent : pj.getNearbyEntities(2, 1.5, 2)) {
				if (ent instanceof Player) {
					target = (Player) ent;
				}
			}
			if (target != null) {
				if (pj instanceof SmallFireball)
					e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 1F, 1F);
				if (pj instanceof EnderPearl)
					e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 2F, 1.50F);
				target.damage(1, d);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntitySpawn(CreatureSpawnEvent e) {
		e.getEntity().getEquipment().clear();
	}

	@EventHandler
	public void onCubeSplit(SlimeSplitEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onCombust(EntityCombustEvent e) {
		if (!(e.getEntity() instanceof Player))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) {
			if (!(e.getCause().equals(DamageCause.ENTITY_ATTACK))) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}

	public static int getMobTier(LivingEntity e) {
		if (e.getEquipment().getItemInHand() != null) {
			if (e.getEquipment().getItemInHand().getType().name().contains("WOOD_"))
				return 1;
			if (e.getEquipment().getItemInHand().getType().name().contains("STONE_"))
				return 2;
			if (e.getEquipment().getItemInHand().getType().name().contains("IRON_"))
				return 3;
			if (e.getEquipment().getItemInHand().getType().name().contains("DIAMOND_"))
				return 4;
			if (e.getEquipment().getItemInHand().getType().name().contains("GOLD_"))
				return 5;
		}
		return 0;
	}

	public static int getPlayerTier(Player e) {
		int tier = 0;
		for (ItemStack is : e.getInventory().getArmorContents()) {
			if (is != null && is.getType() != Material.AIR) {
				if (is.getType().name().contains("LEATHER_"))
					if (1 > tier)
						tier = 1;
				if (is.getType().name().contains("CHAINMAIL_"))
					if (2 > tier)
						tier = 2;
				if (is.getType().name().contains("IRON_"))
					if (3 > tier)
						tier = 3;
				if (is.getType().name().contains("DIAMOND_"))
					if (4 > tier)
						tier = 4;
				if (is.getType().name().contains("GOLD_"))
					if (5 > tier)
						tier = 5;
			}
		}
		return tier;
	}

	public static HashMap<UUID, Long> sound = new HashMap<UUID, Long>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onKnockback(EntityDamageEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity l = (LivingEntity) e.getEntity();
			if (e.getDamage() <= 0)
				return;
			if (!(sound.containsKey(l.getUniqueId())) || (sound.containsKey(l.getUniqueId())
					&& System.currentTimeMillis() - sound.get(l.getUniqueId()) > 500)) {
				sound.put(l.getUniqueId(), System.currentTimeMillis());
				if (e.getEntity() instanceof Skeleton) {
					if (e.getDamage() >= l.getHealth())
						l.getWorld().playSound(l.getLocation(), Sound.SKELETON_DEATH, 1, 1);
					l.getWorld().playSound(l.getLocation(), Sound.SKELETON_HURT, 1, 1);
				}
				if (e.getEntity() instanceof Zombie) {
					if (e.getDamage() >= l.getHealth())
						l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_DEATH, 1, 1);
					l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_HURT, 1, 1);
				}
				if (e.getEntity() instanceof Spider || e.getEntity() instanceof CaveSpider) {
					if (e.getDamage() >= l.getHealth())
						l.getWorld().playSound(l.getLocation(), Sound.SPIDER_DEATH, 1, 1);
				}
				if (e.getEntity() instanceof PigZombie) {
					if (e.getDamage() >= l.getHealth())
						l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_PIG_DEATH, 1, 1);
					l.getWorld().playSound(l.getLocation(), Sound.ZOMBIE_PIG_HURT, 1, 1);
				}
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent e) {
		if (e.getReason() == TargetReason.CLOSEST_PLAYER) {
			if (e.getTarget() instanceof Player && e.getEntity() instanceof Creature) {
				Creature l = (Creature) e.getEntity();
				Player p = (Player) e.getTarget();
				if (l.getLocation().distance(p.getLocation()) > 15) {
					e.setCancelled(true);
					e.setTarget(null);
					return;
				}
				if (p.hasMetadata("NPC") || p.getPlayerListName() == " ") {
					e.setCancelled(true);
					e.setTarget(null);
					return;
				}
				if ((getPlayerTier(p) - getMobTier(l)) > 2) {
					e.setCancelled(true);
					e.setTarget(null);
					return;
				}
				if (l.hasPotionEffect(PotionEffectType.SLOW)) {
					l.removePotionEffect(PotionEffectType.SLOW);
					if (l.getEquipment().getItemInHand() != null
							&& l.getEquipment().getItemInHand().getType().name().contains("_HOE"))
						l.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2), true);
				}
				if (l.hasPotionEffect(PotionEffectType.JUMP))
					l.removePotionEffect(PotionEffectType.JUMP);
				target.put(l, p);
				return;
			}
		}
		e.setTarget(null);
		e.setCancelled(true);
	}

	@EventHandler
	public void onEntityTargetLastHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Creature && e.getDamager() instanceof Player) {
			Creature c = (Creature) e.getEntity();
			Player p = (Player) e.getDamager();
			if (target.containsKey(c) && target.get(c) != null) {
				if (p.getLocation().distanceSquared(c.getLocation()) < target.get(c).getLocation()
						.distanceSquared(c.getLocation())) {
					c.setTarget(p);
					target.put(c, p);
				}
			} else {
				c.setTarget(p);
				target.put(c, p);
			}
		}
	}

	public static boolean isElite(LivingEntity e) {
		if (e.getEquipment().getItemInHand() != null && e.getEquipment().getItemInHand().getType() != Material.AIR) {
			if (e.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
				return true;
			}
		}
		return false;
	}

	public static int getBarLength(int tier) {
		if (tier == 1) {
			return 25;
		}
		if (tier == 2) {
			return 30;
		}
		if (tier == 3) {
			return 35;
		}
		if (tier == 4) {
			return 40;
		}
		if (tier == 5) {
			return 50;
		}
		return 25;
	}

	public static String generateOverheadBar(Entity ent, double cur_hp, double max_hp, int tier, boolean elite) {
		int max_bar = getBarLength(tier);

		ChatColor cc = null;

		DecimalFormatSymbols HpDot = new DecimalFormatSymbols(Locale.GERMAN);
		HpDot.setDecimalSeparator('.');
		HpDot.setGroupingSeparator(',');
		DecimalFormat df = new DecimalFormat("##.#", HpDot);
		double percent_hp = (double) (Math.round(100.0D * Double.parseDouble((df.format((cur_hp / max_hp)))))); // EX:
																												// 0.5054134131

		if (percent_hp <= 0 && cur_hp > 0) {
			percent_hp = 1;
		}
		double percent_interval = (100.0D / max_bar);
		int bar_count = 0;

		cc = ChatColor.GREEN;
		if (percent_hp <= 45) {
			cc = ChatColor.YELLOW;
		}
		if (percent_hp <= 20) {
			cc = ChatColor.RED;
		}
		if (crit.containsKey(ent) && cur_hp > 0) {
			cc = ChatColor.LIGHT_PURPLE;
		}

		String return_string = cc + ChatColor.BOLD.toString() + "║" + ChatColor.RESET.toString() + cc.toString() + "";
		if (elite) {
			return_string += ChatColor.BOLD.toString();
		}

		while (percent_hp > 0 && bar_count < max_bar) {
			percent_hp -= percent_interval;
			bar_count++;
			return_string += "|";
		}

		return_string += ChatColor.BLACK.toString();

		if (elite) {
			return_string += ChatColor.BOLD.toString();
		}

		while (bar_count < max_bar) {
			return_string += "|";
			bar_count++;
		}

		return_string = return_string + cc + ChatColor.BOLD.toString() + "║";
		return return_string;

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onMobDeath(EntityDamageEvent e) {
		if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)) {
			LivingEntity s = (LivingEntity) e.getEntity();
			if (e.getDamage() >= s.getHealth()) {
				if (crit.containsKey(s)) {
					crit.remove(s);
					String mname = "";
					if (s.getEquipment().getItemInHand() != null
							&& s.getEquipment().getItemInHand().getType() != Material.AIR) {
						mname = generateOverheadBar(s, 0, s.getMaxHealth(), getMobTier(s), isElite(s));
						s.setCustomName(mname);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCrit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)
				&& e.getDamager() instanceof Player) {
			if (e.getDamage() <= 0)
				return;
			LivingEntity s = (LivingEntity) e.getEntity();
			Random random = new Random();
			int rcrt = random.nextInt((100 - 1) + 1) + 1;
			if (!crit.containsKey(s)) {
				if ((getMobTier(s) == 1 && rcrt <= 5) || (getMobTier(s) == 2 && rcrt <= 7)
						|| (getMobTier(s) == 3 && rcrt <= 10) || (getMobTier(s) == 4 && rcrt <= 13)
						|| (getMobTier(s) == 5 && rcrt <= 20)) {
					crit.put(s, 4);
					if (isElite(s)) {
						s.getWorld().playSound(s.getLocation(), Sound.CREEPER_HISS, 1, 4);
						double max = s.getMaxHealth();
						double hp = s.getHealth() - e.getDamage();
						s.setCustomName(generateOverheadBar(s, hp, max, getMobTier(s), isElite(s)));
						s.setCustomNameVisible(true);
						Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
						s.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10), true);
						s.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 127), true);
					} else {
						s.getWorld().playSound(s.getLocation(), Sound.PISTON_EXTEND, 1F, 2.0F);
						double max = s.getMaxHealth();
						double hp = s.getHealth() - e.getDamage();
						s.setCustomName(generateOverheadBar(s, hp, max, getMobTier(s), isElite(s)));
						s.setCustomNameVisible(true);
						Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
					}
				}
			}
		}
	}

	@EventHandler
	public void onSafeSpot(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof LivingEntity && !(e.getEntity() instanceof Player)
				&& e.getDamager() instanceof Player) {
			if (e.getDamage() <= 0)
				return;
			LivingEntity s = (LivingEntity) e.getEntity();
			Player p = (Player) e.getDamager();
			Random random = new Random();
			int rcrt = random.nextInt((10 - 1) + 1) + 1;
			if (rcrt == 1) {
				if (p.getLocation().getY() - s.getLocation().getY() > 1) {
					if (p.getLocation().distance(s.getLocation()) <= 6) {
						s.teleport(p.getLocation().add(0, .25, 0));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onMobHitMob(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player)
				&& !(e.getEntity() instanceof Player)) {
			e.setCancelled(true);
			e.setDamage(0);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onMobHit(EntityDamageByEntityEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getDamager() instanceof LivingEntity && !(e.getDamager() instanceof Player)
				&& e.getEntity() instanceof Player) {
			LivingEntity s = (LivingEntity) e.getDamager();
			Player p = (Player) e.getEntity();
			Random random = new Random();
			int dmg = 1;
			if (s.getEquipment().getItemInHand() != null
					&& s.getEquipment().getItemInHand().getType() != Material.AIR) {
				int min = Damage.getDamageRange(s.getEquipment().getItemInHand()).get(0);
				int max = Damage.getDamageRange(s.getEquipment().getItemInHand()).get(1);
				dmg = random.nextInt((max - min) + 1) + min;
			}
			if (crit.containsKey(s) && crit.get(s) == 0) {
				if (isElite(s)) {
					dmg = dmg * 4;
				} else {
					dmg = dmg * 3;
				}
				if (!isElite(s))
					crit.remove(s);
				p.playSound(p.getLocation(), Sound.EXPLODE, 1, .3f);
				double max = s.getMaxHealth();
				double hp = s.getHealth() - e.getDamage();
				s.setCustomName(generateOverheadBar(s, hp, max, getMobTier(s), isElite(s)));
				s.setCustomNameVisible(true);
				Listeners.named.put(s.getUniqueId(), System.currentTimeMillis());
			}
			if (e.getDamage() <= 0)
				return;
			if (s.getEquipment().getItemInHand().getType().name().contains("WOOD_")) {
				if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
					dmg = (int) (dmg * 2.5);
				} else {
					dmg = (int) (dmg * .8);
				}
			}
			if (s.getEquipment().getItemInHand().getType().name().contains("STONE_")) {
				if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
					dmg = (int) (dmg * 2.5);
				} else {
					dmg = (int) (dmg * .9);
				}
			}
			if (s.getEquipment().getItemInHand().getType().name().contains("IRON_")) {
				if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
					dmg = (int) (dmg * 3);
				} else {
					dmg = (int) (dmg * 1.2);
				}
			}
			if (s.getEquipment().getItemInHand().getType().name().contains("DIAMOND_")) {
				if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
					dmg = (int) (dmg * 5);
				} else {
					dmg = (int) (dmg * 1.4);
				}
			}
			if (s.getEquipment().getItemInHand().getType().name().contains("GOLD_")) {
				if (s.getEquipment().getItemInHand().getItemMeta().hasEnchants()) {
					dmg = (int) (dmg * 7);
				} else {
					dmg = (int) (dmg * 2);
				}
			}
			if (s instanceof MagmaCube)
				dmg *= .5;
			if (dmg < 1)
				dmg = 1;
			e.setDamage(dmg);
		}
	}
}
