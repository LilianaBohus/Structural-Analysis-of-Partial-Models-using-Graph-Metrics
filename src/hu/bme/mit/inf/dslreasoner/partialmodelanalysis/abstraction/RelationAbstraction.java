package hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction;

import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;

public class RelationAbstraction extends AbstractionOperation {
	PartialRelationInterpretation relation;
	BinaryElementRelationLink link;

	@Override
	public void execute() {
		relation.getRelationlinks().remove(link);
	}

	public RelationAbstraction(PartialRelationInterpretation relation, BinaryElementRelationLink link) {
		this.relation = relation;
		this.link = link;
	}
}
