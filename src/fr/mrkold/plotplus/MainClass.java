package fr.mrkold.plotplus;

import java.io.File;
import java.io.IOException;

import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.bukkit.api.BukkitLocation;
import com.worldcretornica.plotme_core.bukkit.api.BukkitPlayer;


public class MainClass extends JavaPlugin implements Listener {
	
	// --- Definition des variables ---
	
	PP2Functions functions;

	public String lang;
	public boolean metrics;
	public File myFile;
	private PluginDescriptionFile pdf = this.getDescription();
	public String version = pdf.getVersion();
	public String nomplugin = pdf.getName();
	private Boolean BarAPIOK;
	public Boolean PEXOK;
	public Boolean enableBar;
	private Boolean checkupdates;
	boolean notationenabled;
	public YamlConfiguration plots;
	
	public PP2Functions fonctions; 
	public UpdateChecker ucheck;
	
	private final PlotMeCoreManager plotManager = PlotMeCoreManager.getInstance();
	
	   
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
		
		metrics = getConfig().getBoolean("metrics");
		if(metrics){
			try {
		        Metrics metrics = new Metrics(this);
		        metrics.start();
		    } catch (IOException e) {
		        // Failed to submit the stats :-(
		    }
		}
		
		getCommand("plotplus").setExecutor(new PP2Commands(this));
		
		fonctions = new PP2Functions(this);
		
		getServer().getPluginManager().registerEvents(this, this);
		// Sauvegarde de la configuration par defaut
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
		// Recuperation de la langue
		lang = getConfig().getString("lang");
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
        
        ucheck = new UpdateChecker(this);
        
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
		
		enableBar = getConfig().getBoolean("enable-BarAPI");
		checkupdates = getConfig().getBoolean("check-for-updates");
		notationenabled = getConfig().getBoolean("rate-plots");
	}
	
	// ---------------------------------
	
	// Verif version � la connection d'un admin
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(checkupdates && p.hasPermission("plotplus.admin")){
			String udmsg = ucheck.checkVersion(pdf);
			if(!udmsg.equalsIgnoreCase("")){
				e.getPlayer().sendMessage(udmsg);;
			}
		}
	}
		
	// Fonction de reload
	public void ReloadPlugin(Player p) {
		reloadConfig();
		p.sendMessage(ChatColor.AQUA + "[PlotPlus] Configuration reloaded");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	// Lorsque le joueur bouge
	private void onMove(PlayerMoveEvent evt){
			Player p = evt.getPlayer();
			BukkitPlayer iplayer = new BukkitPlayer(p);
			// Chargement du fichier plots.yml
			File plotsFile = new File(this.getDataFolder(), "plots.yml");
	        FileConfiguration plots = YamlConfiguration.loadConfiguration(plotsFile);
	        //Location moveFrom = evt.getFrom();
			//Location moveTo = evt.getTo();
	        BukkitLocation moveFrom = new BukkitLocation(evt.getFrom());
	        BukkitLocation moveTo = new BukkitLocation(evt.getTo());
			String idTo = plotManager.getPlotId(moveTo);
	        String idFrom = plotManager.getPlotId(moveFrom);
	        Plot plot = plotManager.getPlotById(idTo, iplayer);
	        String world = p.getWorld().getName();
	        
	     // Si l'on est sur un plot et qu'il appartient � quelqu'un
	        if(fonctions.onPlot(p, idTo)){
	        	if(plot != null){
	        		String plotid = plot.getId();
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
	        	else{
	        		try{
	        			String plotid = plotManager.getPlotId(moveTo);
		        		if(plotid != ""){
		        			fonctions.clearPlotInfos(p, plotid);
		        		}
	        		}
	        		catch (NullPointerException e){
	        			
	        		}
	        	}
	        }
			
	     // On teste si l'on entre sur un plot
			if((!fonctions.onPlot(p, idFrom))&&(fonctions.onPlot(p, idTo))){
				// Si le plot appartient � quelqu'un
				if(plot != null){					
					// Si BarAPI est install� et activ�
					if(BarAPIOK){
						if (enableBar){
							String rank = fonctions.getRank(p, plot.getOwner()); // On r�cup�re le Pr�fix du joueur dans PermissionsEX
							String plotownerm = getConfig().getString("messages."+ lang +".plotowner");

							String ncmessage = plotownerm + " " + rank + plot.getOwner();	
							String message = ChatColor.translateAlternateColorCodes('&', ncmessage);
							BarAPI.setMessage(p, message);
						}
					}
				}
			}
			
			// Si l'on sort d'un plot on r�tablit les param�tres par d�faut
			else if ((fonctions.onPlot(p, idFrom))&&(!fonctions.onPlot(p, idTo))) {
				// Si BarAPI est install� et activ� on masque la barre
				if(enableBar){
					if(BarAPIOK && (BarAPI.hasBar(p))){
						BarAPI.removeBar(p);
					}
				}
				// r�initialisation des param�tres
				p.resetPlayerTime();
				p.resetPlayerWeather();
			}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	// Lorsque le joueur se TP
	private void onTP(PlayerTeleportEvent evt){
		Player p = evt.getPlayer();
		// Si BarAPI est install� et activ� on masque la barre
		if(enableBar){
			if(BarAPIOK && (BarAPI.hasBar(p))){
				BarAPI.removeBar(p);
			}
		}
		// r�initialisation des param�tres
		p.resetPlayerTime();
		p.resetPlayerWeather();
	}
}