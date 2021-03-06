/* *********************************************************************** *
 * project: org.matsim.*
 * CadytsController.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2012 by the members listed in the COPYING,        *
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

package playground.dziemke.cemdapMatsimCadyts.controller;

import javax.inject.Inject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.cadyts.car.CadytsCarModule;
import org.matsim.contrib.cadyts.car.CadytsContext;
import org.matsim.contrib.cadyts.general.CadytsPlanChanger;
import org.matsim.contrib.cadyts.general.CadytsScoring;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.replanning.PlanStrategy;
import org.matsim.core.replanning.PlanStrategyImpl;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.ScoringParameters;
import org.matsim.core.scoring.functions.ScoringParametersForPerson;

/**
 * @author dziemke
 */
public class CemdapMatsimCadytsControllerOld {
	
	public static void main(String[] args) {
		final Config config = ConfigUtils.createConfig();
		
		config.global().setCoordinateSystem("GK4");
		
		config.network().setInputFile("../../../shared-svn/studies/countries/de/berlin/counts/iv_counts/network.xml");
		
		config.plans().setInputFile("../../../shared-svn/projects/cemdapMatsimCadyts/scenario/cemdap2matsim/24/plans.xml.gz");
		
		config.counts().setInputFile("../../../shared-svn/studies/countries/de/berlin/counts/iv_counts/vmz_di-do.xml");
		config.counts().setCountsScaleFactor(100);
		config.counts().setOutputFormat("all"); // default is "txt"
		
		String runId = "run_146c";
		config.controler().setRunId(runId);
		config.controler().setOutputDirectory("../../../runs-svn/cemdapMatsimCadyts/" + runId + "/");
		config.controler().setFirstIteration(0);
		config.controler().setLastIteration(150);
		config.controler().setWritePlansInterval(50);
		config.controler().setWriteEventsInterval(50);
				
		config.qsim().setFlowCapFactor(0.02);
		config.qsim().setStorageCapFactor(0.02);
		config.qsim().setRemoveStuckVehicles(false);
		
//		{
//			StrategySettings strategySettings = new StrategySettings();
//			strategySettings.setStrategyName("ChangeExpBeta");
//			strategySettings.setWeight(1.0);
//			config.strategy().addStrategySettings(strategySettings);
//		}{
		{
			StrategySettings strategySettings = new StrategySettings();
			strategySettings.setStrategyName("ReRoute");
			strategySettings.setWeight(0.5);
			strategySettings.setDisableAfter(90);
			config.strategy().addStrategySettings(strategySettings);
		}
		// not necessary when cadyts as scoring is used
		{
			StrategySettings strategySetinngs = new StrategySettings();
			strategySetinngs.setStrategyName("cadytsCar");
			strategySetinngs.setWeight(1.0);
			config.strategy().addStrategySettings(strategySetinngs);
		}
		
		config.strategy().setMaxAgentPlanMemorySize(10);
		//config.strategy().setMaxAgentPlanMemorySize(5);
		
		ActivityParams homeActivity = new ActivityParams("home");
		homeActivity.setTypicalDuration(12*60*60);
		config.planCalcScore().addActivityParams(homeActivity);
		
		ActivityParams workActivity = new ActivityParams("work");
		workActivity.setTypicalDuration(9*60*60);
		config.planCalcScore().addActivityParams(workActivity);
		
		ActivityParams leisureActivity = new ActivityParams("leis");
		leisureActivity.setTypicalDuration(2*60*60);
		config.planCalcScore().addActivityParams(leisureActivity);
		
		ActivityParams shopActivity = new ActivityParams("shop");
		shopActivity.setTypicalDuration(1*60*60);
		config.planCalcScore().addActivityParams(shopActivity);
		
		ActivityParams otherActivity = new ActivityParams("other");
		otherActivity.setTypicalDuration(0.5*60*60);
		config.planCalcScore().addActivityParams(otherActivity);
		
		// ActivityParams educActivity = new ActivityParams("educ");
		// educActivity.setTypicalDuration(9*60*60);
		// config.planCalcScore().addActivityParams(educActivity);

		config.vspExperimental().addParam("vspDefaultsCheckingLevel", "ignore");
		
		final Controler controler = new Controler(config);	
		controler.addOverridingModule(new CadytsCarModule());
//		controler.getConfig().getModule("cadytsCar").addParam("startTime", "00:00:00"); // TODO reactivate
//		controler.getConfig().getModule("cadytsCar").addParam("endTime", "24:00:00");
		
        // not necessary anymore, just use normal ChangeExpBeta
//		controler.addPlanStrategyFactory("cadytsCar", new PlanStrategyFactory() {
//			@Override
//			public PlanStrategy get() {
//				return new PlanStrategyImpl(new ExpBetaPlanChangerWithCadytsPlanRegistration<Link>(
//						controler.getConfig().planCalcScore().getBrainExpBeta(), cContext));
//			}
//		});
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addPlanStrategyBinding("cadytsCar").toProvider(new javax.inject.Provider<PlanStrategy>() {
					@Inject Scenario scenario;
					@Inject CadytsContext cadytsContext;
					@Override
					public PlanStrategy get() {
						final CadytsPlanChanger<Link> planSelector = new CadytsPlanChanger<Link>(scenario, cadytsContext);
//						planSelector.setCadytsWeight(15.);
						return new PlanStrategyImpl(planSelector);
					}
				});
			}
		});
		
		/* Add Cadyts component to scoring function */
		controler.setScoringFunctionFactory(new ScoringFunctionFactory() {
			@Inject private CadytsContext cadytsContext;
			@Inject ScoringParametersForPerson parameters;
			@Override
			public ScoringFunction createNewScoringFunction(Person person) {
				final ScoringParameters params = parameters.getScoringParameters(person);

				SumScoringFunction sumScoringFunction = new SumScoringFunction();
				sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params, controler.getScenario().getNetwork()));
				sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params)) ;
				sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));

				final CadytsScoring<Link> scoringFunction = new CadytsScoring<Link>(person.getSelectedPlan(), config, cadytsContext);
				final double cadytsScoringWeight = 15.0 * config.planCalcScore().getBrainExpBeta();
				scoringFunction.setWeightOfCadytsCorrection(cadytsScoringWeight);
				sumScoringFunction.addScoringFunction(scoringFunction);

				return sumScoringFunction;
			}
		});

		controler.run();
	}
}