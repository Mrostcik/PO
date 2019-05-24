import java.util.ArrayList;

//Klasa stanu - sposobu zakupu i podziału prętów.
//Trzyma kupione pręty i liczbę już obciętych odcinków z projektu.
public class State {
	private ArrayList<Pole> polesBought;
	private int numberCut;
	
	//Konstruktor użyty do utworzenia stanu początkowego.
	public State() {
		polesBought = new ArrayList<Pole>();
		numberCut = 0;
	}
	
	//Konstruktor używany do kreowania kolejnych stanów.
	public State(State anotherState) {
		this.polesBought = new ArrayList<Pole>(anotherState.polesBought);
		this.numberCut = anotherState.numberCut;
	}
	
	//Gettery.
	public int getNumber() {
		return numberCut;
	}
	
	
	public int getSize() {
		return polesBought.size();
	}
	
	public Pole getPole(int i) {
		return polesBought.get(i);
	}
	
	public long getSum() {
		long sum = 0;
		
		for(int i = 0; i < polesBought.size(); i++) {
			sum += polesBought.get(i).getLength();
		}
		
		return sum;
	}
	
	//Modyfikatory.
	//Dodaje pręt - kupuje go.
	public void addPole(Pole pole) {
		polesBought.add(pole);
	}
	
	//Zwiększamy liczbę wyciętych odcinków o 1.
	public void increaseNumber() {
		this.numberCut++;
	}
	
	//Zastępujemy pręt innym.
	//Metoda używana w strategiach optymalizacyjnych,
	//aby zastąpić pręt takim, z którego obcięliśmy jakiś odcinek.
	public void modifyPole(int i, Pole pole) {
		polesBought.set(i, pole);
	}
	
	//Metoda używana w strategiach zachłannych.
	//Jeżeli możliwe, odcina dany odcinek z pierwszego możliwego pręta.
	public boolean cutMinMax(int length) {
		boolean isCut = false;
		int index = 0;
		
		while(!isCut && index < polesBought.size()) {
			isCut = polesBought.get(index).cut(length);
			index++;
		}
		
		return isCut;
		
	}
	
	//Zachłannie sprawdzamy,
	//czy dany stan ma na pewno wystarczającą liczbę i długości zakupionych prętów,
	//tak aby wystarczyły na odcięcie wszystkich odcinków z projektu.
	//Metoda wykorzystana w celach optymalizacyjnych.
	public boolean isEnough(Project project) {
		int index = numberCut;
		
		for(int i = 0; i < polesBought.size(); i++) {
			int length = polesBought.get(i).getRemainingLength();
			
			while(length >= project.getLength(index)) {
				length -= project.getLength(index);
				index++;
				
				if(index == project.getSize())
					break;
			}
			
			if(index == project.getSize())
				break;
		}
		
		return index == project.getSize();
	}
	
	//Zachłannie dzielimy zakupione pręty z danego stanu na odcinki.
	//Wykorzystana, jeżeli, nasz najlepszy stan został wyznaczony przez isEnough().
	public void divide(Project project) {
		int index = numberCut;
		
		for(int i = 0; i < polesBought.size(); i++) {
			int length = polesBought.get(i).getRemainingLength();
			
			while(length >= project.getLength(index)) {
				length -= project.getLength(index);
				polesBought.get(i).cut(project.getLength(index));
				index++;
				
				if(index == project.getSize())
					break;
			}
			
			if(index == project.getSize())
				break;
		}
	}
	
	//Obliczamy koszt prętów z danego stanu.
	public long calculateCost() {
		long cost = 0;
		
		for(int i = 0; i < polesBought.size(); i++) {
			cost += polesBought.get(i).getPrice();
		}
		
		return cost;
	}
	
	//Obliczamy odpady z danego stanu.
	public long calculateWaste() {
		long waste = 0;
		
		for(int i = 0; i < polesBought.size(); i++) {
			waste += polesBought.get(i).getRemainingLength();
		}
		
		return waste;
	}
	
	//Wypisujemy na wyjście oczekiwany wynik.
	public void raport() {
		System.out.println(calculateCost());
		System.out.println(calculateWaste());
		for(int i = 0; i < polesBought.size(); i++) {
			System.out.println(polesBought.get(i).toString());
			
		}
		
	}
}
