
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;

public class Indexer {
	private static HashSet<String> set = new HashSet<String>();
	  @SuppressWarnings("unchecked")
	public static void main(String[] args) throws TikaException, IOException {
	  	File file = new File(System.getProperty("user.home") +"/DirectionsListUniquedsddsadas");
	  	file.createNewFile();	
	  	try {
		    FileInputStream fileIn = new FileInputStream(System.getProperty("user.home")+"/DirectionsListUniquedsddsadas");
		    ObjectInputStream in = new ObjectInputStream(fileIn);
		    set = (HashSet<String>) in.readObject();
		    in.close();
		    fileIn.close();
		} catch(EOFException y) {
		} catch (IOException i) {
		    i.printStackTrace();
		    return;
		} catch (ClassNotFoundException c) {
		    System.out.println("Employee class not found");
		    c.printStackTrace();
		    return;
		}
		String indexPath = System.getProperty("user.home")+"/index";
		String docsPath = null;
		String deletePath = null;
		boolean delete = false;
		boolean deleteDir = false;
		boolean add = false;
		boolean list = false;
		boolean reindex = false;
		for(int i=0;i<args.length;i++) {
			if ("--add".equals(args[i])) {
				docsPath = args[i+1];
		    	i++;
		    	add = true;
		    }  
		    else if ("--purge".equals(args[i])) {
		    	delete = true;
		    } 
		    else if("--rm".equals(args[i])) {
		    	deleteDir = true;
		    	deletePath = args[i+1];
		    	i++;
		    } 
		    else if("--list".equals(args[i])) {
		    	list = true;
		    }
		    else if("--reindex".equals(args[i])) {
		    	reindex = true;
		    }
		}

		Path docDir = null;
		if(add == true) {
			if (docsPath == null) {
				System.out.println("--add argument not given");
		    	return;
		    }
			docDir = Paths.get(docsPath);
			if (!Files.isReadable(docDir)) {
				System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
			    return;
			}
		}
		    
		IndexWriter writer;
		    
		try {
			Directory dir = FSDirectory.open(Paths.get(indexPath));
		    Map<String, Analyzer> analyzerMap = new HashMap<>();
		    analyzerMap.put("plcontent", new PolishAnalyzer());
		    analyzerMap.put("encontent", new EnglishAnalyzer());
		    analyzerMap.put("path", new KeywordAnalyzer());
		    PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
		 
		    IndexWriterConfig iwc = new IndexWriterConfig(wrapper);
		    iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		    writer = new IndexWriter(dir, iwc);
		      
		    if(delete == true) {
		    	purge(writer);
		    	set.clear();
		    }
		    else if(deleteDir == true) {
		    	deleteByTerm(writer, "id", deletePath );
		    	set.remove(Paths.get(deletePath).normalize().toString());
		    }
		    else if(add == true) {
		    	indexDocs(writer, docDir);
		    	set.add(docDir.normalize().toString());
		    }
		    else if(list == true) {
		    	list();
		    }
		    else if(reindex == true) {
		    	reindex(writer);
		    }
		      
		    if(delete || deleteDir || add || list || reindex)
		    	writer.close();
		      
		    try {
		        FileOutputStream fileOut =
		        new FileOutputStream(System.getProperty("user.home")+ "/DirectionsListUniquedsddsadas");
		        ObjectOutputStream out = new ObjectOutputStream(fileOut);
		        out.writeObject(set);
		        out.close();
		        fileOut.close();
		    } catch (IOException i) {
		    }
		    
		    if(delete || deleteDir || add || list || reindex)
		    	return;
		      
		    WatchDir watcher = new WatchDir();
		      
			for(String element: set) {
				watcher.registerAll(Paths.get(element));
			}
			  
			watcher.processEvents(writer);
		    } catch (IOException e) {
		    	System.out.println(" caught a " + e.getClass() +
		    	"\n with message: " + e.getMessage());
		    	return;
		    }    
		  }
	static void indexDocs(final IndexWriter writer, Path path) throws IOException, TikaException {
		String pathString = path.toString();
		if (Files.isDirectory(path)) {
		    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		    	@Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		        try {
		            indexDoc(pathString, writer, file);
		        } catch (IOException | TikaException ignore) {
		        	System.out.println("Document cannot be indexed");
		        }
		          return FileVisitResult.CONTINUE;
		        }
		      });
		} else {
		    indexDoc(pathString, writer, path);
		  }
		  }
	  
	static void indexDoc(String pathString, IndexWriter writer, Path file) throws IOException, TikaException {
		File filex = new File(file.toString());		     
		Tika tika = new Tika();
		String text = tika.parseToString(filex);

		OptimaizeLangDetector detect = new OptimaizeLangDetector();
		detect.loadModels();
		detect.addText(text.toCharArray(),0, text.length());
		        
		Document doc = new Document();	
		if(detect.detectAll().get(0).getLanguage().equals("pl"))
			doc.add(new TextField("plcontent", text, Field.Store.YES));
		else
			doc.add(new TextField("encontent", text, Field.Store.YES));
		      
		doc.add(new StringField("fileName", file.getFileName().toString(), Field.Store.YES));
		doc.add(new StringField("id", pathString, Field.Store.YES));
		doc.add(new StringField("path", file.normalize().toString(), Field.Store.YES));
		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
		    writer.addDocument(doc);
		} else {
			writer.updateDocument(new Term("path", file.normalize().toString()), doc);
		}   
	}
	  
	static void purge(IndexWriter writer) throws IOException{
		writer.deleteAll();
		writer.commit();
	}
	  
	static void deleteByTerm(IndexWriter writer, String field, String term) throws IOException {
		writer.deleteDocuments(new Term(field, term));
		set.remove(Paths.get(term).normalize().toString());
	}
	  
	static void deleteDocs(final IndexWriter writer, Path path) throws IOException {
		set.remove(path.normalize().toString());
		if (Files.isDirectory(path)) {
		    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
		    	@Override
		        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		        try {
		            deleteByTerm(writer, "path", file.normalize().toString());
		        } catch (IOException ignore) {
		        	System.out.println("Document cannot be deleted");
		        }
		          return FileVisitResult.CONTINUE;
		        }
		      });
		    } else {
	            deleteByTerm(writer, "path", path.normalize().toString());
		    }
	}
	  
	static void list() {
		for(String element: set) {
			System.out.println(element);
		}
	}
	  
	static void reindex(IndexWriter writer) throws IOException, TikaException{
		purge(writer);
		for(String element: set) {
			indexDocs(writer, Paths.get(element).toAbsolutePath());
		}
	}
}
