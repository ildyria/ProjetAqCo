package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class SearchFiles {
	private static Analyzer analyzerImpl;

	public SearchFiles(Analyzer _analyzer) {
		analyzerImpl = _analyzer;
	}

	public Map<Integer, String> getAllQueries() throws Exception {

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream("CISIDonnees/CISI.QRY")));

		Map<Integer, String> queries = new HashMap<Integer, String>();

		int currentIndex = 0;
		String currentLine = "";
		String queryContent = "";
		Pattern indexPattern = Pattern.compile(".I" + " (\\d+)");
		while (currentLine != null) {
			// Handle indexing and document splitting
			System.out.println(currentLine);
			if (currentLine.startsWith(".I")) {
				Matcher m = indexPattern.matcher(currentLine);
				m.matches();
				int index = Integer.parseInt(currentLine.substring(m.start(1),
						m.end(1)));
				System.out.println("Index : " + index);
				currentIndex = index;

				while ((currentLine = br.readLine()) != null) {
					if (currentLine.startsWith(".W"))
						continue;
					if (currentLine.startsWith(".I")) {
						queries.put(currentIndex, queryContent);
						queryContent = "";
						break;
					}
					currentLine = currentLine.trim();
					queryContent = queryContent + " " + currentLine;
				}
			} else
				currentLine = br.readLine();
		}
		queries.put(currentIndex, queryContent);
		System.out.println(queries.get(currentIndex));
		br.close();
		return queries;

	}

	public Map<Integer, TopDocs> executeAllQueries(Map<Integer, String> queries)
			throws IOException, ParseException {
		String indexPath = "index";
		String field = "content";

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = analyzerImpl;
		QueryParser parser = new QueryParser(field, analyzer);
		Map<Integer, TopDocs> topDocsList = new HashMap<Integer, TopDocs>();

		for (Map.Entry<Integer, String> entry : queries.entrySet()) {
			System.out.printf("Key : %s and Value: %s %n", entry.getKey(),
					entry.getValue());

			Query query = parser.parse(QueryParser.escape((entry.getValue())));

			System.out.println("Searching for: " + query.toString(field));

			TopDocs results = searcher.search(query, 200);
			topDocsList.put(entry.getKey(), results);
		}
		reader.close();
		return topDocsList;
	}

	public Map<Integer, List<ScoreDoc>> applyThreshold(Map<Integer, TopDocs> queryResult, float threshold)
	{
		Map<Integer, List<ScoreDoc>> res = new HashMap<Integer, List<ScoreDoc>>();
		
		Iterator<Entry<Integer, TopDocs>> it = queryResult.entrySet().iterator();
	    while (it.hasNext()) {
	    	Entry<Integer, TopDocs> pair = it.next();
	    	List<ScoreDoc> doclist = new ArrayList<ScoreDoc>();

	    	ScoreDoc[] hits = pair.getValue().scoreDocs;
			for(int i = 0; i < hits.length; ++i)
			{
				if(hits[i].score > threshold)
				{
					doclist.add(hits[i]);
				}
			}
			res.put(pair.getKey(), doclist);
	        it.remove();
	    }


		return res;
	}
}
