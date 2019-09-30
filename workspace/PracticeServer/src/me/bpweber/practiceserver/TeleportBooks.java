package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportBooks implements Listener {

	public static Location Cyrennica;
	public static Location Harrison_Field;
	public static Location Dark_Oak_Tavern;
	public static Location Deadpeaks_Mountain_Camp;
	public static Location Trollsbane_Tavern;
	public static Location Tripoli;
	public static Location Gloomy_Hollows;
	public static Location Crestguard_Keep;
	public static Location CrestWatch;

	static HashMap<String, Location> teleporting_loc = new HashMap<String, Location>();
	static HashMap<String, Location> casting_loc = new HashMap<String, Location>();
	static HashMap<String, Integer> casting_time = new HashMap<String, Integer>();

	public void onEnable() {
		Cyrennica = new Location(Bukkit.getWorlds().get(0), -367, 83, 390);
		Harrison_Field = new Location(Bukkit.getWorlds().get(0), -594, 58, 687, 92.0F, 1F);
		Dark_Oak_Tavern = new Location(Bukkit.getWorlds().get(0), 280, 58, 1132, 2.0F, 1F);
		Deadpeaks_Mountain_Camp = new Location(Bukkit.getWorlds().get(0), -1173, 105, 1030, -88.0F, 1F);
		Trollsbane_Tavern = new Location(Bukkit.getWorlds().get(0), 962, 94, 1069, -153.0F, 1F);
		Tripoli = new Location(Bukkit.getWorlds().get(0), -1320, 90, 370, 153F, 1F);
		Gloomy_Hollows = new Location(Bukkit.getWorlds().get(0), -590, 43, 0, 144F, 1F);
		Crestguard_Keep = new Location(Bukkit.getWorlds().get(0), -1428, 115, -489, 95F, 1F);
		CrestWatch = new Location(Bukkit.getWorlds().get(0), -544, 60, -418, 95F, 1F);

		Main.log.info("[TeleportBooks] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (casting_time.containsKey(p.getName())) {
						if (casting_time.get(p.getName()) == 0) {
							ParticleEffect.SPELL_WITCH.display(0, 0, 0, .2f, 200, p.getLocation().add(0, 1, 0), 20);
							p.eject();
							p.teleport(teleporting_loc.get(p.getName()));
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
							casting_time.remove(p.getName());
							casting_loc.remove(p.getName());
							teleporting_loc.remove(p.getName());
						} else {
							p.sendMessage(ChatColor.BOLD + "CASTING" + ChatColor.WHITE + " ... "
									+ casting_time.get(p.getName()) + ChatColor.BOLD + "s");
							casting_time.put(p.getName(), casting_time.get(p.getName()) - 1);
							ParticleEffect.PORTAL.display(0, 0, 0, 4, 300, p.getLocation(), 20);
							ParticleEffect.SPELL_WITCH.display(0, 0, 0, 1, 200, p.getLocation(), 20);
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.plugin, 20, 20);
	}

	public void onDisable() {
		Main.log.info("[TeleportBooks] has been disabled.");
	}

	public static ItemStack cyrennica_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Cyrennica");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to the grand City of Cyrennica."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack harrison_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Harrison Field");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to Harrison Field."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack dark_oak_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Dark Oak Tavern");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to the tavern in Dark Oak Forest."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack deadpeaks_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(
				"" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Deadpeaks Mountain Camp");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to the Deadpeaks.",
				ChatColor.RED + "" + ChatColor.BOLD + "WARNING:" + ChatColor.RED.toString() + " CHAOTIC ZONE"));

		is.setItemMeta(im);
		return is;
	}

	public static ItemStack trollsbane_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Trollsbane Tavern");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to Trollsbane Tavern."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack tripoli_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Tripoli");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to Tripoli."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack gloomy_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Gloomy Hollows");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to the Gloomy Hollows."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack crestguard_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.WHITE + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " Crestguard Keep");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to the Crestguard Keep."));
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack crestwatch_book() {
		ItemStack is = new ItemStack(Material.BOOK);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Teleport:" + ChatColor.WHITE + " CrestWatch");
		im.setLore(Arrays.asList(ChatColor.GRAY + "Teleports the user to the CrestWatch."));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.BOOK
					&& p.getItemInHand().getItemMeta().hasDisplayName()
					&& p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains("teleport:")) {
				if (!casting_time.containsKey(p.getName())) {
					if (!Horses.mounting.containsKey(p.getName())) {
						String type = ChatColor.stripColor(p.getItemInHand().getItemMeta().getDisplayName());
						Location loc = getLocationFromString(type);
						if (Alignments.chaotic.containsKey(p.getName()) && loc != Deadpeaks_Mountain_Camp) {
							p.sendMessage(ChatColor.RED + "You " + ChatColor.UNDERLINE + "cannot" + ChatColor.RED
									+ " teleport to non-chaotic zones while chaotic.");
							p.sendMessage(ChatColor.GRAY + "Neutral in " + ChatColor.BOLD
									+ Alignments.chaotic.get(p.getName()) + "s");
						} else {
							if (p.getItemInHand().getAmount() > 1) {
								p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
							} else {
								p.setItemInHand(null);
							}
							int seconds = 5;
							if ((Alignments.tagged.containsKey(p.getName())
									&& System.currentTimeMillis() - Alignments.tagged.get(p.getName()) < 10 * 1000))
								seconds = 10;
							p.sendMessage("" + ChatColor.WHITE + ChatColor.BOLD + "CASTING " + ChatColor.WHITE
									+ getTeleportMessage(type) + " ... " + seconds + ChatColor.BOLD + "s");
							teleporting_loc.put(p.getName(), loc);
							casting_loc.put(p.getName(), p.getLocation());
							casting_time.put(p.getName(), seconds);
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (seconds + 3) * 20, 1));
							p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1, 1);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onCancelDamager(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof LivingEntity) {
			Player p = (Player) e.getDamager();
			if (casting_time.containsKey(p.getName())) {
				casting_time.remove(p.getName());
				casting_loc.remove(p.getName());
				teleporting_loc.remove(p.getName());
				p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
				p.removePotionEffect(PotionEffectType.CONFUSION);
			}
		}
	}

	@EventHandler
	public void onCancelDamage(EntityDamageEvent e) {
		if (e.getDamage() <= 0)
			return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (casting_time.containsKey(p.getName())) {
				casting_time.remove(p.getName());
				casting_loc.remove(p.getName());
				teleporting_loc.remove(p.getName());
				p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
				p.removePotionEffect(PotionEffectType.CONFUSION);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (casting_time.containsKey(p.getName())) {
			casting_time.remove(p.getName());
			casting_loc.remove(p.getName());
			teleporting_loc.remove(p.getName());
		}
	}

	@EventHandler
	public void onCancelMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (casting_time.containsKey(p.getName())) {
			Location loc = casting_loc.get(p.getName());
			if (loc.distanceSquared(e.getTo()) >= 2) {
				casting_time.remove(p.getName());
				casting_loc.remove(p.getName());
				teleporting_loc.remove(p.getName());
				p.sendMessage(ChatColor.RED + "Teleportation - " + ChatColor.BOLD + "CANCELLED");
				p.removePotionEffect(PotionEffectType.CONFUSION);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if (e.isCancelled())
			return;
		if (e.getItem().getItemStack().getType() == Material.BOOK
				|| e.getItem().getItemStack().getType() == Material.PAPER
				|| (e.getItem().getItemStack().getType() == Material.INK_SACK
						&& e.getItem().getItemStack().getDurability() == 0)) {
			e.setCancelled(true);
			if (p.getInventory().firstEmpty() != -1) {
				int amount = e.getItem().getItemStack().getAmount();
				ItemStack scroll = CraftItemStack.asCraftCopy(e.getItem().getItemStack());
				scroll.setAmount(1);
				while (amount > 0 && p.getInventory().firstEmpty() != -1) {
					p.getInventory().setItem(p.getInventory().firstEmpty(), scroll);
					p.updateInventory();
					amount--;
					if (amount > 0) {
						ItemStack new_stack = e.getItem().getItemStack();
						new_stack.setAmount(amount);
						e.getItem().setItemStack(new_stack);
					}
				}
				if (amount <= 0) {
					e.getItem().remove();
				}
				p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1F, 1F);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getCursor() != null && e.getCursor().getType() == Material.BOOK) {
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BOOK) {
				e.setCancelled(true);
				p.updateInventory();
			}
		}
		if (e.isShiftClick() && e.getCurrentItem() != null && (e.getCurrentItem().getType() == Material.BOOK
				|| e.getCurrentItem().getType() == Material.PAPER || GemPouches.isGemPouch(e.getCurrentItem()))) {
			if (e.getInventory().getName().contains("Bank Chest")) {
				if (e.getRawSlot() < 63) {
					if (p.getInventory().firstEmpty() != -1) {
						p.getInventory().setItem(p.getInventory().firstEmpty(), e.getCurrentItem());
						e.setCurrentItem(null);
					}
				}
				if (!GemPouches.isGemPouch(e.getCurrentItem()))
					if (e.getRawSlot() > 53) {
						if (e.getInventory().firstEmpty() != -1) {
							e.getInventory().setItem(e.getInventory().firstEmpty(), e.getCurrentItem());
							e.setCurrentItem(null);
						}
					}
			}
			e.setCancelled(true);
			p.updateInventory();
		}
		if (e.getClick() == ClickType.DOUBLE_CLICK && e.getCursor() != null && (e.getCursor().getType() == Material.BOOK
				|| e.getCursor().getType() == Material.PAPER || GemPouches.isGemPouch(e.getCursor()))) {
			e.setCancelled(true);
			p.updateInventory();
		}
		if (e.getCursor() != null && e.getCursor().getType() == Material.PAPER) {
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.PAPER) {
				e.setCancelled(true);
				p.updateInventory();
			}
		}
		if (e.getCursor() != null && GemPouches.isGemPouch(e.getCursor())) {
			if (e.getCurrentItem() != null && GemPouches.isGemPouch(e.getCurrentItem())) {
				e.setCancelled(true);
				p.updateInventory();
			}
		}
	}

	/*
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (e.getOldCursor() != null && (e.getOldCursor().getType() == Material.BOOK
				|| e.getOldCursor().getType() == Material.PAPER || GemPouches.isGemPouch(e.getOldCursor()))) {
			e.setCancelled(true);
		}
	}
	*/

	Location getLocationFromString(String s) {
		s = s.toLowerCase();
		if (s.contains("cyrennica"))
			return Cyrennica;
		if (s.contains("harrison field"))
			return Harrison_Field;
		if (s.contains("dark oak tavern"))
			return Dark_Oak_Tavern;
		if (s.contains("deadpeaks mountain camp"))
			return Deadpeaks_Mountain_Camp;
		if (s.contains("trollsbane tavern"))
			return Trollsbane_Tavern;
		if (s.contains("tripoli"))
			return Tripoli;
		if (s.contains("gloomy hollows"))
			return Gloomy_Hollows;
		if (s.contains("crestguard keep"))
			return Crestguard_Keep;
		if (s.contains("crestwatch"))
			return CrestWatch;
		return Cyrennica;
	}

	String getTeleportMessage(String s) {
		s = s.toLowerCase();
		if (s.contains("cyrennica"))
			return "Teleport Scroll: Cyrennica";
		if (s.contains("harrison field"))
			return "Teleport Scroll: Harrison's Field";
		if (s.contains("dark oak tavern"))
			return "Teleport Scroll: Dark Oak Tavern";
		if (s.contains("deadpeaks mountain camp"))
			return "Teleport Scroll: Deadpeaks Mountain Camp";
		if (s.contains("trollsbane tavern"))
			return "Teleport Scroll: Trollsbane Tavern";
		if (s.contains("tripoli"))
			return "Teleport Scroll: Tripoli";
		if (s.contains("gloomy hollows"))
			return "Teleport Scroll: Gloomy Hollows";
		if (s.contains("crestguard keep"))
			return "Teleport Scroll: Crestguard Keep";
		if (s.contains("crestwatch"))
			return "Teleport Scroll: CrestWatch";
		return "Teleport Scroll: Cyrennica";
	}

	@EventHandler
	public void onAvalonTp(PlayerMoveEvent e) {
		Location to = e.getTo();
		Location enter = new Location(Bukkit.getWorlds().get(0), -357.5, 171, -3440.5);
		Location exit = new Location(Bukkit.getWorlds().get(0), -1158.5, 95, -515.5);
		if (to.getX() > -1155 && to.getX() < -1145 && to.getY() > 90 && to.getY() < 100 && to.getZ() < -500
				&& to.getZ() > -530)
			e.getPlayer().teleport(enter.setDirection(to.getDirection()));
		if (to.getX() < -360 && to.getX() > -370 && to.getY() > 165 && to.getY() < 190 && to.getZ() < -3426
				&& to.getZ() > -3455)
			e.getPlayer().teleport(exit.setDirection(to.getDirection()));
	}

	public static Location generateRandomSpawnPoint(String s) {
		ArrayList<Location> spawns = new ArrayList<Location>();
		if (Alignments.chaotic.containsKey(s)) {
			spawns.add(new Location(Bukkit.getWorlds().get(0), -382, 68, 867));
			spawns.add(new Location(Bukkit.getWorlds().get(0), -350, 67, 883));
			spawns.add(new Location(Bukkit.getWorlds().get(0), -330, 65, 898));
			spawns.add(new Location(Bukkit.getWorlds().get(0), -419, 61, 830));
			return spawns.get(new Random().nextInt(spawns.size()));
		}
		return Cyrennica;
	}
}
