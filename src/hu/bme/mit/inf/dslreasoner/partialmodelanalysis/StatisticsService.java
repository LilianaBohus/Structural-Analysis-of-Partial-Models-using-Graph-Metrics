package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import java.util.Iterator;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.Statechart;

public class StatisticsService {
	

	public void createStatistics() {
		StatechartLoader loader = new StatechartLoader();
		Statechart stateChart = loader.loadOne(1);
		
		TreeIterator<EObject> iterator = stateChart.eAllContents();
		int i = 0;
		
		while (iterator.hasNext()) {
			System.out.println(iterator.getClass());
			iterator.next();
			i++;
			
		}
		
		System.out.println(i);
		
		
	}
	

}
