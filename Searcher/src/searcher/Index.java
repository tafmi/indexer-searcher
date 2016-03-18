/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Teo
 */
public class Index {

    private final Directory directory;

    public Index(File store) throws IOException, FileNotFoundException{
        
        directory= FSDirectory.open(store.toPath());
    }
     
     public void searchQuery(String term) throws IOException, ParseException{
         Set<String> results=new HashSet<>();
         DirectoryReader ireader = DirectoryReader.open(directory);
         IndexSearcher searcher = new IndexSearcher(ireader);
         MultiFieldQueryParser parser=new MultiFieldQueryParser(
                 new String[]{"company","title","author","filename","creator","keywords","subject"}
                 ,new StandardAnalyzer());
         Query query=parser.parse(QueryParser.escape(term));
         TopDocs topdocs=searcher.search(query,null,10000);
         ScoreDoc[] hits=topdocs.scoreDocs;
         for (ScoreDoc hit : hits) {
                int docID = hit.doc;
                Document doc=searcher.doc(docID);
                results.add(doc.get("path"));
         }  
         Set<String> tfidfresults=new HashSet<>();
         String[] splitterm=term.split("\\s+");
         for(String split:splitterm){
            QueryParser tfidfparser=new QueryParser("hightfidfcontents",new StandardAnalyzer());
            query=tfidfparser.parse(QueryParser.escape(split));
            topdocs=searcher.search(query,null,10000);
            hits=topdocs.scoreDocs;
            for (ScoreDoc hit : hits) {
                   int docID = hit.doc;
                   Document doc=searcher.doc(docID);
                   tfidfresults.add(doc.get("path"));
            } 
         }
         Set<String> cresults=new HashSet<>();
         QueryParser cparser=new QueryParser("contents",new StandardAnalyzer());
         query=cparser.parse(QueryParser.escape(term));
         topdocs=searcher.search(query,null,10000);
         hits=topdocs.scoreDocs;
         for (ScoreDoc hit : hits) {
                int docID = hit.doc;
                Document doc=searcher.doc(docID);
                cresults.add(doc.get("path"));
         }
         for(String res:cresults){
             if(tfidfresults.contains(res)){
                 results.add(res);
             }
         }
         System.out.println("Found "+results.size()+" results:");
         for(String s:results){
             System.out.println(s);
         }
     }

}
