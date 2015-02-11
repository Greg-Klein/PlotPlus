package fr.mrkold.plotplus;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

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
	public static Boolean PEXOK;
	private boolean notationenabled = getConfig().getBoolean("rate-plots");
	private String a0;
	private String a1;
	private String a2;
	static FileConfiguration configfile;
	
    
    // ---------------------------------
    
	@Override
	// A la désactivation
	public void onDisable()
	{
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	// ---------------------------------
	
	@Override
	// A l'activation
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		// Sauvegarde de la configuration par defaut
		saveDefaultConfig();
		configfile = getConfig();
		configfile.options().copyDefaults(true);
		saveConfig();
		// Recuperation de la langue
		lang = configfile.getString("lang");

		getLogger().info(nomplugin + " v"+ version + " enabled");
		
		// Créer le fichier plots.yml s'il n'existe pas
        myFile = new File(getDataFolder(), "plots.yml");
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // Détection de BarAPI
        Plugin BarAPIPlugin = getServer().getPluginManager().getPlugin("BarAPI");
		if((BarAPIPlugin != null) && BarAPIPlugin.isEnabled()){
			BarAPIOK = true;
			getLogger().info("Plugin 'BarAPI' found.");
		}
		else{
			BarAPIOK = false;
		}
		
		// Détection de PermissionEX
		Plugin PEXPlugin = getServer().getPluginManager().getPlugin("PermissionsEx");
		if((PEXPlugin != null) && PEXPlugin.isEnabled()){
			PEXOK = true;
			getLogger().info("Plugin 'PermissionsEx' found.");
		}
		else{
			PEXOK = false;
		}
	}
	
	// ---------------------------------
	
	// Fonction de reload
	public void ReloadPlugin(Player p) {
		reloadConfig();
		p.sendMessage(AQUA + "[PlotPlus] Configuration reloaded");
	}

	
	@Override
	// Lorsqu'une commande est entrée
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		// On vérifie que la commande est /pp ou /plotplus
		if((label.equalsIgnoreCase("pp"))||(label.equalsIgnoreCase("plotplus"))) {
			// On vérifie que la commande est entrée par un joueur
		    if(sender instanceof Player) {
		    	Player p = (Player) sender;
		    	if(p.hasPermission("plotplus.use")){
		    		// Si la commande n'a pas d'argument on affiche l'aide
		    		if (args.length == 0) {
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
		    		// Si la commande a un argument
			    	else {
			    		a0 = args[0];
			    		// 2 arguments
			    		if (args.length == 2) {
			    			a1 = args[1];
			    		}
			    		// 3 arguments
			    		if (args.length == 3) {
			    			a1 = args[1];
			    			a2 = args[2];
			    		}
			            
			    		// Argument reload et permission ok
			            if ((a0.equalsIgnoreCase("reload")) && (p.hasPermission("plotplus.admin"))) {
			            	ReloadPlugin(p);
				    		return true;
						}
			            
			            // Détecte si l'on est bien sur un plotworld
			            if(PlotManager.getMap(p) == null) {
				            p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplotworld")));
				        } else {
				            String id = PlotManager.getPlotId(p);
				            String world = p.getWorld().getName();
				            
				         // id == "" : Ce n'est pas un plot
				            if(id.equals("")) {
				                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".noplot")));
				            } else {
				            	// récupérer les infos du plot
		    		            Plot plot = PlotManager.getPlotById(p);
		    		            String joueur = p.getName();
		    		            
		    		            // Si plot != null alors le plot appartient à quelqu'un
		    		            if(plot != null) {
		    		            	String plotid = plot.id;
		    		            	// Détecte si le plot appartient au joueur
		    		            	if ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin")){
		    		            		
		    		            		// Commande resettime
			    		            	if (a0.equalsIgnoreCase("resettime")) {	
			    		            		PPFunctions.resetTime(p, world, plotid);
											return true;
										}
			    		            	
			    		            	// Commande rain
			    		            	if (a0.equalsIgnoreCase("rain")) {											// Si l'argument est rain
			    		            		PPFunctions.setRain(p, world, plotid);
			    		            		return true;
										}
			    		            	
			    		            	// Commande resetweather
			    		            	if (a0.equalsIgnoreCase("resetweather")) {									// Si l'argument est resetweather
			    		            		PPFunctions.resetWeather(p, world, plotid);
											return true;
										}
			    		            	
			    		            	// Commande rate
			    		            	if ((a0.equalsIgnoreCase("rate")) && p.hasPermission("plotplus.rate.set")){	
			    		            		PPFunctions.ratePlot(p, world, plotid, a1, a2);
											return true;
										}
			    		            	
			    		            	// Commande unrate
			    		            	if ((a0.equalsIgnoreCase("unrate")) && p.hasPermission("plotplus.rate.set")){
			    		            		PPFunctions.unratePlot(p, world, plotid);
			    		            		return true;	
										}
			    		            	
			    		            	// Commande info
			    		            	if (a0.equalsIgnoreCase("info")){
			    		            		PPFunctions.plotInfo(p, world, plotid);
			    		            		return true;
										}
			    		            	
			    		            	// Si aucune des commandes plus haut on définit l'heure grace à l'argument passé
			    		            	else {
			    		            		PPFunctions.setHeure(p, world, plotid, a0);
										}
		    		            	}
		    		            	
		    		            	// Si le plot n'appartient pas au joueur
		    		            	else {
			    		                p.sendMessage(RED + (getConfig().getString("messages."+ lang +".notyourplot")));
			    		                return false;
			    		            }
		    		            }
		    		            
		    		            // Si le plot n'appartient à personne
		    		            else{
		    		            	p.sendMessage(RED + (getConfig().getString("messages."+ lang +".notowned")));
		    		            	return false;
		    		            }
				            }
				        }
			    	}
		    	}
		    	
		    	// Si le joueur n'a pas la permission
		    	else {
		    		p.sendMessage(RED + (getConfig().getString("messages."+ lang +".nopermission")));
		    	}
		    }
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	// Lorsque le joueur bouge
	private void onMove(PlayerMoveEvent evt){
			Player p = evt.getPlayer();
			// Chargement du fichier plots.yml
			File plotsFile = new File(this.getDataFolder(), "plots.yml");
	        FileConfiguration plots = YamlConfiguration.loadConfiguration(plotsFile);
	        String id = PlotManager.getPlotId(p.getLocation());
			
	     // On teste si l'on est sur un plot
			if(PPFunctions.onPlot(p, id)){
				Plot plot = PlotManager.getPlotById(p, id);
				String world = p.getWorld().getName();
				// Si le plot appartient à quelqu'un
				if(plot != null){
					String plotid = plot.id;
					// Récuperation des données dans le fichier de configuration plots.yml
					int heure = plots.getInt("plots." + world + "." + plotid + ".time");
					Boolean rain = plots.getBoolean("plots." + world + "." + plotid + ".rain");
					// On applique les paramètres
					if(heure != 0){
						p.setPlayerTime(heure, false);
					}
					if(rain){
						p.setPlayerWeather(WeatherType.DOWNFALL);
					}
					
					// Si BarAPI est installé
					if (BarAPIOK){
						String message;
						String ncmessage;
						String joueur = p.getName();
						String rank = PPFunctions.getRank(p, plot.owner); // On récupère le Préfix du joueur dans PermissionsEX
						double rawNote;
						String plotownerm = getConfig().getString("messages."+ lang +".plotowner");
						
						// Si la notation est activée
						if(notationenabled){
							// Si le joueur a la permission de voir la note des plots ou si le plot lui appartient
							if(p.hasPermission("plotplus.rate.view") || plot.owner.equalsIgnoreCase(joueur)){
								// On récupère les notes dans le fichier de configuration
								String ratem = getConfig().getString("messages."+ lang +".rated") + " ";
								String Sstyle = (plots.getString("plots." + world + "." + plotid + ".rate.style"));
								String Sdetails = (plots.getString("plots." + world + "." + plotid + ".rate.details"));
								String Spurpose = (plots.getString("plots." + world + "." + plotid + ".rate.purpose"));
								String Satmosphere = (plots.getString("plots." + world + "." + plotid + ".rate.atmosphere"));
								// Si le plot n'est pas noté
								if((Sstyle==null)||(Sdetails==null)||(Spurpose==null)||(Satmosphere==null)){
										ncmessage = plotownerm + " " + rank + plot.owner + "&f - " + getConfig().getString("messages."+ lang +".notrated");
								}
								// Si le plot a été noté dans les 4 domaines
								else{
									try {
										// Conversion des notes en double
										double style =  Double.parseDouble(Sstyle);
										double details =  Double.parseDouble(Sdetails);
										double purpose =  Double.parseDouble(Spurpose);
										double atmosphere =  Double.parseDouble(Satmosphere);
										// Calcul de la moyenne et formatage
										rawNote = (style + details + purpose + atmosphere)/4;
										DecimalFormat df = new DecimalFormat("########.##"); 
										String note = df.format(rawNote); 
										ncmessage = plotownerm + " " + rank + plot.owner + "&f - " + ratem + note + "/10";
									}
									// Si une des notes n'est pas un nombre on récupère l'erreur et on affiche le plot comme non noté
									catch (NumberFormatException nfe) {
										ncmessage = plotownerm + " " + rank + plot.owner + "&f - " + getConfig().getString("messages."+ lang +".notrated");
									}
								}
							}
							// Si le joueur n'a pas la permission de voir la note des plots ou si le plot ne lui appartient pas
							else{
								ncmessage = plotownerm + " " + rank + plot.owner;
							}
						}
						// Si la notation n'est pas activée
						else{
							ncmessage = plotownerm + " " + rank + plot.owner;	
						}
						message = ChatColor.translateAlternateColorCodes('&', ncmessage);
						BarAPI.setMessage(p, message);
					}
				}
				
				// Si le plot n'appartient à personne on efface les infos le concernant
				else{
					PPFunctions.clearPlotInfos(p);
				}
			}
			
			// Si l'on est pas sur un plot on rétablit les paramètres par défaut
			else
			{
				// Si BarAPI est installé on masque la barre
				if(BarAPIOK && (BarAPI.hasBar(p))){
					BarAPI.removeBar(p);
				}
				p.resetPlayerTime();
				p.resetPlayerWeather();
			}
	}
}

