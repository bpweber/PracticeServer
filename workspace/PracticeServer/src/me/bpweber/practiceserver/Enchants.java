package me.bpweber.practiceserver;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchants implements Listener {

	public static Enchantment glow = new GlowEnchant(69);

	public void onEnable() {
		Main.log.info("[Enchants] has been enabled.");
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		registerNewEnchantment();
	}

	public void onDisable() {
		Main.log.info("[Enchants] has been disabled.");
	}

	public static boolean registerNewEnchantment() {
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			try {
				Enchantment.registerEnchantment(glow);
				return true;
			} catch (IllegalArgumentException e) {

			}
		} catch (Exception e) {
		}
		return false;
	}

	public static int getPlus(ItemStack is) {
		if (is.getItemMeta().hasDisplayName()) {
			String name = ChatColor.stripColor(is.getItemMeta().getDisplayName());
			if (name.startsWith("[+")) {
				name = name.split("\\[+")[1].split("\\]")[0];
				try {
					return Integer.parseInt(name);
				} catch (Exception e) {
					return 0;
				}
			}
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInvClick(InventoryClickEvent e) throws Exception {
		Player p = (Player) e.getWhoClicked();
		if (!e.getInventory().getName().equalsIgnoreCase("container.crafting"))
			return;
		if (e.getSlotType() == SlotType.ARMOR)
			return;
		if (e.getCursor() != null && e.getCursor().getType() == Material.EMPTY_MAP
				&& e.getCursor().getItemMeta().getDisplayName() != null
				&& e.getCursor().getItemMeta().getDisplayName().contains("Armor")) {
			if (e.getCurrentItem() != null) {
				if ((e.getCurrentItem().getType().name().contains("_HELMET")
						|| e.getCurrentItem().getType().name().contains("_CHESTPLATE")
						|| e.getCurrentItem().getType().name().contains("_LEGGINGS")
						|| e.getCurrentItem().getType().name().contains("_BOOTS"))
						&& e.getCurrentItem().getItemMeta().getLore() != null
						&& e.getCurrentItem().getItemMeta().hasDisplayName()) {

					if ((e.getCurrentItem().getType().name().contains("GOLD_")
							&& e.getCursor().getItemMeta().getDisplayName().contains("Gold"))
							|| (e.getCurrentItem().getType().name().contains("DIAMOND_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Diamond"))
							|| (e.getCurrentItem().getType().name().contains("IRON_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Iron"))
							|| (e.getCurrentItem().getType().name().contains("CHAINMAIL_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Chainmail"))
							|| (e.getCurrentItem().getType().name().contains("LEATHER_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Leather"))) {
						List<String> curlore = e.getCurrentItem().getItemMeta().getLore();
						String name = e.getCurrentItem().getItemMeta().getDisplayName();
						if (name.startsWith(ChatColor.RED + "[+")) {
							name = name.split("] ")[1];
						}
						double beforehp = Damage.getHp(e.getCurrentItem());
						double beforehpgen = Damage.getHps(e.getCurrentItem());
						int beforenrg = Damage.getEnergy(e.getCurrentItem());

						int plus = getPlus(e.getCurrentItem());
						// Bukkit.getServer().broadcastMessage("" + plus);
						if (plus < 3) {
							if (e.getCursor().getAmount() > 1) {
								e.getCursor().setAmount(e.getCursor().getAmount() - 1);
							} else if (e.getCursor().getAmount() == 1) {
								e.setCursor(null);
							}
							p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
							Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
							FireworkMeta fwm = fw.getFireworkMeta();
							FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW)
									.withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
							fwm.addEffect(effect);
							fwm.setPower(0);
							fw.setFireworkMeta(fwm);
							e.setCancelled(true);
							double added = beforehp * .05;
							if (added < 1)
								added = 1;
							int newhp = (int) (beforehp + added);
							ItemStack is = e.getCurrentItem();
							ItemMeta im = is.getItemMeta();
							im.setDisplayName(ChatColor.RED + "[+" + (plus + 1) + "] " + name);
							List<String> lore = im.getLore();
							lore.set(1, ChatColor.RED + "HP: +" + newhp);
							if (curlore.get(2).contains("ENERGY REGEN")) {
								lore.set(2, ChatColor.RED + "ENERGY REGEN: +" + (beforenrg + 1) + "%");
							} else if (curlore.get(2).contains("HP REGEN")) {
								double addedhps = beforehpgen * .05;
								if (addedhps < 1)
									addedhps = 1;
								int newhps = (int) (beforehpgen + addedhps);
								lore.set(2, ChatColor.RED + "HP REGEN: +" + newhps + " HP/s");
							}
							im.setLore(lore);
							is.setItemMeta(im);
							e.setCurrentItem(is);
						}
						if (plus >= 3 && plus < 12) {
							if (e.getCursor().getAmount() > 1) {
								e.getCursor().setAmount(e.getCursor().getAmount() - 1);
							} else if (e.getCursor().getAmount() == 1) {
								e.setCursor(null);
							}
							Random random = new Random();
							int drop = random.nextInt((100 - 1) + 1) + 1;
							int doifail = 0;
							if (plus == 3)
								doifail = 30;
							if (plus == 4)
								doifail = 40;
							if (plus == 5)
								doifail = 50;
							if (plus == 6)
								doifail = 65;
							if (plus == 7)
								doifail = 75;
							if (plus == 8)
								doifail = 80;
							if (plus == 9)
								doifail = 85;
							if (plus == 10)
								doifail = 90;
							if (plus == 11)
								doifail = 95;
							e.setCancelled(true);
							if (drop <= doifail) {
								p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2, 1.25f);
								ParticleEffect.LAVA.display(0, 0, 0, 5, 10, p.getEyeLocation(), 20);
								e.setCurrentItem(null);
							} else {
								p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
								Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
								FireworkMeta fwm = fw.getFireworkMeta();
								FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW)
										.withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
								fwm.addEffect(effect);
								fwm.setPower(0);
								fw.setFireworkMeta(fwm);
								e.setCancelled(true);
								double added = beforehp * .05;
								if (added < 1)
									added = 1;
								int newhp = (int) (beforehp + added);
								ItemStack is = e.getCurrentItem();
								ItemMeta im = is.getItemMeta();
								im.setDisplayName(ChatColor.RED + "[+" + (plus + 1) + "] " + name);
								List<String> lore = im.getLore();
								lore.set(1, ChatColor.RED + "HP: +" + newhp);
								if (curlore.get(2).contains("ENERGY REGEN")) {
									lore.set(2, ChatColor.RED + "ENERGY REGEN: +" + (beforenrg + 1) + "%");
								} else if (curlore.get(2).contains("HP REGEN")) {
									double addedhps = beforehpgen * .05;
									if (addedhps < 1)
										addedhps = 1;
									int newhps = (int) (beforehpgen + addedhps);
									lore.set(2, ChatColor.RED + "HP REGEN: +" + newhps + " HP/s");
								}
								im.setLore(lore);
								is.setItemMeta(im);
								is.addUnsafeEnchantment(glow, 1);
								e.setCurrentItem(is);
							}
						}
					}
				}
			}
		}
		if (e.getCursor() != null && e.getCursor().getType() == Material.EMPTY_MAP
				&& e.getCursor().getItemMeta().getDisplayName() != null
				&& e.getCursor().getItemMeta().getDisplayName().contains("Weapon")) {
			if (e.getCurrentItem() != null) {
				if ((e.getCurrentItem().getType().name().contains("_SWORD")
						|| e.getCurrentItem().getType().name().contains("_HOE")
						|| e.getCurrentItem().getType().name().contains("_SPADE")
						|| e.getCurrentItem().getType().name().contains("_AXE"))
						&& e.getCurrentItem().getItemMeta().getLore() != null
						&& e.getCurrentItem().getItemMeta().hasDisplayName()) {
					if ((e.getCurrentItem().getType().name().contains("GOLD_")
							&& e.getCursor().getItemMeta().getDisplayName().contains("Gold"))
							|| (e.getCurrentItem().getType().name().contains("DIAMOND_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Diamond"))
							|| (e.getCurrentItem().getType().name().contains("IRON_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Iron"))
							|| (e.getCurrentItem().getType().name().contains("STONE_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Stone"))
							|| (e.getCurrentItem().getType().name().contains("WOOD_")
									&& e.getCursor().getItemMeta().getDisplayName().contains("Wooden"))) {

						String name = e.getCurrentItem().getItemMeta().getDisplayName();
						if (name.startsWith(ChatColor.RED + "[+")) {
							name = name.split("] ")[1];
						}
						double beforemin = Damage.getDamageRange(e.getCurrentItem()).get(0);
						double beforemax = Damage.getDamageRange(e.getCurrentItem()).get(1);
						int plus = getPlus(e.getCurrentItem());
						if (plus < 3) {
							if (e.getCursor().getAmount() > 1) {
								e.getCursor().setAmount(e.getCursor().getAmount() - 1);
							} else if (e.getCursor().getAmount() == 1) {
								e.setCursor(null);
							}
							p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
							Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
							FireworkMeta fwm = fw.getFireworkMeta();
							FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW)
									.withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
							fwm.addEffect(effect);
							fwm.setPower(0);
							fw.setFireworkMeta(fwm);
							e.setCancelled(true);
							double addedmin = beforemin * .05;
							if (addedmin < 1)
								addedmin = 1;
							int min = (int) (beforemin + addedmin);
							double addedmax = beforemax * .05;
							if (addedmax < 1)
								addedmax = 1;
							int max = (int) (beforemax + addedmax);
							ItemStack is = e.getCurrentItem();
							ItemMeta im = is.getItemMeta();
							im.setDisplayName(ChatColor.RED + "[+" + (plus + 1) + "] " + name);
							List<String> lore = im.getLore();
							lore.set(0, ChatColor.RED + "DMG: " + min + " - " + max);
							im.setLore(lore);
							is.setItemMeta(im);
							e.setCurrentItem(is);
						}
						if (plus >= 3 && plus < 12) {
							if (e.getCursor().getAmount() > 1) {
								e.getCursor().setAmount(e.getCursor().getAmount() - 1);
							} else if (e.getCursor().getAmount() == 1) {
								e.setCursor(null);
							}
							Random random = new Random();
							int drop = random.nextInt((100 - 1) + 1) + 1;
							int doifail = 0;
							if (plus == 3)
								doifail = 30;
							if (plus == 4)
								doifail = 40;
							if (plus == 5)
								doifail = 50;
							if (plus == 6)
								doifail = 65;
							if (plus == 7)
								doifail = 75;
							if (plus == 8)
								doifail = 80;
							if (plus == 9)
								doifail = 85;
							if (plus == 10)
								doifail = 90;
							if (plus == 11)
								doifail = 95;
							e.setCancelled(true);
							if (drop <= doifail) {
								p.getWorld().playSound(p.getLocation(), Sound.FIZZ, 2, 1.25f);
								ParticleEffect.LAVA.display(0, 0, 0, 5, 10, p.getEyeLocation(), 20);
								e.setCurrentItem(null);
							} else {
								p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 1.0F, 1.25F);
								Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
								FireworkMeta fwm = fw.getFireworkMeta();
								FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW)
										.withFade(Color.YELLOW).with(Type.BURST).trail(true).build();
								fwm.addEffect(effect);
								fwm.setPower(0);
								fw.setFireworkMeta(fwm);
								e.setCancelled(true);
								double addedmin = beforemin * .05;
								if (addedmin < 1)
									addedmin = 1;
								int min = (int) (beforemin + addedmin);
								double addedmax = beforemax * .05;
								if (addedmax < 1)
									addedmax = 1;
								int max = (int) (beforemax + addedmax);
								ItemStack is = e.getCurrentItem();
								ItemMeta im = is.getItemMeta();
								im.setDisplayName(ChatColor.RED + "[+" + (plus + 1) + "] " + name);
								List<String> lore = im.getLore();
								lore.set(0, ChatColor.RED + "DMG: " + min + " - " + max);
								im.setLore(lore);
								is.setItemMeta(im);
								is.addUnsafeEnchantment(glow, 1);
								e.setCurrentItem(is);
							}
						}
					}
				}
			}
		}
	}
}
