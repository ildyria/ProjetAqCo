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

import printer.Printer;
import Bench.Benchmark;
import Indexing.IndexFiles;
import Indexing.SearchFiles;


public class Main {

	public static void main(String[] args) throws Exception{
		
//		System.out.println("Bonjour");
//		Thread.sleep(200);
//		System.out.println("Bienvenue dans le Super Moteur de Recherche");
//		Thread.sleep(200);
//		System.out.println("Nous vous attendions !");
//		Thread.sleep(200);
//		System.out.println("Plusieurs choix vont s'offrir à vous, choisissez bien !");
//		Thread.sleep(200);
		int speed = 50;
		Printer.slow("Bonjour",speed);
		Printer.slow("Bienvenue dans le Super Moteur de Recherche",speed);
		Printer.slow("Nous vous attendions !",speed);
		Printer.slow("Plusieurs choix vont s'offrir à vous, choisissez bien !",speed);
		
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
			Printer.slow("Le tokenizer actuel est : " + tokenizer ,speed);
			Printer.slow("Voulez vous le changer ? (y/n)",speed);
			Scanner sc = new Scanner(System.in);
			String val = sc.nextLine();
			while(!val.equals("y") && !val.equals("n"))
			{
				Printer.slow("essayez encore !",speed);
				val = sc.nextLine();
			}
			if(val.equals("y")){
				Printer.slow("Les tokenizers disponibles sont :",speed);
				System.out.println(TokenizerFactory.availableTokenizers());
				Printer.slow("Entrez celui que vous voulez",speed);
				tokenizer = sc.nextLine();
				while(!TokenizerFactory.availableTokenizers().contains(tokenizer))
				{
					Printer.slow("Les tokenizers disponibles sont :",speed);
					System.out.println(TokenizerFactory.availableTokenizers());
					Printer.slow("Entrez celui que vous voulez",speed);
					tokenizer = sc.nextLine();
				}
			}
			
			System.out.println("Aucun filtre n'est paramétré pour le moment");
			System.out.println("Rajouter un filtre ? (y/n)");
			val = sc.nextLine();
			while(!val.equals("n"))
			{
				System.out.println("Voici quelques exemples de filtres, avec leurs noms et paramètres :");
				System.out.println("	standard");
				System.out.println("	lowercase");
				System.out.println("	stop PARAMS(6) : ignoreCase, false, words, motsvides.txt, format, wordset");
				System.out.println("	SnowBallPorter PARAMS(2) : language, English");
				System.out.println("	removeduplicates");
				System.out.println("---------");
				System.out.println("Alors, on choisit quoi comme filtre ?");
				String name = sc.nextLine();
				System.out.println("Entrez le nombre de paramètres de ce filtre (>=0) :");
				int nb = sc.nextInt();
				String[] params = new String[nb];
				if(nb>0){
					for (int i=1;i<=nb;i++){
						System.out.println("Entrez le paramètre " + i);
						Thread.sleep(1000);
						String param = sc.nextLine();
						while(param.equals(""))
						{
							param = sc.nextLine();
						}
						params[i-1]= param;
					}
				}
				builder.addTokenFilter(name,params);
				System.out.println("Filtre ajouté !");
				System.out.println("Encore ? (y/n)");
				val = sc.nextLine();
				while(!val.equals("y") && !val.equals("n"))
				{
					val = sc.nextLine();
				}
			}
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
