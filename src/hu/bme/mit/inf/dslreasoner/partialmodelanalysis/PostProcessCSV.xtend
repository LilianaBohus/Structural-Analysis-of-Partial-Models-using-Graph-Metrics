package hu.bme.mit.inf.dslreasoner.partialmodelanalysis

import java.io.BufferedReader
import java.io.FileReader
import java.util.LinkedList
import java.util.List
import java.util.Map
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.xbase.lib.Functions.Function1
import java.io.BufferedWriter
import java.io.FileWriter
import java.util.HashSet
import java.util.HashMap

class PostProcessCSV {
	def static void main(String[] args) {
		val res = loadFile("typeStatistics_AllSeed.csv")
		val modelIDs = res.map[model].toSet.toList.sort
		val model2Step = modelToStep(modelIDs, res);
		//percentagesOfTypesByModel(res, modelIDs, model2Step)
		percentagesOfTypesSummarized(res, modelIDs, model2Step)
		println("done")
	}
	
	
	def static loadFile(String path) {
		val BufferedReader reader = new BufferedReader(new FileReader(path))
		reader.readLine // header
		val res = new LinkedList
		var line = reader.readLine
		while(line!==null) {
			res += new Line(line)
			line = reader.readLine
		}
		return res
	}
	
	def static header(String name) {
		'''low-«name»,median-«name»,high-«name»'''
	}
	
	def static percentagesOfTypesByModel(LinkedList<Line> data, List<Integer> modelIDs, Map<Integer, Integer> model2MaxStep) {
		val BufferedWriter writer = new BufferedWriter(new FileWriter("typeStatisticsByPercentageByModel.csv"))
		writer.write('''model,percentage,«header("Choice")»,«header("Entry")»,«header("Exit")»,«header("Region")»,«header("State")»,«header("Statechart")»,«header("Transition")»''')
		writer.newLine()
		for(model : modelIDs) {
			val dataWithModel = data.filter[it.model == model].toList
			val int maxSteps = 100
			var int currentStep = 0
			while(currentStep <= maxSteps) {
				val percentage = (currentStep+0.0)/maxSteps 
				val expectedStep = percetage2Step(model,percentage,model2MaxStep) 
				
				val differentRuns = dataWithModel.filter[it.step == expectedStep]
				writer.write('''«model»,«percentage»,«
					differentRuns.allQuartile[choice]»,«
					differentRuns.allQuartile[entry]»,«
					differentRuns.allQuartile[exit]»,«
					differentRuns.allQuartile[region]»,«
					differentRuns.allQuartile[state]»,«
					differentRuns.allQuartile[statechart]»,«
					differentRuns.allQuartile[transition]»'''
				)
				writer.newLine()
				writer.flush()	
				currentStep++
			}
		}
	}
	
	def static percentagesOfTypesSummarized(LinkedList<Line> data, List<Integer> modelIDs, Map<Integer, Integer> model2MaxStep){
		val BufferedWriter writer = new BufferedWriter(new FileWriter("typeStatisticsByPercentageSummarized.csv"))
		writer.write('''percentage,«header("Choice")»,«header("Entry")»,«header("Exit")»,«header("Region")»,«header("State")»,«header("Statechart")»,«header("Transition")»''')
		writer.newLine
		
		println("Caching")
		val cache = new HashMap<Pair<Integer,Integer>,List<Line>>
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
			writer.write('''«percentage»,«
				differentRuns.allQuartile[choice]»,«
				differentRuns.allQuartile[entry]»,«
				differentRuns.allQuartile[exit]»,«
				differentRuns.allQuartile[region]»,«
				differentRuns.allQuartile[state]»,«
				differentRuns.allQuartile[statechart]»,«
				differentRuns.allQuartile[transition]»'''
			)
			writer.newLine()
			writer.flush()		
			currentStep++
		}
//		}
	}
	

	
	//differentRuns.map[(choice+0.0)/size].reduce[p1, p2|p1+p2]/differentRuns.size
	def static allQuartile(Iterable<Line> differentRuns, Function1<Line,Integer> selector) {
		'''«differentRuns.getPercentileData(selector,0.25)»,«differentRuns.getPercentileData(selector,0.5)»,«differentRuns.getPercentileData(selector,0.75)»'''
	}
	def static getAverageData(Iterable<Line> differentRuns, Function1<Line,Integer> selector) {
		differentRuns.map[(selector.apply(it)+0.0)/size].reduce[p1, p2|p1+p2]/differentRuns.size
	}
	def static getPercentileData(Iterable<Line> differentRuns, Function1<Line,Integer> selector, double percentile) {
		val data = differentRuns.map[(selector.apply(it)+0.0)/size]
		return data.sort.get((percentile*(data.size-1)) as int)
	}
	
	def static percetage2Step(int model, double percentage, Map<Integer, Integer> model2MaxStep) {
		val maxStep = model2MaxStep.get(model)
		return (percentage*maxStep) as int
	}
	
	def static modelToStep(List<Integer> modelIDs, LinkedList<Line> data) {
		modelIDs.toInvertedMap[id | data.filter[it.run == 1 && it.model == id].map[it.step].max]
	}
}
@Data class Line {
	int model
	int run
	int step
	
	int Choice
	int CompositeElement
	int Entry
	int Exit
	int FinalState
	int Pseudostate
	int Region
	int RegularState
	int State
	int Statechart
	int Synchronization
	int Transition
	int Vertex
	
	new(String line) {
		val cells = line.split(",")
		model = Integer.parseInt(cells.get(0)) 
		run = Integer.parseInt(cells.get(1))
		step = Integer.parseInt(cells.get(2))
		
		Choice = Integer.parseInt(cells.get(3))
		CompositeElement = Integer.parseInt(cells.get(4))
		Entry = Integer.parseInt(cells.get(5))
		Exit = Integer.parseInt(cells.get(6))
		FinalState = Integer.parseInt(cells.get(7))
		Pseudostate = Integer.parseInt(cells.get(8))
		Region = Integer.parseInt(cells.get(9))
		RegularState = Integer.parseInt(cells.get(10))
		State = Integer.parseInt(cells.get(11))
		Statechart = Integer.parseInt(cells.get(12))
		Synchronization = Integer.parseInt(cells.get(13))
		Transition = Integer.parseInt(cells.get(14))
		Vertex = Integer.parseInt(cells.get(15))	
	}
	
	def getSize() {
		return Entry + Exit + Region + Transition + Statechart + State + Choice
	}
}