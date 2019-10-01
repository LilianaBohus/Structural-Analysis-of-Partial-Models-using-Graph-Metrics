package hu.bme.mit.inf.dslreasoner.partialmodelanalysis

import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.YakindummPackage
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2LogicConfiguration
import hu.bme.mit.inf.dslreasoner.ecore2logic.EcoreMetamodelDescriptor
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretation2logic.InstanceModel2Logic
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.PartialInterpretationInitialiser
import hu.bme.mit.inf.dslreasoner.logic.model.builder.TypeScopes
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethodProvider
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.TypeInferenceMethod
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ScopePropagator
import hu.bme.mit.inf.dslreasoner.logic.model.builder.DocumentationLevel
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine
import org.eclipse.viatra.query.runtime.api.scope.QueryScope
import org.eclipse.viatra.query.runtime.emf.EMFScope
import java.util.LinkedList

class ModelLoader {
	val ecore2Logic = new Ecore2Logic
	val  instanceModel2Logic = new InstanceModel2Logic
	val initializer = new PartialInterpretationInitialiser
	val modelGenerationMethod = new ModelGenerationMethodProvider
	
	def loadModel(String path) {
		val rs = new ResourceSetImpl
		val r = rs.getResource(URI.createURI(path),true)
		return r
	}
	def static void main(String[] args) {
		ReteEngine.getClass();
		YakindummPackage.eINSTANCE.eClass
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl)
		
		
		val loader = new ModelLoader
		val model = loader.loadModel("instancemodels/ICSE2020-InstanceModels/yakindumm/human/humanInput100/run1/1.xmi")
		loader.model2PartialModel(model)
	}
	
	def model2PartialModel(Resource model) {
		val metamodelDescriptor = new EcoreMetamodelDescriptor(
			YakindummPackage.eINSTANCE.EClassifiers.filter(EClass).toList,
			emptySet,
			false,
			YakindummPackage.eINSTANCE.EClassifiers.filter(EEnum).toList,
			YakindummPackage.eINSTANCE.EClassifiers.filter(EEnum).map[ELiterals].flatten.toList,
			YakindummPackage.eINSTANCE.EClassifiers.filter(EClass).map[it.EReferences].flatten.toList,
			emptyList
		)
		val modelDescriptor = model.allContents.toList
		val metamodelProblem = ecore2Logic.transformMetamodel(metamodelDescriptor, new Ecore2LogicConfiguration)
		val instanceModelProblem = instanceModel2Logic.transform(
					metamodelProblem,
					modelDescriptor
				).output
		
		val partialModel = initializer.initialisePartialInterpretation(instanceModelProblem, new TypeScopes).output
		partialModel.problemConainer = instanceModelProblem
		
		val modelGenerationMethod = modelGenerationMethod.createModelGenerationMethod(
			instanceModelProblem,
			partialModel,
			null,
			false,
			TypeInferenceMethod.PreliminaryAnalysis,
			new ScopePropagator(partialModel),
			DocumentationLevel.NONE
		)
		val ViatraQueryEngine engine = ViatraQueryEngine.on(new EMFScope(partialModel))
		val matchers = new LinkedList
		for(p : modelGenerationMethod.allPatterns) {
			println(p.fullyQualifiedName + "/" + (p.parameters.size+1))
			matchers += p.getMatcher(engine)
		}
		println("::::::::::::::::")
		for(matcher : matchers) {
			println('''«matcher.patternName» -> «matcher.countMatches»''')
		}
	}
}