package hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction;

import hu.bme.mit.inf.dslreasoner.logic.model.logiclanguage.DefinedElement;
import hu.bme.mit.inf.dslreasoner.logic.model.logiclanguage.TypeReference;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialTypeInterpratation;

public class NodeAbstraction extends AbstractionOperation {
	PartialInterpretation partialmodel;
	PartialRelationInterpretation relation;
	BinaryElementRelationLink link;

	@Override
	public void execute() {
		relation.getRelationlinks().remove(link);
		DefinedElement child = link.getParam2();
		for(PartialTypeInterpratation type : partialmodel.getPartialtypeinterpratation()) {
			type.getElements().remove(child);
		}
		partialmodel.getNewElements().remove(child);
	}
	
	public NodeAbstraction(PartialRelationInterpretation relation, BinaryElementRelationLink link, PartialInterpretation partialmodel) {
		this.relation = relation;
		this.link = link;
		this.partialmodel = partialmodel;
	}

}
