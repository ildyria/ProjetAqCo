package Indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import printer.Printer;

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
	private static Analyzer analyzerImpl;
	
	public IndexFiles(Analyzer _analyzer) {
		analyzerImpl = _analyzer;
	}

	/** Index all text files under a directory. 
	 * @throws Exception */
	public void run() throws Exception {
		String indexPath = "index";
		String docsPath = "CISIDonnees/CISI.ALLnettoye";

		final File doc = getDoc(docsPath);

		Date start = new Date();
		try {
			Printer.slow("Indexing the file '" + indexPath + "'...", 50);

			Analyzer analyzer = analyzerImpl;
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			// Create a new index in the directory, removing any previously
			// indexed documents:
			iwc.setOpenMode(OpenMode.CREATE);
			
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDoc(writer, doc);

			writer.close();
			Date end = new Date();
			Printer.slow(end.getTime() - start.getTime() + " total milliseconds",50);
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
	private static void indexDoc(IndexWriter writer, File file) throws Exception {
		try {
			ArrayList<Document> docs = splitFile(file);
			int i = 0;
			
			for (Document doc : docs) {
				writer.addDocument(doc);
				i++;
			}
			Printer.slow("Added " + i + " documents in index.",50);
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
			//System.out.println(currentLine);
			if (currentLine.startsWith(".I")) {
				currentDoc = new Document();
				Matcher m = indexPattern.matcher(currentLine);
				m.matches();
				int index = Integer.parseInt(currentLine.substring(m.start(1), m.end(1)));
				//System.out.println("Index : " + index);
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
		//System.out.println(currentDoc.toString());
		reader.close();
		return docs;
	}


}
