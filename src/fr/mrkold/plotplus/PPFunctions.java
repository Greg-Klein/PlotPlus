package fr.mrkold.plotplus;

import java.io.IOException;

import fr.mrkold.plotplus.PlotPlusPlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PPFunctions {
	
	private final static ChatColor RED = ChatColor.RED;
	private final static ChatColor GREEN = ChatColor.GREEN;
	private final static ChatColor AQUA = ChatColor.AQUA;
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
	
	// save Plot Config
	public static void savePlotConfig() {
		try {
			plots.save(PlotPlusPlugin.myFile);
		} catch (IOException e) {
			// catch block
			e.printStackTrace();
		}
	}
	
	// reset Time
	public static void resetTime(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".time", null);
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".resettime")));
	}
	
	// set Rain
	public static void setRain(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".rain", true);
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".setrain")));
	}
	
	// reset Weather
	public static void resetWeather(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".rain", null);	
		savePlotConfig();
		p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".resetweather")));
	}
	
	// rate Plot
	public static boolean ratePlot(Player p, String world, String plotid, String a1, String a2) {
		// Style
		if(a1.equalsIgnoreCase("style")){
			if(a2 == null){
				p.sendMessage((configfile.getString("messages."+ PlotPlusPlugin.lang +".badnotation")));
				return false;
			}
			else{
				int style = testNote(p, a2);
				plots.set("plots." + world + "." + plotid + ".rate.style", style);
				savePlotConfig();
				p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".noteset")) + " style: " + style + "/10");
				return true;
			}
		}
		// Atmosphere
		if(a1.equalsIgnoreCase("atmosphere")){
			if(a2 == null){
				p.sendMessage((configfile.getString("messages."+ PlotPlusPlugin.lang +".badnotation")));
				return false;
			}
			else{
				int atmosphere = testNote(p, a2);
				plots.set("plots." + world + "." + plotid + ".rate.atmosphere", atmosphere);
				savePlotConfig();
				p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".noteset")) + " atmosphere: " + atmosphere + "/10");
				return true;
			}
		}
		// Detail
		if(a1.equalsIgnoreCase("details")){
			if(a2 == null){
				p.sendMessage((configfile.getString("messages."+ PlotPlusPlugin.lang +".badnotation")));
				return false;
			}
			else{
				int details = testNote(p, a2);
				plots.set("plots." + world + "." + plotid + ".rate.details", details);
				savePlotConfig();
				p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".noteset")) + " details: " + details + "/10");
				return true;
			}
		}
		// Purpose
		if(a1.equalsIgnoreCase("purpose")){
			if(a2 == null){
				p.sendMessage((configfile.getString("messages."+ PlotPlusPlugin.lang +".badnotation")));
				return false;
			}
			else{
				int purpose = testNote(p, a2);
				plots.set("plots." + world + "." + plotid + ".rate.purpose", purpose);
				savePlotConfig();
				p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".noteset")) + " purpose: " + purpose + "/10");
				return true;
			}
		}
		else{
			p.sendMessage(GREEN + (configfile.getString("messages."+ lang +".rateusage")));
			return false;
		}
		
	}
	
	// unrate Plot
	public static void unratePlot(Player p, String world, String plotid) {
		plots.set("plots." + world + "." + plotid + ".rate", null);
		savePlotConfig();
		p.sendMessage(GREEN + configfile.getString("messages."+ lang +".notereset"));
	}
	
	// set Heure
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
	
	// plot Info
	public static void plotInfo(Player p, String world, String plotid) {
		String plotownerm = configfile.getString("messages."+ lang +".plotowner");
		Plot plot = PlotManager.getPlotById(p);
		String owner = plot.owner;
		String note;
		owner = plotownerm + " " + AQUA + owner;

		String Sstyle = (plots.getString("plots." + world + "." + plotid + ".rate.style"));
		String Sdetails = (plots.getString("plots." + world + "." + plotid + ".rate.details"));
		String Spurpose = (plots.getString("plots." + world + "." + plotid + ".rate.purpose"));
		String Satmosphere = (plots.getString("plots." + world + "." + plotid + ".rate.atmosphere"));
		try {
			int style =  Integer.parseInt(Sstyle);
			int details =  Integer.parseInt(Sdetails);
			int purpose =  Integer.parseInt(Spurpose);
			int atmosphere =  Integer.parseInt(Satmosphere);
			String mstyle = GREEN+"Style: "+AQUA+style+"/10";
			String mdetails = GREEN+"Details: "+AQUA+details+"/10";
			String mpurpose = GREEN+"Purpose: "+AQUA+purpose+"/10";
			String matmosphere = GREEN+"Atmosphere: "+AQUA+atmosphere+"/10";
			note = mstyle + "\n" + mdetails + "\n" + mpurpose + "\n" + matmosphere;
		}
		catch (NumberFormatException nfe) {
				note = configfile.getString("messages."+ lang +".notrated");
		}
		owner = ChatColor.translateAlternateColorCodes('&', owner);
		note = ChatColor.translateAlternateColorCodes('&', note);
		p.sendMessage(GREEN + "------------------------------");
		p.sendMessage(GREEN + owner);
		p.sendMessage(GREEN + note);
		p.sendMessage(GREEN + "------------------------------");
	}
	
	// clear Plot Infos
	public static void clearPlotInfos(Player p) {
		String world = p.getWorld().getName();
		String plotid = PlotManager.getPlotId(p.getLocation());
		plots.set("plots." + world + "." + plotid, null);
		savePlotConfig();
	}
	
	// Test if note is between 0 and 10
	private static int testNote(Player p, String a2){
		int note;
		try {
			note =  Integer.parseInt(a2);
		}
		catch (NumberFormatException nfe) {
			p.sendMessage(RED + (configfile.getString("messages."+ lang +".badargument")));
			return 0;
		}
		if ((note < 0)||(note > 10)){
			p.sendMessage((configfile.getString("messages."+ PlotPlusPlugin.lang +".badnotation")));
			return 0;
		}
		return note;
	}
	
	// get Rank
		public static String getRank(Player p, String owner) {
			String rank;
			if(PlotPlusPlugin.PEXOK){
				try{
					rank = PermissionsEx.getUser(owner).getPrefix();
					if(rank == ""){
						rank = "";
					}
				}
				catch (NullPointerException e){
					rank = "";
				}
			}
			else{
				rank = "";
			}
			return rank;
		}

}
