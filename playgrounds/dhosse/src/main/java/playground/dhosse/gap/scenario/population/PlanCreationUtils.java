package playground.dhosse.gap.scenario.population;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.utils.geometry.geotools.MGC;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import playground.dhosse.gap.Global;

public class PlanCreationUtils {

	public static Coord shoot(Geometry geometry){
		
		Point point = null;
		double x, y;
		
		do{
			
			x = geometry.getEnvelopeInternal().getMinX() + Global.random.nextDouble() * (geometry.getEnvelopeInternal().getMaxX() - geometry.getEnvelopeInternal().getMinX());
	  	    y = geometry.getEnvelopeInternal().getMinY() + Global.random.nextDouble() * (geometry.getEnvelopeInternal().getMaxY() - geometry.getEnvelopeInternal().getMinY());
	  	    point = MGC.xy2Point(x, y);
			
		}while(!geometry.contains(point));
		
		return MGC.point2Coord(point);
		
	}
	
	public static double getTravelDistanceForMode(String mode){
		
		double mean = 0.;
		
		if(mode.equals(TransportMode.car)){
			
			mean = 14560;
			
		} else if(mode.equals(TransportMode.ride)){
			
			mean = 19091;
			
		} else if(mode.equals(TransportMode.walk)){
			
			mean = 1276;
			
		} else if(mode.equals(TransportMode.bike)){
			
			mean = 3786;
			
		} else if(mode.equals(TransportMode.pt)){
			
			mean = 21515;
			
		} else {
			
		}
		
		double rnd = Global.random.nextDouble();
		return (- mean * Math.log(rnd));
		
	}
	
	public static Coord createNewRandomCoord(Coord c, double d){
		
		double x = Global.random.nextDouble() * d;
		double y = Math.sqrt(d * d - x * x);
		
		int signX = Global.random.nextDouble() <= 0.5 ? 1 : -1;
		int signY = Global.random.nextDouble() <= 0.5 ? 1 : -1;
		
		return new Coord(c.getX() + signX * x, c.getY() + signY * y);
		
	}
	
	public static double createRandomTimeShift(double variance){
		
		//draw two random numbers [0;1] from uniform distribution
		double r1 = Global.random.nextDouble();
		double r2 = Global.random.nextDouble();
		
		//Box-Muller-Method in order to get a normally distributed variable
		double normal = Math.cos(2 * Math.PI * r1) * Math.sqrt(-2 * Math.log(r2));
		double endTime = variance*3600 * normal;
		
		return endTime;
		
	}
	
	public static boolean setBooleanAttribute(String personId, double proba, String attribute){
		
		double random = Global.random.nextDouble();
		boolean attr = random <= proba ? true : false;
//		agentAttributes.putAttribute(personId, attribute, attr);
		
		return attr;
		
	}
	
}
