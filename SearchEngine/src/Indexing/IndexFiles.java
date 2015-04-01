package Indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing. Run
 * it with no command-line arguments for usage information.
 */
public class IndexFiles {
	private static Document currentDoc;
	private IndexFiles() {
	}

	/** Index all text files under a directory. */
	public static void main(String[] args) {
		String indexPath = "index";
		String docsPath = "CISIDonnees/CISI.ALLnettoye";

		final File doc = getDoc(docsPath);

		Date start = new Date();
		try {
			System.out.println("Indexing the file '" + indexPath + "'...");

			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			// Create a new index in the directory, removing any previously
			// indexed documents:
			iwc.setOpenMode(OpenMode.CREATE);
			
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDoc(writer, doc);

			writer.close();
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	private static File getDoc(String docsPath) {
		Path doc = Paths.get(docsPath);
		if (!Files.isReadable(doc)) {
			System.out.println("File '" + doc.toAbsolutePath() + "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		return new File(doc.toString());
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, File file) throws IOException {
		try {
			ArrayList<Document> docs = splitFile(file);
			int i = 0;
			
			for (Document doc : docs) {
				writer.addDocument(doc);
				i++;
			}
			System.out.println("Added " + i + " documents.");
		} catch (IOException e) {
			writer.close();
			e.printStackTrace();
		}

		/*try (InputStream stream = Files.newInputStream(file)) {
			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
		}*/
	}

	/**
	 * Splits huge file in many Documents
	 * 
	 * @param file
	 *            File to split
	 * @return List of Documents
	 * @throws IOException
	 */
	private static ArrayList<Document> splitFile(File file) throws IOException {
		String currentLine = "";
		ArrayList<Document> docs = new ArrayList<Document>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String docContent = "";
		Pattern indexPattern = Pattern.compile(".I" + " (\\d+)");
		while (currentLine != null) {
			// Handle indexing and document splitting
			System.out.println(currentLine);
			if (currentLine.startsWith(".I")) {
				currentDoc = new Document();
				Matcher m = indexPattern.matcher(currentLine);
				m.matches();
				int index = Integer.parseInt(currentLine.substring(m.start(1), m.end(1)));
				System.out.println("Index : " + index);
				currentDoc.add(new IntField("index", index, Field.Store.YES));
				docs.add(currentDoc);
				
				while((currentLine = reader.readLine()) != null){
					if (currentLine.startsWith(".I")){
						currentDoc.add(new TextField("content", docContent, Field.Store.YES));
						docContent = "";
						break;
					}
					docContent = docContent + " " + currentLine;
				}
			}
			else
				currentLine = reader.readLine();
		}
		currentDoc.add(new TextField("content", docContent, Field.Store.YES));
		System.out.println(currentDoc.toString());
		reader.close();
		return docs;
	}


}
