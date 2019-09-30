package me.bpweber.practiceserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Mining implements Listener {

	ConcurrentHashMap<Location, Integer> regenores = new ConcurrentHashMap<Location, Integer>();

	HashMap<Location, Material> oretypes = new HashMap<Location, Material>();

	public void onEnable() {
		Main.log.info("[Mining] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		new BukkitRunnable() {
			public void run() {
				for (Location loc : regenores.keySet()) {
					int time = regenores.get(loc);
					if (time < 1) {
						regenores.remove(loc);
						loc.getBlock().setType(oretypes.get(loc));
					} else {
						time--;
						regenores.put(loc, time);
					}
				}
			}
		}.runTaskTimer(Main.plugin, 20, 20);
	}

	public void onDisable() {
		Main.log.info("[Mining] has been disabled.");
	}

	public static ItemStack ore(int tier) {
		Material m = null;
		ChatColor cc = ChatColor.WHITE;
		String name = "";
		String lore = "";
		if (tier == 1) {
			m = Material.COAL_ORE;
			name = "Coal";
			lore = "A chunk of coal ore.";
		}
		if (tier == 2) {
			m = Material.EMERALD_ORE;
			name = "Emerald";
			lore = "An unrefined piece of emerald ore.";
			cc = ChatColor.GREEN;
		}
		if (tier == 3) {
			m = Material.IRON_ORE;
			name = "Iron";
			lore = "A piece of raw iron.";
			cc = ChatColor.AQUA;
		}
		if (tier == 4) {
			m = Material.DIAMOND_ORE;
			name = "Diamond";
			lore = "A sharp chunk of diamond ore.";
			cc = ChatColor.LIGHT_PURPLE;
		}
		if (tier == 5) {
			m = Material.GOLD_ORE;
			name = "Gold";
			lore = "A sparkling piece of gold ore.";
			cc = ChatColor.YELLOW;
		}

		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(cc + name + " Ore");
		im.setLore(Arrays.asList("" + ChatColor.GRAY + ChatColor.ITALIC + lore));
		is.setItemMeta(im);
		return is;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		int seconds = 0;
		int level = 0;
		if (e.getAction() == Action.LEFT_CLICK_BLOCK && p.getItemInHand().getType().name().contains("_PICKAXE")
				&& e.getClickedBlock().getType().name().contains("_ORE")) {
			int picktier = ProfessionMechanics.getPickaxeTier(p.getItemInHand());
			int oretier = ProfessionMechanics.getOreTier(e.getClickedBlock().getType());
			if (oretier == 1) {
				if (picktier == 1) {
					seconds = 5;
					level = 1;
				}
				if (picktier == 2) {
					seconds = 2;
					level = 1;
				}
				if (picktier == 3) {
					seconds = 2;
					level = 1;
				}
				if (picktier == 4) {
					seconds = 2;
					level = 1;
				}
				if (picktier == 5) {
					seconds = 2;
					level = 0;
				}
			}
			if (oretier == 2) {
				if (picktier == 2) {
					seconds = 0;
					level = 0;
				}
				if (picktier == 3) {
					seconds = 2;
					level = 2;
				}
				if (picktier == 4) {
					seconds = 2;
					level = 2;
				}
				if (picktier == 5) {
					seconds = 2;
					level = 0;
				}
			}
			if (oretier == 3) {
				if (picktier == 3) {
					seconds = 5;
					level = 2;
				}
				if (picktier == 4) {
					seconds = 2;
					level = 2;
				}
				if (picktier == 5) {
					seconds = 0;
					level = 0;
				}
			}
			if (oretier == 4) {
				if (picktier == 4) {
					seconds = 5;
					level = 3;
				}
				if (picktier == 5) {
					seconds = 2;
					level = 0;
				}
			}
			if (oretier == 5) {
				if (picktier == 5) {
					seconds = 5;
					level = 2;
				}
			}
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, seconds * 20, level), true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (!(p.getItemInHand().getType().name().contains("_PICKAXE")))
			return;
		Material m = e.getBlock().getType();
		if (m == Material.COAL_ORE || m == Material.EMERALD_ORE || m == Material.IRON_ORE || m == Material.DIAMOND_ORE
				|| m == Material.GOLD_ORE) {
			Random random = new Random();
			int dura = random.nextInt(2000);
			int fail = random.nextInt(100);
			if (dura < p.getItemInHand().getType().getMaxDurability())
				p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 1));
			if (p.getItemInHand().getDurability() >= p.getItemInHand().getType().getMaxDurability())
				p.setItemInHand(null);
			if (p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR)
				return;
			p.updateInventory();
			oretypes.put(e.getBlock().getLocation(), m);
			int oretier = ProfessionMechanics.getOreTier(m);
			int level = ProfessionMechanics.getPickaxeLevel(p.getItemInHand());
			if (oretier > 0 && oretier <= ProfessionMechanics.getPickaxeTier(p.getItemInHand())) {
				e.setCancelled(true);
				e.getBlock().setType(Material.STONE);
				if (p.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
					p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
				regenores.put(e.getBlock().getLocation(), oretier * 30);
				if (fail < ProfessionMechanics.getFailPercent(oretier, level)) {
					addToInv(p, ore(oretier));

					int gemfind = random.nextInt(100);
					int dore = random.nextInt(100);
					int tore = random.nextInt(100);
					if (gemfind < ProfessionMechanics.getPickEnchants(p.getItemInHand(), "GEM FIND")) {
						int gemamt = 0;
						if (oretier == 1)
							gemamt = random.nextInt((32 - 1) + 1) + 1;
						if (oretier == 2)
							gemamt = random.nextInt((64 - 32) + 1) + 32;
						if (oretier == 3)
							gemamt = random.nextInt((128 - 64) + 1) + 64;
						if (oretier == 4)
							gemamt = random.nextInt((512 - 256) + 1) + 256;
						if (oretier == 5)
							gemamt = random.nextInt((1024 - 512) + 1) + 512;
						p.sendMessage("          " + ChatColor.YELLOW + ChatColor.BOLD + "FOUND " + gemamt + " GEM(s)");
						while (gemamt > 0) {
							ItemStack gem = new ItemStack(Material.EMERALD, 64);
							ItemMeta gm = gem.getItemMeta();
							gm.setDisplayName(ChatColor.WHITE + "Gem");
							gm.setLore(Arrays.asList(ChatColor.GRAY + "The currency of Andalucia"));
							gem.setItemMeta(gm);
							e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), gem);
							gemamt = gemamt - 64;
						}
						ItemStack gem = new ItemStack(Material.EMERALD, gemamt);
						ItemMeta gm = gem.getItemMeta();
						gm.setDisplayName(ChatColor.WHITE + "Gem");
						gm.setLore(Arrays.asList(ChatColor.GRAY + "The currency of Andalucia"));
						gem.setItemMeta(gm);
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), gem);
					}
					if (dore < ProfessionMechanics.getPickEnchants(p.getItemInHand(), "DOUBLE ORE")) {
						p.sendMessage("          " + ChatColor.YELLOW + ChatColor.BOLD + "DOUBLE ORE DROP"
								+ ChatColor.YELLOW + " (2x)");
						addToInv(p, ore(oretier));
					}
					if (tore < ProfessionMechanics.getPickEnchants(p.getItemInHand(), "TRIPLE ORE")) {
						p.sendMessage("          " + ChatColor.YELLOW + ChatColor.BOLD + "TRIPLE ORE DROP"
								+ ChatColor.YELLOW + " (3x)");
						addToInv(p, ore(oretier));
						addToInv(p, ore(oretier));
					}

					int xp = ProfessionMechanics.getExpFromOre(oretier);
					ProfessionMechanics.addExp(p, p.getItemInHand(), xp);
				} else {
					e.setCancelled(true);
					p.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "You fail to gather any ore.");
					e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, Material.STONE);
				}
			} else {
				e.setCancelled(true);
				p.sendMessage("" + ChatColor.GRAY + ChatColor.ITALIC + "You cannot mine this ore.");
			}
		}
	}

	public void addToInv(Player p, ItemStack is) {
		for (ItemStack i : p.getInventory().getContents()) {
			if (i != null && i.getType() != Material.AIR) {
				int amt = i.getAmount();
				if (amt < 64) {
					if (i.getType() == is.getType() && i.getItemMeta().equals(is.getItemMeta())) {
						p.getInventory().addItem(is);
						return;
					}
				}
			}
		}
		int slot = p.getInventory().firstEmpty();
		if (slot != -1)
			p.getInventory().setItem(slot, is);
		else
			p.getWorld().dropItemNaturally(p.getLocation(), is);
	}
}
