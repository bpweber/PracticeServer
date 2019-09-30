package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateDrop {
	public static ItemStack createDrop(int tier, int item, int rarity) {
		// 1 sword, 2 axe, 3 helm
		String name = "";
		String rare = "";

		ItemStack is = new ItemStack(Material.AIR);
		List<String> lore = new ArrayList<String>();

		Random random = new Random();
		// 0=common,1=uncommon etc.
		int armdps = random.nextInt((2 - 1) + 1) + 1;
		int nrghp = random.nextInt((2 - 1) + 1) + 1;
		int elem = random.nextInt((12 - 1) + 1) + 1;
		int pure = random.nextInt((4 - 1) + 1) + 1;
		int life = random.nextInt((4 - 1) + 1) + 1;
		int crit = random.nextInt((4 - 1) + 1) + 1;
		int acc = random.nextInt((4 - 1) + 1) + 1;
		int dodge = random.nextInt((3 - 1) + 1) + 1;
		int block = random.nextInt((3 - 1) + 1) + 1;
		int vit = random.nextInt((3 - 1) + 1) + 1;
		int str = random.nextInt((3 - 1) + 1) + 1;
		int intel = random.nextInt((3 - 1) + 1) + 1;

		int hp = 0;
		int mindmg = 0;
		int maxdmg = 0;
		int dpsamt = 0;
		int dodgeamt = 0;
		int blockamt = 0;
		int vitamt = 0;
		int intamt = 0;
		int stramt = 0;
		int elemamt = 0;
		int pureamt = 0;
		int lifeamt = 0;
		int critamt = 0;
		int accamt = 0;
		int hps = 0;
		int nrg = 0;
		// is it unique/common/uncommon, etc.

		if (rarity == 3) {
			rare = "" + ChatColor.YELLOW + ChatColor.ITALIC + "Unique";
		} else if (rarity == 2) {
			rare = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
		} else if (rarity == 1) {
			rare = "" + ChatColor.GREEN + ChatColor.ITALIC + "Uncommon";
		} else if (rarity == 0) {
			rare = "" + ChatColor.GRAY + ChatColor.ITALIC + "Common";
		}
		if (tier == 1) {
			tier = 1;
			if (rarity == 0) {
				dpsamt = 1;
				hp = random.nextInt((30 - 10) + 1) + 10;
				int min_min_dmg = 1;
				int max_min_dmg = 2 + 1;
				int max_max_dmg = 5 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 1) {
				dpsamt = random.nextInt((3 - 1) + 1) + 1;
				hp = random.nextInt((60 - 31) + 1) + 31;
				int min_min_dmg = 3;
				int max_min_dmg = 5 + 1;
				int max_max_dmg = 8 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 2) {
				dpsamt = random.nextInt((3 - 1) + 1) + 1;
				hp = random.nextInt((90 - 61) + 1) + 61;
				int min_min_dmg = 6;
				int max_min_dmg = 9 + 1;
				int max_max_dmg = 23 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 3) {
				dpsamt = random.nextInt((3 - 1) + 1) + 1;
				hp = random.nextInt((120 - 91) + 1) + 91;
				int min_min_dmg = 9;
				int max_min_dmg = 15 + 1;
				int max_max_dmg = 24 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			hps = random.nextInt((15 - 5) + 1) + 5;
			nrg = random.nextInt((3 - 1) + 1) + 1;
			dodgeamt = random.nextInt((5 - 1) + 1) + 1;
			blockamt = random.nextInt((5 - 1) + 1) + 1;
			vitamt = random.nextInt((15 - 1) + 1) + 1;
			stramt = random.nextInt((15 - 1) + 1) + 1;
			intamt = random.nextInt((15 - 1) + 1) + 1;
			elemamt = random.nextInt((4 - 1) + 1) + 1;
			pureamt = random.nextInt((4 - 1) + 1) + 1;
			lifeamt = random.nextInt((30 - 1) + 1) + 1;
			critamt = random.nextInt((3 - 1) + 1) + 1;
			accamt = random.nextInt((10 - 1) + 1) + 1;
			if (item == 0) {
				name = "Staff";
				is.setType(Material.WOOD_HOE);
			}
			if (item == 1) {
				name = "Spear";
				is.setType(Material.WOOD_SPADE);
			}
			if (item == 2) {
				name = "Shortsword";
				is.setType(Material.WOOD_SWORD);
			}
			if (item == 3) {
				name = "Hatchet";
				is.setType(Material.WOOD_AXE);
			}
			if (item == 4) {
				name = "Leather Coif";
				is.setType(Material.LEATHER_HELMET);
			}
			if (item == 5) {
				name = "Leather Chestplate";
				is.setType(Material.LEATHER_CHESTPLATE);
			}
			if (item == 6) {
				name = "Leather Leggings";
				is.setType(Material.LEATHER_LEGGINGS);
			}
			if (item == 7) {
				name = "Leather Boots";
				is.setType(Material.LEATHER_BOOTS);
			}
		}
		if (tier == 2) {
			tier = 2;
			// name = "" + ChatColor.GREEN;
			if (rarity == 0) {
				dpsamt = random.nextInt((3 - 1) + 1) + 1;
				hp = random.nextInt((110 - 70) + 1) + 70;
				int min_min_dmg = 10;
				int max_min_dmg = 12 + 1;
				int max_max_dmg = 17 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 1) {
				dpsamt = random.nextInt((5 - 3) + 1) + 3;
				hp = random.nextInt((190 - 111) + 1) + 111;
				int min_min_dmg = 16;
				int max_min_dmg = 18 + 1;
				int max_max_dmg = 24 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 2) {
				dpsamt = random.nextInt((7 - 5) + 1) + 5;
				hp = random.nextInt((240 - 191) + 1) + 191;
				int min_min_dmg = 20;
				int max_min_dmg = 30 + 1;
				int max_max_dmg = 65 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 3) {
				dpsamt = random.nextInt((7 - 5) + 1) + 5;
				hp = random.nextInt((310 - 241) + 1) + 241;
				int min_min_dmg = 22;
				int max_min_dmg = 35 + 1;
				int max_max_dmg = 70 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			hps = random.nextInt((25 - 10) + 1) + 10;
			nrg = random.nextInt((3 - 1) + 1) + 1;
			dodgeamt = random.nextInt((8 - 1) + 1) + 1;
			blockamt = random.nextInt((8 - 1) + 1) + 1;
			vitamt = random.nextInt((35 - 1) + 1) + 1;
			stramt = random.nextInt((35 - 1) + 1) + 1;
			intamt = random.nextInt((35 - 1) + 1) + 1;
			elemamt = random.nextInt((9 - 1) + 1) + 1;
			pureamt = random.nextInt((9 - 1) + 1) + 1;
			lifeamt = random.nextInt((15 - 1) + 1) + 1;
			critamt = random.nextInt((6 - 1) + 1) + 1;
			accamt = random.nextInt((12 - 1) + 1) + 1;
			if (item == 0) {
				name = "Battletaff";
				is.setType(Material.STONE_HOE);
			}
			if (item == 1) {
				name = "Halberd";
				is.setType(Material.STONE_SPADE);
			}
			if (item == 2) {
				name = "Broadsword";
				is.setType(Material.STONE_SWORD);
			}
			if (item == 3) {
				name = "Great Axe";
				is.setType(Material.STONE_AXE);
			}
			if (item == 4) {
				name = "Medium Helmet";
				is.setType(Material.CHAINMAIL_HELMET);
			}
			if (item == 5) {
				name = "Chainmail";
				is.setType(Material.CHAINMAIL_CHESTPLATE);
			}
			if (item == 6) {
				name = "Chainmail Leggings";
				is.setType(Material.CHAINMAIL_LEGGINGS);
			}
			if (item == 7) {
				name = "Chainmail Boots";
				is.setType(Material.CHAINMAIL_BOOTS);
			}
		}
		if (tier == 3) {
			tier = 3;
			// name = "" + ChatColor.AQUA;
			if (rarity == 0) {
				dpsamt = random.nextInt((7 - 5) + 1) + 5;
				hp = random.nextInt((450 - 200) + 1) + 200;
				int min_min_dmg = 25;
				int max_min_dmg = 30 + 1;
				int max_max_dmg = 45 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 1) {
				dpsamt = random.nextInt((10 - 6) + 1) + 6;
				hp = random.nextInt((650 - 451) + 1) + 451;
				int min_min_dmg = 30;
				int max_min_dmg = 35 + 1;
				int max_max_dmg = 70 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 2) {
				dpsamt = random.nextInt((10 - 8) + 1) + 8;
				hp = random.nextInt((749 - 651) + 1) + 651;
				int min_min_dmg = 50;
				int max_min_dmg = 90 + 1;
				int max_max_dmg = 150 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 3) {
				dpsamt = random.nextInt((11 - 8) + 1) + 8;
				hp = random.nextInt((850 - 750) + 1) + 750;
				int min_min_dmg = 60;
				int max_min_dmg = 100 + 1;
				int max_max_dmg = 160 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			hps = random.nextInt((55 - 35) + 1) + 35;
			nrg = random.nextInt((4 - 2) + 1) + 2;
			dodgeamt = random.nextInt((10 - 1) + 1) + 1;
			blockamt = random.nextInt((10 - 1) + 1) + 1;
			vitamt = random.nextInt((75 - 1) + 1) + 1;
			stramt = random.nextInt((75 - 1) + 1) + 1;
			intamt = random.nextInt((75 - 1) + 1) + 1;
			elemamt = random.nextInt((15 - 1) + 1) + 1;
			pureamt = random.nextInt((15 - 1) + 1) + 1;
			lifeamt = random.nextInt((12 - 1) + 1) + 1;
			critamt = random.nextInt((8 - 1) + 1) + 1;
			accamt = random.nextInt((25 - 1) + 1) + 1;
			if (item == 0) {
				name = "Wizard Staff";
				is.setType(Material.IRON_HOE);
			}
			if (item == 1) {
				name = "Magic Polearm";
				is.setType(Material.IRON_SPADE);
			}
			if (item == 2) {
				name = "Magic Sword";
				is.setType(Material.IRON_SWORD);
			}
			if (item == 3) {
				name = "War Axe";
				is.setType(Material.IRON_AXE);
			}
			if (item == 4) {
				name = "Full Helmet";
				is.setType(Material.IRON_HELMET);
			}
			if (item == 5) {
				name = "Platemail";
				is.setType(Material.IRON_CHESTPLATE);
			}
			if (item == 6) {
				name = "Platemail Leggings";
				is.setType(Material.IRON_LEGGINGS);
			}
			if (item == 7) {
				name = "Platemail Boots";
				is.setType(Material.IRON_BOOTS);
			}
		}
		if (tier == 4) {
			tier = 4;
			// name = "" + ChatColor.LIGHT_PURPLE;
			if (rarity == 0) {
				dpsamt = random.nextInt((10 - 8) + 1) + 8;
				hp = random.nextInt((960 - 650) + 1) + 650;
				int min_min_dmg = 65;
				int max_min_dmg = 80 + 1;
				int max_max_dmg = 125 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 1) {
				dpsamt = random.nextInt((12 - 10) + 1) + 10;
				hp = random.nextInt((1450 - 961) + 1) + 961;
				int min_min_dmg = 70;
				int max_min_dmg = 85 + 1;
				int max_max_dmg = 155 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 2) {
				dpsamt = random.nextInt((13 - 11) + 1) + 11;
				hp = random.nextInt((2300 - 1451) + 1) + 1451;
				int min_min_dmg = 90;
				int max_min_dmg = 110 + 1;
				int max_max_dmg = 220 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 3) {
				dpsamt = random.nextInt((14 - 12) + 1) + 12;
				hp = random.nextInt((2800 - 2301) + 1) + 2301;
				int min_min_dmg = 110;
				int max_min_dmg = 150 + 1;
				int max_max_dmg = 240 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			hps = random.nextInt((75 - 60) + 1) + 60;
			nrg = random.nextInt((5 - 3) + 1) + 3;
			dodgeamt = random.nextInt((12 - 1) + 1) + 1;
			blockamt = random.nextInt((12 - 1) + 1) + 1;
			vitamt = random.nextInt((115 - 1) + 1) + 1;
			stramt = random.nextInt((115 - 1) + 1) + 1;
			intamt = random.nextInt((115 - 1) + 1) + 1;
			elemamt = random.nextInt((25 - 1) + 1) + 1;
			pureamt = random.nextInt((25 - 1) + 1) + 1;
			lifeamt = random.nextInt((10 - 1) + 1) + 1;
			critamt = random.nextInt((10 - 1) + 1) + 1;
			accamt = random.nextInt((28 - 1) + 1) + 1;
			if (item == 0) {
				name = "Ancient Staff";
				is.setType(Material.DIAMOND_HOE);
			}
			if (item == 1) {
				name = "Ancient Polearm";
				is.setType(Material.DIAMOND_SPADE);
			}
			if (item == 2) {
				name = "Ancient Sword";
				is.setType(Material.DIAMOND_SWORD);
			}
			if (item == 3) {
				name = "Ancient Axe";
				is.setType(Material.DIAMOND_AXE);
			}
			if (item == 4) {
				name = "Ancient Full Helmet";
				is.setType(Material.DIAMOND_HELMET);
			}
			if (item == 5) {
				name = "Magic Platemail";
				is.setType(Material.DIAMOND_CHESTPLATE);
			}
			if (item == 6) {
				name = "Magic Platemail Leggings";
				is.setType(Material.DIAMOND_LEGGINGS);
			}
			if (item == 7) {
				name = "Magic Platemail Boots";
				is.setType(Material.DIAMOND_BOOTS);
			}
		}
		if (tier == 5) {
			tier = 5;
			// name = "" + ChatColor.YELLOW;
			if (rarity == 0) {
				dpsamt = random.nextInt((13 - 11) + 1) + 11;
				hp = random.nextInt((2500 - 1450) + 1) + 1450;
				int min_min_dmg = 130;
				int max_min_dmg = 140 + 1;
				int max_max_dmg = 210 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 1) {
				dpsamt = random.nextInt((15 - 13) + 1) + 13;
				hp = random.nextInt((3800 - 2501) + 1) + 2501;
				int min_min_dmg = 150;
				int max_min_dmg = 160 + 1;
				int max_max_dmg = 260 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 2) {
				dpsamt = random.nextInt((18 - 16) + 1) + 16;
				hp = random.nextInt((5500 - 3801) + 1) + 3801;
				int min_min_dmg = 160;
				int max_min_dmg = 230 + 1;
				int max_max_dmg = 407 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			if (rarity == 3) {
				dpsamt = random.nextInt((19 - 17) + 1) + 17;
				hp = random.nextInt((6000 - 5501) + 1) + 5501;
				int min_min_dmg = 190;
				int max_min_dmg = 250 + 1;
				int max_max_dmg = 450 + 1;
				mindmg = random.nextInt(max_min_dmg - min_min_dmg) + min_min_dmg;
				maxdmg = random.nextInt(max_max_dmg - mindmg) + mindmg;
			}
			hps = random.nextInt((120 - 80) + 1) + 80;
			nrg = random.nextInt((5 - 3) + 1) + 3;
			dodgeamt = random.nextInt((12 - 1) + 1) + 1;
			blockamt = random.nextInt((12 - 1) + 1) + 1;
			vitamt = random.nextInt((315 - 1) + 1) + 1;
			stramt = random.nextInt((315 - 1) + 1) + 1;
			intamt = random.nextInt((315 - 1) + 1) + 1;
			elemamt = random.nextInt((55 - 1) + 1) + 1;
			pureamt = random.nextInt((55 - 1) + 1) + 1;
			lifeamt = random.nextInt((8 - 1) + 1) + 1;
			critamt = random.nextInt((11 - 1) + 1) + 1;
			accamt = random.nextInt((35 - 1) + 1) + 1;
			if (item == 0) {
				name = "Legendary Staff";
				is.setType(Material.GOLD_HOE);
			}
			if (item == 1) {
				name = "Legendary Polearm";
				is.setType(Material.GOLD_SPADE);
			}
			if (item == 2) {
				name = "Legendary Sword";
				is.setType(Material.GOLD_SWORD);
			}
			if (item == 3) {
				name = "Legendary Axe";
				is.setType(Material.GOLD_AXE);
			}
			if (item == 4) {
				name = "Legendary Full Helmet";
				is.setType(Material.GOLD_HELMET);
			}
			if (item == 5) {
				name = "Legendary Platemail";
				is.setType(Material.GOLD_CHESTPLATE);
			}
			if (item == 6) {
				name = "Legendary Platemail Leggings";
				is.setType(Material.GOLD_LEGGINGS);
			}
			if (item == 7) {
				name = "Legendary Platemail Boots";
				is.setType(Material.GOLD_BOOTS);
			}
		}
		if (item == 0 || item == 1) {
			mindmg *= .5;
			if (mindmg < 1)
				mindmg = 1;
			maxdmg *= .5;
			if (maxdmg < 1)
				maxdmg = 1;
			lore.add(ChatColor.RED + "DMG: " + mindmg + " - " + maxdmg);
			if (life == 1) {
				lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
				name = "Vampyric " + name;
			}
			if (crit == 1) {
				lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
				name = "Deadly " + name;
			}
			if (elem == 3) {
				lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
				name = name + " of Ice";
			}
			if (elem == 2) {
				lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
				name = name + " of Poison";
			}
			if (elem == 1) {
				lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
				name = name + " of Fire";
			}
		}
		if (item == 2) {
			lore.add(ChatColor.RED + "DMG: " + mindmg + " - " + maxdmg);
			if (acc == 1) {
				lore.add(ChatColor.RED + "ACCURACY: " + accamt + "%");
				name = "Accurate " + name;
			}
			if (life == 1) {
				lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
				name = "Vampyric " + name;
			}
			if (crit == 1) {
				lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
				name = "Deadly " + name;
			}
			if (elem == 3) {
				lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
				name = name + " of Ice";
			}
			if (elem == 2) {
				lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
				name = name + " of Poison";
			}
			if (elem == 1) {
				lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
				name = name + " of Fire";
			}
		}
		if (item == 3) {
			lore.add(ChatColor.RED + "DMG: " + (int) (mindmg * 1.2) + " - " + (int) (maxdmg * 1.2));
			if (pure == 1) {
				lore.add(ChatColor.RED + "PURE DMG: +" + pureamt);
				name = "Pure " + name;
			}
			if (life == 1) {
				lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
				name = "Vampyric " + name;
			}
			if (crit == 1) {
				lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
				name = "Deadly " + name;
			}
			if (elem == 3) {
				lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
				name = name + " of Ice";
			}
			if (elem == 2) {
				lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
				name = name + " of Poison";
			}
			if (elem == 1) {
				lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
				name = name + " of Fire";
			}
		}
		if (item == 4 || item == 7) {
			dpsamt *= .5;
			if (dpsamt < 1)
				dpsamt = 1;
			hp *= .5;
			if (hp < 1)
				hp = 1;
			if (armdps == 1)
				lore.add(ChatColor.RED + "ARMOR: " + dpsamt + " - " + dpsamt + "%");
			if (armdps == 2)
				lore.add(ChatColor.RED + "DPS: " + dpsamt + " - " + dpsamt + "%");
			lore.add(ChatColor.RED + "HP: +" + hp);
			if (nrghp == 2)
				lore.add(ChatColor.RED + "ENERGY REGEN: +" + nrg + "%");
			if (nrghp == 1)
				lore.add(ChatColor.RED + "HP REGEN: +" + hps + " HP/s");
			if (intel == 1)
				lore.add(ChatColor.RED + "INT: +" + intamt);
			if (str == 1)
				lore.add(ChatColor.RED + "STR: +" + stramt);
			if (vit == 1)
				lore.add(ChatColor.RED + "VIT: +" + vitamt);
			if (dodge == 1) {
				lore.add(ChatColor.RED + "DODGE: " + dodgeamt + "%");
				name = "Agile " + name;
			}
			if (nrghp == 1) {
				name = "Mending " + name;
			}
			if (block == 1) {
				lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
				name = "Protective " + name;
			}
			if (nrghp == 2) {
				name = name + " of Fortitude";
			}
		}
		if (item == 5 || item == 6) {
			if (armdps == 1)
				lore.add(ChatColor.RED + "ARMOR: " + dpsamt + " - " + dpsamt + "%");
			if (armdps == 2)
				lore.add(ChatColor.RED + "DPS: " + dpsamt + " - " + dpsamt + "%");
			lore.add(ChatColor.RED + "HP: +" + hp);
			if (nrghp == 2)
				lore.add(ChatColor.RED + "ENERGY REGEN: +" + nrg + "%");
			if (nrghp == 1)
				lore.add(ChatColor.RED + "HP REGEN: +" + hps + " HP/s");
			if (intel == 1)
				lore.add(ChatColor.RED + "INT: +" + intamt);
			if (str == 1)
				lore.add(ChatColor.RED + "STR: +" + stramt);
			if (vit == 1)
				lore.add(ChatColor.RED + "VIT: +" + vitamt);
			if (dodge == 1) {
				lore.add(ChatColor.RED + "DODGE: " + dodgeamt + "%");
				name = "Agile " + name;
			}
			if (nrghp == 1) {
				name = "Mending " + name;
			}
			if (block == 1) {
				lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
				name = "Protective " + name;
			}
			if (nrghp == 2) {
				name = name + " of Fortitude";
			}
		}
		lore.add(rare);
		if (tier == 1)
			name = ChatColor.WHITE + name;
		if (tier == 2)
			name = ChatColor.GREEN + name;
		if (tier == 3)
			name = ChatColor.AQUA + name;
		if (tier == 4)
			name = ChatColor.LIGHT_PURPLE + name;
		if (tier == 5)
			name = ChatColor.YELLOW + name;
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}
