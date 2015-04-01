package Bench;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import Indexing.*;

public class Benchmark {

	 public static void main(String[] args) throws Exception{
		//Choisir l'implémentation d'analyzer voulue ici
		//TODO : faire un choix en commande à la limite ? pour vendredi (démo et rendu code)
		Analyzer analyzerImpl = new StandardAnalyzer();
		
		IndexFiles indexing = new IndexFiles(analyzerImpl);
		indexing.run();
		
		SearchFiles searching = new SearchFiles(analyzerImpl);
		Map<Integer,String> queries = searching.getAllQueries();
		System.out.println(queries.size());
		searching.executeAllQueries(queries);
		
	}
}
