package hu.bme.mit.inf.dslreasoner.partialmodelanalysis

import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.YakindummPackage
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2LogicConfiguration
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic_Trace
import hu.bme.mit.inf.dslreasoner.ecore2logic.EcoreMetamodelDescriptor
import hu.bme.mit.inf.dslreasoner.ecore2logic.ecore2logicannotations.InverseRelationAssertion
import hu.bme.mit.inf.dslreasoner.logic.model.builder.DocumentationLevel
import hu.bme.mit.inf.dslreasoner.logic.model.builder.TracedOutput
import hu.bme.mit.inf.dslreasoner.logic.model.logicproblem.LogicProblem
import hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction.NodeAbstraction
import hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction.RelationAbstraction
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethod
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethodProvider
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ScopePropagator
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.TypeInferenceMethod
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretation2logic.InstanceModel2PartialInterpretation
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialComplexTypeInterpretation
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation
import java.util.HashMap
import java.util.Iterator
import java.util.LinkedList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.viatra.query.runtime.api.IPatternMatch
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine
import java.util.Random

class ModelLoader {
	val ecore2Logic = new Ecore2Logic
	val modelGenerationMethod = new ModelGenerationMethodProvider

	def loadModel(String path) {
		val rs = new ResourceSetImpl
		val r = rs.getResource(URI.createURI(path), true)
		return r
	}

	def static void main(String[] args) {
		ReteEngine.getClass();
		YakindummPackage.eINSTANCE.eClass
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl)

		val statistics = new StatisticsService
		statistics.createStatistics

		val loader = new ModelLoader
		val modelinstancesURI = "instancemodels/ICSE2020-InstanceModels/yakindumm/human/humanInput100/run1/"
		val random = new Random()

		// ciklusba:
		val model = loader.loadModel(modelinstancesURI + 1 + ".xmi")
		val partialmodel = loader.model2PartialModel(model)

		var abstractionoperations = loader.collectAbstractionOperations(partialmodel, loader)

		var i = 0;
		while (!abstractionoperations.isEmpty) {
			val randomNumber = random.nextInt(abstractionoperations.size)
			abstractionoperations.get(randomNumber).execute
			println("Executed " + randomNumber)
			abstractionoperations = loader.collectAbstractionOperations(partialmodel, loader)
			i++;
		}
		
		for(element : partialmodel.newElements){
			println(partialmodel.partialtypeinterpratation
				.filter(PartialComplexTypeInterpretation)
				.filter[it.elements.contains(element)]
				.map[it.interpretationOf.name].toList)
		}
		
		println(i)

	}

	def collectAbstractionOperations(PartialInterpretation partialmodel, ModelLoader loader) {
		val containmentRelations = loader.getContainmentRelations(partialmodel)
		val inverseRelations = partialmodel.problem.annotations.filter(InverseRelationAssertion)

		val inverseMap = new HashMap
		for (inverseRelation : inverseRelations) {
			inverseMap.put(inverseRelation.inverseA, inverseRelation.inverseB)
			inverseMap.put(inverseRelation.inverseB, inverseRelation.inverseA)
		}

		var inverseCount = 0;
		val removableRelationLinks = new LinkedList
		for (relation : partialmodel.partialrelationinterpretation) {
			for (element : relation.relationlinks.filter(BinaryElementRelationLink)) {
				if (!containmentRelations.contains(relation.interpretationOf)) {
					if (inverseMap.containsKey(relation.interpretationOf)) {
						val inverseType = partialmodel.partialrelationinterpretation.filter [
							it.interpretationOf === inverseMap.get(relation.interpretationOf)
						].head
						val inverseLink = inverseType.relationlinks.filter(BinaryElementRelationLink).filter [
							it.param1 === element.param2 && it.param2 === element.param1
						].head
						removableRelationLinks += new RelationAbstraction(relation, element, inverseType, inverseLink)
						inverseCount++;
					}
					removableRelationLinks += new RelationAbstraction(relation, element)
				}
			}
		}

		println("Number of removable relationlinks: " + removableRelationLinks.size)
		println("Has inverse: " + inverseCount)

		// no incoming, except one containment
		// no outgoing, except one container
		val removableNodes = new LinkedList
		val cr = partialmodel.partialrelationinterpretation.filter[containmentRelations.contains(it.interpretationOf)]
		val ncr = partialmodel.partialrelationinterpretation.filter[!containmentRelations.contains(it.interpretationOf)]
		for (element : partialmodel.newElements) {
			val incomingContainment = new LinkedList<Pair<PartialRelationInterpretation, BinaryElementRelationLink>>
			val outgoingContainment = new LinkedList<Pair<PartialRelationInterpretation, BinaryElementRelationLink>>
			val incomingNonContainment = new LinkedList<Pair<PartialRelationInterpretation, BinaryElementRelationLink>>
			val outgoingNonContainment = new LinkedList<Pair<PartialRelationInterpretation, BinaryElementRelationLink>>
			for (r : cr) {
				val links = r.relationlinks.filter(BinaryElementRelationLink)
				outgoingContainment.addAll(links.filter[it.param1 === element].map[r -> it])
				incomingContainment.addAll(links.filter[it.param2 === element].map[r -> it])
			}
			for (r : ncr) {
				val links = r.relationlinks.filter(BinaryElementRelationLink)
				outgoingNonContainment.addAll(links.filter[it.param1 === element].map[r -> it])
				incomingNonContainment.addAll(links.filter[it.param2 === element].map[r -> it])
			}

			if (outgoingContainment.empty && incomingNonContainment.empty) {
				if (incomingContainment.size > 1) {
					throw new IllegalArgumentException('Gebasz')
				} else if (incomingContainment.size == 0) {
					// this is the root
				} else { // /incomingContainment.size == 1
					val incomingOnlyContainment = incomingContainment.head
					if (outgoingNonContainment.isEmpty) {
						removableNodes +=
							new NodeAbstraction(incomingOnlyContainment.key, incomingOnlyContainment.value,
								partialmodel)
					} else if (outgoingNonContainment.size === 1) {
						val onlyOutgoingNonContainment = outgoingNonContainment.head
						val inverseOfIncomingContainment = inverseMap.get(incomingOnlyContainment.key.interpretationOf)
						if (onlyOutgoingNonContainment.key.interpretationOf === inverseOfIncomingContainment) {
							removableNodes +=
								new NodeAbstraction(incomingOnlyContainment.key, incomingOnlyContainment.value,
									partialmodel, onlyOutgoingNonContainment.key, onlyOutgoingNonContainment.value)
						}
					}
				}
			}
		}
		println("Number of removable nodes: " + removableNodes.size)
		val abstractionOperations = removableNodes.toList + removableRelationLinks.toList
		println("Number of available abstraction operations: " + abstractionOperations.size())
		return abstractionOperations
	}

	def printNodesByType(PartialInterpretation partialmodel) {
		for (type : partialmodel.partialtypeinterpratation.filter(PartialComplexTypeInterpretation)) {
			print(type.interpretationOf.name)
			print(" > ")
			println(type.elements.size)
			for (element : type.elements) {
				println(" > " + element.name)
			}
		}
	}

	def model2PartialModel(Resource model) {
		val metamodelDescriptor = new EcoreMetamodelDescriptor(
			YakindummPackage.eINSTANCE.EClassifiers.filter(EClass).toList,
			emptySet,
			false,
			YakindummPackage.eINSTANCE.EClassifiers.filter(EEnum).toList,
			YakindummPackage.eINSTANCE.EClassifiers.filter(EEnum).map[ELiterals].flatten.toList,
			YakindummPackage.eINSTANCE.EClassifiers.filter(EClass).map[EReferences].flatten.toList,
			emptyList
		)

		val metamodelProblem = ecore2Logic.transformMetamodel(metamodelDescriptor, new Ecore2LogicConfiguration)
		val partialModel = (new InstanceModel2PartialInterpretation).transform(metamodelProblem, model, true)
		partialModel.problemConainer = metamodelProblem.output

		// createMethod(metamodelProblem, partialModel)
		return partialModel

	// partialModel.problemConainer.elements.remove()
	}

	protected def void createMethod(TracedOutput<LogicProblem, Ecore2Logic_Trace> metamodelProblem,
		PartialInterpretation partialModel) {
		val modelGenerationMethod = modelGenerationMethod.createModelGenerationMethod(
			metamodelProblem.output,
			partialModel,
			null,
			false,
			TypeInferenceMethod.PreliminaryAnalysis,
			new ScopePropagator(partialModel),
			DocumentationLevel.NONE
		)

		val ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(partialModel))
		val matchers = new LinkedList

		printPatterns(modelGenerationMethod, matchers, engine)
	}

	protected def void printPatterns(ModelGenerationMethod modelGenerationMethod,
		LinkedList<ViatraQueryMatcher<? extends IPatternMatch>> matchers, ViatraQueryEngine engine) {
		println("\n--- PatternName -> EntitiesInPattern ---")
		for (p : modelGenerationMethod.allPatterns) {
			println(p.fullyQualifiedName + "/" + (p.parameters.size + 1))
			matchers += p.getMatcher(engine)
		}

		println("\n--- PatternName -> CountMatches ---")
		for (matcher : matchers) {
			println('''«matcher.patternName» -> «matcher.countMatches»''')
		}
	}

	def getContainmentRelations(PartialInterpretation partialmodel) {
		return partialmodel.problem.containmentHierarchies.head.containmentRelations.toSet
	}

	static def <X> x(Iterator<X> p) {
		p.toIterable
	}
}
