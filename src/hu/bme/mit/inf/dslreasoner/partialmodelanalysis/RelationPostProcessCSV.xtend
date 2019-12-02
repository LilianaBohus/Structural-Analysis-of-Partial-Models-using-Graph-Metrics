package hu.bme.mit.inf.dslreasoner.partialmodelanalysis
//import static hu.bme.mit.inf.dslreasoner.partialmodelanalysis.PostProcessCSV.*;
import org.eclipse.xtend.lib.annotations.Data
import java.io.BufferedReader
import java.io.FileReader
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.io.BufferedWriter
import java.io.FileWriter
import java.util.HashMap
import org.eclipse.xtext.xbase.lib.Functions.Function1

class RelationPostProcessCSV {
	def static void main(String[] args) {
		val res = loadFile("RelationStatistics.csv")
		val modelIDs = res.map[model].toSet.toList.sort
		val model2Step = modelToStep(modelIDs, res);
		percentagesOfTypesSummarized(res, modelIDs, model2Step)
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
	
	def static percentagesOfTypesSummarized(LinkedList<RelLine> data, List<Integer> modelIDs, Map<Integer, Integer> model2MaxStep){
		val BufferedWriter writer = new BufferedWriter(new FileWriter("relationStatisticsByPercentageSummarized.csv"))
		writer.write('''percentage,«FOR i:0..15 SEPARATOR ","»low-Degree=«i»,medium-Degree=«i»,high-Degree=«i»«ENDFOR»''')
		writer.newLine
		
		println("Caching")
		val cache = new HashMap<Pair<Integer,Integer>,List<RelLine>>
		for(line:data) {
			val key = line.model->line.step
			val list = if(!cache.containsKey(key)) {
				val l = new LinkedList
				cache.put(key,l)
				l
			} else {
				cache.get(key)
			}
			list.add(line)
		}
		println("caching finished")
		
		val int maxSteps = 100
		var int currentStep = 0
		while(currentStep <= maxSteps) {
			println('''«currentStep»%''')
			val percentage = (currentStep+0.0)/maxSteps
			
			val differentRunsSeparately = new LinkedList
			for(model : modelIDs) {
				val expectedStep = percetage2Step(model,percentage,model2MaxStep)
				val differentRunForModel = cache.get(model->expectedStep)
				//data.filter[it.model == model && it.step == expectedStep].toList
				//println('''> «differentRunForModel.size»''')
				differentRunsSeparately.add(differentRunForModel)
			}
			val differentRuns = differentRunsSeparately.flatten
			println('''>«differentRuns.size»''')
			writer.write('''«percentage»,«FOR i:0..15 SEPARATOR ","»«differentRuns.allQuartile(i)»«ENDFOR»'''
			)
			writer.newLine()
			writer.flush()		
			currentStep++
		}
//		}
	}
	
	def static allQuartile(Iterable<RelLine> differentRuns, int selector) {
		'''«differentRuns.getPercentileData(selector,0.25)»,«differentRuns.getPercentileData(selector,0.5)»,«differentRuns.getPercentileData(selector,0.75)»'''
	}
//	def static getAverageData(Iterable<Line> differentRuns, Function1<Line,Integer> selector) {
//		differentRuns.map[(selector.apply(it)+0.0)/size].reduce[p1, p2|p1+p2]/differentRuns.size
//	}
	def static getPercentileData(Iterable<RelLine> differentRuns, int selector, double percentile) {
		val data = differentRuns.map[(it.outgoing.get(selector)+0.0)/size]
		return data.sort.get((percentile*(data.size-1)) as int)
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
	
	def int getSize() {
		outgoing.reduce[x,y|x+y]
	}
	
	new(String line) {
		val cells = line.split(",")
		model = Integer.parseInt(cells.get(0))
		run = Integer.parseInt(cells.get(1))
		step = Integer.parseInt(cells.get(2))
		
		outgoing = cells.subList(2,cells.size).map[Integer.parseInt(it)]
	}
}