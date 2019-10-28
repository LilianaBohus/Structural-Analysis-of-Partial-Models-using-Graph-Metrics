package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.Statechart;

public class StatisticsService {
	

	public void createStatistics() {
		StatechartLoader loader = new StatechartLoader();
		Statechart stateChart = loader.loadOne(1);
		
		Map typeToAmount = new HashMap();
		
		for (EObject element : IteratorExtensions.toIterable(stateChart.eAllContents())) {
			System.out.println(element.eClass().getName());
			if (typeToAmount.containsKey(element.getClass().getName())) {
				//typeToAmount
		}}
	}
}
