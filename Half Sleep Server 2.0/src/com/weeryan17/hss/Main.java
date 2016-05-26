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
			this.getMessageConfig().set("Messages.NotHalfSleep", "&YELLOW&&PLAYERSSLEEPING&/&AMOUNTNEEDED& Players are sleaping that need to be sleeping for the time to be set to day");
			this.getMessageConfig().set("Messages.HalfSleeping", "&YELLOW&&HALF& of the server is asleep so the time has been set to day");
			this.saveMessageConfig();
		}
		if(!this.getPlayersConfig().contains("Players")){
			ArrayList<String> playerList = new ArrayList<String>();
			playerList.add(":RandomExamplePlayer1");
			playerList.add(":RandomExamplePlayer2");
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
				if(p.isSleeping() && p.getWorld() == e.getPlayer().getWorld() && !list.contains(p.getName())){
					inBed = inBed + 1;
				}
			}
			if(inBed >= ((int)Bukkit.getOnlinePlayers().size()/2) - this.ingoredPlayerNum(e.getPlayer().getWorld())){
				for(Player p : Bukkit.getOnlinePlayers()){
					if(p.getWorld() == e.getPlayer().getWorld()){
						p.sendMessage(this.getAcctualMessage(this.getMessageConfig().getString("Messages.HalfSleeping"), e.getPlayer().getWorld(), e.getPlayer().getName()));
					}
				}
				World world = e.getPlayer().getWorld();
				world.setTime(1000L);
				if(world.hasStorm()){
					world.setStorm(false);
				} else if(world.isThundering()){
					world.setThundering(false);
				}
			} else {
				for(Player p : Bukkit.getOnlinePlayers()){
					if(p.getWorld() == e.getPlayer().getWorld()){
						p.sendMessage(this.getAcctualMessage(this.getMessageConfig().getString("Messages.NotHalfSleep"), e.getPlayer().getWorld(), e.getPlayer().getName()));
					}
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
	public String getAcctualMessage(String message, World world, String player){
		String finnal = "";
		for(String peice : message.split("&")){
				switch(peice){
				case "BLACK" :{
					finnal = finnal + "§0";
				}
				break;
				case "DARKBLUE" :{
					finnal = finnal + "§1";
				}
				break;
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
				case "GRAY" :{
					finnal = finnal + "§7";
				}
				break;
				case "PLAYERSSLEEPING" :{
					finnal = finnal + inBed;
				}
				break;
				case "AMOUNTNEEDED" :{
					finnal = finnal + (((int)Bukkit.getOnlinePlayers().size() / 2) - this.ingoredPlayerNum(world));
				}
				break;
				case "PLAYERSLEEP" :{
					finnal = finnal + inBed;
				}
				break;
				case "HALFSLEEP" :{
					finnal = finnal + (((int)Bukkit.getOnlinePlayers().size() / 2) - this.ingoredPlayerNum(world));
				}
				break;
				case "PLAYERNAME" :{
					finnal = finnal + player;
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
	
	public int ingoredPlayerNum(World world){
		ArrayList<String> list = this.getIngoredPlayers();
		int num = 0;
		for(String player : list){
			Player p = Bukkit.getPlayer(player);
			if(p != null){
				if(p.getWorld() == world){
					num++;
				}
			}
		}
		return num;
	}
	public ArrayList<String> getIngoredPlayers(){
		@SuppressWarnings("unchecked")
		ArrayList<String> list = (ArrayList<String>) this.getPlayersConfig().get("Players");
		ArrayList<String> players = new ArrayList<String>();
		for(String combinedName : list){
			String[] rawName = combinedName.split(":");
			String name = rawName[1];
			players.add(name);
			if(rawName[0].equals("")){
				Player p = Bukkit.getPlayer(name);
				if(p == null){
					this.getLogger().log(Level.SEVERE, "The specified player " + name + " doesn't exist. Please remove them from the config!");
				} else {
					this.getLogger().log(Level.WARNING, "There is no UUID specifyed for " + name + " in the ignored players config. It is not needed atm because I don't use uuids yet but I'm slowly switching.");
				}
			}
		}
		return players;
	}
}
