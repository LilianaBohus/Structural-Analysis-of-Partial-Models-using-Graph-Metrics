package hu.bme.mit.inf.dslreasoner.partialmodelanalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import hu.bme.mit.inf.dslreasoner.domains.yakindu.sgraph.yakindumm.Statechart;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialComplexTypeInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialInterpretation;
import hu.bme.mit.inf.dslreasoner.viatrasolver.partialinterpretationlanguage.partialinterpretation.PartialTypeInterpratation;

public class StatisticsService {
	private static final String FILE_NAME = "typeStatistics_AllSeed";
	private static final String SEPARATOR = ",";
	private List<String> labels; // = createSortedLabelList();
	BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME + ".csv"));
	private int model;
	private int run;
	private int step;

	public StatisticsService() throws IOException {
		System.out.println("-- File initialized. --");
	}

	public void appendStatistics(int model, int run, int step, PartialInterpretation partialmodel) throws IOException {
		this.model = model;
		this.run = run;
		this.step = step;

		Map<String, Integer> typeToAmount = new HashMap<String, Integer>();

		for (PartialTypeInterpratation t : partialmodel.getPartialtypeinterpratation()) {
			if (t instanceof PartialComplexTypeInterpretation) {
				PartialComplexTypeInterpretation ct = (PartialComplexTypeInterpretation) t;
				String name = ct.getInterpretationOf().getName();
				int size = ct.getElements().size();
				typeToAmount.put(name, size);
			}
		}
		writer.write(createDataRow(labels, typeToAmount));
		writer.newLine();
		writer.flush();
	}

	private String createDataRow(List<String> labels, Map<String, Integer> statechartTypeToAmount) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(model + SEPARATOR);
		stringBuilder.append(run + SEPARATOR);
		stringBuilder.append(step + SEPARATOR);
		labels.forEach(label -> {
			if (statechartTypeToAmount.containsKey(label)) {
				stringBuilder.append(statechartTypeToAmount.get(label));
			} else {
				stringBuilder.append(0);
			}
			stringBuilder.append(SEPARATOR);
		});
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

	private String createLabelRow(List<String> labels) {
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append("MODEL" + SEPARATOR);
		labelBuilder.append("RUN" + SEPARATOR);
		labelBuilder.append("STEP" + SEPARATOR);
		labels.forEach(label -> {
			labelBuilder.append(label);
			labelBuilder.append(SEPARATOR);
		});
		labelBuilder.deleteCharAt(labelBuilder.length() - 1);
		return labelBuilder.toString();
	}

	public void createSortedLabelList(PartialInterpretation partialmodel) throws IOException {
		Set<String> labels = new HashSet<String>();
		for (PartialTypeInterpratation t : partialmodel.getPartialtypeinterpratation()) {
			if (t instanceof PartialComplexTypeInterpretation) {
				PartialComplexTypeInterpretation ct = (PartialComplexTypeInterpretation) t;
				String name = ct.getInterpretationOf().getName();
				labels.add(name);
			}

		}
		List<String> labelList = new ArrayList<String>(labels);
		Collections.sort(labelList);
		this.labels = labelList;
		writer.write(createLabelRow(this.labels));
		writer.newLine();
	}

}
