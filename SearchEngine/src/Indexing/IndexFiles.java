package Indexing;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
		
	private IndexFiles() {
	}

	/** Index all text files under a directory. */
	public static void main(String[] args) {
		String indexPath = "index";
		String docsPath = "CISIDonnees/CISI.ALLNettoye";

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
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");
		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	private static File getDoc(String docsPath) {
		Path doc = Paths.get(docsPath);
		if (!Files.isReadable(doc)) {
			System.out
					.println("File '"
							+ doc.toAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		return new File(doc.toString());
	}

	/** Indexes a single document */
	static void indexDoc(IndexWriter writer, File file) throws IOException {
		try {
			ArrayList<Document> docs = splitFile(file);
			int i = 0;
			System.out.println("");
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
		boolean skipNextLine = false;
		String currentLine = "";
		ArrayList<Document> docs = new ArrayList<Document>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Pattern indexPattern = Pattern.compile("I." + " (\\d+)");
		while (currentLine != null) {
			// Skip reading another line if the current one has not already been
			// parsed
			if (!skipNextLine) {
				currentLine = reader.readLine();
			} else {
				skipNextLine = false;
			}
			if (currentLine == null) {
				break;
			}
			// Handle indexing and document splitting
			if (currentLine.startsWith("I.")) {
				Document d = new Document();
				Matcher m = indexPattern.matcher(currentLine);
				m.matches();
				int index = Integer.parseInt(currentLine.substring(
						m.start(1), m.end(1)));
				System.out.println("");
				System.out.println("Index : " + index);
				d.add(new IntField("index", index, Field.Store.YES));
				docs.add(d);
				continue;
			}
			 Document lastDoc = docs.get(docs.size() - 1);
			 this.processLine(reader, lastDoc);
		}
		return docs;
	}

	/**
	 * Handle the current tag and add info to the current Document
	 * 
	 * @param reader
	 * @param doc
	 * @param tag
	 * @throws IOException
	 */
	private static void processLine(BufferedReader reader, Document doc, String tag)
			throws IOException {
		switch (tag) {
		// Title found
		case ".T":
			String title = this.multiLineRead(reader, ".W", ".A");
			System.out.println("Title : " + title);
			doc.add(new TextField("title", title, Field.Store.YES));
			break;
		// Authors found
		case ".A":
			String authors = this.multiLineRead(reader, ".W");
			System.out.println("Authors : " + authors);
			doc.add(new TextField("authors", authors, Field.Store.YES));
			break;
		// Reference found
		case ".B":
			String ref = reader.readLine();
			ref = ref.substring(1, ref.length() - 1); // Remove parenthesis
			System.out.println("Reference : " + ref);
			doc.add(new TextField("references", ref, Field.Store.YES));
			break;
		// Text found
		case ".W":
			String content = this.multiLineRead(reader, ".I", ".B");
			doc.add(new TextField("content", content, Field.Store.YES));
			break;
		default:
			break;
		}
	}

	/**
	 * Keeps concatenating lines to the result string while no stopping tag is
	 * found
	 * 
	 * @param reader
	 *            The current BufferedReader
	 * @return String, result of concatenation
	 */
	/*private String multiLineRead(BufferedReader reader, String... tags)
			throws IOException {
		String result = "";
		this.skipNextLine = true;
		while ((this.currentLine = reader.readLine()) != null) {
			if (this.findTags(this.currentLine, tags) != null)
				break;
			else
				result = result.concat(this.currentLine + " ");
		}
		return result.trim();
	}*/

}
