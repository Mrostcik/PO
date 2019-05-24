import java.util.Scanner;

public class MainClass {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		int C = scan.nextInt();
		Pole[] poles = new Pole[C];
		
		for(int i = 0; i < C; i++) {
			int length = scan.nextInt();
			int price = scan.nextInt();
			
			poles[i] = new Pole(length, price);
		}
		
		PriceList priceList = new PriceList(poles);
		
		int P = scan.nextInt();
		int[] lengths = new int[P];
		
		for(int i = 0; i < P; i++) {
			int length = scan.nextInt();
			
			lengths[i] = length;
		}
		
		Project project = new Project(lengths);
		
		Strategy strategy = null;
		scan.nextLine();
		String s = scan.nextLine();
		
		if(s.equals("maksymalistyczna")) {
			strategy = new MaximalistStrategy();
		}
		else if(s.equals("minimalistyczna")) {
			strategy = new MinimalisticStrategy();
		}
		else if(s.equals("ekonomiczna")) {
			strategy = new EconomicalStrategy();
		}
		else if(s.equals("ekologiczna")) {
			strategy = new EcologicalStrategy();
		}
		
		strategy.buy(priceList, project);
		
		scan.close();
	}

}
