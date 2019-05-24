import java.util.ArrayList;

/*Klasa pręta trzymająca jego długość produkcyjną, cenę,
 * odcinki, na które jest w danym momencie podzielony oraz pozostałą długość.
 */
public class Pole {
    private int length;
    private int price;
    private ArrayList<Integer> division;
    private int remainingLength;
    
    //Konstruktor używany do stworzenia prętów do cennika.
    public Pole(int length, int price) {
    	this.length = length;
    	this.price = price;
    	division = new ArrayList<Integer>();
    	remainingLength = length;
    }
    
    //Konstruktor używany w strategii maksymalistycznej i minimalistycznej.
    public Pole(int length, int price, int usedLength) {
    	this.length = length;
    	this.price = price;
    	division = new ArrayList<Integer>();
    	division.add(usedLength);
    	remainingLength = length - usedLength;
    }
    
    //Konstruktor używany w strategiach optymalizacyjnych.
    //Pozwala on na na łatwe tworzenie nowych stanów - sposobów obcinania prętów.
    public Pole(Pole anotherPole) {
    	this.length = anotherPole.length;
    	this.price = anotherPole.price;
    	this.remainingLength = anotherPole.remainingLength;
    	division = new ArrayList<Integer>(anotherPole.division);
    }
    
    //Gettery.
    public int getLength() {
    	return length;
    }
    
    public int getPrice() {
    	return price;
    }
    
    public int getRemainingLength() {
    	return remainingLength;
    }
    
    //Jeżeli możliwe obcina z pręta zadaną długość.
    //Zwraca informację, czy nastąpiło obcięcie.
    public boolean cut(int x) {
       if(remainingLength >= x) {
    	   remainingLength -= x;
    	   division.add(x);
    	   return true;
       }
       
       return false;
    }
    
    //Standardowa metoda - zwraca napis - podział pręta na odcinki.
    public String toString() {
    	String x = "";
    	x += length;
    	
    	for(int i = 0; i<division.size(); i++) {
    		x+=" " + division.get(i);
    	}
    	
    	return x;
    }
}
