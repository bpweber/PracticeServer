package me.bpweber.practiceserver;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Staffs implements Listener {

	HashMap<Projectile, ItemStack> shots = new HashMap<Projectile, ItemStack>();
	public static HashMap<Player, ItemStack> staff = new HashMap<Player, ItemStack>();

	public void onEnable() {
		Main.log.info("[Staffs] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	public void onDisable() {
		Main.log.info("[Staffs] has been disabled.");
	}

	@EventHandler
	public void onStaffShot(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
				if (p.getItemInHand().getType().name().contains("_HOE") && p.getItemInHand().getItemMeta().hasLore()) {
					if (Alignments.isSafeZone(p.getLocation())) {
						p.playSound(p.getLocation(), Sound.FIZZ, 1, 1.25f);
						ParticleEffect.CRIT_MAGIC.display(0, 0, 0, .5f, 20, p.getLocation().add(0, 1, 0), 20);
					} else {
						if (Energy.nodamage.containsKey(p.getName())
								&& System.currentTimeMillis() - Energy.nodamage.get(p.getName()) < 100) {
							e.setCancelled(true);
							return;
						}
						if (Energy.getEnergy(p) > 0) {
							int amt = 0;
							Projectile ep = null;
							if (p.getItemInHand().getType() == Material.WOOD_HOE) {
								ep = p.launchProjectile(Snowball.class);
								shots.put(ep, p.getItemInHand());
								amt = 7;
							}
							if (p.getItemInHand().getType() == Material.STONE_HOE) {
								ep = p.launchProjectile(SmallFireball.class);
								ep.setVelocity(ep.getVelocity().multiply(1.25));
								ep.setBounce(false);
								shots.put(ep, p.getItemInHand());
								amt = 8;
							}
							if (p.getItemInHand().getType() == Material.IRON_HOE) {
								ep = p.launchProjectile(EnderPearl.class);
								ep.setVelocity(ep.getVelocity().multiply(1.25));
								shots.put(ep, p.getItemInHand());
								amt = 9;
							}
							if (p.getItemInHand().getType() == Material.DIAMOND_HOE) {
								ep = p.launchProjectile(WitherSkull.class);
								ep.setVelocity(ep.getVelocity().multiply(1.5));
								shots.put(ep, p.getItemInHand());
								amt = 10;
							}
							if (p.getItemInHand().getType() == Material.GOLD_HOE) {
								ep = p.launchProjectile(LargeFireball.class);
								ep.setVelocity(ep.getVelocity().multiply(3));
								shots.put(ep, p.getItemInHand());
								amt = 11;
							}
							Random r = new Random();
							int dodura = r.nextInt(2000);
							if (dodura <= p.getItemInHand().getType().getMaxDurability()) {
								if (p.getItemInHand().getDurability() >= p.getItemInHand().getType()
										.getMaxDurability()) {
									p.setItemInHand(null);
									p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
								} else {
									p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 1));
								}
							}
							Energy.removeEnergy(p, amt);
							p.playSound(p.getLocation(), Sound.SHOOT_ARROW, 1F, 0.25F);
							shots.put(ep, p.getItemInHand());
						} else {
							Energy.setEnergy(p, 0);
							Energy.cd.put(p.getName(), System.currentTimeMillis());
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 5), true);
							p.playSound(p.getLocation(), Sound.WOLF_PANT, 10F, 1.5F);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(ProjectileHitEvent e) {
		Projectile pj = e.getEntity();
		if (pj.getShooter() != null && pj.getShooter() instanceof Player) {
			Player d = (Player) pj.getShooter();
			if (shots.containsKey(pj)) {
				LivingEntity target = null;
				ItemStack wep = shots.get(pj);
				shots.remove(pj);
				for (Entity ent : pj.getNearbyEntities(2, 2, 2)) {
					if (ent instanceof LivingEntity) {
						if (ent != d)
							target = (LivingEntity) ent;
					}
				}
				if (target != null) {
					if (pj instanceof SmallFireball)
						e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.EXPLODE, 1F, 1F);
					if (pj instanceof EnderPearl)
						e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENDERMAN_TELEPORT, 2F,
								1.50F);
					staff.put(d, wep);
					target.damage(1, d);
					staff.remove(d);
				}
				shots.remove(pj);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockIgnite(BlockIgniteEvent e) {
		if (e.getCause() == IgniteCause.FIREBALL)
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplodePrimeEvent(ExplosionPrimeEvent e) {
		e.setFire(false);
		e.setRadius(0);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityExplodeEvent(EntityExplodeEvent e) {
		e.setCancelled(true);
		e.setYield(0);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Projectile) {
			e.setCancelled(true);
			e.setDamage(0);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTp(PlayerTeleportEvent e) {
		if (e.getCause().equals(TeleportCause.ENDER_PEARL)) {
			e.setCancelled(true);
		}
	}
}
