/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,     *
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

/**
 * 
 */
package playground.southafrica.population.freight;

import org.matsim.core.gbl.MatsimRandom;

import playground.southafrica.freight.digicore.containers.DigicoreChain;

/**
 * Class to get the start time of a {@link DigicoreChain} given a random number.
 * @author jwjoubert
 */

public class ChainStartTime {
//	private final static double[] CUMULATIVE_PROBABILITY_2009 = {0.0038, 0.0098, 0.0203, 0.0487, 0.1117, 0.2233,
//																 0.3921, 0.5813, 0.6877, 0.7524, 0.7932, 0.8229,
//																 0.8462, 0.8674, 0.8884, 0.9106, 0.9347, 0.9565,
//																 0.9704, 0.9803, 0.9873, 0.9922, 0.9967, 1.0000};
	private final static double[] CUMULATIVE_PROBABILITY_2009 = {0.0096, 0.0177, 0.0372, 0.0828, 0.2002, 0.4247, 
																 0.6557, 0.7565, 0.7985, 0.8228, 0.8404, 0.8565, 
																 0.8755, 0.8970, 0.9203, 0.9447, 0.9612, 0.9722, 
																 0.9793, 0.9843, 0.9894, 0.9952, 1.0000, 1.0000};
	
	/**
	 * Samples a (activity chain) start time from a preset cumulative 
	 * distribution that was generated by analysing all the activity chains from
	 * observed Digicore vehicles.
	 *  
	 * @param random a random value in the range [0; 1].
	 * @return a start time (in seconds) within the range [00:00; 23:59].
	 */
	public static double getStartTimeInSeconds(double random){
		if(random < 0.0 && random > 1.0){
			throw new IllegalArgumentException("Given random number must be in the range [0; 1].");
		}
		
		return getLatestStartTimeInSeconds(random);
	}
	
	
	private static double getLatestStartTimeInSeconds(double random){
		double[] latest = CUMULATIVE_PROBABILITY_2009;
		Double time = null;
		int index = 0;
		do {
			if(latest[index] >= random){
				time = index*3600 + MatsimRandom.getRandom().nextDouble()*3600;
			} else{
				index++;
			}
		} while (time == null);
		
		return time;
	}

}
