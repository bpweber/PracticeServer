package me.bpweber.practiceserver;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Scoreboards {

	public static HashMap<Player, Scoreboard> boards = new HashMap<>();

	public static Scoreboard getBoard(Player p) {
		if (!boards.containsKey(p)) {
			Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
			Team red = sb.registerNewTeam("red");
			red.setPrefix(ChatColor.RED.toString());
			Team yellow = sb.registerNewTeam("yellow");
			yellow.setPrefix(ChatColor.YELLOW.toString());
			Team white = sb.registerNewTeam("white");
			white.setPrefix(ChatColor.WHITE.toString());
			Team gm = sb.registerNewTeam("gm");
			gm.setPrefix(ChatColor.AQUA.toString() + ChatColor.BOLD + "GM " + ChatColor.AQUA);
			Team dev = sb.registerNewTeam("dev");
			dev.setPrefix(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "DEV " + ChatColor.DARK_AQUA);
			Objective o = sb.registerNewObjective("showHealth", "health");
			o.setDisplaySlot(DisplaySlot.BELOW_NAME);
			o.setDisplayName(ChatColor.RED + "\u2764");
			boards.put(p, sb);
			return sb;
		}
		return boards.get(p);
	}

	public static void updateAllColors() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Scoreboard sb = getBoard(p);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				//if (pl.getName().equalsIgnoreCase("bpweber")) {
				//	sb.getTeam("dev").addPlayer(pl);
				//} else 
				if (pl.isOp()) {
					sb.getTeam("gm").addPlayer(pl);
				} else {
					if (Alignments.neutral.containsKey(pl.getName())) {
						sb.getTeam("yellow").addPlayer(pl);
					} else if (Alignments.chaotic.containsKey(pl.getName())) {
						sb.getTeam("red").addPlayer(pl);
					} else {
						sb.getTeam("white").addPlayer(pl);
					}
				}
			}
			p.setScoreboard(sb);
			boards.put(p, sb);
		}
	}

	@SuppressWarnings("deprecation")
	public static void updatePlayerHealth() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Scoreboard sb = getBoard(p);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				Objective o = sb.getObjective(DisplaySlot.BELOW_NAME);
				o.getScore(pl).setScore((int) pl.getHealth());
			}
			p.setScoreboard(sb);
			boards.put(p, sb);
		}
	}
}
