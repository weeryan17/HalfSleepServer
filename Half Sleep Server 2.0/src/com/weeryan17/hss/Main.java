package com.weeryan17.hss;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level; 

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		if(!this.getMessageConfig().contains("Messages.")){
			this.getMessageConfig().set("Messages.NotHalfSleep", "&YELLOW&&PLAYERSLEEP&/&HALFSLEEP& Players are sleaping that need to be sleeping for the time to be set to day");
			this.getMessageConfig().set("Messages.HalfSleeping", "&YELLOW&&HALF& of the server is asleep so the time has been set to day");
			this.saveMessageConfig();
		}
		if(!this.getPlayersConfig().contains("Players")){
			ArrayList<String> playerList = new ArrayList<String>();
			playerList.add("RandomExamplePlayer1");
			playerList.add("RandomExamplePlayer2");
			this.getPlayersConfig().set("Players", playerList);
			this.savePlayersConfig();
		}
	}
	static int inBed;
	@EventHandler
	public void onSleep(PlayerBedEnterEvent e){
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) this.getPlayersConfig().get("Players");
		if(!list.contains(e.getPlayer().getName())){
			inBed = 1;
			for(Player p : Bukkit.getOnlinePlayers()){
				if(p.isSleeping() && !list.contains(p.getName())){
					inBed = inBed + 1;
				}
			}
			if(inBed >= ((int)Bukkit.getOnlinePlayers().size()/2) - this.ingoredPlayerNum()){
				for(Player p : Bukkit.getOnlinePlayers()){
					p.sendMessage(this.getHalfSleep());
				
				}
				for(World world : Bukkit.getWorlds()){
					world.setTime(1000L);
				}
			} else {
				for(Player p : Bukkit.getOnlinePlayers()){
					p.sendMessage(this.getNotHalfSleep());
				}
			}
		}
	}
	HashMap<String, FileConfiguration> datas = new HashMap<String, FileConfiguration>();
	private FileConfiguration data;
	/**
	 * Base method for using configs.
	 * 
	 * @param name Name of the config file you want to load.
	 * @param subFolder The sub folder that you want to store the file in.
	 * @return The config.
	 */
	private FileConfiguration config(String name, String subFolder) {
		final File config;
		if(subFolder == ""){
			config = new File(getDataFolder(), name + ".yml");
		} else {
			config = new File(getDataFolder() + "\\" + subFolder, name + ".yml");
		}
		if (datas.get(name) == null) {
			data = (FileConfiguration) YamlConfiguration.loadConfiguration(config);
			final InputStream defConfigStream = getResource(name + ".yml");
			if (defConfigStream != null) {
				@SuppressWarnings({ "deprecation" })
				final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				data.setDefaults((Configuration) defConfig);
			}
			datas.put(name, data);
		}
		return datas.get(name);
	}
	/**
	 * Base method for saving configs.
	 * 
	 * @param name Name of the config you want to save.
	 * @param subFolder The sub folder you want to store the file in.
	 */
	private void saveConfigs(String name, String subFolder) {
		final File config;
		if(subFolder == ""){
			config = new File(getDataFolder(), name + ".yml");
		} else {
			config = new File(getDataFolder() + "\\" + subFolder, name + ".yml");
		}
		try {
			this.getConfig().options().copyDefaults(true);
			this.config(name, subFolder).save(config);
			this.config(name, subFolder);
		} catch (IOException ex) {
			getLogger().log(Level.WARNING, "Couldn''t save {0}.yml", name);
		}
	}
	
	public FileConfiguration getMessageConfig(){
		return this.config("Messages", "");
	}
	
	public void saveMessageConfig(){
		this.saveConfigs("Messages", "");
	}
	public String getNotHalfSleep(){
		String inital = this.getMessageConfig().getString("Messages.NotHalfSleep");
		String finnal = "";
		for(String peice : inital.split("&")){
				switch(peice){
				case "YELLOW" :{
					finnal = finnal + "§e";
				}
				break;
				case "RED" :{
					finnal = finnal + "§c";
				}
				break;
				case "BLUE" :{
					finnal = finnal + "§9";
				}
				break;
				case "PLAYERSSLEEPING" :{
					finnal = finnal + inBed;
				}
				break;
				case "AMOUNTNEEDED" :{
					finnal = finnal + (((int)Bukkit.getOnlinePlayers().size() / 2) - this.ingoredPlayerNum());
				}
				break;
				default :{
					finnal = finnal + peice;
				}
				break;
			}
		}
		return finnal;
	}
	public String getHalfSleep(){
		String inital = this.getMessageConfig().getString("Messages.HalfSleeping");
		String finnal = "";
		for(String peice : inital.split("&")){
			switch(peice){
			case "YELLOW" :{
				finnal = finnal + "§e";
			}
			break;
			case "RED" :{
				finnal = finnal + "§c";
			}
			break;
			case "BLUE" :{
				finnal = finnal + "§9";
			}
			break;
			case "PLAYERSSLEEPING" :{
				finnal = finnal + inBed;
			}
			break;
			case "AMOUNTNEEDED" :{
				finnal = finnal + (((int)Bukkit.getOnlinePlayers().size() / 2) - this.ingoredPlayerNum());
			}
			break;
			default :{
				finnal = finnal + peice;
			}
			break;
			}
		}
		return finnal;
	}
	public FileConfiguration getPlayersConfig(){
		return this.config("IngoredPlayers", "");
	}
	public void savePlayersConfig(){
		this.saveConfigs("IngoredPlayers", "");
	}
	
	public int ingoredPlayerNum(){
		int num = 0;
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) this.getPlayersConfig().get("Players");
		for(Player p : Bukkit.getOnlinePlayers()){
			if(list.contains(p.getName())){
				num++;
			}
		}
		return num;
	}
}
