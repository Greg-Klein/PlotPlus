package fr.mrkold.plotplus;

import java.io.IOException;

import fr.mrkold.plotplus.PlotPlusPlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PPFunctions {
	
	private final static ChatColor RED = ChatColor.RED;
	private final static ChatColor GREEN = ChatColor.GREEN;
	static YamlConfiguration plots = YamlConfiguration.loadConfiguration(PlotPlusPlugin.myFile);
	static FileConfiguration configfile = PlotPlusPlugin.configfile;
	static String lang = PlotPlusPlugin.lang;
	
	
	// If on plot
	public static boolean onPlot(Player p, String id) {
		if(!id.equalsIgnoreCase("")){
				return true;
		}
		return false;
	}
	
	// savePlotConfig
	public static void savePlotConfig() {
		try {
			plots.save(PlotPlusPlugin.myFile);
		} catch (IOException e) {
			// catch block
			e.printStackTrace();
		}
	}
	
	// resetTime
	public static void resetTime(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".time", null);
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".resettime")));
	}
	
	// setRain
	public static void setRain(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".rain", true);
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".setrain")));
	}
	
	// resetWeather
	public static void resetWeather(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".rain", null);	
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".resetweather")));
	}
	
	// ratePlot
	public static int ratePlot(Player p, String world, String plotid, String a1) {
		int note;
		try {
			note =  Integer.parseInt(a1);
		}
		catch (NumberFormatException nfe) {
			p.sendMessage(RED + (configfile.getString("messages."+ lang +".badargument")));
			return -1;
		}
		if ((note < 0)||(note > 20)){
			p.sendMessage((configfile.getString("messages."+ PlotPlusPlugin.lang +".badnotation")));
			return -1;
		}
		plots.set("plots." + world + "." + plotid + ".rate", note);						// On met la note dans le fichier plots.yml
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".noteset")) + ": " + note + "/20");
		return note;
	}
	
	//unratePlot
	public static void unratePlot(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".rate", null);
		savePlotConfig();
		p.sendMessage(GREEN + configfile.getString("messages."+ lang +".notereset"));
	}
	
	//setHeure
	public static boolean setHeure(Player p, String world, String plotid, String a0) {
		try {
			int setheure;
			setheure =  Integer.parseInt(a0);
			if ((setheure <= 0)||(setheure > 24000)){
				p.sendMessage(RED + (configfile.getString("messages."+ lang +".badtime")));
				return false;
			}
			plots.set("plots." + world + "." + plotid + ".time", setheure);
			savePlotConfig();
		    p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".settime")) + ": " + setheure + "ticks");
		    return true;
		}
		catch (NumberFormatException nfe) {
			p.sendMessage(RED + (configfile.getString("messages."+ lang +".badargument")));
			return false;
		}
	}
	
	//plotInfo
	public static void plotInfo(Player p, String world, String plotid) {
		String ratem = configfile.getString("messages."+ lang +".rated") + " ";
		String plotownerm = configfile.getString("messages."+ lang +".plotowner");
		String note = (plots.getString("plots." + world + "." + plotid + ".rate"));
		Plot plot = PlotManager.getPlotById(p);
		String owner = plot.owner;
		owner = plotownerm + " " + owner;
		if(note == null){
			note = configfile.getString("messages."+ lang +".notrated");
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
	}
	
	// clearPlot
	public static void clearPlotInfos(Player p) {
		String world = p.getWorld().getName();
		String plotid = PlotManager.getPlotId(p.getLocation());
		plots.set("plots." + world + "." + plotid, null);
		savePlotConfig();
	}

}
