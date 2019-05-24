import java.util.LinkedList;

public class EconomicalStrategy extends OptimizingStrategy {
	
	//Konstruktor tworzący kolejkę z jednym pustym stanem.
	//Najlepsze dotychczasowe koszty i odpady = Infinity.
	EconomicalStrategy(){
		cost = Long.MAX_VALUE;
		waste = Long.MAX_VALUE;
		states = new LinkedList<State>();
		states.add(new State());
		bestState = null;
	}
	
	public void buy(PriceList priceList, Project project) {
		
		//Dopóki kolejka stanów nie jest pusta.
		while(!states.isEmpty()) {
			//Bierzemy stan z przodu kolejki.
			State state = states.poll();
			
			//Jeżeli w danym stanie zostały już obcięte wyszystkie odcinki ||lub
			//zakupione pręty w danym stanie są zachłannie wystarczające.
			//Wtedy gdy koszt jest mniejszy,
			//poprawiamy najlepsze dotychczasowe: stan, koszt i odpady.
			if(state.getNumber() == project.getSize() || state.isEnough(project) ) {
				if(state.calculateCost() <= cost) {
					cost = state.calculateCost();
					waste = state.calculateWaste();
					bestState = state;
				}
			}
			//W przeciwnym wypadku generujemy z danego stanu nowe stany.
			else if(state.getNumber() < project.getSize()){
				
				//Nowy stan = obcinamy z j-tego pręta z danego stanu odpowiedni odcinek.
				for(int j = 0; j < state.getSize(); j++) {
					Pole pole = state.getPole(j);
					
					if(pole.getRemainingLength() >= project.getLength(state.getNumber())) {
						Pole newPole = new Pole(pole);
						newPole.cut(project.getLength(state.getNumber()));
						State newState = new State(state);
						newState.increaseNumber();
						newState.modifyPole(j, newPole);
						states.add(newState);
					}
				}
				
				//Nowy stan = dokupujemy j-ty pręt z cennika i obcinamy z niego odpowiedni odcinek.
				for(int j = 0; j < priceList.getSize(); j++) {
					Pole pole = priceList.getPole(j);
					
					if(pole.getRemainingLength() >= project.getLength(state.getNumber()) && state.calculateCost()+pole.getPrice() <= cost) {
						Pole newPole = new Pole(pole);
						newPole.cut(project.getLength(state.getNumber()));
						State newState = new State(state);
						newState.increaseNumber();
						newState.addPole(newPole);
						states.add(newState);
					}				
				}			
			}	
		}
		
		//Jeżeli najlepszy stan był wybrany zachłannie przez state.isEnough(),
		//to musimy go poprawić.
		if(bestState.getNumber() < project.getSize())
			bestState.divide(project);
		
		bestState.raport();
	}

}
