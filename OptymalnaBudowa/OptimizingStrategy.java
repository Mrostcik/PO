import java.util.Queue;

//Klasa abstrakcyjna dla strategii optymalizacyjnych.
//Trzyma najlepsze dotychczasowe: koszt i odpady.
//Trzyma kolejkę stanów oraz najlepszy dotychczasowy stan.
public abstract class OptimizingStrategy extends Strategy {
	protected long cost;
	protected long waste;
	protected Queue<State> states;
	protected State bestState;
	
	public abstract void buy(PriceList priceList, Project project) ;

	

}
