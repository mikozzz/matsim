/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
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

package playground.michalm.drt.schedule;

import java.util.*;

import org.matsim.contrib.dvrp.path.VrpPathWithTravelData;
import org.matsim.contrib.dvrp.schedule.DriveTaskImpl;

import playground.michalm.drt.data.NDrtRequest;

/**
 * @author michalm
 */
public class NDrtDriveTask extends DriveTaskImpl implements NDrtTask {
	private final Set<NDrtRequest> onBoardRequests = new HashSet<>();// TODO really needed??

	public NDrtDriveTask(VrpPathWithTravelData path) {
		super(path);
	}

	@Override
	public NDrtTaskType getDrtTaskType() {
		return NDrtTaskType.DRIVE;
	}

	public Set<NDrtRequest> getOnBoardRequests() {
		return Collections.unmodifiableSet(onBoardRequests);
	}

	public void addOnBoardRequest(NDrtRequest request) {
		onBoardRequests.add(request);
	}

	public boolean isEmpty() {
		return onBoardRequests.isEmpty();
	}
}
