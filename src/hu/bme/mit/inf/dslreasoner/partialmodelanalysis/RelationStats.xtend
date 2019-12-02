package hu.bme.mit.inf.dslreasoner.partialmodelanalysis

import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialComplexTypeInterpretation
import java.util.Map
import hu.bme.mit.inf.dslreasoner.logic.model.logiclanguage.DefinedElement
import java.util.HashMap
import java.io.FileWriter
import java.io.BufferedWriter
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PrimitiveElement

class RelationStats {
	val FILE_NAME = "RelationStatistics"
	val SEPARATOR = ","
	val MAX_OUTGOING = 15;
	val writer = new BufferedWriter(new FileWriter(FILE_NAME + ".csv"));

	def createStatistics(int model, int run, int step, PartialInterpretation partialmodel) {
		val Map<String, Integer> outgoingToAmount = new HashMap
		for (element : partialmodel.newElements.filter[!(it instanceof PrimitiveElement)]) {
			outgoingToAmount.put(element.name, 0)
		}

		for (relation : partialmodel.partialrelationinterpretation) {
			// println(relation.interpretationOf.name + ": " + relation.relationlinks.size)
			for (element : relation.relationlinks.filter(BinaryElementRelationLink)) {

				val outgoinglink = element.param1.name
				if (outgoingToAmount.containsKey(outgoinglink)) {
					outgoingToAmount.put(outgoinglink, outgoingToAmount.get(outgoinglink) + 1)
				} else {
					println("----------------fos")
					outgoingToAmount.put(outgoinglink, 1)
				}
			}
		}
		val Map<Integer, Integer> nodeAmountToOutgoingAmount = new HashMap
		for (i : 0 .. MAX_OUTGOING) {
			nodeAmountToOutgoingAmount.put(i, 0)
		}
		for (value : outgoingToAmount.values) {
			if (nodeAmountToOutgoingAmount.containsKey(value)) {
				nodeAmountToOutgoingAmount.put(value, nodeAmountToOutgoingAmount.get(value) + 1)
			}
		}
//		println("NAO: " + nodeAmountToOutgoingAmount)
		writer.write(model + SEPARATOR)
		writer.write(run + SEPARATOR)
		writer.write(step + SEPARATOR)
		for (i : 0 .. MAX_OUTGOING) {
			writer.write(nodeAmountToOutgoingAmount.get(i) + SEPARATOR)
		}

		writer.newLine
		writer.flush
	}

	def createLabels() {
		writer.write("Model" + SEPARATOR)
		writer.write("Run" + SEPARATOR)
		writer.write("Step" + SEPARATOR)
		for (i : 0 .. MAX_OUTGOING) {
			writer.write("Degree = " + i + SEPARATOR)
		}
		writer.newLine
		writer.flush
	}

}
