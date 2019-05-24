//Klasa strategii minimalistycznej.
public class MinimalisticStrategy extends Strategy {

    public MinimalisticStrategy(){};
    
    //Tworzymy stan początkowy i odcinamy po kolei potrzebne odcinki.
    //Jeżeli niemożlwie to zakupujemy pręt zgodnie z strategią minimalistyczną.
	public void buy(PriceList priceList, Project project) {
		State state = new State();
		int index = priceList.getSize() - 1;
		
		for(int i = project.getSize() - 1; i >= 0; i--) {
			if(!state.cutMinMax(project.getLength(i))) {
				while(priceList.getPole(index).getLength() >= project.getLength(i)) {
					index--;
					
					if(index<0)break;
				}
				
				index++;
				
				Pole newPole = new Pole(priceList.getPole(index).getLength(), priceList.getPole(index).getPrice(), project.getLength(i));			
				state.addPole(newPole);
				
			}
		}

		state.raport();
	}

}
