package es.projectalpha.ac.api;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import es.projectalpha.ac.AC;

public class BossBarAPI {

	private static AC plugin;

	public BossBarAPI(AC Main){
		BossBarAPI.plugin = Main;
	}

	private static BossBar globalBossBar = null;
	private static BossBar globalJoinBossBar = null;
	private static double currentTime = 0.0D;
	private static int taskID = 0;
	private static boolean perTick = false;

	public static void sendMessageToAllPlayersRecurring(final String message, double seconds, final BarColor barColor, final BarStyle barStyle){
		Bukkit.getScheduler().cancelTask(taskID);
		final double perTime;
		if (perTick) {
			currentTime = seconds * 20.0D;
			perTime = 1.0D / (seconds * 20.0D);
		} else {
			currentTime = seconds;
			perTime = 1.0D / seconds;
		}
		Runnable runnable = new Runnable() {
			public void run(){
				if (BossBarAPI.currentTime < 0.0D) {
					BossBarAPI.clearAllPlayers();
					Bukkit.getScheduler().cancelTask(BossBarAPI.taskID);
					return;
				}
				BossBarAPI.sendMessageToAllPlayers(message, BossBarAPI.currentTime * perTime, barColor, barStyle);
			}
		};
		if (perTick) {
			taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, 1L).getTaskId();
		} else {
			taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0L, 20L).getTaskId();
		}
	}

	public static void sendMessageToAllPlayers(String message, double progress, BarColor barColor, BarStyle barStyle){
		if (globalBossBar == null) {
			globalBossBar = Bukkit.createBossBar(message, barColor, barStyle, new BarFlag[0]);
		} else {
			globalBossBar.setTitle(message);
			globalBossBar.setColor(barColor);
			globalBossBar.setStyle(barStyle);
		}
		globalBossBar.removeAll();
		for (Player player : Bukkit.getOnlinePlayers()) {
			globalBossBar.addPlayer(player);
		}
		globalBossBar.setProgress(progress);
		globalBossBar.setVisible(true);
	}

	public static void clearAllPlayers(){
		Bukkit.getScheduler().cancelTask(taskID);
		globalBossBar.setVisible(false);
		globalBossBar.removeAll();
		globalBossBar.setTitle("");
	}

	public static void createJoinBossBar(String message, double time, BarColor barColor, BarStyle barStyle){
		if (globalJoinBossBar == null) {
			globalJoinBossBar = Bukkit.createBossBar(message, barColor, barStyle, new BarFlag[0]);
		}
		if ((Bukkit.getOnlinePlayers().size() > 0) && (time <= 0.0D)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				globalJoinBossBar.addPlayer(player);
			}
		}
		globalJoinBossBar.setVisible(true);
	}

	public static BossBar getGlobalJoinBossBar(){
		return globalJoinBossBar;
	}
}
