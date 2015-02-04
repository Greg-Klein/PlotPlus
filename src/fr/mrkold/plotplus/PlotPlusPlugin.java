package fr.mrkold.plotplus;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PlotPlusPlugin extends JavaPlugin implements Listener {
	
	private final ChatColor RED = ChatColor.RED;
	private final ChatColor GREEN = ChatColor.GREEN;
	private final ChatColor AQUA = ChatColor.AQUA;
	private String lang = "";
    private File myFile;
	
	@Override
	public void onDisable() 
	{
		PluginDescriptionFile pdf = this.getDescription(); //Gets plugin.yml
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = this.getDescription(); //Gets plugin.yml
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " enabled");
		
		//Get/Create File
        myFile = new File(getDataFolder(), "plots.yml");
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		if(label.equalsIgnoreCase("pp")) {
		    if(sender instanceof Player) {
		    	if(sender.hasPermission("plotplus.use")){
		    		if (args.length == 0) {
			    		PluginDescriptionFile pdf = this.getDescription(); //Gets plugin.yml
			    		String version = pdf.getVersion();
			    		String nomplugin = pdf.getName();
						sender.sendMessage(AQUA + "------------------------------");
						sender.sendMessage(AQUA + nomplugin + " v" + version + " by MrKold");
						sender.sendMessage(AQUA + "------------------------------");
						sender.sendMessage(GREEN + "");
						sender.sendMessage(AQUA + "Syntax:");
						sender.sendMessage(GREEN + "Time: " + AQUA + "/pp ticks|resettime");
						sender.sendMessage(GREEN + "Weather: " + AQUA + "/pp rain|resetweather");
						sender.sendMessage(GREEN + "Reload: " + AQUA + "/pp reload");
						return true;
					}
			    	else {
			    		String a0 = args[0];
			            Player p = (Player) sender;
			            lang = getConfig().getString("lang");
			            YamlConfiguration plots = YamlConfiguration.loadConfiguration(myFile);
			            
			            if (sender.hasPermission("plotplus.admin")) {
				    		if (a0.equalsIgnoreCase("reload")){
				    			reloadConfig();
				    			sender.sendMessage(AQUA + "[PlotPlus] Configuration reloaded");
				    			return true;
				    		}
						}
			            
			            if(PlotManager.getMap(p) == null) {
				            p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplotworld")));
				        } else {
				            
				            //The plotmanager class contains static methods. Note that this is not the case for PlotMe-Core
				            String id = PlotManager.getPlotId(p);
				            
				            if(id.equals("")) {
				                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplot")));
				            } else {	                
		    		            Plot plot = PlotManager.getPlotById(p);  //this function supports many arguments; world, location, id, etc..
		    		            String joueur = p.getName();
		    		            
		    		            if(plot != null && ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin"))) {
		    		            	
		    		            	String plotid = plot.id;
		    		            	
		    		            	if (a0.equalsIgnoreCase("resettime")) {
		    		            		plots.set("plots." + plotid + ".time", 0);
		    		            	    try {
											plots.save(myFile);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resettime")));
									}
		    		            	else if (a0.equalsIgnoreCase("rain")) {
		    		            		plots.set("plots." + plotid + ".rain", true);
		    		            		try {
		    		            			plots.save(myFile);
		    		            		} catch (IOException e) {
		    		            			// TODO Auto-generated catch block
		    		            			e.printStackTrace();
		    		            		}
		    		            		p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".setrain")));
									}
		    		            	else if (a0.equalsIgnoreCase("resetweather")) {
		    		            		plots.set("plots." + plotid + ".rain", false);
		    		            		try {
											plots.save(myFile);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resetweather")));
									}
		    		            	else {
		    		            		int setheure;
		    		            		try {
		    		            			setheure =  Integer.parseInt(a0);
		    		            			if ((setheure <= 0)||(setheure > 24000)){
		    		            				p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badtime")));
		    		            				return false;
		    		            			}
		    		            			plots.set("plots." + plotid + ".time", setheure);
		    		            			try {
												plots.save(myFile);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										    p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".settime")) + ": " + setheure + "ticks");
		    		            		}
		    		            		catch (NumberFormatException nfe) {
		    		            			p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badargument")));
		    		            			return false;
		    		            		}
									}
		    		            	
		    		            } else {
		    		                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".notyourplot")));
		    		            }
				            }
				        }
			    	}
		    	}
		    	else {
		    		sender.sendMessage(RED + (getConfig().getString("messages."+ lang +".nopermission")));
		    	}
		    }
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent evt)
	{
		Player p = evt.getPlayer();
		File plotsFile = new File(this.getDataFolder(), "plots.yml");
        FileConfiguration plots = YamlConfiguration.loadConfiguration(plotsFile);
		
			Location to = evt.getTo();
			
			String idTo = PlotManager.getPlotId(to);
			
			if(!idTo.equalsIgnoreCase(""))
			{
				Plot plot = PlotManager.getPlotById(p, idTo);
				
				if(plot != null)
				{
					String plotid = plot.id;
					int heure = plots.getInt("plots." + plotid + ".time");
					Boolean rain = plots.getBoolean("plots." + plotid + ".rain");
					if(heure != 0){
						p.setPlayerTime(heure, false);
					}
					if(rain){
						p.setPlayerWeather(WeatherType.DOWNFALL);
					}
				}
			}
			else
			{
				p.resetPlayerTime();
				p.resetPlayerWeather();
			}
	}
}
