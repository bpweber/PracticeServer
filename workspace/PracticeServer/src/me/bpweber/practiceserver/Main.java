package me.bpweber.practiceserver;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	public static Plugin plugin;

	public static Logger log;

	private static Alignments alignments;
	private static Antibuild antibuild;
	private static Banks banks;
	private static Buddies buddies;
	private static ChatMechanics chatMechanics;
	private static Damage damage;
	private static Durability durability;
	private static Enchants enchants;
	private static Energy energy;
	private static GemPouches gemPouches;
	private static Hearthstone hearthstone;
	private static Horses horses;
	private static ItemVendors itemVendors;
	private static Listeners listeners;
	private static Logout logout;
	private static LootChests lootChests;
	private static MerchantMechanics merchantMechanics;
	private static Mining mining;
	private static Mobdrops mobdrops;
	private static Mobs mobs;
	private static ModerationMechanics moderationMechanics;
	private static Orbs orbs;
	private static Parties parties;
	private static ProfessionMechanics professionMechanics;
	private static Repairing repairing;
	private static Respawn respawn;
	private static Spawners spawners;
	private static Speedfish speedfish;
	private static Staffs staffs;
	private static TeleportBooks teleportBooks;
	private static Toggles toggles;
	private static Untradeable untradeable;

	public static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}

	public static Economy econ = null;

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public void onEnable() {
		Bukkit.getWorlds().get(0).setAutoSave(false);
		Bukkit.getWorlds().get(0).setTime(14000);
		Bukkit.getWorlds().get(0).setGameRuleValue("doDaylightCycle", "false");

		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.saveData();
				}
			}
		}.runTaskTimerAsynchronously(this, 20 * 60 * 5, 20 * 60 * 5);

		if (!getDataFolder().exists())
			getDataFolder().mkdirs();

		plugin = this;
		log = plugin.getLogger();

		getCommand("gl").setExecutor(new ChatMechanics());
		getCommand("br").setExecutor(new ChatMechanics());
		getCommand("message").setExecutor(new ChatMechanics());
		getCommand("reply").setExecutor(new ChatMechanics());
		getCommand("roll").setExecutor(new ChatMechanics());
		getCommand("toggle").setExecutor(new Toggles());
		getCommand("togglepvp").setExecutor(new Toggles());
		getCommand("togglechaos").setExecutor(new Toggles());
		getCommand("toggleff").setExecutor(new Toggles());
		getCommand("toggledebug").setExecutor(new Toggles());
		getCommand("add").setExecutor(new Buddies());
		getCommand("del").setExecutor(new Buddies());
		getCommand("logout").setExecutor(new Logout());
		getCommand("sync").setExecutor(new Logout());
		getCommand("setrank").setExecutor(new ModerationMechanics());
		getCommand("psban").setExecutor(new ModerationMechanics());
		getCommand("psunban").setExecutor(new ModerationMechanics());
		getCommand("psmute").setExecutor(new ModerationMechanics());
		getCommand("psunmute").setExecutor(new ModerationMechanics());
		getCommand("banksee").setExecutor(new ModerationMechanics());
		getCommand("psvanish").setExecutor(new ModerationMechanics());
		//getCommand("createdrop").setExecutor(new ModerationMechanics());
		getCommand("toggletrail").setExecutor(new ModerationMechanics());
		getCommand("showms").setExecutor(new Spawners());
		getCommand("hidems").setExecutor(new Spawners());
		getCommand("killall").setExecutor(new Spawners());
		getCommand("monspawn").setExecutor(new Spawners());
		getCommand("showloot").setExecutor(new LootChests());
		getCommand("hideloot").setExecutor(new LootChests());
		getCommand("pinvite").setExecutor(new Parties());
		getCommand("paccept").setExecutor(new Parties());
		getCommand("pkick").setExecutor(new Parties());
		getCommand("pquit").setExecutor(new Parties());
		getCommand("pdecline").setExecutor(new Parties());
		getCommand("p").setExecutor(new Parties());

		alignments = new Alignments();
		antibuild = new Antibuild();
		banks = new Banks();
		buddies = new Buddies();
		chatMechanics = new ChatMechanics();
		damage = new Damage();
		durability = new Durability();
		enchants = new Enchants();
		energy = new Energy();
		gemPouches = new GemPouches();
		hearthstone = new Hearthstone();
		horses = new Horses();
		itemVendors = new ItemVendors();
		listeners = new Listeners();
		logout = new Logout();
		lootChests = new LootChests();
		merchantMechanics = new MerchantMechanics();
		mining = new Mining();
		mobdrops = new Mobdrops();
		mobs = new Mobs();
		moderationMechanics = new ModerationMechanics();
		orbs = new Orbs();
		parties = new Parties();
		professionMechanics = new ProfessionMechanics();
		repairing = new Repairing();
		respawn = new Respawn();
		spawners = new Spawners();
		speedfish = new Speedfish();
		staffs = new Staffs();
		teleportBooks = new TeleportBooks();
		toggles = new Toggles();
		untradeable = new Untradeable();

		alignments.onEnable();
		antibuild.onEnable();
		banks.onEnable();
		buddies.onEnable();
		chatMechanics.onEnable();
		damage.onEnable();
		durability.onEnable();
		enchants.onEnable();
		energy.onEnable();
		gemPouches.onEnable();
		hearthstone.onEnable();
		horses.onEnable();
		itemVendors.onEnable();
		listeners.onEnable();
		logout.onEnable();
		lootChests.onEnable();
		merchantMechanics.onEnable();
		mining.onEnable();
		mobdrops.onEnable();
		mobs.onEnable();
		moderationMechanics.onEnable();
		orbs.onEnable();
		parties.onEnable();
		professionMechanics.onEnable();
		repairing.onEnable();
		respawn.onEnable();
		spawners.onEnable();
		speedfish.onEnable();
		staffs.onEnable();
		teleportBooks.onEnable();
		toggles.onEnable();
		untradeable.onEnable();

		if (!setupEconomy()) {
			getLogger().severe(
					String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

	public void onDisable() {
		alignments.onDisable();
		antibuild.onDisable();
		banks.onDisable();
		buddies.onDisable();
		chatMechanics.onDisable();
		damage.onDisable();
		durability.onDisable();
		enchants.onDisable();
		energy.onDisable();
		gemPouches.onDisable();
		hearthstone.onDisable();
		horses.onDisable();
		itemVendors.onDisable();
		listeners.onDisable();
		logout.onDisable();
		lootChests.onDisable();
		merchantMechanics.onDisable();
		mining.onDisable();
		mobdrops.onDisable();
		mobs.onDisable();
		moderationMechanics.onDisable();
		orbs.onDisable();
		parties.onDisable();
		professionMechanics.onDisable();
		repairing.onDisable();
		respawn.onDisable();
		spawners.onDisable();
		speedfish.onDisable();
		staffs.onDisable();
		teleportBooks.onDisable();
		toggles.onDisable();
		untradeable.onDisable();

		plugin = null;

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (Alignments.tagged.containsKey(p.getName()))
				Alignments.tagged.remove(p.getName());
			p.saveData();
			p.kickPlayer(ChatColor.GREEN.toString() + "You have been safely logged out by the server." + "\n\n"
					+ ChatColor.GRAY.toString() + "Your player data has been synced.");
		}
	}
}
