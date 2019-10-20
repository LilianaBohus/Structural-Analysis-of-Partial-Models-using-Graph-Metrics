package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import com.google.common.collect.Iterables;
import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.YakindummPackage;
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic;
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2LogicConfiguration;
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic_Trace;
import hu.bme.mit.inf.dslreasoner.ecore2logic.EcoreMetamodelDescriptor;
import hu.bme.mit.inf.dslreasoner.ecore2logic.ecore2logicannotations.InverseRelationAssertion;
import hu.bme.mit.inf.dslreasoner.logic.model.builder.DocumentationLevel;
import hu.bme.mit.inf.dslreasoner.logic.model.builder.TracedOutput;
import hu.bme.mit.inf.dslreasoner.logic.model.logiclanguage.Relation;
import hu.bme.mit.inf.dslreasoner.logic.model.logicproblem.ContainmentHierarchy;
import hu.bme.mit.inf.dslreasoner.logic.model.logicproblem.LogicProblem;
import hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstraction.RelationAbstraction;
import hu.bme.mit.inf.dslreasoner.partialmodelanalysis.abstractionfilter.RelationAbstractionOperationFilter;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethod;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethodProvider;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ScopePropagator;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.TypeInferenceMethod;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretation2logic.InstanceModel2PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.BinaryElementRelationLink;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialRelationInterpretation;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteEngine;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

@SuppressWarnings("all")
public class ModelLoader {
  private final Ecore2Logic ecore2Logic = new Ecore2Logic();
  
  private final ModelGenerationMethodProvider modelGenerationMethod = new ModelGenerationMethodProvider();
  
  public Resource loadModel(final String path) {
    final ResourceSetImpl rs = new ResourceSetImpl();
    final Resource r = rs.getResource(URI.createURI(path), true);
    return r;
  }
  
  public static void main(final String[] args) {
    ReteEngine.class.getClass();
    YakindummPackage.eINSTANCE.eClass();
    Map<String, Object> _extensionToFactoryMap = Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();
    XMIResourceFactoryImpl _xMIResourceFactoryImpl = new XMIResourceFactoryImpl();
    _extensionToFactoryMap.put("xmi", _xMIResourceFactoryImpl);
    final ModelLoader loader = new ModelLoader();
    final String modelinstancesURI = "instancemodels/ICSE2020-InstanceModels/yakindumm/human/humanInput100/run1/";
    final Resource model = loader.loadModel((modelinstancesURI + "1.xmi"));
    final PartialInterpretation partialmodel = loader.model2PartialModel(model);
    final Set<Relation> containmentRelations = IterableExtensions.<Relation>toSet(IterableExtensions.<ContainmentHierarchy>head(partialmodel.getProblem().getContainmentHierarchies()).getContainmentRelations());
    final Iterable<InverseRelationAssertion> inverseRelations = Iterables.<InverseRelationAssertion>filter(partialmodel.getProblem().getAnnotations(), InverseRelationAssertion.class);
    final HashMap<Relation, Relation> inverseMap = new HashMap<Relation, Relation>();
    for (final InverseRelationAssertion inverseRelation : inverseRelations) {
      inverseMap.put(inverseRelation.getInverseA(), inverseRelation.getInverseB());
    }
    final RelationAbstractionOperationFilter relationFilter = new RelationAbstractionOperationFilter();
    final LinkedList<BinaryElementRelationLink> removableRelations = relationFilter.getRemovableRelations(partialmodel, containmentRelations);
    int _size = removableRelations.size();
    String _plus = ("Number of removable relations: " + Integer.valueOf(_size));
    InputOutput.<String>println(_plus);
    /* Iterables.<BinaryElementRelationLink>filter(partialmodel.getPartialrelationinterpretation(), BinaryElementRelationLink.class); */
    final LinkedList<RelationAbstraction> removableRelationLinks = new LinkedList<RelationAbstraction>();
    EList<PartialRelationInterpretation> _partialrelationinterpretation = partialmodel.getPartialrelationinterpretation();
    for (final PartialRelationInterpretation relation : _partialrelationinterpretation) {
      Iterable<BinaryElementRelationLink> _filter = Iterables.<BinaryElementRelationLink>filter(relation.getRelationlinks(), BinaryElementRelationLink.class);
      for (final BinaryElementRelationLink element : _filter) {
        boolean _contains = containmentRelations.contains(relation.getInterpretationOf());
        boolean _not = (!_contains);
        if (_not) {
          RelationAbstraction _relationAbstraction = new RelationAbstraction(relation, element);
          removableRelationLinks.add(_relationAbstraction);
        }
      }
    }
    final LinkedList<RelationAbstraction> removableNodes = new LinkedList<RelationAbstraction>();
    EList<PartialRelationInterpretation> _partialrelationinterpretation_1 = partialmodel.getPartialrelationinterpretation();
    for (final PartialRelationInterpretation relation_1 : _partialrelationinterpretation_1) {
      Iterable<BinaryElementRelationLink> _filter_1 = Iterables.<BinaryElementRelationLink>filter(relation_1.getRelationlinks(), BinaryElementRelationLink.class);
      for (final BinaryElementRelationLink element_1 : _filter_1) {
        boolean _contains_1 = containmentRelations.contains(relation_1.getInterpretationOf());
        boolean _not_1 = (!_contains_1);
        if (_not_1) {
          RelationAbstraction _relationAbstraction_1 = new RelationAbstraction(relation_1, element_1);
          removableRelationLinks.add(_relationAbstraction_1);
        }
      }
    }
    final List<RelationAbstraction> abstractionOperations = IterableExtensions.<RelationAbstraction>toList(Iterables.<RelationAbstraction>concat(removableRelationLinks, removableNodes));
    final Set<EObject> remainingContent = IteratorExtensions.<EObject>toSet(partialmodel.eAllContents());
    int _size_1 = remainingContent.size();
    String _plus_1 = ("Before: " + Integer.valueOf(_size_1));
    InputOutput.<String>println(_plus_1);
    remainingContent.remove(removableRelations.get(0));
    int _size_2 = remainingContent.size();
    String _plus_2 = ("After: " + Integer.valueOf(_size_2));
    InputOutput.<String>println(_plus_2);
    EList<PartialRelationInterpretation> _partialrelationinterpretation_2 = partialmodel.getPartialrelationinterpretation();
    for (final PartialRelationInterpretation relation_2 : _partialrelationinterpretation_2) {
      {
        String _name = relation_2.getInterpretationOf().getName();
        String _plus_3 = (_name + ": ");
        int _size_3 = relation_2.getRelationlinks().size();
        String _plus_4 = (_plus_3 + Integer.valueOf(_size_3));
        InputOutput.<String>println(_plus_4);
        boolean _containsKey = inverseMap.containsKey(relation_2.getInterpretationOf());
        if (_containsKey) {
          String _name_1 = inverseMap.get(relation_2.getInterpretationOf()).getName();
          String _plus_5 = ("--> inverse: " + _name_1);
          InputOutput.<String>println(_plus_5);
        }
        Iterable<BinaryElementRelationLink> _filter_2 = Iterables.<BinaryElementRelationLink>filter(relation_2.getRelationlinks(), BinaryElementRelationLink.class);
        for (final BinaryElementRelationLink element_2 : _filter_2) {
          {
            InputOutput.<String>print(element_2.getParam1().getName());
            boolean _contains_2 = containmentRelations.contains(relation_2.getInterpretationOf());
            if (_contains_2) {
              InputOutput.<String>print(" => ");
            } else {
              InputOutput.<String>print(" -> ");
            }
            InputOutput.<String>println(element_2.getParam2().getName());
          }
        }
      }
    }
  }
  
  public PartialInterpretation model2PartialModel(final Resource model) {
    List<EClass> _list = IterableExtensions.<EClass>toList(Iterables.<EClass>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EClass.class));
    Set<EClass> _emptySet = CollectionLiterals.<EClass>emptySet();
    List<EEnum> _list_1 = IterableExtensions.<EEnum>toList(Iterables.<EEnum>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EEnum.class));
    final Function1<EEnum, EList<EEnumLiteral>> _function = (EEnum it) -> {
      return it.getELiterals();
    };
    List<EEnumLiteral> _list_2 = IterableExtensions.<EEnumLiteral>toList(Iterables.<EEnumLiteral>concat(IterableExtensions.<EEnum, EList<EEnumLiteral>>map(Iterables.<EEnum>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EEnum.class), _function)));
    final Function1<EClass, EList<EReference>> _function_1 = (EClass it) -> {
      return it.getEReferences();
    };
    List<EReference> _list_3 = IterableExtensions.<EReference>toList(Iterables.<EReference>concat(IterableExtensions.<EClass, EList<EReference>>map(Iterables.<EClass>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EClass.class), _function_1)));
    List<EAttribute> _emptyList = CollectionLiterals.<EAttribute>emptyList();
    final EcoreMetamodelDescriptor metamodelDescriptor = new EcoreMetamodelDescriptor(_list, _emptySet, 
      false, _list_1, _list_2, _list_3, _emptyList);
    Ecore2LogicConfiguration _ecore2LogicConfiguration = new Ecore2LogicConfiguration();
    final TracedOutput<LogicProblem, Ecore2Logic_Trace> metamodelProblem = this.ecore2Logic.transformMetamodel(metamodelDescriptor, _ecore2LogicConfiguration);
    final PartialInterpretation partialModel = new InstanceModel2PartialInterpretation().transform(metamodelProblem, model, true);
    partialModel.setProblemConainer(metamodelProblem.getOutput());
    return partialModel;
  }
  
  protected void createMethod(final TracedOutput<LogicProblem, Ecore2Logic_Trace> metamodelProblem, final PartialInterpretation partialModel) {
    ScopePropagator _scopePropagator = new ScopePropagator(partialModel);
    final ModelGenerationMethod modelGenerationMethod = this.modelGenerationMethod.createModelGenerationMethod(
      metamodelProblem.getOutput(), partialModel, 
      null, 
      false, 
      TypeInferenceMethod.PreliminaryAnalysis, _scopePropagator, 
      DocumentationLevel.NONE);
    EMFScope _eMFScope = new EMFScope(partialModel);
    final ViatraQueryEngine engine = ViatraQueryEngine.on(_eMFScope);
    final LinkedList<ViatraQueryMatcher<? extends IPatternMatch>> matchers = new LinkedList<ViatraQueryMatcher<? extends IPatternMatch>>();
    this.printPatterns(modelGenerationMethod, matchers, engine);
  }
  
  protected void printPatterns(final ModelGenerationMethod modelGenerationMethod, final LinkedList<ViatraQueryMatcher<? extends IPatternMatch>> matchers, final ViatraQueryEngine engine) {
    InputOutput.<String>println("\n--- PatternName -> EntitiesInPattern ---");
    Collection<? extends IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>>> _allPatterns = modelGenerationMethod.getAllPatterns();
    for (final IQuerySpecification<? extends ViatraQueryMatcher<? extends IPatternMatch>> p : _allPatterns) {
      {
        String _fullyQualifiedName = p.getFullyQualifiedName();
        String _plus = (_fullyQualifiedName + "/");
        int _size = p.getParameters().size();
        int _plus_1 = (_size + 1);
        String _plus_2 = (_plus + Integer.valueOf(_plus_1));
        InputOutput.<String>println(_plus_2);
        ViatraQueryMatcher<? extends IPatternMatch> _matcher = p.getMatcher(engine);
        matchers.add(_matcher);
      }
    }
    InputOutput.<String>println("\n--- PatternName -> CountMatches ---");
    for (final ViatraQueryMatcher<? extends IPatternMatch> matcher : matchers) {
      StringConcatenation _builder = new StringConcatenation();
      String _patternName = matcher.getPatternName();
      _builder.append(_patternName);
      _builder.append(" -> ");
      int _countMatches = matcher.countMatches();
      _builder.append(_countMatches);
      InputOutput.<String>println(_builder.toString());
    }
  }
}