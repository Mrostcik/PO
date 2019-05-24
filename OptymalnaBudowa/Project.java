//Klasa projektu trzymająca potrzebne odcinki oraz całkowitą sumę ich długości.
//Suma ta jest potrzebna w celach optymalizacji strategii ekologicznej.
public class Project {
	private int[] lengthsNeeded;
	private long sum;
	
	public Project(int[] lengths) {
		lengthsNeeded = new int[lengths.length];
		
		for(int i = 0; i < lengthsNeeded.length; i++) {
			lengthsNeeded[i] = lengths[i];
			sum += lengths[i];
		}
	}
	
	//Gettery.
	public long getSum() {
		return sum;
	}
	
	public int getLength(int i){
		return lengthsNeeded[i];
	}
	
	public int getSize() {
		return lengthsNeeded.length;
	}

}
