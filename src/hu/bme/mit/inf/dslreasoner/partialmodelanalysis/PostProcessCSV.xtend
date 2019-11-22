package hu.bme.mit.inf.dslreasoner.partialmodelanalysis

import java.io.BufferedReader
import java.io.FileReader
import java.util.LinkedList
import java.util.List
import java.util.Map
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtext.xbase.lib.Functions.Function1

class PostProcessCSV {
	def static void main(String[] args) {
		val res = loadFile("typeStatistics_AllSeed.csv")
		println('''Read «res.size» lines of data''')
		val modelIDs = res.map[model].toSet.toList.sort
		val model2Step = modelToStep(modelIDs,res);
		println('''steps sizes determined''')
		percentagesOfTypes(res,modelIDs,model2Step)
	}
	
	def static loadFile(String path) {
		val BufferedReader reader = new BufferedReader(new FileReader(path))
		println("Header:")
		println(reader.readLine)
		val res = new LinkedList
		var line = reader.readLine
		while(line!==null) {
			res += new Line(line)
			line = reader.readLine
		}
		return res
	}
	
	def static percentagesOfTypes(LinkedList<Line> data, List<Integer> modelIDs, Map<Integer, Integer> model2MaxStep) {
		println('''model,percentage,classes...''')
		for(model : modelIDs) {
			val dataWithModel = data.filter[it.model == model].toList
			val int maxSteps = 100
			var int currentStep = 0
			while(currentStep <= maxSteps) {
				val percentage = (currentStep+0.0)/maxSteps 
				val expectedStep = percetage2Step(model,percentage,model2MaxStep) 
				
				val differentRuns = dataWithModel.filter[it.step == expectedStep]
				//println(">" + differentRuns.size)
				println('''«model»,«percentage»,«
					differentRuns.getAverageData[choice]»,«
					differentRuns.getAverageData[entry]»,«
					differentRuns.getAverageData[exit]»,«
					differentRuns.getAverageData[region]»,«
					differentRuns.getAverageData[state]»,«
					differentRuns.getAverageData[statechart]»,«
					differentRuns.getAverageData[transition]»'''
				)
				
				
				currentStep++
			}
		}
	}
	
	//differentRuns.map[(choice+0.0)/size].reduce[p1, p2|p1+p2]/differentRuns.size
	
	def static getAverageData(Iterable<Line> differentRuns, Function1<Line,Integer> selector) {
		differentRuns.map[(selector.apply(it)+0.0)/size].reduce[p1, p2|p1+p2]/differentRuns.size
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
	
	public new(String line) {
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