package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.Statechart;
import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.YakindummPackage;


public class StatechartLoader {
	
	private static final String YAKINDU_HUMAN_100_URI = "instancemodels/ICSE2020-InstanceModels/yakindumm/human/humanInput100/run1/";
	
	public Statechart loadOne(int index) {
		YakindummPackage.eINSTANCE.eClass();
		
		Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> map = registry.getExtensionToFactoryMap();
		map.put("xmi", new XMIResourceFactoryImpl());
		
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createURI(YAKINDU_HUMAN_100_URI + index + ".xmi"), true);
		
		Statechart statechart = (Statechart) resource.getContents().get(0);
		
		return statechart;
	}

}
