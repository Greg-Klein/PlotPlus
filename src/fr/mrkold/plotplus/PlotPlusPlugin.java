package fr.mrkold.plotplus;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import me.confuser.barapi.BarAPI;
import fr.mrkold.plotplus.PPFunctions;
import fr.mrkold.plotplus.UpdateChecker;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PlotPlusPlugin extends JavaPlugin implements Listener {
	
	// --- Definition des variables ---
	
	PPFunctions functions;
	
	public final ChatColor RED = ChatColor.RED;
	public final ChatColor GREEN = ChatColor.GREEN;
	public final ChatColor AQUA = ChatColor.AQUA;
	public String lang;
    public File myFile;
    private PluginDescriptionFile pdf = this.getDescription();						// r�cup�rer les infos de plugin.yml
    public String version = pdf.getVersion();
	public String nomplugin = pdf.getName();
	private Boolean BarAPIOK;
	public Boolean PEXOK;
	boolean notationenabled = getConfig().getBoolean("rate-plots");
	private boolean viewrating = getConfig().getBoolean("view-rating");
	private boolean checkupdates = getConfig().getBoolean("check-for-updates");
	public FileConfiguration configfile;
	public YamlConfiguration plots;
	
    
    // ---------------------------------
    
	@Override
	// A la d�sactivation
	public void onDisable()
	{
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	// ---------------------------------
	
	@Override
	// A l'activation
	public void onEnable() {
		getCommand("plotplus").setExecutor(new PPCommands(this));
		
		getServer().getPluginManager().registerEvents(this, this);
		// Sauvegarde de la configuration par defaut
		saveDefaultConfig();
		configfile = getConfig();
		configfile.options().copyDefaults(true);
		saveConfig();
		// Recuperation de la langue
		lang = configfile.getString("lang");

		getLogger().info(nomplugin + " v"+ version + " enabled");
		
		// Cr�er le fichier plots.yml s'il n'existe pas
        myFile = new File(getDataFolder(), "plots.yml");
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        plots = YamlConfiguration.loadConfiguration(myFile);
        
        functions = new PPFunctions(this);
        
        // D�tection de BarAPI
        Plugin BarAPIPlugin = getServer().getPluginManager().getPlugin("BarAPI");
		if((BarAPIPlugin != null) && BarAPIPlugin.isEnabled()){
			BarAPIOK = true;
			getLogger().info("Plugin 'BarAPI' found.");
		}
		else{
			BarAPIOK = false;
		}
		
		// D�tection de PermissionEX
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
	
	// A la connection
		@EventHandler
		public void onJoin(PlayerJoinEvent e){
			Player p = e.getPlayer();
			if(checkupdates && p.hasPermission("plotplus.admin")){
				String udmsg = UpdateChecker.checkVersion(pdf);
				if(!udmsg.equalsIgnoreCase("")){
					e.getPlayer().sendMessage(udmsg);;
				}
			}
		}
	
	// Fonction de reload
	public void ReloadPlugin(Player p) {
		reloadConfig();
		p.sendMessage(AQUA + "[PlotPlus] Configuration reloaded");
	}

	
	//
	// Lorsqu'une commande est entr�e
	//
	
	@EventHandler(priority = EventPriority.HIGH)
	// Lorsque le joueur bouge
	private void onMove(PlayerMoveEvent evt){
			Player p = evt.getPlayer();
			// Chargement du fichier plots.yml
			File plotsFile = new File(this.getDataFolder(), "plots.yml");
	        FileConfiguration plots = YamlConfiguration.loadConfiguration(plotsFile);
	        Location moveFrom = evt.getFrom();
			Location moveTo = evt.getTo();
			String idTo = PlotManager.getPlotId(moveTo);
	        String idFrom = PlotManager.getPlotId(moveFrom);
	        Plot plot = PlotManager.getPlotById(p, idTo);
	        String world = p.getWorld().getName();
	        
	     // Si l'on est sur un plot et qu'il appartient � quelqu'un
	        if((PPFunctions.onPlot(p, idTo))&&(plot != null)){
	        	String plotid = plot.id;
	        	// R�cuperation des donn�es dans le fichier de configuration plots.yml
				int heure = plots.getInt("plots." + world + "." + plotid + ".time");
				Boolean rain = plots.getBoolean("plots." + world + "." + plotid + ".rain");
				// On applique les param�tres
				if(heure != 0){
					p.setPlayerTime(heure, false);
				}
				else {
					p.resetPlayerTime();
				}
				
				if(rain){
					p.setPlayerWeather(WeatherType.DOWNFALL);
				}
				else {
					p.resetPlayerWeather();
				}
	        }
			
	     // On teste si l'on entre sur un plot
			if((!PPFunctions.onPlot(p, idFrom))&&(PPFunctions.onPlot(p, idTo))){
				// Si le plot appartient � quelqu'un
				if(plot != null){
					String plotid = plot.id;
					
					// Si BarAPI est install�
					if (BarAPIOK){
						String message;
						String ncmessage;
						String joueur = p.getName();
						String rank = functions.getRank(p, plot.owner); // On r�cup�re le Pr�fix du joueur dans PermissionsEX
						double rawNote;
						String plotownerm = getConfig().getString("messages."+ lang +".plotowner");
						
						// Si la vue de la notation est activ�e
						if(notationenabled){
							// Si le joueur a la permission de voir la note des plots ou si le plot lui appartient
							if(p.hasPermission("plotplus.rate.view") || (plot.owner.equalsIgnoreCase(joueur) && viewrating)){
								// On r�cup�re les notes dans le fichier de configuration
								String ratem = getConfig().getString("messages."+ lang +".rated") + " ";
								String Sstyle = (plots.getString("plots." + world + "." + plotid + ".rate.style"));
								String Sdetails = (plots.getString("plots." + world + "." + plotid + ".rate.details"));
								String Spurpose = (plots.getString("plots." + world + "." + plotid + ".rate.purpose"));
								String Satmosphere = (plots.getString("plots." + world + "." + plotid + ".rate.atmosphere"));
								// Si le plot n'est pas not�
								if((Sstyle==null)||(Sdetails==null)||(Spurpose==null)||(Satmosphere==null)){
										ncmessage = plotownerm + " " + rank + plot.owner + "&f - " + getConfig().getString("messages."+ lang +".notrated");
								}
								// Si le plot a �t� not� dans les 4 domaines
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
									// Si une des notes n'est pas un nombre on r�cup�re l'erreur et on affiche le plot comme non not�
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
						// Si la notation n'est pas activ�e
						else{
							ncmessage = plotownerm + " " + rank + plot.owner;	
						}
						message = ChatColor.translateAlternateColorCodes('&', ncmessage);
						BarAPI.setMessage(p, message);
					}
				}
			}
			
			// Si l'on sort d'un plot on r�tablit les param�tres par d�faut
			else if ((PPFunctions.onPlot(p, idFrom))&&(!PPFunctions.onPlot(p, idTo))) {
				// Si BarAPI est install� on masque la barre
				if(BarAPIOK && (BarAPI.hasBar(p))){
					BarAPI.removeBar(p);
				}
				p.resetPlayerTime();
				p.resetPlayerWeather();
			}
	}
}

