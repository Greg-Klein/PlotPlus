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
	
	// --- Definition des variables ---
	
	private final ChatColor RED = ChatColor.RED;
	private final ChatColor GREEN = ChatColor.GREEN;
	private final ChatColor AQUA = ChatColor.AQUA;
	private String lang = "";
    private File myFile;
    
    // ---------------------------------
    
	@Override
	public void onDisable() 														// A la desactivation
	{
		PluginDescriptionFile pdf = this.getDescription(); 							// recuperer les infos de plugin.yml
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	// ---------------------------------
	
	@Override
	public void onEnable() {														// A l'activation
		PluginDescriptionFile pdf = this.getDescription(); 							// recuperer les infos de plugin.yml
		saveDefaultConfig(); 														// ecrire le fichier de config par defaut
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " enabled");
		
        myFile = new File(getDataFolder(), "plots.yml");							// Creer le fichier plots.yml s'il n'existe pas
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	// ---------------------------------
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		if((label.equalsIgnoreCase("pp"))||(label.equalsIgnoreCase("plotplus"))) { 						// On verifie que la commande est /pp ou /plotplus
		    if(sender instanceof Player) { 																// On verifie que la commande est entree par le joueur
		    	if(sender.hasPermission("plotplus.use")){
		    		if (args.length == 0) { 																// Si la commande n'a pas d'argument
			    		PluginDescriptionFile pdf = this.getDescription();
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
			            YamlConfiguration plots = YamlConfiguration.loadConfiguration(myFile);					// Chargement du fichier plots.yml
			            
			            if ((a0.equalsIgnoreCase("reload")) && (sender.hasPermission("plotplus.admin"))) {
				    		reloadConfig();
				    		sender.sendMessage(AQUA + "[PlotPlus] Configuration reloaded");
				    		return true;
						}
			            
			            if(PlotManager.getMap(p) == null) {															// Si la map n'est pas un plotworld
				            p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplotworld")));
				        } else {
				            String id = PlotManager.getPlotId(p);													// recuperer l'id du plot où se trouve le joueur
				            
				            if(id.equals("")) {																		// id == 0 : Ce n'est pas un plot
				                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplot")));
				            } else {	                
		    		            Plot plot = PlotManager.getPlotById(p);												// recuperer les infos du plot
		    		            String joueur = p.getName();
		    		            
		    		            if(plot != null && ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin"))) {		// On vérifie que le plot appartient au joueur et qu'il a la permission
		    		            	
		    		            	String plotid = plot.id;
		    		            	
		    		            	if (a0.equalsIgnoreCase("resettime")) {											// Si l'argument est resettime
		    		            		plots.set("plots." + plotid + ".time", 0);									// On met time a 0 dans le fichier plots.yml
		    		            	    try {
											plots.save(myFile);
										} catch (IOException e) {
											// catch block
											e.printStackTrace();
										}
										p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resettime")));
									}
		    		            	else if (a0.equalsIgnoreCase("rain")) {											// Si l'argument est resettime
		    		            		plots.set("plots." + plotid + ".rain", true);								// On met rain a true dans le fichier plots.yml
		    		            		try {
		    		            			plots.save(myFile);
		    		            		} catch (IOException e) {
		    		            			// catch block
		    		            			e.printStackTrace();
		    		            		}
		    		            		p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".setrain")));
									}
		    		            	else if (a0.equalsIgnoreCase("resetweather")) {									// Si l'argument est resetweather
		    		            		plots.set("plots." + plotid + ".rain", false);								// On met rain a false dans le fichier plots.yml
		    		            		try {
											plots.save(myFile);
										} catch (IOException e) {
											// catch block
											e.printStackTrace();
										}
										p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resetweather")));
									}
		    		            	else {
		    		            		int setheure;
		    		            		try {
		    		            			setheure =  Integer.parseInt(a0);										// Recuperation de l'heure dans setheure depuis l'argument
		    		            			if ((setheure <= 0)||(setheure > 24000)){
		    		            				p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badtime")));
		    		            				return false;
		    		            			}
		    		            			plots.set("plots." + plotid + ".time", setheure);						// On met l'heure à setheure dans le fichier plots.yml
		    		            			try {
												plots.save(myFile);
											} catch (IOException e) {
												// catch block
												e.printStackTrace();
											}
										    p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".settime")) + ": " + setheure + "ticks");
		    		            		}
		    		            		catch (NumberFormatException nfe) {
		    		            			p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badargument")));	// Si l'argument ne correspond à rien on renvoi un message d'erreur
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
	public void onMove(PlayerMoveEvent evt)												// Lorsque le joueur bouge
	{
		Player p = evt.getPlayer();
		File plotsFile = new File(this.getDataFolder(), "plots.yml");
        FileConfiguration plots = YamlConfiguration.loadConfiguration(plotsFile);		// Chargement du fichier plots.yml
		
			Location to = evt.getTo();
			
			String idTo = PlotManager.getPlotId(to);
			
			if(!idTo.equalsIgnoreCase(""))
			{
				Plot plot = PlotManager.getPlotById(p, idTo);
				
				if(plot != null)														// On teste si l'on est sur un plot
				{
					String plotid = plot.id;
					int heure = plots.getInt("plots." + plotid + ".time");				// Recuperation des donnees dans le fichier de configuration plots.yml
					Boolean rain = plots.getBoolean("plots." + plotid + ".rain");
					if(heure != 0){
						p.setPlayerTime(heure, false);
					}																	// On applique les parametres
					if(rain){
						p.setPlayerWeather(WeatherType.DOWNFALL);
					}
				}
			}
			else
			{
				p.resetPlayerTime();													// Reinitialisation des parametres
				p.resetPlayerWeather();
			}
	}
}
