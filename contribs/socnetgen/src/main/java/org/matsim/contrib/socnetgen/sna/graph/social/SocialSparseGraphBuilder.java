/* *********************************************************************** *
 * project: org.matsim.*
 * SampledSocialNetFactory.java
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
package org.matsim.contrib.socnetgen.sna.graph.social;

import com.vividsolutions.jts.geom.Point;
import org.matsim.contrib.socnetgen.sna.graph.AbstractSparseGraphBuilder;
import org.matsim.contrib.socnetgen.sna.graph.GraphFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * @author illenberger
 *
 */
public class SocialSparseGraphBuilder extends AbstractSparseGraphBuilder<SocialSparseGraph, SocialSparseVertex, SocialSparseEdge> {

	/**
	 * @param factory
	 */
	public SocialSparseGraphBuilder(GraphFactory<SocialSparseGraph, SocialSparseVertex, SocialSparseEdge> factory) {
		super(factory);
	}

	public SocialSparseGraphBuilder(CoordinateReferenceSystem crs) {
		super(new SocialSparseGraphFactory(crs));
	}
	
	public SocialSparseGraph createGraph() {
		return ((SocialSparseGraphFactory)getFactory()).createGraph();
	}

	@Override
	public SocialSparseVertex addVertex(SocialSparseGraph g) {
		SocialSparseVertex vertex = ((SocialSparseGraphFactory)getFactory()).createVertex();
		if(insertVertex(g, vertex))
			return vertex;
		else
			return null;
	}
	
	public SocialSparseVertex addVertex(SocialSparseGraph graph, SocialPerson person, Point point) {
		SocialSparseVertex vertex = ((SocialSparseGraphFactory)getFactory()).createVertex(person, point);
		if(insertVertex(graph, vertex))
			return vertex;
		else
			return null;
	}

}
