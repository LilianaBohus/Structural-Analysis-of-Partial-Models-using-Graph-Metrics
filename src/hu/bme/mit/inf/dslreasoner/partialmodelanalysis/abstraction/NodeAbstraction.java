package hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction;

import hu.bme.mit.inf.dslreasoner.logic.model.logiclanguage.DefinedElement;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialTypeInterpratation;

public class NodeAbstraction extends AbstractionOperation {
	PartialInterpretation partialmodel = null;
	PartialRelationInterpretation relation = null;
	BinaryElementRelationLink link = null;
	PartialRelationInterpretation inverseRelation = null;
	BinaryElementRelationLink inverseLink = null;

	@Override
	public void execute() {
		System.out.println("NodeAbstraction " + inverseLink==null);

		
		boolean removed1 = relation.getRelationlinks().remove(link);
		if(!removed1) throw new IllegalArgumentException("Gebasz1");
		if (inverseLink != null) {
			boolean removed2 = inverseRelation.getRelationlinks().remove(inverseLink);
			if(!removed2) throw new IllegalArgumentException("Gebasz2");
		}
		DefinedElement child = link.getParam2();
		for (PartialTypeInterpratation type : partialmodel.getPartialtypeinterpratation()) {
			type.getElements().remove(child);
		}
		boolean r = partialmodel.getNewElements().remove(child);
		if (!r) throw new IllegalArgumentException("Gebasz3");
		
		
	}

	public NodeAbstraction(PartialRelationInterpretation relation, BinaryElementRelationLink link,
			PartialInterpretation partialmodel) {
		this.relation = relation;
		this.link = link;
		this.partialmodel = partialmodel;
	}

	// with inverse
	public NodeAbstraction(PartialRelationInterpretation relation, BinaryElementRelationLink link,
			PartialInterpretation partialmodel, PartialRelationInterpretation inverseRelation,
			BinaryElementRelationLink inverse) {
		this.relation = relation;
		this.link = link;
		this.inverseLink = inverse;
		this.inverseRelation = inverseRelation;
		this.partialmodel = partialmodel;
	}

}
