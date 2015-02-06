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
	private boolean notationenabled = getConfig().getBoolean("rate-plots");
	private String a0 = "rain";
	private String a1;
	
    
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
						if(p.hasPermission("plotplus.rate.set")){
							p.sendMessage(AQUA + "------------------------------");
							p.sendMessage(GREEN + "Rate plot: " + AQUA + "/pp rate XX (Where WW is an integer between 0 and 20)");
							p.sendMessage(GREEN + "Unrate: " + AQUA + "/pp unrate");
						}
						if(p.hasPermission("plotplus.rate.view")){
							p.sendMessage(GREEN + "Get infos about plot: " + AQUA + "/pp info");
						}
						if(p.hasPermission("plotplus.admin")){
							p.sendMessage(AQUA + "------------------------------");
							p.sendMessage(GREEN + "Reload: " + AQUA + "/pp reload");
						}
						return true;
					}
			    	else {
			    		a0 = args[0];
			    		if (args.length == 2) {
			    			a1 = args[1];
			    		}
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
		    		            
		    		            if(plot != null) {
		    		            	String plotid = plot.id;
		    		            	if ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin")){
			    		            	if (a0.equalsIgnoreCase("resettime")) {											// Si l'argument est resettime
			    		            		plots.set("plots." + world + "." + plotid + ".time", null);									// On supprime time dans le fichier plots.yml
			    		            	    try {
												plots.save(myFile);
											} catch (IOException e) {
												// catch block
												e.printStackTrace();
											}
											p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resettime")));
											return true;
										}
			    		            	if (a0.equalsIgnoreCase("rain")) {											// Si l'argument est rain
			    		            		plots.set("plots." + world + "." + plotid + ".rain", true);								// On met rain a true dans le fichier plots.yml
			    		            		try {
			    		            			plots.save(myFile);
			    		            		} catch (IOException e) {
			    		            			// catch block
			    		            			e.printStackTrace();
			    		            		}
			    		            		p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".setrain")));
			    		            		return true;
										}
			    		            	if (a0.equalsIgnoreCase("resetweather")) {									// Si l'argument est resetweather
			    		            		plots.set("plots." + world + "." + plotid + ".rain", null);								// On supprime rain dans le fichier plots.yml
			    		            		try {
												plots.save(myFile);
											} catch (IOException e) {
												// catch block
												e.printStackTrace();
											}
											p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".resetweather")));
											return true;
										}
			    		            	if ((a0.equalsIgnoreCase("rate")) && p.hasPermission("plotplus.rate.set")){	
			    		            		int note;
			    		            		try {
			    		            			note =  Integer.parseInt(a1);
			    		            		}
			    		            		catch (NumberFormatException nfe) {
			    		            			p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badargument")));	// Si l'argument ne correspond à rien on renvoi un message d'erreur
			    		            			return false;
			    		            		}
			    		            		if ((note < 0)||(note > 20)){
			    		            			p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badnotation")));
			    		            			return false;
			    		            		}
			    		            		plots.set("plots." + world + "." + plotid + ".rate", note);						// On met la note dans le fichier plots.yml
			    		            		try {
												plots.save(myFile);
											} catch (IOException e) {
												// catch block
												e.printStackTrace();
											}
											p.sendMessage(GREEN + (getConfig().getString("messages."+ lang +".noteset")) + ": " + note + "/20");
											return true;
			    		            		
										}
			    		            	if ((a0.equalsIgnoreCase("unrate")) && p.hasPermission("plotplus.rate.set")){
			    		            		plots.set("plots." + world + "." + plotid + ".rate", null);
			    		            		try {
												plots.save(myFile);
											} catch (IOException e) {
												// catch block
												e.printStackTrace();
											}
			    		            		p.sendMessage(GREEN + getConfig().getString("messages."+ lang +".notereset"));
			    		            		return true;	
										}
			    		            	if ((a0.equalsIgnoreCase("info")) && p.hasPermission("plotplus.rate.view")){
			    		            		String ratem = getConfig().getString("messages."+ lang +".rated") + " ";
			    		            		String plotownerm = getConfig().getString("messages."+ lang +".plotowner");
			    							String note = (plots.getString("plots." + world + "." + plotid + ".rate"));
			    							String owner = plot.owner;
			    							owner = plotownerm + " " + owner;
			    							if(note == null){
			    								note = getConfig().getString("messages."+ lang +".notrated");
			    							}
			    							else{
			    								note = ratem + note + "/20";
			    							}
			    							owner = ChatColor.translateAlternateColorCodes('&', owner);
			    							note = ChatColor.translateAlternateColorCodes('&', note);
			    							p.sendMessage(GREEN + "------------------------------");
			    							p.sendMessage(GREEN + owner);
			    							p.sendMessage(note);
			    							p.sendMessage(GREEN + "------------------------------");
			    		            		return true;
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
											    return true;
			    		            		}
			    		            		catch (NumberFormatException nfe) {
			    		            			p.sendMessage(RED + (getConfig().getString("messages."+ lang +".badargument")));	// Si l'argument ne correspond à rien on renvoi un message d'erreur
			    		            			return false;
			    		            		}
										}
		    		            	}
		    		            	else {
			    		                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".notyourplot")));
			    		                return false;
			    		            }
		    		            }
		    		            else{
		    		            	p.sendMessage(RED + (getConfig().getString("messages."+ lang +".notowned")));
		    		            	return false;
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
					if (BarAPIOK){
						String message;
						String plotownerm = getConfig().getString("messages."+ lang +".plotowner");
						String ratem = getConfig().getString("messages."+ lang +".rated") + " ";
						String note = (plots.getString("plots." + world + "." + plotid + ".rate"));
						if((notationenabled) && p.hasPermission("plotplus.rate.view")){
							if(note == null){
								note = getConfig().getString("messages."+ lang +".notrated");
							}
							else{
								note = ratem + note + "/20";
							}
							String ncmessage = plotownerm + " " + plot.owner + "&f - " + note;
							message = ChatColor.translateAlternateColorCodes('&', ncmessage);
						}
						else{
							String ncmessage = plotownerm + ": " + plot.owner;
							message = ChatColor.translateAlternateColorCodes('&', ncmessage);
						}
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

