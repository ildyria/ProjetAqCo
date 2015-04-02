import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import Bench.Benchmark;
import Indexing.IndexFiles;
import Indexing.SearchFiles;


public class Main {

	public static void main(String[] args) throws Exception{
		//Choisir l'implémentation d'analyzer voulue ici
		//TODO : faire un choix en commande à la limite ? pour vendredi (démo et rendu code)
//Analyzer analyzerImpl = new StandardAnalyzer();
		
//		Analyzer analyzerImpl = CustomAnalyzer.builder(Paths.get("CISIDonnees"))
//				   .withTokenizer("classic")
//				   .addTokenFilter("standard")
//				   .addTokenFilter("lowercase")
//				   .addTokenFilter("stop", "ignoreCase", "false", "words", "motsvides.txt", "format", "wordset")
//				   .build();
		Analyzer analyzerImpl = CustomAnalyzer.builder(Paths.get("CISIDonnees"))
				   .withTokenizer("standard")
//				   .addTokenFilter("standard")
//				   .addTokenFilter("lowercase")
//				   .addTokenFilter("stop", "ignoreCase", "false", "words", "motsvides.txt", "format", "wordset")
//				   .addTokenFilter("SnowBallPorter","language", "English")
//				  // .addTokenFilter("removeduplicates")
				   .build();
				
		
		IndexFiles indexing = new IndexFiles(analyzerImpl);
		indexing.run();
		
		SearchFiles searching = new SearchFiles(analyzerImpl);
		Map<Integer,String> queries = searching.getAllQueries();
		System.out.println("Taille queries" + queries.size());
		
		Map<Integer, TopDocs> queryResults = searching.executeAllQueries(queries);
		searching.displayResults(queryResults);
		System.out.println("-----------");
		System.out.println("-----------");
		System.out.println("-----------");
		Map<Integer, List<ScoreDoc>> queryFilteredResults = searching.applyThreshold(queryResults, (float)0.2);
		searching.displayFilteredResults(queryFilteredResults);
		Benchmark bench = new Benchmark(queryFilteredResults);
		System.out.println("Total results : ");
		System.out.println(bench.toString());
	}
}
