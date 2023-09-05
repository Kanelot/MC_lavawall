//This class handles players pressing the button to queue up,
//deciding if the queues are full, and ending the game if someone leaves early

package me.kan.wall;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ButtonPress
implements Listener
{
	public static Main plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	int tx;
	int ty;
	int tz;

	public ButtonPress(Main instance) {
		plugin = instance;
		tx = plugin.tx;
		ty = plugin.ty;
		tz = plugin.tz;
	}



	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player P = event.getPlayer();
			Block clicked = event.getClickedBlock();
			Location buttonLocation = clicked.getLocation();
			if (isTeamButton(buttonLocation, P) && clicked.getType() == Material.STONE_BUTTON) {
				if(!plugin.listenerHasStarted&&plugin.RED[2]!=null && plugin.BLUE[2]!=null){
					Bukkit.broadcastMessage("The Lava Wall game is starting");
					plugin.startGame(p);
				}
			}
		}
	}
	@EventHandler
	public void leaveEvent (PlayerQuitEvent e) {
		if(!(plugin.isGaming)){
			Player p = e.getPlayer();
			for(int i = 0; i < 3; i++){
				if(p.equals(plugin.BLUE[i])){
					plugin.getServer().broadcastMessage("Due to " + p.getDisplayName() + " leaving the game, the Blue team lineup must be rearanged. If you were on the Blue team, please re-signup.");
					for(int x = 0; x < 3; x++){
						plugin.BLUE[x] = null;
						plugin.blueNumber = 0;
					}
				}else{
					if(p.equals(plugin.RED[i])){
						plugin.getServer().broadcastMessage("Due to " + p.getDisplayName() + " leaving the game, the Red team lineup must be rearanged. If you were on the Red team, please re-signup.");
						for(int x = 0; x < 3; x++){
							plugin.RED[x] = null;
							plugin.redNumber = 0;
						}
					}
				}
			}
		}
	}

	private boolean isTeamButton(Location B, Player P) {
		if(isRedButton(B,P) || isBlueButton(B,P)){
			return true;
		}else{
			return false;
		}
	}



	private boolean isBlueButton(Location b, Player P) {
		int BX = b.getBlockX();
		int BY = b.getBlockY();
		int BZ = b.getBlockZ();
		if (BX == tx-801 && BY == ty+19 && BZ == tz+58){
			plugin.addBlue(P);
			return true;
		}else{return false;}
	}



	private boolean isRedButton(Location b, Player P) {
		int BX = b.getBlockX();
		int BY = b.getBlockY();
		int BZ = b.getBlockZ();
		if (BX == tx-801 && BY == ty+19 && BZ == tz+56){
			plugin.addRed(P);
			return true;
		}else{return false;}
	}
}