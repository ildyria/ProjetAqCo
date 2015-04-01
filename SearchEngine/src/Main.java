import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.TopDocs;

import Bench.Benchmark;
import Indexing.IndexFiles;
import Indexing.SearchFiles;


public class Main {

	public static void main(String[] args) throws Exception{
		//Choisir l'implémentation d'analyzer voulue ici
		//TODO : faire un choix en commande à la limite ? pour vendredi (démo et rendu code)
		Analyzer analyzerImpl = new StandardAnalyzer();
		IndexFiles indexing = new IndexFiles(analyzerImpl);
		indexing.run();
		
		SearchFiles searching = new SearchFiles(analyzerImpl);
		Map<Integer,String> queries = searching.getAllQueries();
		System.out.println(queries.size());
		
		Map<Integer, TopDocs> queryResults = searching.executeAllQueries(queries);
		Benchmark bench = new Benchmark(queryResults);
		
		System.out.println(bench.toString());
	}
}
