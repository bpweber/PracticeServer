package minecade.dungeonrealms.DonationMechanics.commands;

import java.util.List;

import minecade.dungeonrealms.Main;
import minecade.dungeonrealms.DonationMechanics.DonationMechanics;
import minecade.dungeonrealms.Hive.Hive;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddShards implements CommandExecutor {
	
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player ps = null;
		if(sender instanceof Player) {
			ps = (Player) sender;
			if(!(ps.isOp())) { return true; }
		}
		if (args.length != 3) {
			ps.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Incorrect Syntax." + ChatColor.RESET + ""
					+ ChatColor.GRAY + " Usage: /addshards <player> <amount>");
		}
			final String p_name = args[0];
	        List<Integer> portal_shards = Hive.player_portal_shards.get(p_name);
			final int amount = Integer.parseInt(args[1]);
			final int tier = Integer.parseInt(args[2]);
	        int current_shards = portal_shards.get(tier);
			current_shards += amount;
	
			// Add <amount> E-CASH to player. (player_database->ecash)
			
			Main.log.info("[DonationMechanics] Adding " + amount + "T" + tier + " Shards to " + p_name + "'s stash of " + current_shards + " EC!");
			
	
	        portal_shards.set(tier, current_shards);
	
	        Hive.player_portal_shards.put(p_name, portal_shards);
	   
			DonationMechanics.sendPacketCrossServer("[portal shards]" + p_name + ":" + current_shards, -1, true);
			
			if(Bukkit.getPlayer(p_name) != null) {
				Player pl = Bukkit.getPlayer(p_name);
				pl.sendMessage(ChatColor.GOLD + "  +" + amount + ChatColor.BOLD + "T" + tier + " Portal Shards");
				pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			}
			return true;
		}
	}
