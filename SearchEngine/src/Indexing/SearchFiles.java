package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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

	public void executeAllQueries(Map<Integer, String> queries) throws IOException, ParseException {
		String indexPath = "index";
		String field = "content";
		//int hitsPerPage = 10;

		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths
				.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = analyzerImpl;
		QueryParser parser = new QueryParser(field, analyzer);

		for (Map.Entry<Integer, String> entry : queries.entrySet()) {
			System.out.printf("Key : %s and Value: %s %n", entry.getKey(),
					entry.getValue());
			Query query = parser.parse(QueryParser.escape((entry.getValue())));
			System.out.println("Searching for: " + query.toString(field));
		}
		reader.close();
	}
/*
	public static void doPagingSearch(BufferedReader in,
			IndexSearcher searcher, Query query, int hitsPerPage, boolean raw,
			boolean interactive) throws IOException {

		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out
						.println("Only results 1 - " + hits.length + " of "
								+ numTotalHits
								+ " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) { // output raw format
					System.out.println("doc=" + hits[i].doc + " score="
							+ hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				if (path != null) {
					System.out.println((i + 1) + ". " + path);
					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i + 1) + ". "
							+ "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out
							.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit)
					break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}*/
}
