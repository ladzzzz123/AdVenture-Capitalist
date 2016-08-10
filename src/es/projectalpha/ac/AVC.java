package es.projectalpha.ac;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import es.projectalpha.ac.cmd.Help;
import es.projectalpha.ac.events.Fancy;
import es.projectalpha.ac.events.ManagerInteract;
import es.projectalpha.ac.events.ProtectWorld;
import es.projectalpha.ac.events.invs.IAchievements;
import es.projectalpha.ac.files.Files;
import es.projectalpha.ac.game.Currency;
import es.projectalpha.ac.game.Game;
import es.projectalpha.ac.managers.ManagerCore;
import es.projectalpha.ac.mysql.Data;
import es.projectalpha.ac.mysql.MySQL;
import es.projectalpha.ac.shops.ShopsCore;
import es.projectalpha.ac.utils.Messages;
import es.projectalpha.ac.utils.ServerVersion;
import es.projectalpha.ac.world.Generator;

public class AVC extends JavaPlugin {

	private static AVC plugin;

	private MySQL mysql;
	private Data data;

	private static boolean debug = false;

	public void onEnable(){
		Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "========================");
		Bukkit.getConsoleSender().sendMessage(" ");

		plugin = this;

		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Checking Server Version. . .");
		if (!ServerVersion.isMC110() && !ServerVersion.isMC19()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please, update your server to 1.9.X or 1.10.X to use this plugin");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (ServerVersion.isMC18()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Please, update your server to 1.9.X or 1.10.X to use this plugin, 1.8.X is not supported");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Checking Complete");

		Bukkit.getConsoleSender().sendMessage(" ");

		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Checking and Creating files. . .");
		Files.setupFiles();
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Setup Files Complete");

		Bukkit.getConsoleSender().sendMessage(" ");

		if (!Bukkit.getWorlds().contains(Bukkit.getWorld("ac"))) {

			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Creating World. . .");
			Bukkit.createWorld(new WorldCreator("ac").generator(getDefaultWorldGenerator("ac", "ac")).environment(Environment.NORMAL));
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "World Created");

			Bukkit.getConsoleSender().sendMessage(" ");

		}

		if (Files.cfg.getBoolean("MySQL.enabled")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Connecting to MySQL. . .");
			this.mysql = new MySQL(this);
			this.data = new Data(this);
			Bukkit.getConsoleSender().sendMessage(" ");
		}

		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Registering Commands and Events. . .");
		regCMDs();
		regEvents();
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Register Complete");

		Bukkit.getConsoleSender().sendMessage(" ");

		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Loading Game. . .");
		ManagerCore.loadManagers();
		ShopsCore.loadShops();
		Game.startTimer(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Game Loaded");

		Bukkit.getConsoleSender().sendMessage(" ");

		Bukkit.getConsoleSender().sendMessage(Messages.prefix + ChatColor.GREEN + "AC enabled");
		Bukkit.getConsoleSender().sendMessage(Messages.prefix + ChatColor.GREEN + "AC Version: " + ChatColor.RED + getDescription().getVersion());
		Bukkit.getConsoleSender().sendMessage(Messages.prefix + ChatColor.GREEN + "AC Autor: " + ChatColor.RED + getDescription().getAuthors().toString());
		Bukkit.getConsoleSender().sendMessage(Messages.prefix + ChatColor.GREEN + "AC Utils: " + ChatColor.RED + "https://github.com/ProjectAlphaES/AdVenture-Capitalist");

		Bukkit.getConsoleSender().sendMessage(" ");
		Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "========================");
	}

	public void onDisable(){

		for (Player p : Game.playing) {
			Currency.saveMoney(p);
		}

	}

	private void regEvents(){
		new ManagerInteract(this);
		new ProtectWorld(this);
		new IAchievements(this);
		new Fancy(this);
	}

	private void regCMDs(){
		getCommand("avc").setExecutor(new Help(this));
	}

	public static AVC getPlugin(){
		return plugin;
	}

	public MySQL getMySQL(){
		return this.mysql;
	}

	public Data getData(){
		return this.data;
	}

	public static boolean getDebug(){
		return debug;
	}

	public static void setDebug(boolean debug){
		AVC.debug = debug;
	}

	//For Multiverse or Bukkit Settings
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id){
		return new Generator();
	}
}