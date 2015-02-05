package fr.mrkold.plotplus;

import java.io.File;
import java.io.IOException;

import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PlotPlusPlugin extends JavaPlugin implements Listener {
	
	// --- Definition des variables ---
	
	private final ChatColor RED = ChatColor.RED;
	private final ChatColor GREEN = ChatColor.GREEN;
	private final ChatColor AQUA = ChatColor.AQUA;
	private String lang;
    private File myFile;
    private PluginDescriptionFile pdf = this.getDescription();						// recuperer les infos de plugin.yml
    private String version = pdf.getVersion();
	private String nomplugin = pdf.getName();
	private Boolean BarAPIOK;
    
    // ---------------------------------
    
	@Override
	public void onDisable() 														// A la desactivation
	{
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	// ---------------------------------
	
	@Override
	public void onEnable() {														// A l'activation
		saveDefaultConfig();														// ecrire le fichier de config par defaut
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		lang = getConfig().getString("lang");
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info(nomplugin + " v"+ version + " enabled");
		
        myFile = new File(getDataFolder(), "plots.yml");							// Creer le fichier plots.yml s'il n'existe pas
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        Plugin BarAPIPlugin = getServer().getPluginManager().getPlugin("BarAPI");
		if((BarAPIPlugin != null) && BarAPIPlugin.isEnabled()){
			BarAPIOK = true;
			getLogger().info("Plugin 'BarAPI' found. Using it now.");
		}
		else{
			BarAPIOK = false;
		}
	}
	
	// ---------------------------------
	
	public void ReloadPlugin(Player p) {
		this.reloadConfig();
		lang = getConfig().getString("lang");
		p.sendMessage(AQUA + "[PlotPlus] Configuration reloaded");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		if((label.equalsIgnoreCase("pp"))||(label.equalsIgnoreCase("plotplus"))) { 						// On verifie que la commande est /pp ou /plotplus
		    if(sender instanceof Player) {																// On verifie que la commande est entree par le joueur
		    	Player p = (Player) sender;
		    	if(p.hasPermission("plotplus.use")){
		    		if (args.length == 0) { 																// Si la commande n'a pas d'argument
						p.sendMessage(AQUA + "------------------------------");
						p.sendMessage(AQUA + nomplugin + " v" + version + " by MrKold");
						p.sendMessage(AQUA + "------------------------------");
						p.sendMessage(GREEN + "");
						p.sendMessage(AQUA + "Syntax:");
						p.sendMessage(GREEN + "Time: " + AQUA + "/pp ticks|resettime");
						p.sendMessage(GREEN + "Weather: " + AQUA + "/pp rain|resetweather");
						p.sendMessage(GREEN + "Reload: " + AQUA + "/pp reload");
						return true;
					}
			    	else {
			    		String a0 = args[0];
			            YamlConfiguration plots = YamlConfiguration.loadConfiguration(myFile);					// Chargement du fichier plots.yml
			            
			            if ((a0.equalsIgnoreCase("reload")) && (p.hasPermission("plotplus.admin"))) {
			            	ReloadPlugin(p);
				    		return true;
						}
			            
			            if(PlotManager.getMap(p) == null) {															// Si la map n'est pas un plotworld
				            p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplotworld")));
				        } else {
				            String id = PlotManager.getPlotId(p);													// recuperer l'id du plot où se trouve le joueur
				            String world = p.getWorld().getName();
				            
				            if(id.equals("")) {																		// id == 0 : Ce n'est pas un plot
				                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplot")));
				            } else {	                
		    		            Plot plot = PlotManager.getPlotById(p);												// recuperer les infos du plot
		    		            String joueur = p.getName();
		    		            
		    		            if(plot != null && ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin"))) {		// On vérifie que le plot appartient au joueur et qu'il a la permission
		    		            	
		    		            	String plotid = plot.id;
		    		            	
		    		            	if (a0.equalsIgnoreCase("resettime")) {											// Si l'argument est resettime
		    		            		plots.set("plots." + world + "." + plotid + ".time", null);									// On supprime time dans le fichier plots.yml
		    		            	    try {
											plots.save(myFile);
										} catch (IOException e) {
											// catch block
											e.printStackTrace();
										}
										p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resettime")));
									}
		    		            	else if (a0.equalsIgnoreCase("rain")) {											// Si l'argument est rain
		    		            		plots.set("plots." + world + "." + plotid + ".rain", true);								// On met rain a true dans le fichier plots.yml
		    		            		try {
		    		            			plots.save(myFile);
		    		            		} catch (IOException e) {
		    		            			// catch block
		    		            			e.printStackTrace();
		    		            		}
		    		            		p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".setrain")));
									}
		    		            	else if (a0.equalsIgnoreCase("resetweather")) {									// Si l'argument est resetweather
		    		            		plots.set("plots." + world + "." + plotid + ".rain", null);								// On supprime rain dans le fichier plots.yml
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
		    		            			plots.set("plots." + world + "." + plotid + ".time", setheure);						// On met l'heure à setheure dans le fichier plots.yml
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
		    		p.sendMessage(RED + (getConfig().getString("messages."+ lang +".nopermission")));
		    	}
		    }
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent evt){	// Lorsque le joueur bouge
			Player p = evt.getPlayer();
			File plotsFile = new File(this.getDataFolder(), "plots.yml");
	        FileConfiguration plots = YamlConfiguration.loadConfiguration(plotsFile);		// Chargement du fichier plots.yml
			String id = PlotManager.getPlotId(p.getLocation());
			
			if(onPlot(p, id)){															// On teste si l'on est sur un plot
				Plot plot = PlotManager.getPlotById(p, id);
				String world = p.getWorld().getName();
				if(plot != null){
					String plotid = plot.id;
					int heure = plots.getInt("plots." + world + "." + plotid + ".time");				// Recuperation des donnees dans le fichier de configuration plots.yml
					Boolean rain = plots.getBoolean("plots." + world + "." + plotid + ".rain");
					if(heure != 0){
						p.setPlayerTime(heure, false);
					}																	// On applique les parametres
					if(rain){
						p.setPlayerWeather(WeatherType.DOWNFALL);
					}

					
						if(BarAPIOK){
							String message= getConfig().getString("messages."+ lang +".plotowner") + " " + plot.owner;
							BarAPI.setMessage(p, message);
						}
					
				}
				else{
					String plotid = PlotManager.getPlotId(p.getLocation());
					plots.set("plots." + world + "." + plotid, null);
					try {
						plots.save(myFile);
					} catch (IOException e) {
						// catch block
						e.printStackTrace();
					}
				}
			}
			else
			{
				if(BarAPIOK && (BarAPI.hasBar(p))){
					BarAPI.removeBar(p);
				}
				p.resetPlayerTime();													// Reinitialisation des parametres
				p.resetPlayerWeather();
			}
	}

	private boolean onPlot(Player p, String id) {
		if(!id.equalsIgnoreCase("")){
				return true;
		}
		return false;
	}
}

