package me.bpweber.practiceserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class Alignments implements Listener {

	public static HashMap<String, Integer> neutral = new HashMap<String, Integer>();

	public static HashMap<String, Integer> chaotic = new HashMap<String, Integer>();

	public static HashMap<String, Long> tagged = new HashMap<String, Long>();

	public void onEnable() {
		Main.log.info("[Alignments] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.isOnline()) {
						if (chaotic.containsKey(p.getName())) {
							int time = chaotic.get(p.getName());
							if (time <= 1) {
								chaotic.remove(p.getName());
								neutral.put(p.getName(), 120);
								updatePlayerAlignment(p);
								p.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL"
										+ ChatColor.YELLOW + " ALIGNMENT *");
								p.sendMessage(ChatColor.GRAY
										+ "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
								p.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL"
										+ ChatColor.YELLOW + " ALIGNMENT *");
							} else {
								time--;
								chaotic.put(p.getName(), time);
							}
						}
						if (neutral.containsKey(p.getName())) {
							int time = neutral.get(p.getName());
							if (time == 1) {
								neutral.remove(p.getName());
								updatePlayerAlignment(p);
								p.sendMessage(ChatColor.GREEN + "          * YOU ARE NOW " + ChatColor.BOLD + "LAWFUL"
										+ ChatColor.GREEN + " ALIGNMENT *");
								p.sendMessage(ChatColor.GRAY
										+ "While lawful, you will not lose any equipped armor on death, instead, all armor will lose 30% of its durability when you die. Any players who kill you while you're lawfully aligned will become chaotic.");
								p.sendMessage(ChatColor.GREEN + "          * YOU ARE NOW " + ChatColor.BOLD + "LAWFUL"
										+ ChatColor.GREEN + " ALIGNMENT *");
							} else {
								time--;
								neutral.put(p.getName(), time--);
							}
						}
					}
					if (!tagged.containsKey(p.getName()) || (tagged.containsKey(p.getName())
							&& System.currentTimeMillis() - tagged.get(p.getName()) > 10 * 1000)) {
						if (p.getHealth() > 0) {
							PlayerInventory i = p.getInventory();
							double amt = 5;
							// int vit = 0;
							for (ItemStack is : i.getArmorContents()) {
								if (is != null && is.getType() != Material.AIR && is.hasItemMeta()
										&& is.getItemMeta().hasLore()) {
									double added = Damage.getHps(is);
									amt = amt + added;
									// int addedvit = Damage.getElem(is, "VIT");
									// vit = vit + addedvit;
								}
							}
							// if (vit > 0) {
							// amt = amt + (int) Math.round(vit * .3);
							// }
							if (p.getHealth() + amt > p.getMaxHealth()) {
								p.setHealth(p.getMaxHealth());
							} else {
								p.setHealth(p.getHealth() + amt);
							}
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 20, 20);
		File file = new File(Main.plugin.getDataFolder(), "alignments.yml");
		YamlConfiguration config = new YamlConfiguration();
		if (!(file.exists()))
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			config.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (config.getConfigurationSection("chaotic") != null) {
			for (String key : config.getConfigurationSection("chaotic").getKeys(false)) {
				int time = config.getConfigurationSection("chaotic").getInt(key);
				chaotic.put(key, time);
			}
		}
		if (config.getConfigurationSection("neutral") != null) {
			for (String key : config.getConfigurationSection("neutral").getKeys(false)) {
				int time = config.getConfigurationSection("neutral").getInt(key);
				neutral.put(key, time);
			}
		}
	}

	public void onDisable() {
		Main.log.info("[Alignments] has been disabled.");
		File file = new File(Main.plugin.getDataFolder(), "alignments.yml");
		YamlConfiguration config = new YamlConfiguration();
		for (String s : chaotic.keySet())
			config.set("chaotic." + s, chaotic.get(s));
		for (String s : neutral.keySet())
			config.set("neutral." + s, neutral.get(s));
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChaoticSpawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (chaotic.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter "
					+ ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
			e.setRespawnLocation(TeleportBooks.generateRandomSpawnPoint(p.getName()));
		} else {
			e.setRespawnLocation(TeleportBooks.Cyrennica);
		}
	}

	@EventHandler
	public void onZoneMessage(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (isSafeZone(e.getFrom()) && chaotic.containsKey(p.getName())) {
			p.sendMessage(ChatColor.RED + "The guards have kicked you out of the " + ChatColor.UNDERLINE
					+ "protected area" + ChatColor.RED + " due to your chaotic alignment.");
			p.teleport(TeleportBooks.generateRandomSpawnPoint(p.getName()));
			return;
		}
		if (isSafeZone(e.getTo())) {
			if (chaotic.containsKey(p.getName())) {
				p.teleport(e.getFrom());
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter "
						+ ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
				return;
			} else if (Listeners.combat.containsKey(p.getName())
					&& System.currentTimeMillis() - Listeners.combat.get(p.getName()) <= 10 * 1000) {
				p.teleport(e.getFrom());
				long combattime = Listeners.combat.get(p.getName());
				double left = (System.currentTimeMillis() - combattime) / 1000;
				int time = (int) (10 - Math.round(left));
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED
						+ " leave a chaotic zone while in combat.");
				p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + time + "s");
				return;
			}
		}
		if (!isSafeZone(e.getFrom()) && isSafeZone(e.getTo())) {
			p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "          *** SAFE ZONE (DMG-OFF) ***");
			p.playSound(p.getLocation(), Sound.WITHER_SHOOT, .25f, .3f);
		}
		if (isSafeZone(e.getFrom()) && !isSafeZone(e.getTo())) {
			p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "          *** CHAOTIC ZONE (PVP-ON) ***");
			p.playSound(p.getLocation(), Sound.WITHER_SHOOT, .25f, .3f);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onTeleportChaotic(PlayerTeleportEvent e) {
		if (e.isCancelled())
			return;
		Player p = e.getPlayer();
		if (isSafeZone(e.getTo())) {
			if (chaotic.containsKey(p.getName())) {
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED + " enter "
						+ ChatColor.BOLD + "NON-PVP" + ChatColor.RED + " zones with a chaotic alignment.");
				e.setCancelled(true);
				return;
			} else if (Listeners.combat.containsKey(p.getName())
					&& System.currentTimeMillis() - Listeners.combat.get(p.getName()) <= 10 * 1000) {
				long combattime = Listeners.combat.get(p.getName());
				double left = (System.currentTimeMillis() - combattime) / 1000;
				int time = (int) (10 - Math.round(left));
				p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED
						+ " leave a chaotic zone while in combat.");
				p.sendMessage(ChatColor.GRAY + "Out of combat in: " + ChatColor.BOLD + time + "s");
				e.setCancelled(true);
				return;
			}
		}
		if (!isSafeZone(e.getFrom()) && isSafeZone(e.getTo())) {
			p.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "          *** SAFE ZONE (DMG-OFF) ***");
			p.playSound(e.getTo(), Sound.WITHER_SHOOT, .25f, .3f);
		}
		if (isSafeZone(e.getFrom()) && !isSafeZone(e.getTo())) {
			p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "          *** CHAOTIC ZONE (PVP-ON) ***");
			p.playSound(e.getTo(), Sound.WITHER_SHOOT, .25f, .3f);
		}
	}

	public static boolean isSafeZone(Location loc) {
		ApplicableRegionSet locset = WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		if (locset.queryState(null, DefaultFlag.PVP) == StateFlag.State.DENY)
			return true;
		return false;
	}

	static void updatePlayerAlignment(Player p) {
		ChatColor cc = ChatColor.GRAY;
		// if (p.getName().equalsIgnoreCase("bpweber")) {
		// cc = ChatColor.DARK_AQUA;
		// } else
		if (p.isOp()) {
			cc = ChatColor.AQUA;
		} else {
			if (neutral.containsKey(p.getName())) {
				cc = ChatColor.YELLOW;
			} else if (chaotic.containsKey(p.getName())) {
				cc = ChatColor.RED;
			} else {
				cc = ChatColor.GRAY;
			}
		}
		p.setDisplayName(getPlayerPrefix(p) + cc + p.getName());
		p.playSound(p.getLocation(), Sound.ZOMBIE_INFECT, 10, 1);
		Scoreboards.updateAllColors();
	}

	static String getPlayerPrefix(Player p) {
		String prefix = "";
		String rank = "";
		if (ModerationMechanics.ranks.containsKey(p.getName()))
			rank = ModerationMechanics.ranks.get(p.getName());
		if (rank.equals("sub"))
			prefix = "" + ChatColor.GREEN + ChatColor.BOLD + "S ";
		if (rank.equals("sub+"))
			prefix = "" + ChatColor.GOLD + ChatColor.BOLD + "S+ ";
		if (rank.equals("sub++"))
			prefix = "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "S++ ";
		if (rank.equals("pmod"))
			prefix = "" + ChatColor.WHITE + ChatColor.BOLD + "PMOD ";
		if (p.isOp())
			prefix = "" + ChatColor.AQUA + ChatColor.BOLD + "GM ";
		// if (p.getName().equals("bpweber"))
		// prefix = "" + ChatColor.DARK_AQUA + ChatColor.BOLD + "DEV ";
		return prefix;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		updatePlayerAlignment(p);
		Scoreboards.updatePlayerHealth();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onNeutral(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player d = (Player) e.getDamager();
			if (e.getDamage() <= 0)
				return;
			if (!chaotic.containsKey(d.getName())) {
				if (neutral.containsKey(d.getName())) {
					neutral.put(d.getName(), 120);
				} else {
					d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL"
							+ ChatColor.YELLOW + " ALIGNMENT *");
					d.sendMessage(ChatColor.GRAY
							+ "While neutral, players who kill you will	 not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
					d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL"
							+ ChatColor.YELLOW + " ALIGNMENT *");
					neutral.put(d.getName(), 120);
					updatePlayerAlignment(d);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onChaotic(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		if (!Damage.lastphit.containsKey(p))
			return;
		if (Damage.lasthit.containsKey(p)) {
			// if (Damage.lasthit.get(p) == 0)
			// return;
			if (System.currentTimeMillis() - Damage.lasthit.get(p) > (8 * 1000))
				return;
		}
		Player d = Damage.lastphit.get(p);
		if (!neutral.containsKey(p.getName()) && !chaotic.containsKey(p.getName())) {
			if (chaotic.containsKey(d.getName())) {
				int time = chaotic.get(d.getName());
				chaotic.put(d.getName(), time + 600);
				d.sendMessage("§cLAWFUL player slain, §l+600s §cadded to Chaotic timer");
				neutral.remove(d.getName());
				updatePlayerAlignment(d);
			} else {
				d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED
						+ " ALIGNMENT *");
				d.sendMessage(ChatColor.GRAY
						+ "While chaotic, you cannot enter any major cities or safe zones. If you are killed while chaotic, you will lose everything in your inventory. Chaotic alignment will expire 10 minutes after your last player kill.");
				d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD + "CHAOTIC" + ChatColor.RED
						+ " ALIGNMENT *");
				d.sendMessage(ChatColor.RED + "LAWFUL player slain, " + ChatColor.BOLD + "+600s" + ChatColor.RED
						+ " added to Chaotic timer.");
				chaotic.put(d.getName(), 600);
				neutral.remove(d.getName());
				updatePlayerAlignment(d);
			}
		}
		if (neutral.containsKey(p.getName()) && !chaotic.containsKey(p.getName())) {
			if (chaotic.containsKey(d.getName())) {
				int time = chaotic.get(d.getName());
				chaotic.put(d.getName(), time + 300);
				d.sendMessage(ChatColor.RED + "NEUTRAL player slain, " + ChatColor.BOLD + "+300s" + ChatColor.RED
						+ " added to Chaotic timer.");
				neutral.remove(d.getName());
				updatePlayerAlignment(d);
			}
		}
		if (chaotic.containsKey(p.getName()) && chaotic.containsKey(d.getName())) {
			int time = chaotic.get(d.getName());
			if (time <= 300) {
				chaotic.remove(d.getName());
				neutral.put(d.getName(), 120);
				updatePlayerAlignment(d);
				d.sendMessage("§cCHAOTIC player slain, §l-300s §ctaken to Chaotic timer");
				d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL"
						+ ChatColor.YELLOW + " ALIGNMENT *");
				d.sendMessage(ChatColor.GRAY
						+ "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player.");
				d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " + ChatColor.BOLD + "NEUTRAL"
						+ ChatColor.YELLOW + " ALIGNMENT *");
			} else {
				time = time - 300;
				chaotic.put(d.getName(), time);
				d.sendMessage(ChatColor.GREEN + "Chaotic player slain, " + ChatColor.BOLD + "-300s" + ChatColor.GREEN
						+ " removed from Chatoic timer.");
			}
		}
	}

	/*
	 * @EventHandler(priority = EventPriority.HIGH) public void
	 * onChaotic(EntityDamageByEntityEvent e) { if (e.getEntity() instanceof
	 * Player) { Player p = (Player) e.getEntity(); if (e.getDamager()
	 * instanceof Player) { final Player d = (Player) e.getDamager(); if
	 * (e.getDamage() >= p.getHealth()) { if (!neutral.containsKey(p.getName())
	 * && !chaotic.containsKey(p.getName())) { if
	 * (chaotic.containsKey(d.getName())) { int time = chaotic.get(d.getName());
	 * chaotic.put(d.getName(), time + 600); d.sendMessage(
	 * "§cLAWFUL player slain, §l+600s §cadded to Chaotic timer");
	 * neutral.remove(d.getName()); makeChaotic(d); } else { makeChaotic(d);
	 * d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " + ChatColor.BOLD
	 * + "CHAOTIC" + ChatColor.RED + " ALIGNMENT *");
	 * d.sendMessage(ChatColor.GRAY +
	 * "While chaotic, you cannot enter any major cities or safe zones. If you are killed while chaotic, you will lose everything in your inventory. Chaotic alignment will expire 10 minutes after your last player kill."
	 * ); d.sendMessage(ChatColor.RED + "          * YOU ARE NOW " +
	 * ChatColor.BOLD + "CHAOTIC" + ChatColor.RED + " ALIGNMENT *");
	 * d.sendMessage("§cLAWFUL player slain, §l+600s §cadded to Chaotic timer");
	 * chaotic.put(d.getName(), 600); neutral.remove(d.getName()); } } if
	 * (neutral.containsKey(p.getName()) && !chaotic.containsKey(p.getName())) {
	 * if (chaotic.containsKey(d.getName())) { int time =
	 * chaotic.get(d.getName()); chaotic.put(d.getName(), time + 300);
	 * d.sendMessage("§cNEUTRAL player slain, §l+300s §cadded to Chaotic timer"
	 * ); neutral.remove(d.getName()); makeChaotic(d); } } if
	 * (chaotic.containsKey(p.getName()) && chaotic.containsKey(d.getName())) {
	 * int time = chaotic.get(d.getName()); if (time <= 300) {
	 * chaotic.remove(d.getName()); neutral.put(d.getName(), 120);
	 * makeNeutral(d); d.sendMessage(
	 * "§cCHAOTIC player slain, §l-300s §ctaken to Chaotic timer");
	 * d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " +
	 * ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *");
	 * d.sendMessage(ChatColor.GRAY +
	 * "While neutral, players who kill you will not become chaotic. You have a 50% chance of dropping your weapon, and a 25% chance of dropping each piece of equiped armor on death. Neutral alignment will expire 2 minutes after last hit on player."
	 * ); d.sendMessage(ChatColor.YELLOW + "          * YOU ARE NOW " +
	 * ChatColor.BOLD + "NEUTRAL" + ChatColor.YELLOW + " ALIGNMENT *"); } else {
	 * time = time - 300; chaotic.put(d.getName(), time); d.sendMessage(
	 * "§cCHAOTIC player slain, §l-300s §ctaken to Chaotic timer"); } } } } } }
	 */

	@EventHandler(priority = EventPriority.HIGH)
	public void onDeathMessage(PlayerDeathEvent e) {
		Player p = e.getEntity();
		String reason = " has died";
		if (p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null) {
			if (p.getLastDamageCause().getCause().equals(DamageCause.LAVA)
					|| p.getLastDamageCause().getCause().equals(DamageCause.FIRE)
					|| p.getLastDamageCause().getCause().equals(DamageCause.FIRE_TICK)) {
				reason = " burned to death";
			}
			if (p.getLastDamageCause().getCause().equals(DamageCause.SUICIDE)) {
				reason = " ended their own life";
			}
			if (p.getLastDamageCause().getCause().equals(DamageCause.FALL)) {
				reason = " fell to their death";
			}
			if (p.getLastDamageCause().getCause().equals(DamageCause.SUFFOCATION)) {
				reason = " was crushed to death";
			}
			if (p.getLastDamageCause().getCause().equals(DamageCause.DROWNING)) {
				reason = " drowned to death";
			}
			if (p.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent et = (EntityDamageByEntityEvent) p.getLastDamageCause();
				if (et.getDamager() instanceof LivingEntity) {
					if (et.getDamager() instanceof Player) {
						Player d = (Player) et.getDamager();
						ItemStack wep = d.getItemInHand();
						if (Staffs.staff.containsKey(d))
							wep = Staffs.staff.get(d);
						String wepname = "";
						if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasDisplayName()) {
							wepname = wep.getItemMeta().getDisplayName();
						} else {
							wepname = wep.getType().name().substring(0, 1).toUpperCase()
									+ wep.getType().name().substring(1, wep.getType().name().length()).toLowerCase();
						}
						reason = " was killed by " + ChatColor.RESET + d.getDisplayName() + ChatColor.WHITE
								+ " with a(n) " + wepname;
					} else if (et.getDamager() instanceof LivingEntity) {
						LivingEntity l = (LivingEntity) et.getDamager();
						String name = "";
						if (l.hasMetadata("name"))
							name = l.getMetadata("name").get(0).asString();
						reason = " was killed by a(n) " + ChatColor.UNDERLINE + name;
					}
				}
			}
			if (Damage.lastphit.containsKey(p)) {
				if (!Damage.lasthit.containsKey(p)
						|| System.currentTimeMillis() - Damage.lasthit.get(p) <= (8 * 1000)) {
					Player d = Damage.lastphit.get(p);
					ItemStack wep = d.getItemInHand();
					if (Staffs.staff.containsKey(d))
						wep = Staffs.staff.get(d);
					String wepname = "";
					if (wep != null && wep.getType() != Material.AIR && wep.getItemMeta().hasDisplayName()) {
						wepname = wep.getItemMeta().getDisplayName();
					} else {
						wepname = wep.getType().name().substring(0, 1).toUpperCase()
								+ wep.getType().name().substring(1, wep.getType().name().length()).toLowerCase();
					}
					reason = " was killed by " + ChatColor.RESET + d.getDisplayName() + ChatColor.WHITE + " with a(n) "
							+ wepname;
				}
			}
		}
		p.sendMessage(p.getDisplayName() + ChatColor.WHITE + reason);
		for (Entity near : p.getNearbyEntities(50, 50, 50))
			if (near instanceof Player)
				((Player) near).sendMessage(p.getDisplayName() + ChatColor.WHITE + reason);
	}
}
