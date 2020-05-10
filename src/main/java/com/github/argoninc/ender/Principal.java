package com.github.argoninc.ender;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;

import com.github.argoninc.ender.config.Config;
import com.github.argoninc.ender.listener.EnderChestListener;
import com.github.rillis.Files.FileUtils;

public class Principal extends JavaPlugin{
	public static FileConfiguration config = null;
	public static Config configc = null;
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new EnderChestListener(), this);
		this.config = getConfig();
		configc = new Config(newFile("argoninc/inv.cfg", "{}"));
		try {
			config.load(newFile("argoninc/inv.yml", null));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			InventoryView iv = player.getOpenInventory();
			if(iv.getType().equals(InventoryType.CHEST) && iv.getTitle().startsWith("Armazem de ")) {
				EnderChestListener.saveToFile(player.getUniqueId().toString(), newFile("argoninc/inv.yml", null), iv.getInventory(0).getContents());
				iv.close();
			}
		}
	}
	
	private File newFile(String url, String text) {
		File file = new File(url);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if(!file.exists()) {
			try {
				file.createNewFile();
				
				if(text!=null) {
					FileUtils.write(file, text);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
}
