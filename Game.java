//This handles all of the game logic

package me.kan.wall;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Game
implements Listener
{
	protected boolean redHasPlaced = false;
	protected boolean blueHasPlaced = false;
	public static Main plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	boolean hasBurned = false;
	int[] deathsRed = new int[3];
	int[] deathsBlue = new int[3];
	/*Scoreboard board;
Team redteam;
Team blueteam;*/


	public Game(Main instance) {
		plugin = instance;
		sendToRed(ChatColor.RED+"You have 3 minutes to place your tower");
		sendToBlue(ChatColor.BLUE+"You have 3 minutes to place your tower");
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(!(redHasPlaced&&blueHasPlaced)){
					sendToTeams("Teams have one minute to place their towers");
				}
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						if(!(redHasPlaced&&blueHasPlaced)){
							sendToTeams("Teams have 30 seconds to place their towers!");
						}
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							public void run() {
								if(!(redHasPlaced&&blueHasPlaced)){
									sendToTeams("10 seconds!");
								}
								plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
									public void run() {
										if(!(redHasPlaced&&blueHasPlaced)){
											if((!redHasPlaced)&&(!blueHasPlaced)){
												placeRandomRed();
												redHasPlaced = true;
												placeRandomBlue();
												blueHasPlaced = true;
												sendToTeams("Both towers have been placed randomly. Game on!");
											}else{
												if(!redHasPlaced){
													sendToBlue(ChatColor.BLUE+"The Red tower has been randomly placed. The battle can now commence!");
													sendToRed(ChatColor.RED+"Your tower has been randomly placed. The battle can now commence!");
													placeRandomRed();
													redHasPlaced = true;
												}
												if(!blueHasPlaced){
													sendToRed(ChatColor.RED+"The Blue tower has been randomly placed. The battle can now commence!");
													sendToBlue(ChatColor.BLUE+"Your tower has been randomly placed. The battle can now commence!");
													placeRandomBlue();
													blueHasPlaced = true;
												}
											}
										}
									}

									private void placeRandomBlue() {
										boolean cont = true;
										while(cont){
											for(int x = plugin.blueCorner1.getBlockX()+1; x < plugin.blueCorner2.getBlockX()&&cont; x++){
												for(int z = plugin.blueCorner1.getBlockZ()+1; z < plugin.blueCorner2.getBlockZ()&&cont; z++){
													Random r = new Random();
													int q = r.nextInt(500);
													if (q == 100){
														Random f = new Random();
														int y =f.nextInt(13)+13;
														Location l = new Location(plugin.RED[0].getWorld(),x,y-1,z);
														for(int c = 0; c <5; c++){
															l.setY(l.getBlockY()+1);
															l.getBlock().setType(Material.WOOL);
														}
														plugin.bluewool = l;
														cont = false;
													}
												}
											}
										}
									}

									private void placeRandomRed() {
										boolean cont = true;
										while(cont){
											for(int x = plugin.redCorner1.getBlockX()+1; x < plugin.redCorner2.getBlockX()&&cont; x++){
												for(int z = plugin.redCorner1.getBlockZ()+1; z < plugin.redCorner2.getBlockZ()&&cont; z++){
													Random r = new Random();
													int q = r.nextInt(500);
													if (q == 100){
														Random f = new Random();
														int y =f.nextInt(13)+13;
														Location l = new Location(plugin.RED[0].getWorld(),x,y-1,z);
														for(int c = 0; c <5; c++){
															l.setY(l.getBlockY()+1);
															l.getBlock().setType(Material.WOOL);
														}
														plugin.redwool = l;
														cont = false;
													}
												}
											}
										}
									}
								}, 200L);

							}
						}, 400L);
					}
				}, 600L);

			}
		}, 2400L);
	}



	private void sendToRed(String s){
		for(int i = 0; i < plugin.RED.length; i++){
			plugin.RED[i].sendMessage(s);
		}
	}

	private void sendToBlue(String s){
		for(int i = 0; i < plugin.BLUE.length; i++){
			plugin.BLUE[i].sendMessage(s);
		}
	}

	private void sendToTeams(String s){
		for(int i = 0; i < plugin.BLUE.length; i++){
			plugin.BLUE[i].sendMessage(s);
			plugin.RED[i].sendMessage(s);
		}
	}

	private boolean isPlayingGame(Player p) {
		boolean playing = false;
		for(int i = 0; i < 3; i++){
			if (plugin.RED[i].equals(p) || (plugin.BLUE[i].equals(p))){
				playing = true;
			}
		}
		if (playing){
			return true;
		}else{
			return false;
		}
	}
	@EventHandler
	public void leaveEvent (PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			p.getInventory().clear();
			Game.plugin.endGame();
			Game.plugin.getServer().broadcastMessage(p.getDisplayName()+" quit while the game was in progress, so the game must end");
		}
	}
	@EventHandler
	public void rightclickEvent (PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			if(e.getAction().equals(Action.PHYSICAL)){
				if(e.getClickedBlock().getTypeId()==70){
					if(getTeamColor(p).equals("red")){
						for(int x = plugin.redCorner1.getBlockX(); x <= plugin.blueCorner2.getBlockX(); x++){
							for(int y = plugin.redCorner1.getBlockY(); y <= plugin.blueCorner2.getBlockY(); y++){
								p.sendBlockChange((new Location(p.getWorld(), x, y, plugin.tz+58)),11,(byte)0);
							}
						}
					}else{
						for(int x = plugin.redCorner1.getBlockX(); x <= plugin.blueCorner2.getBlockX(); x++){
							for(int y = plugin.redCorner1.getBlockY(); y <= plugin.blueCorner2.getBlockY(); y++){
								p.sendBlockChange((new Location(p.getWorld(), x, y, plugin.tz+56)),11,(byte)0);
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void chat (AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			if(this.getTeamColor(p).equals("red")){
				this.sendToRed(ChatColor.RED+ p.getDisplayName() + ": " + ChatColor.RESET + e.getMessage());
				e.setCancelled(true);
			}else{
				if(this.getTeamColor(p).equals("blue")){
					this.sendToBlue(ChatColor.BLUE + p.getDisplayName() + ": " + ChatColor.RESET + e.getMessage());
					e.setCancelled(true);
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void placeblock (BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(this.isPlayingGame(p)){
			if(isTouchingWool(e.getBlock().getLocation())){
				p.sendMessage("You can't place blocks touching the tower");
				e.setCancelled(true);	
				p.updateInventory();
			}
			if(isTouchingWater(e.getBlock().getLocation())){
				if(e.getBlock().getTypeId() != 60){
					p.sendMessage("You can't place blocks in the water");
					e.setCancelled(true);
					p.updateInventory();
				}
			}
			if(isTouchingLava(e.getBlock().getLocation())){
				p.sendMessage("You can't touch the lava wall!");
				e.setCancelled(true);
				p.updateInventory();
			}
			if(isTouchingBricks(e.getBlock().getLocation())){
				p.sendMessage("You can't place blocks touching the stone bricks");
				e.setCancelled(true);
				p.updateInventory();
			}
			if(e.getBlock().getType().equals(Material.WORKBENCH)){
				p.sendMessage("You can't use workbenches in this game");
				e.setCancelled(true);
			}
			if(e.getBlock().getType().equals(Material.WOOL)){
				if (getTeamColor(p).equals("red")){
					if(this.redHasPlaced){
						p.getInventory().remove(Material.WOOL);
						e.setCancelled(true);
						p.updateInventory();
					}else{
						if(isSafeToPlaceTower(e.getBlock().getLocation())){
							Location l = e.getBlock().getLocation();
							for(int y = 0; y < 5; y++){
								Block wool = new Location(l.getWorld(), l.getX(), l.getY()+y, l.getZ()).getBlock();
								wool.setType(Material.WOOL);
								if(y>0){
									wool.setData((byte)DyeColor.YELLOW.getData());
								}else{
									wool.setData((byte)DyeColor.BLACK.getData());
								}
							}
							p.getInventory().remove(Material.WOOL);
							redHasPlaced = true;
							plugin.redwool = l;
							if(blueHasPlaced){
								sendToTeams("Both teams have placed their towers! Game on!");
							}else{
								this.sendToBlue("The "+ ChatColor.RED + "Red " + ChatColor.RESET+ "team has placed their tower");
							}
							p.updateInventory();
						}else{
							e.getPlayer().sendMessage("The tower cannot be placed here because it would be touching other blocks");
							e.setCancelled(true);
							p.updateInventory();
						}

					}
				}
				if (getTeamColor(p).equals("blue")){
					if(this.blueHasPlaced){
						p.getInventory().remove(Material.WOOL);
						e.setCancelled(true);
						p.updateInventory();
					}else{
						if(isSafeToPlaceTower(e.getBlock().getLocation())){
							Location l = e.getBlock().getLocation();
							for(int y = 0; y < 5; y++){
								Block wool = new Location(l.getWorld(), l.getX(), l.getY()+y, l.getZ()).getBlock();
								wool.setType(Material.WOOL);
								if(y>0){
									wool.setData(DyeColor.YELLOW.getData());
								}else{
									wool.setData(DyeColor.BLACK.getData());
								}
							}
							p.getInventory().remove(Material.WOOL);
							plugin.bluewool = l;
							blueHasPlaced = true;
							if(redHasPlaced){
								sendToTeams("Both teams have placed their towers. Game on!");
							}else{
								this.sendToRed("The "+ ChatColor.BLUE + "Blue " + ChatColor.RESET+ "team has placed their tower");
							}
							p.updateInventory();
						}else{
							e.getPlayer().sendMessage("The tower cannot be placed here because it would be touching other blocks");
							e.setCancelled(true);
							p.updateInventory();
						}
					}
				}
			}
		}
	}
	private boolean isTouchingWater(Location templ) {
		Location l = templ;
		l.setX(l.getBlockX()+1);
		if(l.getBlock().getTypeId() == 9){
			return true;
		}else{
			l.setX(l.getBlockX()-2);
			if(l.getBlock().getTypeId() == 9){
				return true;
			}else{
				l.setX(l.getBlockX()+1);
				l.setZ(l.getBlockZ()+1);
				if(l.getBlock().getTypeId() == 9){
					return true;
				}else{
					l.setZ(l.getBlockZ()-2);
					if(l.getBlock().getTypeId() == 9){
						return true;
					}else{
						return false;
					}
				}
			}
		}
	}
	private boolean isTouchingLava(Location templ) {
		Location l = templ;
		l.setX(l.getBlockX()+1);
		if(l.getBlock().getTypeId() == 10||l.getBlock().getTypeId() == 11){
			return true;
		}
		l.setX(l.getBlockX()-2);
		if(l.getBlock().getTypeId() == 10||l.getBlock().getTypeId() == 11){
			return true;
		}
		l.setX(l.getBlockX()+1);
		l.setZ(l.getBlockZ()+1);
		if(l.getBlock().getTypeId() == 10||l.getBlock().getTypeId() == 11){
			return true;
		}
		l.setZ(l.getBlockZ()-2);
		if(l.getBlock().getTypeId() == 10||l.getBlock().getTypeId() == 11){
			return true;
		}
		l.setZ(l.getBlockZ()+1);
		l.setY(l.getBlockY()+1);
		if(l.getBlock().getTypeId() == 10||l.getBlock().getTypeId() == 11){
			return true;
		}
		l.setY(l.getBlockY()-2);
		if(l.getBlock().getTypeId() == 10||l.getBlock().getTypeId() == 11){
			return true;
		}
		return false;

	}

	private boolean isTouchingBricks(Location templ) {
		Location l = templ;
		l.setX(l.getBlockX()+1);
		if(l.getBlock().getTypeId() == 98){
			return true;
		}
		l.setX(l.getBlockX()-2);
		if(l.getBlock().getTypeId() == 98){
			return true;
		}
		l.setX(l.getBlockX()+1);
		l.setZ(l.getBlockZ()+1);
		if(l.getBlock().getTypeId() == 98){
			return true;
		}
		l.setZ(l.getBlockZ()-2);
		if(l.getBlock().getTypeId() == 98){
			return true;
		}
		l.setZ(l.getBlockZ()+1);
		l.setY(l.getBlockY()+1);
		if(l.getBlock().getTypeId() == 98){
			return true;
		}
		l.setY(l.getBlockY()-2);
		if(l.getBlock().getTypeId() == 98){
			return true;
		}
		return false;

	}

	private boolean isSafeToPlaceTower(Location templ) {
		Location l = templ;
		l.setX(l.getBlockX()-1);
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 5; y++){
				if(!((new Location(l.getWorld(), l.getBlockX()+x, l.getBlockY()+y, l.getBlockZ())).getBlock().getType().equals(Material.AIR)|| (new Location(l.getWorld(), l.getBlockX()+x, l.getBlockY()+y, l.getBlockZ())).getBlock().getType().equals(Material.WOOL))){
					return false;
				}

			}
		}
		l.setX(l.getBlockX()+1);
		l.setZ(l.getBlockZ()-1);
		for(int z = 0; z < 3; z++){
			for(int y = 0; y < 5; y++){
				if(!((new Location(l.getWorld(), l.getBlockX(), l.getBlockY()+y, l.getBlockZ()+z)).getBlock().getType().equals(Material.AIR)||(new Location(l.getWorld(), l.getBlockX(), l.getBlockY()+y, l.getBlockZ()+z)).getBlock().getType().equals(Material.WOOL))){
					return false;
				}
			}
		}
		return true;
	}


	private String getTeamColor(Player p) {
		for(int i = 0; i < 3; i++){
			if(plugin.RED[i].equals(p)){
				return "red";
			}
			if(plugin.BLUE[i].equals(p)){
				return "blue";
			}
		}
		return null;
	}

	private boolean isTouchingWool(Location templ) {
		Location l = templ;
		l.setX(l.getBlockX()+1);
		if(l.getBlock().getType().equals(Material.WOOL)){
			return true;
		}
		l.setX(l.getBlockX()-2);
		if(l.getBlock().getType().equals(Material.WOOL)){
			return true;
		}
		l.setX(l.getBlockX()+1);
		l.setZ(l.getBlockZ()+1);
		if(l.getBlock().getType().equals(Material.WOOL)){
			return true;
		}
		l.setZ(l.getBlockZ()-2);
		if(l.getBlock().getType().equals(Material.WOOL)){
			return true;
		}
		return false;
	}

	@EventHandler
	public void breakblock (BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(this.isPlayingGame(p)){
			Material m = e.getBlock().getType();
			if(!(m.equals(Material.LEAVES)||m.equals(Material.LOG)||m.equals(Material.MELON_BLOCK)||m.equals(Material.WOOD)||m.equals(Material.SAPLING)||m.equals(Material.DISPENSER)||m.equals(Material.STONE_BUTTON))){
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void respawnevent (PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if(isPlayingGame(p)){
			if(this.getTeamColor(p).equals("red")){
				e.setRespawnLocation(plugin.rstrip);
			}
			if(this.getTeamColor(p).equals("blue")){
				e.setRespawnLocation(plugin.bstrip);
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void dieevent (PlayerDeathEvent e) {
		if(e.getEntityType().equals(EntityType.PLAYER)){
			Player p = (Player)e.getEntity();
			if(isPlayingGame(p)){
				if(this.getTeamColor(p).equals("red")){
					plugin.BLUE[1].getInventory().addItem(new ItemStack(352));
					plugin.BLUE[1].updateInventory();
					int i = 0;
					for(i = 0; i <3; i++){
						if(plugin.RED[i].equals(p)){
							deathsRed[i]++;
							break;
						}
					}
					if(this.deathsRed[i]>5){
						for(int c = 0; c < 30; c++){
							if (plugin.redblazes[c]== null){
								plugin.redblazes[c]=(Blaze)plugin.getServer().getWorld("world").spawnCreature(p.getLocation(), EntityType.BLAZE);
								this.sendToTeams(p.getDisplayName() + " has died more than five times: a blaze rises from the ashes");
								break;
							}
						}
					}else{
						if(deathsRed[i]>1){
							this.sendToTeams(p.getDisplayName() + " has died " + deathsRed[i] + " times");	
						}
					}
				}
				if(this.getTeamColor(p).equals("blue")){
					plugin.RED[1].getInventory().addItem(new ItemStack(352));
					plugin.RED[1].updateInventory();
					int i = 0;
					for(i = 0; i <3; i++){
						if(plugin.BLUE[i].equals(p)){
							deathsBlue[i]++;
							break;
						}
					}
					if(this.deathsBlue[i]>5){
						for(int c = 0; c < 30; c++){
							if (plugin.blueblazes[c]== null){
								plugin.blueblazes[c]=(Blaze)plugin.getServer().getWorld("world").spawnCreature(p.getLocation(), EntityType.BLAZE);
								this.sendToTeams(p.getDisplayName() + " has died more than five times: a blaze rises from the ashes");
								break;
							}
						}
					}else{
						if(deathsBlue[i]>1){
							this.sendToTeams(p.getDisplayName() + " has died " + deathsBlue[i] + " times");
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void burnevent (BlockBurnEvent e) {
		if(e.getBlock().getType().equals(Material.WOOL)){
			String winner = null;
			if(isInRedCourt(e.getBlock().getLocation())){
				winner = (ChatColor.BLUE + "BLUE " + ChatColor.RESET);
			}else{
				if(isInBlueCourt(e.getBlock().getLocation())){
					winner = (ChatColor.RED + "RED " + ChatColor.RESET);
				}
			}
			if(winner != null && !hasBurned){
				hasBurned = true;
				this.sendToTeams(winner + "wins!");
				removeLavaWall();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						plugin.endGame();
					}
				}, 300L);
			}
		}
		if(e.getBlock().getType().equals(Material.WOOD)){
			Location l = e.getBlock().getLocation();
			if(this.isInRedCourt(l)){
				for(int i = 0; i < 3; i++){
					plugin.BLUE[i].sendBlockChange((new Location(l.getWorld(), l.getX(), l.getY(), plugin.tz+57)),35,(byte)5);
				}
			}else{
				if(this.isInBlueCourt(l)){
					for(int i = 0; i < 3; i++){
						plugin.RED[i].sendBlockChange((new Location(l.getWorld(), l.getX(), l.getY(), plugin.tz+57)),35,(byte)5);
					}
				}
			}
		}
	}

	private void removeLavaWall() {
		for(int x = plugin.blueCorner1.getBlockX(); x <= plugin.redCorner2.getBlockX(); x++){
			for(int y = plugin.blueCorner1.getBlockY(); y < plugin.redCorner2.getBlockY(); y++){
				new Location(plugin.RED[0].getWorld(), x, y, plugin.redCorner2.getBlockZ()+2).getBlock().setType(Material.AIR);
			}
		}
	}

	private boolean isInBlueCourt(Location l) {
		if(l.getBlockX() >= plugin.blueCorner1.getBlockX()&& l.getBlockX() <= plugin.blueCorner2.getBlockX()){
			if(l.getBlockZ() >= plugin.blueCorner1.getBlockZ()&& l.getBlockZ() <= plugin.blueCorner2.getBlockZ()){
				return true;
			}
		}
		return false;
	}

	private boolean isInRedCourt(Location l) {
		if(l.getBlockX() >= plugin.redCorner1.getBlockX()&& l.getBlockX() <= plugin.redCorner2.getBlockX()){
			if(l.getBlockZ() >= plugin.redCorner1.getBlockZ()&& l.getBlockZ() <= plugin.redCorner2.getBlockZ()){
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void lavaburn (EntityDamageEvent e) {
		if(e.getEntityType().equals(EntityType.PLAYER)){
			Player p = (Player)e.getEntity();
			if(isPlayingGame(p)){
				if(e.getCause().equals(DamageCause.LAVA)){
					if (this.getTeamColor(p).equals("red")){
						p.teleport(plugin.rstrip);
					}
					if (this.getTeamColor(p).equals("blue")){
						p.teleport(plugin.bstrip);
					}
				}
			}
		}
	}
	@EventHandler
	public void craftworkbench (PrepareItemCraftEvent e) {
		boolean available = true;
		Player p = null;
		try{
			p = (Player) e.getViewers().get(0);
		}catch(Exception q){
			available = false;
		}
		if(available){
			if(this.isPlayingGame(p)){
				if(e.getRecipe().getResult().getType().equals(Material.WORKBENCH)){
					p.sendMessage("Don't waste wood on a workbench - you can't use it here");
				}
			}
		}
	}
	/*TODO, 
	 * NO DUPLICATE PLAYERS ENABLED
	 * Make messages sent to all three players once upgraded for 6 player games*/
}