package hu.bme.mit.inf.dslreasoner.partialmodelanalysis

import java.io.BufferedReader
import java.io.FileReader
import org.eclipse.xtend.lib.annotations.Data

class PostProcessCSV {
	def static void main(String[] args) {
		
	}
	
	def loadFile(String path) {
		val BufferedReader file = new BufferedReader(new FileReader(path))
		
	}
}
@Data class Line {
	int model
	int run
	int step
	
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
		val cells = line.split(',')
		model = Integer.getInteger(cells.get(0)) 
		run = Integer.getInteger(cells.get(1))
		step = Integer.getInteger(cells.get(2))
		
		CompositeElement = Integer.getInteger(cells.get(3))
		Entry = Integer.getInteger(cells.get(4))
		Exit = Integer.getInteger(cells.get(5))
		FinalState = Integer.getInteger(cells.get(6))
		Pseudostate = Integer.getInteger(cells.get(7))
		Region = Integer.getInteger(cells.get(8))
		RegularState = Integer.getInteger(cells.get(9))
		State = Integer.getInteger(cells.get(10))
		Statechart = Integer.getInteger(cells.get(11))
		Synchronization = Integer.getInteger(cells.get(12))
		Transition = Integer.getInteger(cells.get(13))
		Vertex = Integer.getInteger(cells.get(14))	
	}
}