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

public class StatisticsService {
	private static final String SEPARATOR = ",";
	private static final int NUMBER_OF_STATECHARTS = 300;

	public void createStatistics() throws IOException {
		List<String> labels = createSortedLabelList();

		BufferedWriter writer = new BufferedWriter(new FileWriter("typeStatistics.csv"));

		StringBuilder labelBuilder = new StringBuilder();
		labels.forEach(label -> {
			labelBuilder.append(label);
			labelBuilder.append(SEPARATOR);
		});
		labelBuilder.deleteCharAt(labelBuilder.length() - 1);

		writer.write(labelBuilder.toString());
		writer.newLine();

		for (int i = 1; i < NUMBER_OF_STATECHARTS; i++) {
			Map<String, Integer> statechartTypeToAmount = createTypeToAmount(i);
			StringBuilder stringBuilder = new StringBuilder();
			labels.forEach(label -> {
				if (statechartTypeToAmount.containsKey(label)) {
					stringBuilder.append(statechartTypeToAmount.get(label));
				} else {
					stringBuilder.append(0);
				}
				stringBuilder.append(SEPARATOR);
			});
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			writer.write(stringBuilder.toString());
			writer.newLine();
			stringBuilder.setLength(0);
		}

		writer.close();
	}

	private Map<String, Integer> createTypeToAmount(int index) {
		StatechartLoader loader = new StatechartLoader();
		Statechart statechart = loader.loadOne(index);
		Map<String, Integer> typeToAmount = new HashMap<String, Integer>();
		for (EObject element : IteratorExtensions.toIterable(statechart.eAllContents())) {
			String key = element.eClass().getName();
			if (typeToAmount.containsKey(key)) {
				typeToAmount.put(key, typeToAmount.get(key) + 1);
			} else {
				typeToAmount.put(key, 1);
			}
		}
		return typeToAmount;
	}

	private List<String> createSortedLabelList() {
		StatechartLoader loader = new StatechartLoader();
		Set<String> labels = new HashSet<String>();
		for (int i = 1; i <= NUMBER_OF_STATECHARTS; i++) {
			Statechart statechart = loader.loadOne(i);
			for (EObject element : IteratorExtensions.toIterable(statechart.eAllContents())) {
				labels.add(element.eClass().getName());
			}

		}
		List<String> labelList = new ArrayList<String>(labels);
		Collections.sort(labelList);
		return labelList;
	}
}
