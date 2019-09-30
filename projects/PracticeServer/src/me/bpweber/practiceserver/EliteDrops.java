package me.bpweber.practiceserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EliteDrops {

	public static ItemStack createCustomEliteDrop(String mobname) {
		String name = "";
		String llore = ChatColor.GRAY.toString();
		ItemStack is = new ItemStack(Material.AIR);
		List<String> lore = new ArrayList<String>();

		Random random = new Random();

		int item = random.nextInt((8 - 1) + 1) + 1;

		int tier = 0;

		String rarity = "" + ChatColor.YELLOW + ChatColor.ITALIC + "Unique";

		int armdps = 0;
		int nrghp = 0;
		int elem = 0;
		int pure = 0;
		int life = 0;
		int crit = 0;
		int acc = 0;
		int dodge = 0;
		int block = 0;
		int vit = 0;
		int str = 0;
		int intel = 0;
		int hp = 0;
		int mindmg = 0;
		int maxdmg = 0;
		int dpsamt = 0;
		int dodgeamt = 0;
		int blockamt = 0;
		int vitamt = 0;
		int stramt = 0;
		int intamt = 0;
		int elemamt = 0;
		int pureamt = 0;
		int lifeamt = 0;
		int critamt = 0;
		int accamt = 0;
		int hps = 0;
		int nrg = 0;
		if (mobname.equalsIgnoreCase("mitsuki")) {
			nrghp = 2;
			armdps = 1;
			block = 1;
			str = 1;
			elem = 1;
			block = 1;
			life = 1;
			elemamt = 5;
			lifeamt = random.nextInt((45 - 30) + 1) + 30;
			mindmg = random.nextInt((12 - 6) + 1) + 6;
			maxdmg = random.nextInt((25 - 17) + 1) + 17;
			if (item <= 4) {
				name = "Mitsukis Sword of Bloodthirst";
				is.setType(Material.WOOD_SWORD);
				llore += "The Master of Ruins blood-stained ridged Sword.";
			}
			if (item == 5) {
				nrg = random.nextInt((3 - 2) + 1) + 2;
				blockamt = random.nextInt((3 - 2) + 1) + 2;
				dpsamt = 1;
				stramt = 10;
				hp = random.nextInt((60 - 40) + 1) + 40;
				name = "Mitsukis Leather Coif";
				is.setType(Material.LEATHER_HELMET);
				llore += "A ripped remains of a Leather Coif far from industry standards.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 6) {
				nrg = random.nextInt((5 - 4) + 1) + 4;
				blockamt = random.nextInt((6 - 5) + 1) + 5;
				stramt = 25;
				dpsamt = random.nextInt((3 - 2) + 1) + 2;
				hp = random.nextInt((110 - 100) + 1) + 100;
				name = "Mitsukis Dirty Leather Rags";
				is.setType(Material.LEATHER_CHESTPLATE);
				llore += "Blood stained rags that reek of Zombie flesh";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 7) {
				nrg = random.nextInt((5 - 4) + 1) + 4;
				blockamt = random.nextInt((6 - 5) + 1) + 5;
				stramt = 25;
				dpsamt = random.nextInt((3 - 2) + 1) + 2;
				hp = random.nextInt((110 - 100) + 1) + 100;
				name = "Mitsukis Ripped Leather Pants";
				is.setType(Material.LEATHER_LEGGINGS);
				llore += "Can be referred to as 'shorts' due to intensive ripping.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 8) {
				nrg = random.nextInt((3 - 2) + 1) + 2;
				blockamt = random.nextInt((3 - 2) + 1) + 2;
				dpsamt = 1;
				stramt = 10;
				hp = random.nextInt((60 - 40) + 1) + 40;
				name = "Mitsukis Leather Sandals";
				is.setType(Material.LEATHER_BOOTS);
				llore += "Blood stained sandals. Not very comfortable.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			tier = 1;
		}
		if (mobname.equalsIgnoreCase("copjak")) {
			nrghp = 2;
			armdps = 1;
			str = 1;
			elem = 2;
			elemamt = 12;
			crit = 1;
			critamt = random.nextInt((10 - 8) + 1) + 8;
			mindmg = random.nextInt((12 - 10) + 1) + 10;
			maxdmg = random.nextInt((18 - 13) + 1) + 13;
			if (item <= 4) {
				name = "Cop'Jaks Deadly Poleaxe";
				is.setType(Material.STONE_SPADE);
				llore += "A long wicked Poleaxe of Trollish design.";
			}
			if (item == 5) {
				stramt = 25;
				nrg = random.nextInt((5 - 3) + 1) + 3;
				hp = random.nextInt((170 - 140) + 1) + 140;
				dpsamt = random.nextInt((4 - 3) + 1) + 3;
				name = "Cop'Jaks Shaman Headgear";
				is.setType(Material.CHAINMAIL_HELMET);
				llore += "A standard Shamans headgear consisting of a bears head.";
			}
			if (item == 6) {
				stramt = 45;
				nrg = random.nextInt((8 - 7) + 1) + 7;
				hp = random.nextInt((370 - 300) + 1) + 300;
				dpsamt = random.nextInt((7 - 6) + 1) + 6;
				name = "Cop'Jaks greased Chainmail Chestpiece";
				is.setType(Material.CHAINMAIL_CHESTPLATE);
				llore += "A bad fit made for the broad chests of Trolls.";
			}
			if (item == 7) {
				stramt = 45;
				nrg = random.nextInt((8 - 7) + 1) + 7;
				hp = random.nextInt((370 - 300) + 1) + 300;
				dpsamt = random.nextInt((7 - 6) + 1) + 6;
				name = "Cop'Jaks Chainlinked Pants";
				is.setType(Material.CHAINMAIL_LEGGINGS);
				llore += "Large greased and ready for action.";
			}
			if (item == 8) {
				stramt = 25;
				nrg = random.nextInt((5 - 3) + 1) + 3;
				hp = random.nextInt((170 - 140) + 1) + 140;
				dpsamt = random.nextInt((4 - 3) + 1) + 3;
				name = "Cop'Jaks Chainmail Boots";
				is.setType(Material.CHAINMAIL_BOOTS);
				llore += "Spiked Chainmail boots.";
			}
			tier = 2;
		}
		if (mobname.equalsIgnoreCase("impa")) {
			nrghp = 2;
			armdps = 1;
			str = 1;
			block = 1;
			elem = 2;
			crit = 1;
			elemamt = 15;
			critamt = random.nextInt((9 - 8) + 1) + 8;
			mindmg = random.nextInt((50 - 40) + 1) + 40;
			maxdmg = random.nextInt((80 - 70) + 1) + 70;
			if (item <= 4) {
				name = "Impas Dreaded Polearm";
				is.setType(Material.IRON_SPADE);
				llore += "The spearhead of the initial attack on Avalon.";
			}
			if (item == 5) {
				stramt = 75;
				blockamt = 8;
				nrg = random.nextInt((5 - 3) + 1) + 3;
				hp = random.nextInt((460 - 375) + 1) + 375;
				dpsamt = random.nextInt((6 - 5) + 1) + 5;
				name = "Crooked Battle Mask";
				is.setType(Material.IRON_HELMET);
				llore += "A skeleton generals black mask";
			}
			if (item == 6) {
				stramt = 75;
				blockamt = 8;
				nrg = random.nextInt((6 - 5) + 1) + 5;
				hp = random.nextInt((799 - 701) + 1) + 701;
				dpsamt = random.nextInt((6 - 5) + 1) + 5;
				name = "Haunting Platemail of Avalons Fright";
				is.setType(Material.IRON_CHESTPLATE);
				llore += "A breastplate with the symbol of Impas army carved into it.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 7) {
				stramt = 75;
				blockamt = 8;
				nrg = random.nextInt((6 - 5) + 1) + 5;
				hp = random.nextInt((799 - 701) + 1) + 701;
				dpsamt = random.nextInt((6 - 5) + 1) + 5;
				name = "Warding Skeletal Leggings";
				is.setType(Material.IRON_LEGGINGS);
				llore += "Spiked bone leggings of greater skeleton invaders.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 8) {
				stramt = 75;
				blockamt = 8;
				nrg = random.nextInt((5 - 3) + 1) + 3;
				hp = random.nextInt((460 - 375) + 1) + 375;
				dpsamt = random.nextInt((6 - 5) + 1) + 5;
				name = "Skeletal Death Walkers";
				is.setType(Material.IRON_BOOTS);
				llore += "The boots with which Impa treaded into this land.";
			}
			tier = 3;
		}
		if (mobname.equalsIgnoreCase("skeletonking")) {
			nrghp = 1;
			armdps = 1;
			vit = 1;
			pure = 1;
			acc = 1;
			pureamt = 25;
			accamt = random.nextInt((14 - 7) + 1) + 7;
			mindmg = random.nextInt((50 - 45) + 1) + 45;
			maxdmg = random.nextInt((90 - 67) + 1) + 67;
			if (item <= 4) {
				name = "The Skeleton Kings Sword of Banishment";
				is.setType(Material.IRON_SWORD);
				llore += "A powerful sword enhanced with the soul of the Skeleton King.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 5) {
				vitamt = 49;
				hps = random.nextInt((40 - 30) + 1) + 30;
				hp = random.nextInt((459 - 400) + 1) + 400;
				dpsamt = random.nextInt((5 - 4) + 1) + 4;
				name = "The Skeleton Kings Soul Helmet";
				is.setType(Material.IRON_HELMET);
				llore += "A shadowy transparent helmet.";
			}
			if (item == 6) {
				vitamt = 99;
				hps = random.nextInt((70 - 60) + 1) + 60;
				hp = random.nextInt((999 - 800) + 1) + 800;
				dpsamt = 8;
				name = "The Skeleton Kings Soul Armour";
				is.setType(Material.IRON_CHESTPLATE);
				llore += "Armor imbued with the power of the Skeleton king.";
			}
			if (item == 7) {
				vitamt = 99;
				hps = random.nextInt((70 - 60) + 1) + 60;
				hp = random.nextInt((999 - 800) + 1) + 800;
				dpsamt = 8;
				name = "The Skeleton Kings Soul Leggings";
				is.setType(Material.IRON_LEGGINGS);
				llore += "Resistant to the most powerful of Physical Damage.";
			}
			if (item == 8) {
				vitamt = 49;
				hps = random.nextInt((40 - 30) + 1) + 30;
				hp = random.nextInt((459 - 400) + 1) + 400;
				dpsamt = random.nextInt((5 - 4) + 1) + 4;
				name = "The Skeleton Kings Soul Boots";
				is.setType(Material.IRON_BOOTS);
				llore += "The shining boots of a king.";
			}
			tier = 3;
		}
		if (mobname.equalsIgnoreCase("blayshan")) {
			nrghp = 2;
			armdps = 2;
			elem = 3;
			str = 1;
			elemamt = 70;
			crit = 1;
			critamt = random.nextInt((10 - 8) + 1) + 8;
			mindmg = random.nextInt((190 - 180) + 1) + 180;
			maxdmg = random.nextInt((240 - 210) + 1) + 210;
			if (item <= 4) {
				name = "Blayshans Wicked Axe";
				is.setType(Material.DIAMOND_AXE);
				llore += "An Axe with the face of the cursed Blayshan carved into it.";
			}
			if (item == 5) {
				dpsamt = random.nextInt((7 - 5) + 1) + 5;
				stramt = 145;
				hp = random.nextInt((1200 - 800) + 1) + 800;
				nrg = random.nextInt((5 - 4) + 1) + 4;
				name = "Blayshans Accursed Helmet";
				is.setType(Material.DIAMOND_HELMET);
				llore += "A weirdly shaped aqua blue helmet.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 6) {
				dpsamt = random.nextInt((7 - 6) + 1) + 6;
				stramt = 245;
				hp = random.nextInt((1900 - 1800) + 1) + 1800;
				nrg = random.nextInt((10 - 8) + 1) + 8;
				name = "Blayshans Wicked Horned Platemail";
				is.setType(Material.DIAMOND_CHESTPLATE);
				llore += "Not well made but light with studded mail fists.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 7) {
				dpsamt = random.nextInt((7 - 6) + 1) + 6;
				stramt = 245;
				hp = random.nextInt((1900 - 1800) + 1) + 1800;
				nrg = random.nextInt((10 - 8) + 1) + 8;
				name = "Blayshans Wicked Horned Leggings";
				is.setType(Material.DIAMOND_LEGGINGS);
				llore += "Glistening with the blood of fallen enemies.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 8) {
				dpsamt = random.nextInt((7 - 5) + 1) + 5;
				stramt = 145;
				hp = random.nextInt((1200 - 800) + 1) + 800;
				nrg = random.nextInt((5 - 4) + 1) + 4;
				name = "Blayshans Platemail Boots";
				is.setType(Material.DIAMOND_BOOTS);
				llore += "A pair of boots shaped to fit a Naga.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			tier = 4;
		}
		if (mobname.equalsIgnoreCase("kilatan")) {
			nrghp = 2;
			armdps = 2;
			elem = 1;
			intel = 1;
			dodge = 1;
			elemamt = 30;
			mindmg = random.nextInt((150 - 140) + 1) + 140;
			maxdmg = random.nextInt((180 - 170) + 1) + 170;
			if (item <= 4) {
				name = "Kilatans Staff of Destruction";
				is.setType(Material.GOLD_HOE);
				llore += "A powerful staff imbued with the magics of Kilatan";
			}
			if (item == 5) {
				dpsamt = random.nextInt((7 - 6) + 1) + 6;
				intamt = 145;
				hp = random.nextInt((2000 - 1400) + 1) + 1400;
				nrg = random.nextInt((8 - 3) + 1) + 3;
				dodgeamt = random.nextInt((7 - 5) + 1) + 5;
				name = "Kilatans Crown of Death";
				is.setType(Material.GOLD_HELMET);
				llore += "A golden crown of tyranny and power.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 6) {
				dpsamt = random.nextInt((15 - 12) + 1) + 12;
				intamt = 345;
				hp = random.nextInt((3900 - 2800) + 1) + 2800;
				nrg = random.nextInt((10 - 6) + 1) + 6;
				dodgeamt = random.nextInt((20 - 10) + 1) + 10;
				name = "Kilatans Legendary Platemail";
				is.setType(Material.GOLD_CHESTPLATE);
				llore += "The Legendary platemail piece of the Demon Lord Kilatan.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 7) {
				dpsamt = random.nextInt((15 - 12) + 1) + 12;
				intamt = 345;
				hp = random.nextInt((3900 - 2800) + 1) + 2800;
				nrg = random.nextInt((10 - 6) + 1) + 6;
				dodgeamt = random.nextInt((20 - 10) + 1) + 10;
				name = "Kilatans Legendary Leggings";
				is.setType(Material.GOLD_LEGGINGS);
				llore += "You can feel the power emanating from this armor piece.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			if (item == 8) {
				dpsamt = random.nextInt((7 - 6) + 1) + 6;
				intamt = 145;
				hp = random.nextInt((2000 - 1400) + 1) + 1400;
				nrg = random.nextInt((8 - 3) + 1) + 3;
				dodgeamt = random.nextInt((7 - 5) + 1) + 5;
				name = "Kilatans Legendary Boots";
				is.setType(Material.GOLD_BOOTS);
				llore += "Boots that carried the weight of the underworld.";
				rarity = "" + ChatColor.AQUA + ChatColor.ITALIC + "Rare";
			}
			tier = 5;
		}
		if (item <= 4) {
			lore.add(ChatColor.RED + "DMG: " + mindmg + " - " + maxdmg);
			if (pure == 1)
				lore.add(ChatColor.RED + "PURE DMG: +" + pureamt);
			if (acc == 1)
				lore.add(ChatColor.RED + "ACCURACY: " + accamt + "%");
			if (life == 1) {
				lore.add(ChatColor.RED + "LIFE STEAL: " + lifeamt + "%");
			}
			if (crit == 1) {
				lore.add(ChatColor.RED + "CRITICAL HIT: " + critamt + "%");
			}
			if (elem == 3) {
				lore.add(ChatColor.RED + "ICE DMG: +" + elemamt);
			}
			if (elem == 2) {
				lore.add(ChatColor.RED + "POISON DMG: +" + elemamt);
			}
			if (elem == 1) {
				lore.add(ChatColor.RED + "FIRE DMG: +" + elemamt);
			}
		}
		if (item == 5 || item == 6 || item == 7 || item == 8) {
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
			}
			if (block == 1) {
				lore.add(ChatColor.RED + "BLOCK: " + blockamt + "%");
			}
		}
		lore.add(llore);
		lore.add(rarity);
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
