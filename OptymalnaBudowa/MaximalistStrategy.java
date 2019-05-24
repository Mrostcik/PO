public class MaximalistStrategy extends Strategy {

	MaximalistStrategy(){};
	
	//Tworzymy stan początkowy i odcinamy po kolei potrzebne odcinki.
    //Jeżeli niemożlwie to zakupujemy pręt zgodnie z strategią maksymalistyczną.
	public void buy(PriceList priceList, Project project) {
		State state = new State();
		int index = priceList.getSize() - 1;
		
		for(int i = project.getSize() - 1; i >= 0; i--) {
			if(!state.cutMinMax(project.getLength(i))) {
				
				Pole newPole = new Pole(priceList.getPole(index).getLength(), priceList.getPole(index).getPrice(), project.getLength(i));			
				state.addPole(newPole);
				
			}
		}

		state.raport();
	}

}
