import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer.Builder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import Bench.Benchmark;
import Indexing.IndexFiles;
import Indexing.SearchFiles;


public class Main {

	public static void main(String[] args) throws Exception{
		
		System.out.println("Bonjour");
		Thread.sleep(200);
		System.out.println("Bienvenue dans le Super Moteur de Recherche");
		Thread.sleep(200);
		System.out.println("Nous vous attendions !");
		Thread.sleep(200);
		System.out.println("Plusieurs choix vont s'offrir à vous, choisissez bien !");
		Thread.sleep(200);
		
		String tokenizer = "standard";
		List<List<String>> filters = new ArrayList<List<String>>();
			//Analyzer analyzerImpl = new StandardAnalyzer();
			/*Analyzer analyzerImpl = CustomAnalyzer.builder(Paths.get("CISIDonnees"))
					   .withTokenizer("standard")
					   .addTokenFilter("standard")
					   .addTokenFilter("lowercase")
					   .addTokenFilter("stop", "ignoreCase", "false", "words", "motsvides.txt", "format", "wordset")
					   .addTokenFilter("SnowBallPorter","language", "English")
					   .addTokenFilter("removeduplicates")
					   .build();*/
			
			//new builder
			Builder builder = CustomAnalyzer.builder(Paths.get("CISIDonnees"));
			System.out.println("Le tokenizer actuel est : " + tokenizer);
			System.out.println("Voulez vous le changer ? (y/n)");
			Scanner sc = new Scanner(System.in);
			String val = sc.nextLine();
			while(!val.equals("y") && !val.equals("n"))
			{
				System.out.println("essayez encore !");
				val = sc.nextLine();
			}
			if(val.equals("y")){
				System.out.println("Les tokenizers disponibles sont :");
				System.out.println(TokenizerFactory.availableTokenizers());
				System.out.println("Entrez celui que vous voulez");
				tokenizer = sc.nextLine();
			}
			sc.close();
			
			
			
			builder.withTokenizer(tokenizer);
			Analyzer analyzerImpl = builder.build();
			
			//Fin de construction de l'analyzer
			//Indexing
			IndexFiles indexing = new IndexFiles(analyzerImpl);
			indexing.run();
			//Querying
			SearchFiles searching = new SearchFiles(analyzerImpl);
			Map<Integer,String> queries = searching.getAllQueries();
	
			System.out.println("thre ; prcs ; rcll ; speci ; error ; false");
			
			for(int i = 20; i < 30; ++i)
			{
				Map<Integer, TopDocs> queryResults = searching.executeAllQueries(queries);
				Map<Integer, List<ScoreDoc>> queryFilteredResults = searching.applyThreshold(queryResults, (float)(i/100.));
				
	//			searching.displayResults(queryResults);
	//			searching.displayFilteredResults(queryFilteredResults);
				
				Benchmark bench = new Benchmark(queryFilteredResults);
				
	//			System.out.println("Total results : ");
				System.out.println(String.format("%3.2g",(i/100.)) + " ; " + bench.toString());
			}
			System.out.println("thre ; prcs ; rcll ; speci ; error ; false");		
	}
}
