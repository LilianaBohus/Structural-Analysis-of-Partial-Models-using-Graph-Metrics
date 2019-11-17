package hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction;

import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;

public class RelationAbstraction extends AbstractionOperation {
	PartialRelationInterpretation relation;
	BinaryElementRelationLink link;
	PartialRelationInterpretation inverseRelation;
	BinaryElementRelationLink inverseLink;

	@Override
	public void execute() {
		relation.getRelationlinks().remove(link);
		if (inverseLink != null) {
			inverseRelation.getRelationlinks().remove(inverseLink);
			// System.out.println(">>> Removing " + link.getParam1().getName() + " <--> " + link.getParam2().getName() + " relation (with inverse)");
		}
	}

	public RelationAbstraction(PartialRelationInterpretation relation, BinaryElementRelationLink link) {
		this.relation = relation;
		this.link = link;
	}

	public RelationAbstraction(PartialRelationInterpretation relation, BinaryElementRelationLink link,
			PartialRelationInterpretation inverseRelation, BinaryElementRelationLink inverse) {
		this.relation = relation;
		this.link = link;
		this.inverseRelation = inverseRelation;
		this.inverseLink = inverse;
	}
}
