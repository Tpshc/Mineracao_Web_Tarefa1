package lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import util.IndexedFileMapper;
import util.PDFswitchTXT;

public class Searcher {
	
	private String INDEX_DIR;
	
	public Searcher(String indexDir) {
		this.INDEX_DIR = indexDir;
	}
	
	public ArrayList<String> search(String textQuery, boolean isUsingStemming, boolean isUsingStopwords) throws IOException, ParseException{

		IndexSearcher searcher = getIndexSearcher(isUsingStemming,isUsingStopwords);

		Analyzer analyzer = getAnalyzer(isUsingStemming,isUsingStopwords);

		QueryParser qp = new QueryParser("contents", analyzer);

		Query query = qp.parse(textQuery);

		TopDocs hits = searcher.search(query, 236);

		//paths to the TXT files
		ArrayList<String> doc_paths = new ArrayList<String>();
		for (int i = 0; i < hits.scoreDocs.length; i++) 
        {
            int docid = hits.scoreDocs[i].doc;
            Document doc = searcher.doc(docid);
            Path path = PDFswitchTXT.txtToPDF(Paths.get(doc.get("path")));
            doc_paths.add(path.toString());
        }
		
		return doc_paths;

	}

	private Analyzer getAnalyzer(boolean isUsingStemming, boolean isUsingStopwords){
		
		if(isUsingStopwords) {
			//using Stopwords
			if(isUsingStemming) {
				/*analyzer with english stopwords and stemming*/
				return new PorterAnalyzer(new EnglishAnalyzer());
			}else {
				/*analyzer with english stopwords*/
				return new EnglishAnalyzer();
			}
		}else {
			//not using Stopwords
			if(isUsingStemming) {
				/*analyzer with only stemming*/
				return new PorterAnalyzer(getEmptyAnalyzer());
			}else {
				//default analyzer
				return getEmptyAnalyzer();
			}
		}
	}

	private Analyzer getEmptyAnalyzer(){
		
		CharArraySet stopWords = new CharArraySet(0, true);
		
		return new StandardAnalyzer(stopWords);
	}


	private IndexSearcher getIndexSearcher(boolean isUsingStemming, boolean isUsingStopwords) throws IOException{

		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR+"_"+IndexedFileMapper.getIndexedFileInt(isUsingStemming, isUsingStopwords)));

		IndexReader reader = DirectoryReader.open(dir);

		return new IndexSearcher(reader);
	}
}