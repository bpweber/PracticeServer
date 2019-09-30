package me.bpweber.practiceserver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class LootChests implements Listener, CommandExecutor {

	static HashMap<Location, Integer> loot = new HashMap<Location, Integer>();
	static HashMap<Location, Integer> respawn = new HashMap<Location, Integer>();
	static HashMap<String, Location> creatingloot = new HashMap<String, Location>();

	public void onEnable() {
		Main.log.info("[LootChests] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Location loc : loot.keySet()) {
					if (respawn.containsKey(loc)) {
						if (respawn.get(loc) >= 1) {
							respawn.put(loc, respawn.get(loc) - 1);
						} else {
							respawn.remove(loc);
						}
					} else {
						if (loc.getWorld().getChunkAt(loc).isLoaded())
							if (!loc.getWorld().getBlockAt(loc).getType().equals(Material.GLOWSTONE))
								loc.getWorld().getBlockAt(loc).setType(Material.CHEST);
					}
				}
			}
		}.runTaskTimer(Main.plugin, 20, 20);
		File file = new File(Main.plugin.getDataFolder(), "loot.yml");
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
		for (String key : config.getKeys(false)) {
			int val = config.getInt(key);
			String[] str = key.split(",");
			World world = Bukkit.getWorld(str[0]);
			double x = Double.valueOf(str[1]);
			double y = Double.valueOf(str[2]);
			double z = Double.valueOf(str[3]);
			Location loc = new Location(world, x, y, z);
			loot.put(loc, val);
		}
	}

	public void onDisable() {
		Main.log.info("[LootChests] has been disabled.");
		File file = new File(Main.plugin.getDataFolder(), "loot.yml");
		if (file.exists())
			file.delete();
		YamlConfiguration config = new YamlConfiguration();
		for (Location loc : loot.keySet()) {
			String s = loc.getWorld().getName() + "," + (int) loc.getX() + "," + (int) loc.getY() + ","
					+ (int) loc.getZ();
			config.set(s, loot.get(loc));
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (p.isOp()) {
				if (cmd.getName().equalsIgnoreCase("showloot")) {
					if (args.length != 1) {
						p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED
								+ "/showloot <radius>");
						return true;
					}
					int radius = 0;
					try {
						radius = Integer.parseInt(args[0]);
					} catch (Exception e) {
						radius = 0;
					}
					Location loc = p.getLocation();
					World w = loc.getWorld();
					int i, j, k;
					int x = (int) loc.getX();
					int y = (int) loc.getY();
					int z = (int) loc.getZ();
					int count = 0;
					for (i = -radius; i <= radius; i++) {
						for (j = -radius; j <= radius; j++) {
							for (k = -radius; k <= radius; k++) {
								loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
								if (loot.containsKey(loc)) {
									count++;
									loc.getBlock().setType(Material.GLOWSTONE);
								}
							}
						}
					}
					p.sendMessage(ChatColor.YELLOW + "Displaying " + count + " lootchests in a " + radius
							+ " block radius...");
					p.sendMessage(ChatColor.GRAY + "Break them to unregister the spawn point.");
				}
				if (cmd.getName().equalsIgnoreCase("hideloot")) {
					if (args.length != 1) {
						p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax. " + ChatColor.RED
								+ "/hideloot <radius>");
						return true;
					}
					int radius = 0;
					try {
						radius = Integer.parseInt(args[0]);
					} catch (Exception e) {
						radius = 0;
					}
					Location loc = p.getLocation();
					World w = loc.getWorld();
					int i, j, k;
					int x = (int) loc.getX();
					int y = (int) loc.getY();
					int z = (int) loc.getZ();
					int count = 0;
					for (i = -radius; i <= radius; i++) {
						for (j = -radius; j <= radius; j++) {
							for (k = -radius; k <= radius; k++) {
								loc = w.getBlockAt(x + i, y + j, z + k).getLocation();
								if (loot.containsKey(loc)) {
									if (loc.getBlock().getType() == Material.GLOWSTONE) {
										loc.getBlock().setType(Material.AIR);
										count++;
									}
								}
							}
						}
					}
					p.sendMessage(
							ChatColor.YELLOW + "Hiding " + count + " loot chests in a " + radius + " block radius...");
				}
			}
		}
		return false;
	}

	public boolean isMobNear(Player p) {
		for (Entity ent : p.getNearbyEntities(6, 6, 6))
			if (ent instanceof LivingEntity && !(ent instanceof Player) && !(ent instanceof Horse))
				return true;
		return false;
	}

	HashMap<Location, Inventory> opened = new HashMap<Location, Inventory>();
	HashMap<Player, Location> viewers = new HashMap<Player, Location>();

	@EventHandler
	public void onChestClick(PlayerInteractEvent e) {
		if (e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if (e.hasBlock()) {
				if (e.getClickedBlock().getType() == Material.CHEST) {
					Location loc = e.getClickedBlock().getLocation();
					if (loot.containsKey(loc)) {
						e.setCancelled(true);
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (isMobNear(p)) {
								p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED
										+ " safe to open that right now.");
								p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
							} else {
								if (!opened.containsKey(loc)) {
									Inventory inv = Bukkit.createInventory(null, 27, "Loot Chest");
									inv.addItem(LootDrops.createLootDrop(loot.get(loc)));
									p.openInventory(inv);
									p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1, 1);
									viewers.put(e.getPlayer(), loc);
									opened.put(loc, inv);
								} else {
									p.openInventory(opened.get(loc));
									p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1, 1);
									viewers.put(e.getPlayer(), loc);
								}
							}
						} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
							if (isMobNear(p)) {
								p.sendMessage(ChatColor.RED + "It is " + ChatColor.BOLD + "NOT" + ChatColor.RED
										+ " safe to open that right now.");
								p.sendMessage(ChatColor.GRAY + "Eliminate the monsters in the area first.");
							} else {
								if (opened.containsKey(loc)) {
									loc.getWorld().getBlockAt(loc).setType(Material.AIR);
									p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
									for (ItemStack is : opened.get(loc).getContents())
										if (is != null)
											loc.getWorld().dropItemNaturally(loc, is);
									opened.remove(loc);
									int tier = loot.get(loc);
									respawn.put(loc, 60 * tier);
									for (Player v : viewers.keySet()) {
										if (viewers.get(v).equals(loc)) {
											viewers.remove(v);
											v.closeInventory();
											v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
											v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1, 1);
										}
									}
								} else {
									loc.getWorld().getBlockAt(loc).setType(Material.AIR);
									loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.WOOD);
									p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
									loc.getWorld().dropItemNaturally(loc, LootDrops.createLootDrop(loot.get(loc)));
									int tier = loot.get(loc);
									respawn.put(loc, 60 * tier);
									for (Player v : viewers.keySet()) {
										if (viewers.get(v).equals(loc)) {
											viewers.remove(v);
											v.closeInventory();
											v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
											v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1, 1);
										}
									}
								}
							}
						}
					} else {
						if (!p.isOp()) {
							e.setCancelled(true);
							p.sendMessage(ChatColor.GRAY + "The chest is locked.");
						}
					}
				} else if (e.getClickedBlock().getType() == Material.GLOWSTONE) {
					if (p.isOp()) {
						Location loc = e.getClickedBlock().getLocation();
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (getPlayerTier(p) > 0) {
								e.setCancelled(true);
								loot.put(loc, getPlayerTier(p));
								p.sendMessage(
										"" + ChatColor.GREEN + ChatColor.BOLD + "     *** LOOT CHEST CREATED ***");
								loc.getWorld().getBlockAt(loc).setType(Material.CHEST);
								loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.CHEST);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (p.isOp()) {
			if (e.getBlock().getType().equals(Material.GLOWSTONE)) {
				Location loc = e.getBlock().getLocation();
				if (loot.containsKey(loc)) {
					loot.remove(loc);
					p.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "     *** LOOT CHEST REMOVED ***");
					loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.CHEST);
				}
			}
		}
	}

	public static int getPlayerTier(Player e) {
		ItemStack is = e.getItemInHand();
		if (is != null && is.getType() != Material.AIR) {
			if (is.getType().name().contains("WOOD_"))
				return 1;
			if (is.getType().name().contains("STONE_"))
				return 2;
			if (is.getType().name().contains("IRON_"))
				return 3;
			if (is.getType().name().contains("DIAMOND_"))
				return 4;
			if (is.getType().name().contains("GOLD_"))
				return 5;
		}
		return 0;
	}

	@EventHandler
	public void onCloseChest(InventoryCloseEvent e) {
		if (e.getPlayer() instanceof Player) {
			Player p = (Player) e.getPlayer();
			if (e.getInventory().getName().contains("Loot Chest")) {
				if (viewers.containsKey(p)) {
					Location loc = viewers.get(p);
					viewers.remove(p);
					p.playSound(p.getLocation(), Sound.CHEST_CLOSE, 1, 1);
					boolean isempty = true;
					for (ItemStack itms : e.getInventory().getContents())
						if (itms != null && itms.getType() != Material.AIR)
							isempty = false;
					if (isempty) {
						loc.getWorld().getBlockAt(loc).setType(Material.AIR);
						loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.WOOD);
						p.playSound(p.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
						opened.remove(loc);
						int tier = loot.get(loc);
						respawn.put(loc, 60 * tier);
						for (Player v : viewers.keySet()) {
							if (viewers.get(v).equals(loc)) {
								viewers.remove(v);
								v.closeInventory();
								v.playSound(v.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.5F, 1.2F);
								v.playSound(v.getLocation(), Sound.CHEST_CLOSE, 1, 1);
							}
						}
					}
				}
			}
		}
	}
}
