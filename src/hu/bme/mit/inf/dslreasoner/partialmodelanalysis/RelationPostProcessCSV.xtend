package hu.bme.mit.inf.dslreasoner.partialmodelanalysis
//import static hu.bme.mit.inf.dslreasoner.partialmodelanalysis.PostProcessCSV.*;
import org.eclipse.xtend.lib.annotations.Data
import java.io.BufferedReader
import java.io.FileReader
import java.util.LinkedList
import java.util.List
import java.util.Map

class RelationPostProcessCSV {
	def static void main(String[] args) {
		val res = loadFile("RelationStatistics.csv")
		val modelIDs = res.map[model].toSet.toList.sort
		val model2Step = modelToStep(modelIDs, res);
		//percentagesOfTypesByModel(res, modelIDs, model2Step)
		//percentagesOfTypesSummarized(res, modelIDs, model2Step)
		println("done")
	}
	
	def static loadFile(String path) {
		val BufferedReader reader = new BufferedReader(new FileReader(path))
		reader.readLine // header
		val res = new LinkedList
		var line = reader.readLine
		while(line!==null) {
			res += new RelLine(line)
			line = reader.readLine
		}
		return res
	}
	
	def static percetage2Step(int model, double percentage, Map<Integer, Integer> model2MaxStep) {
		val maxStep = model2MaxStep.get(model)
		return (percentage*maxStep) as int
	}
	
	def static modelToStep(List<Integer> modelIDs, LinkedList<RelLine> data) {
		modelIDs.toInvertedMap[id | data.filter[it.run == 1 && it.model == id].map[it.step].max]
	}
}


	

@Data class RelLine {
	int model
	int run
	int step
	
	int[] outgoing
	
	new(String line) {
		val cells = line.split(",")
		model = Integer.parseInt(cells.get(0))
		run = Integer.parseInt(cells.get(1))
		step = Integer.parseInt(cells.get(2))
		
		outgoing = cells.subList(2,cells.size).map[Integer.parseInt(it)]
	}
}