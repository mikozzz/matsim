/* *********************************************************************** *
 * project: org.matsim.*
 * DigicoreVehicleReader_v1.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package playground.southafrica.freight.digicore.io;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.core.utils.io.MatsimXmlParser;
import org.matsim.facilities.ActivityFacility;
import org.matsim.vehicles.Vehicle;
import org.xml.sax.Attributes;

import playground.southafrica.freight.digicore.containers.DigicoreActivity;
import playground.southafrica.freight.digicore.containers.DigicoreChain;
import playground.southafrica.freight.digicore.containers.DigicorePosition;
import playground.southafrica.freight.digicore.containers.DigicoreTrace;
import playground.southafrica.freight.digicore.containers.DigicoreVehicle;

public class DigicoreVehicleReader_v2 extends MatsimXmlParser {
	private final static String VEHICLE = "digicoreVehicle";
	private final static String CHAIN = "chain";
	private final static String ACTIVITY = "activity";
	private final static String TRACE = "trace";
	private final static String POSITION = "position";
	
	/* Attributes. */
	private final static String ATTR_ID = "id";
	private final static String ATTR_TIMEZONE = "timezone";
	private final static String ATTR_LOCALE = "locale";
	private final static String ATTR_X = "x";
	private final static String ATTR_Y = "y";
	private final static String ATTR_TYPE = "type";
	private final static String ATTR_STARTTIME = "start";
	private final static String ATTR_ENDTIME = "end";
	private final static String ATTR_ACTIVITYTYPE = "type";
	private final static String ATTR_FACILITY = "facility";
	private final static String ATTR_LINK = "link";
	private final static String ATTR_TRACE_CRS = "crs";
	private final static String ATTR_POS_TIME = "time";
	private final static String ATTR_POS_X = "x";
	private final static String ATTR_POS_Y = "y";
	
	private DigicoreVehicle vehicle;
	private DigicoreChain currentChain = null;
	private DigicoreActivity currentActivity = null;
	private DigicoreTrace currentTrace = null;
	private TimeZone timeZone;
	private Locale locale;
	
	public DigicoreVehicleReader_v2() {
	}
	
	
	public DigicoreVehicle getVehicle(){
		return this.vehicle;
	}
	
	
	@Override
	public void startTag(String name, Attributes atts, Stack<String> context) {
		if(VEHICLE.equals(name)){
			startVehicle(atts);
		} else if(CHAIN.equals(name)){
			currentChain = new DigicoreChain();
		} else if(ACTIVITY.equals(name)){
			startActivity(atts);
		} else if(TRACE.equals(name)){
			startTrace(atts);
		} else if(POSITION.equals(name)){
			startPosition(atts);
		} else {
			throw new RuntimeException(this + "[tag=" + name + " not known or not supported]");
		}
	}

	
	@Override
	public void endTag(String name, String content, Stack<String> context) {
		if(CHAIN.equals(name)){
			vehicle.getChains().add(currentChain);
			currentChain = null;
		} else if (ACTIVITY.equals(name)){
			currentChain.add(currentActivity);
			currentActivity = null;
		} else if(TRACE.equals(name)){
			currentChain.add(currentTrace);
			currentTrace = null;
		}
	}
	
	
	private void startVehicle(final Attributes atts){
		DigicoreVehicle dv = new DigicoreVehicle(Id.create(atts.getValue(ATTR_ID), Vehicle.class ));
		String type = atts.getValue(ATTR_TYPE);
		dv.setType(type);
		String tz = atts.getValue(ATTR_TIMEZONE);
		String l = atts.getValue(ATTR_LOCALE);
		if(tz != null){
			timeZone = TimeZone.getTimeZone(tz);
		}
		if(l != null){
			locale = new Locale(l);
		}
		this.vehicle = dv;
	}
	
	private void startActivity(final Attributes atts){
		String x = atts.getValue(ATTR_X);
		String y= atts.getValue(ATTR_Y);
		String startTime = atts.getValue(ATTR_STARTTIME);
		String endTime = atts.getValue(ATTR_ENDTIME);
		String type = atts.getValue(ATTR_ACTIVITYTYPE);
		String facility = atts.getValue(ATTR_FACILITY);
		String link = atts.getValue(ATTR_LINK);
		
		currentActivity = new DigicoreActivity(type, timeZone, locale);
		currentActivity.setCoord(new Coord(Double.parseDouble(x), Double.parseDouble(y)));
		currentActivity.setStartTime(parseDate(startTime));
		currentActivity.setEndTime(parseDate(endTime));
		currentActivity.setType(type);
		if(facility != null){
			currentActivity.setFacilityId(Id.create(facility, ActivityFacility.class));
		}
		if(link != null){
			currentActivity.setLinkId(Id.createLinkId(link));
		}		
	}
	
	private void startTrace(final Attributes atts){
		String crs = atts.getValue(ATTR_TRACE_CRS);
		currentTrace = new DigicoreTrace(crs);
	}
	
	private void startPosition(final Attributes atts){
		double time = parseDate(atts.getValue(ATTR_POS_TIME));
		double x = Double.parseDouble(atts.getValue(ATTR_POS_X));
		double y = Double.parseDouble(atts.getValue(ATTR_POS_Y));
		
		DigicorePosition pos = new DigicorePosition((long) time, x, y);
		currentTrace.add(pos);
	}
	
	
	private double parseDate(String string){
		GregorianCalendar g = new GregorianCalendar();
		g.setTimeZone(timeZone);
		
		int year = Integer.parseInt(string.substring(0,4));
		int month = Integer.parseInt(string.substring(4, 6)) - 1;
		int day = Integer.parseInt(string.substring(6, 8));
		int hour = Integer.parseInt(string.substring(9, 11));
		int min = Integer.parseInt(string.substring(12, 14));
		int sec = Integer.parseInt(string.substring(15, 17));
		
		g.set(year, month, day, hour, min, sec);
		
		return (g.getTimeInMillis() / 1000);		
	}
	
	
	@Override
	protected void setDoctype(final String doctype) {
		super.setDoctype(doctype);
		// Currently the only digicoreVehicle-type is v1
		if ("digicoreVehicle_v1.dtd".equals(doctype)) {
		} else {
			throw new IllegalArgumentException("Doctype \"" + doctype + "\" not known.");
		}
	}

}

