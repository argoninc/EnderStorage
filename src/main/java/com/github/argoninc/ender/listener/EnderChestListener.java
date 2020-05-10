package com.github.argoninc.ender.listener;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.argoninc.ender.Principal;

public class EnderChestListener implements Listener {
	private String title = null;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (e.hasBlock() && e.getMaterial() != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block blocoClicado = e.getClickedBlock();
			Material materialClicado = blocoClicado.getType();
			
			if (materialClicado.equals(Material.ENDER_CHEST)) {

				e.setCancelled(true);
				title = "Armazem de "+player.getName();
				Inventory inv = Bukkit.createInventory(player, getSize(player.getUniqueId().toString()), title);

				initializeItems(inv, player.getUniqueId().toString());

				player.openInventory(inv);

			}
		}
	}
	
	private int getSize(String uuid) {
		
		
		if(!Principal.configc.has(uuid)) {
			Principal.configc.set(uuid, 9);
		}
		return (int) Principal.configc.get(uuid);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();
		String titleEvent = event.getView().getTitle();
		
		if(titleEvent.equals(title)) {
			
			saveToFile(player.getUniqueId().toString(), newFile("argoninc/inv.yml"), inventory.getContents());			
		}
		
	}

	private void initializeItems(Inventory inv, String uuid) {
		ItemStack[] items = getfromFile(uuid);
		
		if(items!=null) {
			int i = 0;	
			for(ItemStack item : items) {
				if(item!=null) {
					inv.setItem(i, item);
				}
				i++;
			}
		}
	}
	
	public static void saveToFile(String uuid, File file, ItemStack[] itemstacks) {
		System.out.println("EnderChest de "+uuid+" salvo.");
		FileConfiguration myYmlConfig = Principal.config;
		myYmlConfig.set(uuid, itemstacks);
		try {
			myYmlConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private ItemStack[] getfromFile(String uuid) {
		ItemStack[] itemstack = new ItemStack[0];
		Object data = Principal.config.get(uuid);
		ItemStack[] myStack;
		if (data instanceof List) {
			myStack = ((List<ItemStack>) data).toArray(itemstack);
		}else {
			myStack = (ItemStack[]) data;
		}
		return myStack;
	}
	
	private File newFile(String url) {
		File file = new File(url);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
}
