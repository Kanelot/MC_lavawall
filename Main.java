package me.kan.wall;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	public static Main plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public Game playerListener;
	public final ButtonPress buttonListener = new ButtonPress(this);
	Player[] RED = new Player[3];
	Player[] BLUE = new Player[3];
	public int redNumber;
	public int blueNumber;
	boolean listenerHasStarted = false;
	boolean isGaming = false;
	Location rstrip;
	Location bstrip;
	Location redCorner1;
	Location redCorner2;
	Location blueCorner1;
	Location blueCorner2;
	int tx = 0;
	int ty = 0;
	int tz = 0;
	int taskID;
	Blaze[] redblazes;
	Blaze[] blueblazes;
	Location redwool = null;
	Location bluewool = null;
	public void endGame(){
		HandlerList.unregisterAll(playerListener);
		Bukkit.getScheduler().cancelTask(taskID);
		listenerHasStarted = false;
		redNumber = 0;
		blueNumber = 0;
		redblazes = null;
		blueblazes = null;
		redwool = null;
		bluewool = null;
		for(int x = redCorner1.getBlockX(); x <= blueCorner2.getBlockX(); x++){
			for(int z = redCorner1.getBlockZ(); z <= blueCorner2.getBlockZ(); z++){
				int y = redCorner1.getBlockY()-1;
				int id = new Location(this.getServer().getWorld("world"),x,y,z).getBlock().getTypeId();
				if(id == 60 || id == 0){
					new Location(this.getServer().getWorld("world"),x,y,z).getBlock().setType(Material.DIRT);
				}

			}
		}
		for(int x = redCorner1.getBlockX(); x <= blueCorner2.getBlockX(); x++){
			for(int y = redCorner1.getBlockY(); y <= blueCorner2.getBlockY(); y++){
				for(int z = redCorner1.getBlockZ(); z <= blueCorner2.getBlockZ(); z++){
					int id = new Location(this.getServer().getWorld("world"),x,y,z).getBlock().getTypeId();

					if(id != 44 && id != 10 && id != 11){
						new Location(this.getServer().getWorld("world"),x,y,z).getBlock().setType(Material.AIR);
					}
				}
			}
		}
		List<Entity> entList = getServer().getWorld("world").getEntities();
		for(Entity current : entList){
			if (current instanceof Item){
				current.remove();
			}
			if (current instanceof Blaze){
				current.remove();
			}
		}
		Location EXIT = new Location(this.getServer().getWorld("world"), tx-805.5,ty+17,tz+57.5);
		for(int i = 0; i < 3; i++){
			try{
				RED[i].teleport(EXIT);
				RED[i].getInventory().clear();
			}catch(Exception e){}
		}
		for(int i = 0; i < 3; i++){
			try{
				BLUE[i].teleport(EXIT);
				BLUE[i].getInventory().clear();
			}catch(Exception e){}
		}
		RED[0] = null;
		RED[1] = null;
		RED[2] = null;
		BLUE[0] = null;
		BLUE[1] = null;
		BLUE[2] = null;
		isGaming = false;
	}

	public void addRed(Player p){
		boolean isFirstTime = true;
		for(int i = 0; redNumber < 4 && i<redNumber&&isFirstTime; i++){
			if (RED[i].equals(p)){
				isFirstTime = false; // COMMENT OUT FOR 2 player (and same for blue)
			}
		}
		for(int i = 0; blueNumber < 4 &&i<blueNumber&&isFirstTime; i++){
			if (BLUE[i].equals(p)){
				isFirstTime = false;
			}
		}
		if(RED[2] != null){
			p.sendMessage("This team is already full");
		}else{
			if(isFirstTime){
				redNumber++;
				if (redNumber<4){
					RED[redNumber-1] = p;
					p.sendMessage("You are now Player " + redNumber + " of the red team!");
				}
			}
			else
			{
				p.sendMessage("You have already joined the game!");
			}
		}

	}

	public void addBlue(Player p){
		boolean isFirstTime = true;
		for(int i = 0; redNumber < 4 && i<redNumber&&isFirstTime; i++){
			if (RED[i].equals(p)){
				isFirstTime = false;
			}
		}
		for(int i = 0; blueNumber < 4 &&i<blueNumber&&isFirstTime; i++){
			if (BLUE[i].equals(p)){
				isFirstTime = false; //COMMENT OUT FOR 2 player
			}
		}
		if(BLUE[2] != null){
			p.sendMessage("This team is already full");
		}else{
			if(isFirstTime){
				blueNumber++;
				if (blueNumber<4){
					BLUE[blueNumber-1] = (p);
					p.sendMessage("You are now Player " + blueNumber + " of the blue team!");
				}
			}else{p.sendMessage("You have already joined the game!");}
		}

	}


	@SuppressWarnings("deprecation")
	public void startGame(Player p) {
		isGaming = true;
		this.activateListener();
		listenerHasStarted = true;
		bstrip = new Location(p.getWorld(), tx-779.5,ty+13,tz+25.0);
		rstrip = new Location(p.getWorld(), tx-786.5,ty+13,tz+90.0);
		redCorner1 = new Location(p.getWorld(), tx-798,ty+13,tz+27);
		redCorner2 = new Location(p.getWorld(), tx-769,ty+30,tz+55);
		blueCorner1 = new Location(p.getWorld(), tx-798,ty+13,tz+59);
		blueCorner2 = new Location(p.getWorld(), tx-769,ty+30,tz+87);
		redblazes = new Blaze[30];
		blueblazes = new Blaze[30];
		for(int i = 0; i<3; i++){
			RED[i].getInventory().clear();
			BLUE[i].getInventory().clear();
			RED[i].getInventory().addItem(new ItemStack(261,1));
			RED[i].getInventory().addItem(new ItemStack(6,1));
			RED[i].getInventory().addItem(new ItemStack(23,1));
			RED[i].getInventory().addItem(new ItemStack(77,1));
			RED[i].getInventory().addItem(new ItemStack(362,8));
			RED[i].getInventory().addItem(new ItemStack(261,1));
			RED[i].getInventory().addItem(new ItemStack(352,5));
			RED[i].getInventory().addItem(new ItemStack(385,576));
			RED[i].getInventory().addItem(new ItemStack(262,256));
			BLUE[i].getInventory().addItem(new ItemStack(261,1));
			BLUE[i].getInventory().addItem(new ItemStack(6,1));
			BLUE[i].getInventory().addItem(new ItemStack(23,1));
			BLUE[i].getInventory().addItem(new ItemStack(77,1));
			BLUE[i].getInventory().addItem(new ItemStack(362,8));
			BLUE[i].getInventory().addItem(new ItemStack(261,1));
			BLUE[i].getInventory().addItem(new ItemStack(352,5));
			BLUE[i].getInventory().addItem(new ItemStack(385,576));
			BLUE[i].getInventory().addItem(new ItemStack(262,256));
			RED[i].teleport(rstrip);
			BLUE[i].teleport(bstrip);
		}
		RED[0].getInventory().addItem(new ItemStack(35,1));
		BLUE[0].getInventory().addItem(new ItemStack(35,1));
		RED[0].getInventory().addItem(new ItemStack(292,1));
		BLUE[0].getInventory().addItem(new ItemStack(292,1));
		RED[1].getInventory().addItem(new ItemStack(257,1));
		BLUE[1].getInventory().addItem(new ItemStack(257,1));
		RED[2].getInventory().addItem(new ItemStack(258,1));
		BLUE[2].getInventory().addItem(new ItemStack(258,1));
		for(int i = 0; i<3; i++){
			RED[i].updateInventory();
			RED[i].setHealth(20);
			RED[i].setFoodLevel(20);
			BLUE[i].updateInventory();
			BLUE[i].setHealth(20);
			BLUE[i].setFoodLevel(20);
		}
		
	}
	public void onDisable()
	{
		if(isGaming){
			endGame();
		}
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable()
	{
		redNumber = 0;
		blueNumber = 0;
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.buttonListener, this);
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled. Lets go!!!");

	}
	public void activateListener()
	{
		PluginManager pm = getServer().getPluginManager();
		playerListener = new Game(this);
		pm.registerEvents(this.playerListener, this);
		taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for(int red = 0; red < 30; red++){
					if(redblazes[red]!= null){
						redblazes[red].launchProjectile(SmallFireball.class).setDirection(redwool.toVector().subtract(redblazes[red].getLocation().toVector()));
					}
				}
				for(int blue = 0; blue < 30; blue++){
					if(blueblazes[blue]!= null){
						blueblazes[blue].launchProjectile(SmallFireball.class).setDirection(bluewool.toVector().subtract(blueblazes[blue].getLocation().toVector()));
					}
				}
			}
		}, 40L, 40L);
		this.logger.info("A Lava Wall fight has started");
	}
}

