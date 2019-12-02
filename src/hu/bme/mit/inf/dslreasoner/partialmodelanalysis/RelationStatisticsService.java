package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import hu.bme.mit.inf.dslreasoner.logic.model.logiclanguage.TypeReference;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialComplexTypeInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialTypeInterpratation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.RelationLink;

public class RelationStatisticsService {

	public void createStatistics(int model, int run, int step, PartialInterpretation partialmodel){
		

		Map<String, Integer> referenceToAmount = new HashMap<String, Integer>();
		
		for (PartialRelationInterpretation r : partialmodel.getPartialrelationinterpretation()) {
			

			String name = r.getInterpretationOf().getName();
			int size = r.getRelationlinks().size();
			
			referenceToAmount.put(name, size);
			System.out.println(name +" > "+size);
			

//			EList<RelationLink> relationlinks = r.getRelationlinks();
//			System.out.println(relationlinks);
//			for (RelationLink relationLink : relationlinks) {
//			}
//			if (r instanceof BinaryElementRelationLink) {
//				BinaryElementRelationLink br = (BinaryElementRelationLink) r;
//				System.out.println(br.getParam1().getName() +" > "+ br.getParam2().getName());
//			}
			
		}
		
		for (PartialTypeInterpratation t : partialmodel.getPartialtypeinterpratation()) {
			if (t instanceof PartialComplexTypeInterpretation) {
				PartialComplexTypeInterpretation ct = (PartialComplexTypeInterpretation) t;
				String name = ct.getInterpretationOf().getName();
				int size = ct.getElements().size();
	
			}
		}
		
		
	}
}
