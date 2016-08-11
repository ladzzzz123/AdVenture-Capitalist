package es.projectalpha.ac.achievements;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import es.projectalpha.ac.api.FireworkAPI;
import es.projectalpha.ac.api.fancy.TitleAPI;
import es.projectalpha.ac.files.Files;
import es.projectalpha.ac.game.Currency;
import es.projectalpha.ac.game.Game;
import es.projectalpha.ac.utils.Messages;

public class AchievementsCore {

	private Game game = new Game();
	private Currency c = new Currency();

	public void addAchievement(Player p, AchievementsType at){
		if (hasAchievement(p, at)) {
			return;
		} else {
			List<String> players = Files.achie.getStringList(at.toString());

			players.add(p.getName());

			Files.achie.set(at.toString(), players);
			Files.saveFiles();

			Messages.newAchievement(at, p);
			TitleAPI.sendTitle(p, 0, 3, 0, "", ChatColor.GREEN + "New Achievement!");
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 5.0F);

			for (int g = 0; g < 4; g++) {
				FireworkAPI.spawnFirework(p);
			}
		}
	}

	public void remAchievement(Player p, AchievementsType at){
		if (hasAchievement(p, at)) {
			List<String> players = Files.achie.getStringList(at.toString());

			players.remove(p.getName());

			Files.achie.set(at.toString(), players);
			Files.saveFiles();
		}
	}

	public boolean hasAchievement(Player p, AchievementsType at){
		if (Files.achie.getStringList(at.toString()).contains(p)) {
			return true;
		}
		return false;
	}

	public void checkAchievements(){
		for (Player p : game.playing) {
			if (c.getMoney(p) >= 5000) {
				addAchievement(p, AchievementsType.PC);
			}
		}
	}
}
