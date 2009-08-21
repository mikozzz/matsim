/* *********************************************************************** *
 * project: org.matsim.*
 * SignalSystemsConsistencyChecker
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2009 by the members listed in the COPYING,        *
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
package playground.dgrether.signalsystems;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.matsim.api.basic.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.network.NetworkLayer;
import org.matsim.lanes.MatsimLaneDefinitionsReader;
import org.matsim.lanes.basic.BasicLaneDefinitions;
import org.matsim.lanes.basic.BasicLaneDefinitionsImpl;
import org.matsim.lanes.basic.BasicLanesToLinkAssignment;
import org.matsim.signalsystems.MatsimSignalSystemsReader;
import org.matsim.signalsystems.basic.BasicSignalGroupDefinition;
import org.matsim.signalsystems.basic.BasicSignalSystems;
import org.matsim.signalsystems.basic.BasicSignalSystemsImpl;

import playground.dgrether.DgPaths;
import playground.dgrether.consistency.ConsistencyChecker;


/**
 * @author dgrether
 *
 */
public class SignalSystemsConsistencyChecker implements ConsistencyChecker {
	
	private static final Logger log = Logger.getLogger(SignalSystemsConsistencyChecker.class);
	
	private Network network;
	private BasicLaneDefinitions lanes;
	private boolean removeMalformed = false;

	private BasicSignalSystems signals;
	
	public SignalSystemsConsistencyChecker(Network net, BasicLaneDefinitions laneDefs, BasicSignalSystems signals) {
		this.network = net;
		this.lanes = laneDefs;
		this.signals = signals;
	}
	
	public void checkConsistency() {
		log.info("checking consistency...");
		Set<BasicSignalGroupDefinition> malformedGroups = new HashSet<BasicSignalGroupDefinition>();
		//check link
		for (BasicSignalGroupDefinition sg : this.signals.getSignalGroupDefinitions().values()){
			if (!this.network.getLinks().containsKey(sg.getLinkRefId())){
				log.error("No link in network with id " + sg.getLinkRefId() + " for attached signalGroup id " + sg.getId());
				malformedGroups.add(sg);
			}
			// check toLinks
			for (Id id : sg.getToLinkIds()){
				if (!this.network.getLinks().containsKey(id)){
					log.error("No link in network with id " + id + " set as toLink of signalGroup id " + sg.getId());
					malformedGroups.add(sg);
				}
			}
			//check signalsystem
			if (!this.signals.getSignalSystemDefinitions().containsKey(sg.getSignalSystemDefinitionId())){
				log.error("No signalSystemDefinition with Id " + sg.getSignalSystemDefinitionId() + " set as system of signalGroup id " + sg.getId());
				malformedGroups.add(sg);
			}
			//check lanes
			if (!this.lanes.getLanesToLinkAssignments().containsKey(sg.getLinkRefId())){
				log.error("No LanesToLinkAssignment found in lane definitions for referenced link id " + sg.getLinkRefId() + " set as Link of signalGroup id " + sg.getId());
				malformedGroups.add(sg);
			} 
			else {
				BasicLanesToLinkAssignment l2l = this.lanes.getLanesToLinkAssignments().get(sg.getLinkRefId());
				for (Id id : sg.getLaneIds()){
					if (!l2l.getLanes().containsKey(id)){
						log.error("No lane defined in laneDefinitions with id " + id + " set in signalGroup id " + sg.getId());
						malformedGroups.add(sg);
					}

				}
			}
		}
		
		if (this.removeMalformed){
			for (BasicSignalGroupDefinition id : malformedGroups) {
				this.lanes.getLanesToLinkAssignments().remove(id);
			}
		}
		log.info("checked consistency.");
	}
	
	public boolean isRemoveMalformed() {
		return removeMalformed;
	}

	
	public void setRemoveMalformed(boolean removeMalformed) {
		this.removeMalformed = removeMalformed;
	}

	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String netFile = DgPaths.IVTCHBASE + "baseCase/network/ivtch-osm.xml";
		String lanesFile = DgPaths.STUDIESDG + "signalSystemsZh/laneDefinitions.xml";
		String signalSystemsFile = DgPaths.STUDIESDG + "signalSystemsZh/signalSystems.xml";
		NetworkLayer net = new NetworkLayer();
		MatsimNetworkReader netReader = new MatsimNetworkReader(net);
		netReader.readFile(netFile);
	  log.info("read network");
	  
	  
	  BasicLaneDefinitions laneDefs = new BasicLaneDefinitionsImpl();
		MatsimLaneDefinitionsReader laneReader = new MatsimLaneDefinitionsReader(laneDefs );
	  laneReader.readFile(lanesFile);
	  
	  BasicSignalSystems signals = new BasicSignalSystemsImpl();
	  MatsimSignalSystemsReader signalReader = new MatsimSignalSystemsReader(signals);
	  signalReader.readFile(signalSystemsFile);
	  
	  SignalSystemsConsistencyChecker sscc = new SignalSystemsConsistencyChecker((Network)net, laneDefs, signals);
		sscc.setRemoveMalformed(true);
		sscc.checkConsistency();

		
		
//		MatsimLaneDefinitionsWriter laneWriter = new MatsimLaneDefinitionsWriter(laneDefs);
//		laneWriter.writeFile(lanesFile);
	}

}
