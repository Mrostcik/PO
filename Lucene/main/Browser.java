
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.jline.builtins.Completers;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Browser {
	
	public static void main(String[] args) throws InvalidTokenOffsetsException {
		try (Terminal terminal = TerminalBuilder.builder()
			.jna(false)
			.jansi(true)
			.build()) {
			LineReader lineReader = LineReaderBuilder.builder()
				.terminal(terminal)
				.completer(new Completers.FileNameCompleter())
				.build();
			
			String language = "encontent";
			boolean details = false;
			int n = Integer.MAX_VALUE;
			boolean color = false;
			int searchType = 0;
			while (true) {
				String line = null;
				try {
					line = lineReader.readLine("> ");
					if(line.equals("%lang en")) {
						language = "encontent";
					}
					else if(line.equals("%lang pl")) {
						language = "plcontent";
					}
					else if(line.equals("%details on")) {
						details = true;
					}
					else if(line.equals("%details off")) {
						details = false;
					}
					else if(line.equals("%color on")) {
						color = true;
					}
					else if(line.equals("%color off")) {
						color = false;
					}
					else if(line.equals("%term")) {
						searchType = 0;
					}
					else if(line.equals("%phrase")) {
						searchType = 1;
					}
					else if(line.equals("%fuzzy")) {
						searchType = 2;
					}
					else if((StringUtils.substring(line, 0, 6)).equals("%limit")) {
						String x = StringUtils.substring(line, 7);
						int number = Integer.parseInt(x);
						if (number == 0)
							n = Integer.MAX_VALUE;
						else
							n = number;
					}
					else {
						Analyzer analyzer = null;
						if(language.equals("plcontent"))
							analyzer = new PolishAnalyzer();
						else
							analyzer = new EnglishAnalyzer();
						TokenStream stream = analyzer.tokenStream("", line);
						CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
						stream.reset();
						line = "";
						while (stream.incrementToken()) {
							if(searchType == 1)
								line +=  charTermAttribute.toString() + " ";
							else
								line = charTermAttribute.toString();
						}
						stream.close();
						System.out.println(line);
						IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(System.getProperty("user.home")+"/index")));
					    IndexSearcher searcher = new IndexSearcher(reader);
				        SimpleHTMLFormatter formatter = null;
				        if(color == true)
				        	formatter = new SimpleHTMLFormatter("\033[31m", "\033[0m");
				        else
				        	formatter = new SimpleHTMLFormatter();;
				        
				        Highlighter highlighter = null;
					    TopDocs docs = null;
					    String[] fragments = null;
						if(searchType == 0) {
							TermQuery query = new TermQuery(new Term(language, line));
						    QueryScorer scorer = new QueryScorer(query);
					        highlighter = new Highlighter(formatter, scorer);
							docs = searcher.search(query, n);
						}
						else if(searchType == 1) {
							String[] lineSplit = line.split("\\s+");
							PhraseQuery.Builder builder = new PhraseQuery.Builder();
							for(String word: lineSplit) {
								System.out.println(word + "x");
								builder.add(new Term(language, word));
							}
							PhraseQuery query = builder.build();
						    QueryScorer scorer = new QueryScorer(query);
					        highlighter = new Highlighter(formatter, scorer);
							docs = searcher.search(query, n);
						}
						else if(searchType == 2) {
							if(line.length() < 4)
								continue;
							FuzzyQuery query = new FuzzyQuery(new Term(language, line));
							docs = searcher.search(query, n);
						    QueryScorer scorer = new QueryScorer(query);
					        highlighter = new Highlighter(formatter, scorer);
						}
					    ScoreDoc[] hits = docs.scoreDocs;
					    for(int i = 0; i<hits.length; i++) {
					    	System.out.println(reader.document(hits[i].doc).getValues("path")[0]);
					    if(details == true) {
					    	int docid = hits[i].doc;
					    	Document doc = searcher.doc(docid);
					    	String text = doc.get(language);
					    	TokenStream stream3 = analyzer.tokenStream(language, new StringReader(text));
					    	fragments = highlighter.getBestFragments(stream3, text, 5);
					    	for(String frag: fragments) {
					    		System.out.print(frag + ".....");
					    	}
					    System.out.println();
					    }
					    }
						
						analyzer.close();
					}
				} catch (UserInterruptException e) {
					break;
				} catch (EndOfFileException e) {
					break;
				}
			}
		} catch (IOException e) {
		}

	}
	
}
