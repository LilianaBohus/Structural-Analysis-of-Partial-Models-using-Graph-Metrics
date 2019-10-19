package hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstractionfilter;

import com.google.common.collect.Iterables;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;
import java.util.LinkedList;
import java.util.Set;
import org.eclipse.emf.common.util.EList;

@SuppressWarnings("all")
public class RelationAbstractionOperationFilter {
  public LinkedList<BinaryElementRelationLink> getRemovableRelations(final PartialInterpretation partialmodel, final Set containmentRelations) {
    final LinkedList<BinaryElementRelationLink> removableRelations = new LinkedList<BinaryElementRelationLink>();
    EList<PartialRelationInterpretation> _partialrelationinterpretation = partialmodel.getPartialrelationinterpretation();
    for (final PartialRelationInterpretation relation : _partialrelationinterpretation) {
      Iterable<BinaryElementRelationLink> _filter = Iterables.<BinaryElementRelationLink>filter(relation.getRelationlinks(), BinaryElementRelationLink.class);
      for (final BinaryElementRelationLink element : _filter) {
        boolean _contains = containmentRelations.contains(relation.getInterpretationOf());
        boolean _not = (!_contains);
        if (_not) {
          removableRelations.add(element);
        }
      }
    }
    return removableRelations;
  }
}
