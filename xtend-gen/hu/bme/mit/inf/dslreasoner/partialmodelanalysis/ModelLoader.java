package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import com.google.common.collect.Iterables;
import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.YakindummPackage;
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic;
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2LogicConfiguration;
import hu.bme.mit.inf.dslreasoner.ecore2logic.Ecore2Logic_Trace;
import hu.bme.mit.inf.dslreasoner.ecore2logic.EcoreMetamodelDescriptor;
import hu.bme.mit.inf.dslreasoner.logic.model.builder.DocumentationLevel;
import hu.bme.mit.inf.dslreasoner.logic.model.builder.TracedOutput;
import hu.bme.mit.inf.dslreasoner.logic.model.builder.TypeScopes;
import hu.bme.mit.inf.dslreasoner.logic.model.logicproblem.LogicProblem;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethod;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ModelGenerationMethodProvider;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.ScopePropagator;
import hu.bme.mit.inf.dslreasoner.viatrasolver.logic2viatra.TypeInferenceMethod;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretation2logic.InstanceModel2Logic;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.PartialInterpretationInitialiser;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import java.util.Collection;
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
  
  private final InstanceModel2Logic instanceModel2Logic = new InstanceModel2Logic();
  
  private final PartialInterpretationInitialiser initializer = new PartialInterpretationInitialiser();
  
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
    final Resource model = loader.loadModel("instancemodels/ICSE2020-InstanceModels/yakindumm/human/humanInput100/run1/1.xmi");
    loader.model2PartialModel(model);
  }
  
  public void model2PartialModel(final Resource model) {
    List<EClass> _list = IterableExtensions.<EClass>toList(Iterables.<EClass>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EClass.class));
    Set<EClass> _emptySet = CollectionLiterals.<EClass>emptySet();
    List<EEnum> _list_1 = IterableExtensions.<EEnum>toList(Iterables.<EEnum>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EEnum.class));
    final Function1<EEnum, EList<EEnumLiteral>> _function = new Function1<EEnum, EList<EEnumLiteral>>() {
      public EList<EEnumLiteral> apply(final EEnum it) {
        return it.getELiterals();
      }
    };
    List<EEnumLiteral> _list_2 = IterableExtensions.<EEnumLiteral>toList(Iterables.<EEnumLiteral>concat(IterableExtensions.<EEnum, EList<EEnumLiteral>>map(Iterables.<EEnum>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EEnum.class), _function)));
    final Function1<EClass, EList<EReference>> _function_1 = new Function1<EClass, EList<EReference>>() {
      public EList<EReference> apply(final EClass it) {
        return it.getEReferences();
      }
    };
    List<EReference> _list_3 = IterableExtensions.<EReference>toList(Iterables.<EReference>concat(IterableExtensions.<EClass, EList<EReference>>map(Iterables.<EClass>filter(YakindummPackage.eINSTANCE.getEClassifiers(), EClass.class), _function_1)));
    List<EAttribute> _emptyList = CollectionLiterals.<EAttribute>emptyList();
    final EcoreMetamodelDescriptor metamodelDescriptor = new EcoreMetamodelDescriptor(_list, _emptySet, 
      false, _list_1, _list_2, _list_3, _emptyList);
    final List<EObject> modelDescriptor = IteratorExtensions.<EObject>toList(model.getAllContents());
    Ecore2LogicConfiguration _ecore2LogicConfiguration = new Ecore2LogicConfiguration();
    final TracedOutput<LogicProblem, Ecore2Logic_Trace> metamodelProblem = this.ecore2Logic.transformMetamodel(metamodelDescriptor, _ecore2LogicConfiguration);
    final LogicProblem instanceModelProblem = this.instanceModel2Logic.transform(metamodelProblem, modelDescriptor).getOutput();
    TypeScopes _typeScopes = new TypeScopes();
    final PartialInterpretation partialModel = this.initializer.initialisePartialInterpretation(instanceModelProblem, _typeScopes).getOutput();
    partialModel.setProblemConainer(instanceModelProblem);
    ScopePropagator _scopePropagator = new ScopePropagator(partialModel);
    final ModelGenerationMethod modelGenerationMethod = this.modelGenerationMethod.createModelGenerationMethod(instanceModelProblem, partialModel, 
      null, 
      false, 
      TypeInferenceMethod.PreliminaryAnalysis, _scopePropagator, 
      DocumentationLevel.NONE);
    EMFScope _eMFScope = new EMFScope(partialModel);
    final ViatraQueryEngine engine = ViatraQueryEngine.on(_eMFScope);
    final LinkedList<ViatraQueryMatcher<? extends IPatternMatch>> matchers = new LinkedList<ViatraQueryMatcher<? extends IPatternMatch>>();
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
    InputOutput.<String>println("::::::::::::::::");
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
