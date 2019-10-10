package hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstractionfilter

import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation
import java.util.LinkedList
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink
import java.util.Set

class AbstractRelationOperationFilter {
	
	def getRemovableRelations(PartialInterpretation partialmodel, Set containmentRelations) {
		val removableRelations = new LinkedList
		for (relation : partialmodel.partialrelationinterpretation) {
			for (element : relation.relationlinks.filter(BinaryElementRelationLink)) {
				if (!containmentRelations.contains(relation.interpretationOf)) {
					removableRelations.add(element)
				}
			}
		}
		return removableRelations
	}
	
}
