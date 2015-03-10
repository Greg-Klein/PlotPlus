package fr.mrkold.plotplus;

import java.io.IOException;

import fr.mrkold.plotplus.PlotPlusPlugin;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PPFunctions {
	
	private PlotPlusPlugin plugin;
	public PPFunctions(PlotPlusPlugin plugin){
		this.plugin = plugin;
	}
	
	private final static ChatColor RED = ChatColor.RED;
	private final static ChatColor GREEN = ChatColor.GREEN;
	private final static ChatColor AQUA = ChatColor.AQUA;
	
	// If on plot
	public static boolean onPlot(Player p, String id) {
		if(!id.equalsIgnoreCase("")){
				return true;
		}
		return false;
	}
	
	// save Plot Config
	public void savePlotConfig() {
		try {
			plugin.plots.save(plugin.myFile);
		} catch (IOException e) {
			// catch block
			e.printStackTrace();
		}
	}
	
	// reset Time
	public void resetTime(Player p, String world, String plotid) {
		String weather = (plugin.plots.getString("plots." + world + "." + plotid + ".rain"));
		plugin.plots.set("plots." + world + "." + plotid + ".time", null);
		if(weather == null){
			plugin.plots.set("plots." + world + "." + plotid, null);
		}
		savePlotConfig();
		p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".resettime")));
	}
	
	// set Rain
	public void setRain(Player p, String world, String plotid) {
		plugin.plots.set("plots." + world + "." + plotid + ".rain", true);
		savePlotConfig();
		p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".setrain")));
	}
	
	// reset Weather
	public void resetWeather(Player p, String world, String plotid) {
		String time = (plugin.plots.getString("plots." + world + "." + plotid + ".time"));
		plugin.plots.set("plots." + world + "." + plotid + ".rain", null);
		if(time == null){
			plugin.plots.set("plots." + world + "." + plotid, null);
		}
		savePlotConfig();
		p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".resetweather")));
	}
	
	// rate Plot
	public boolean ratePlot(Player p, String world, String plotid, String a1, String a2) {
		// Style
		if(a1.equalsIgnoreCase("style")){
			if(a2 == null){
				p.sendMessage((plugin.configfile.getString("messages."+ plugin.lang +".badnotation")));
				return false;
			}
			else{
				int style = testNote(p, a2);
				plugin.plots.set("plots." + world + "." + plotid + ".rate.style", style);
				savePlotConfig();
				p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".noteset")) + " style: " + style + "/10");
				return true;
			}
		}
		// Atmosphere
		if(a1.equalsIgnoreCase("atmosphere")){
			if(a2 == null){
				p.sendMessage((plugin.configfile.getString("messages."+ plugin.lang +".badnotation")));
				return false;
			}
			else{
				int atmosphere = testNote(p, a2);
				plugin.plots.set("plots." + world + "." + plotid + ".rate.atmosphere", atmosphere);
				savePlotConfig();
				p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".noteset")) + " atmosphere: " + atmosphere + "/10");
				return true;
			}
		}
		// Detail
		if(a1.equalsIgnoreCase("details")){
			if(a2 == null){
				p.sendMessage((plugin.configfile.getString("messages."+ plugin.lang +".badnotation")));
				return false;
			}
			else{
				int details = testNote(p, a2);
				plugin.plots.set("plots." + world + "." + plotid + ".rate.details", details);
				savePlotConfig();
				p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".noteset")) + " details: " + details + "/10");
				return true;
			}
		}
		// Purpose
		if(a1.equalsIgnoreCase("purpose")){
			if(a2 == null){
				p.sendMessage((plugin.configfile.getString("messages."+ plugin.lang +".badnotation")));
				return false;
			}
			else{
				int purpose = testNote(p, a2);
				plugin.plots.set("plots." + world + "." + plotid + ".rate.purpose", purpose);
				savePlotConfig();
				p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".noteset")) + " purpose: " + purpose + "/10");
				return true;
			}
		}
		else{
			p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".rateusage")));
			return false;
		}
		
	}
	
	// unrate Plot
	public void unratePlot(Player p, String world, String plotid) {
		plugin.plots.set("plots." + world + "." + plotid + ".rate", null);
		savePlotConfig();
		p.sendMessage(GREEN + plugin.configfile.getString("messages."+ plugin.lang +".notereset"));
	}
	
	// set Heure
	public boolean setHeure(Player p, String world, String plotid, String a0) {
		try {
			int setheure;
			setheure =  Integer.parseInt(a0);
			if ((setheure <= 0)||(setheure > 24000)){
				p.sendMessage(RED + (plugin.configfile.getString("messages."+ plugin.lang +".badtime")));
				return false;
			}
			plugin.plots.set("plots." + world + "." + plotid + ".time", setheure);
			savePlotConfig();
		    p.sendMessage(GREEN + (plugin.configfile.getString("messages."+ plugin.lang +".settime")) + ": " + setheure + "ticks");
		    return true;
		}
		catch (NumberFormatException nfe) {
			p.sendMessage(RED + (plugin.configfile.getString("messages."+ plugin.lang +".badargument")));
			return false;
		}
	}
	
	// plot Info
	public void plotInfo(Player p, String world, String plotid) {
		String plotownerm = plugin.configfile.getString("messages."+ plugin.lang +".plotowner");
		Plot plot = PlotManager.getPlotById(p);
		String owner = plot.owner;
		String note;
		owner = plotownerm + " " + AQUA + owner;

		String Sstyle = (plugin.plots.getString("plots." + world + "." + plotid + ".rate.style"));
		String Sdetails = (plugin.plots.getString("plots." + world + "." + plotid + ".rate.details"));
		String Spurpose = (plugin.plots.getString("plots." + world + "." + plotid + ".rate.purpose"));
		String Satmosphere = (plugin.plots.getString("plots." + world + "." + plotid + ".rate.atmosphere"));
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
				note = plugin.configfile.getString("messages."+ plugin.lang +".notrated");
		}
		owner = ChatColor.translateAlternateColorCodes('&', owner);
		note = ChatColor.translateAlternateColorCodes('&', note);
		p.sendMessage(GREEN + "------------------------------");
		p.sendMessage(GREEN + owner);
		p.sendMessage(GREEN + note);
		p.sendMessage(GREEN + "------------------------------");
	}
	
	// clear Plot Infos
	public void clearPlotInfos(Player p, String plotid) {
		String world = p.getWorld().getName();
		p.sendMessage("test: " + world + " " + plotid);
		plugin.plots.set("plots." + world + "." + plotid, null);
		savePlotConfig();
	}
	
	// Test if note is between 0 and 10
	private int testNote(Player p, String a2){
		int note;
		try {
			note =  Integer.parseInt(a2);
		}
		catch (NumberFormatException nfe) {
			p.sendMessage(RED + (plugin.configfile.getString("messages."+ plugin.lang +".badargument")));
			return 0;
		}
		if ((note < 0)||(note > 10)){
			p.sendMessage((plugin.configfile.getString("messages."+ plugin.lang +".badnotation")));
			return 0;
		}
		return note;
	}
	
	// get Rank
		public String getRank(Player p, String owner) {
			String rank = "";
			if(plugin.PEXOK){
				try{
					rank = PermissionsEx.getUser(owner).getPrefix();
					if(rank != ""){
						return rank;
					}
				} catch (NullArgumentException e){
					
				}
			}
			return rank;
		}

}
