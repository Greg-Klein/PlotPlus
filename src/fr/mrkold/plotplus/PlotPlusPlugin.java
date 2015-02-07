package fr.mrkold.plotplus;

import java.io.File;
import java.io.IOException;

import me.confuser.barapi.BarAPI;
import fr.mrkold.plotplus.PPFunctions;

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
	
	public final ChatColor RED = ChatColor.RED;
	public final ChatColor GREEN = ChatColor.GREEN;
	public final ChatColor AQUA = ChatColor.AQUA;
	static String lang;
    static File myFile;
    private PluginDescriptionFile pdf = this.getDescription();						// récupérer les infos de plugin.yml
    private String version = pdf.getVersion();
	private String nomplugin = pdf.getName();
	private Boolean BarAPIOK;
	private boolean notationenabled = getConfig().getBoolean("rate-plots");
	private String a0;
	private String a1;
	private String a2;
	static FileConfiguration configfile;
	
    
    // ---------------------------------
    
	@Override
	public void onDisable() 														// A la désactivation
	{
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	// ---------------------------------
	
	@Override
	public void onEnable() {														// A l'activation
		saveDefaultConfig();
		configfile = getConfig();
		configfile.options().copyDefaults(true);
		saveConfig();
		lang = configfile.getString("lang");
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
		reloadConfig();
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
			    		if (args.length == 3) {
			    			a1 = args[1];
			    			a2 = args[2];
			    		}
			            
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
			    		            	if (a0.equalsIgnoreCase("resettime")) {	
			    		            		PPFunctions.resetTime(p, world, plotid);
											return true;
										}
			    		            	if (a0.equalsIgnoreCase("rain")) {											// Si l'argument est rain
			    		            		PPFunctions.setRain(p, world, plotid);
			    		            		return true;
										}
			    		            	if (a0.equalsIgnoreCase("resetweather")) {									// Si l'argument est resetweather
			    		            		PPFunctions.resetWeather(p, world, plotid);
											return true;
										}
			    		            	if ((a0.equalsIgnoreCase("rate")) && p.hasPermission("plotplus.rate.set")){	
			    		            		p.sendMessage("a1: " + a1);
			    		            		p.sendMessage("a2: " + a2);
			    		            		PPFunctions.ratePlot(p, world, plotid, a1, a2);
											return true;
										}
			    		            	if ((a0.equalsIgnoreCase("unrate")) && p.hasPermission("plotplus.rate.set")){
			    		            		PPFunctions.unratePlot(p, world, plotid);
			    		            		return true;	
										}
			    		            	if (a0.equalsIgnoreCase("info")){
			    		            		PPFunctions.plotInfo(p, world, plotid);
			    		            		return true;
										}
			    		            	else {
			    		            		PPFunctions.setHeure(p, world, plotid, a0);
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
			
			if(PPFunctions.onPlot(p, id)){															// On teste si l'on est sur un plot
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
						String ncmessage;
						String joueur = p.getName();
						int note;
						String plotownerm = getConfig().getString("messages."+ lang +".plotowner");
						if(notationenabled){
							if(p.hasPermission("plotplus.rate.view") || plot.owner.equalsIgnoreCase(joueur)){
								String ratem = getConfig().getString("messages."+ lang +".rated") + " ";
								String Sstyle = (plots.getString("plots." + world + "." + plotid + ".rate.style"));
								String Sdetails = (plots.getString("plots." + world + "." + plotid + ".rate.details"));
								String Spurpose = (plots.getString("plots." + world + "." + plotid + ".rate.purpose"));
								String Satmosphere = (plots.getString("plots." + world + "." + plotid + ".rate.atmosphere"));
								if((Sstyle==null)||(Sdetails==null)||(Spurpose==null)||(Satmosphere==null)){
										ncmessage = plotownerm + " " + plot.owner + "&f - " + getConfig().getString("messages."+ lang +".notrated");
								}
								else{
									try {
										int style =  Integer.parseInt(Sstyle);
										int details =  Integer.parseInt(Sdetails);
										int purpose =  Integer.parseInt(Spurpose);
										int atmosphere =  Integer.parseInt(Satmosphere);
										note = style + details + purpose + atmosphere;
										ncmessage = plotownerm + " " + plot.owner + "&f - " + ratem + note + "/40";
									}
									catch (NumberFormatException nfe) {
										ncmessage = plotownerm + " " + plot.owner + "&f - " + getConfig().getString("messages."+ lang +".notrated");
									}
								}
							}
							else{
								ncmessage = plotownerm + " " + plot.owner;
							}
						}
						else{
							ncmessage = plotownerm + " " + plot.owner;	
						}
						message = ChatColor.translateAlternateColorCodes('&', ncmessage);
						BarAPI.setMessage(p, message);
					}
				}
				else{
					PPFunctions.clearPlotInfos(p);
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
}

