
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	private static final String INDEX_DIR = "indexedFiles";

	public TopDocs search(String textQuery) throws IOException, ParseException{

		IndexSearcher searcher = getIndexSearcher();

		Analyzer analyzer = getAnalyzer();

		QueryParser qp = new QueryParser("contents", analyzer);

		Query query = qp.parse(textQuery);

		TopDocs hits = searcher.search(query, 200);

		return hits;

	}

	private Analyzer getAnalyzer(){
		Analyzer analyzer;

		//default analyzer
		analyzer = getEmptyAnalyzer();

		/*analyzer with english stopwords*/
		//analyzer = new EnglishAnalyzer();

		/*analyzer with stemming*/
		//analyzer = new PorterAnalyzer(getEmptyAnalyzer());

		/*analyzer with english stopwords and stemming*/
		//analyzer = new PorterAnalyzer(new EnglishAnalyzer());

		return analyzer;
	}

	private Analyzer getEmptyAnalyzer(){
		
		CharArraySet stopWords = new CharArraySet(0, true);
		
		return new StandardAnalyzer(stopWords);
	}


	private IndexSearcher getIndexSearcher() throws IOException{

		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

		IndexReader reader = DirectoryReader.open(dir);

		return new IndexSearcher(reader);
	}
}