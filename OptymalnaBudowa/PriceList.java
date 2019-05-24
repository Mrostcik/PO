//Klasa cennnika trzymająca dostępne do kupienia pręty.
public class PriceList {
	private Pole[] polesAvailable;
	
	public PriceList(Pole[] poles) {
		this.polesAvailable = new Pole[poles.length];
		
		for(int i = 0; i < poles.length; i++) {
			polesAvailable[i] = poles[i];
		}
	}
	
	//Gettery.
	public int getSize() {
		return polesAvailable.length;
	}
	
	public Pole getPole(int i) {
		return polesAvailable[i];
	}
}

