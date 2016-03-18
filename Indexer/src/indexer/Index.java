/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author Teo
 */
public class Index {

    private final Directory directory;
    private final IndexWriter iwriter;

    public Index(File file,File store) throws IOException, FileNotFoundException, SAXException, TikaException{
        
        if(store.exists()){
           deleteDirectory(store);
        }
        directory= FSDirectory.open(store.toPath());
        StandardAnalyzer analyzer=new StandardAnalyzer();
        IndexWriterConfig config=new IndexWriterConfig(analyzer);
        iwriter = new IndexWriter(directory,config);
        indexDirectory(file);
        iwriter.close();
        tfidf();    
    }
    
    private void deleteDirectory(File directory) {
     if(directory.exists()){
        File[] files = directory.listFiles();
        if(null!=files){
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
     } 
    }

    private void indexDirectory(File file) throws IOException, FileNotFoundException, SAXException, TikaException{

        File[] files=file.listFiles();
        for(File f:files){
            if(f.isDirectory()){
               indexDirectory(f);
            }
            else{
                indexFile(f);
            }
        }       
    }

     private void indexFile(File file) throws IOException, FileNotFoundException, SAXException, TikaException{

         if(file.isHidden() || !file.canRead()){
           return;
         }
         FileMetadata meta=new FileMetadata(file);
         Document document=new Document();
         document.add(new Field("path",file.getCanonicalPath(),TextField.TYPE_STORED));
         document.add(new Field("filename",meta.getFilename(),TextField.TYPE_STORED));
         String author=meta.getAuthor();
         if(author!=null){
             document.add(new Field("author",author,TextField.TYPE_STORED));
         }
         String creator=meta.getCreator();
         if(creator!=null){
             document.add(new Field("creator",creator,TextField.TYPE_STORED));
         }
         String title=meta.getTitle();
         if(title!=null){
             document.add(new Field("title",title,TextField.TYPE_STORED));
         }
         String subject=meta.getSubject();
         if(subject!=null){
             document.add(new Field("subject",subject,TextField.TYPE_STORED));
         }
         String keywords=meta.getKeywords();
         if(keywords!=null){
             document.add(new Field("keywords",keywords,TextField.TYPE_STORED));
         }
         String company=meta.getCompany();
         if(company!=null){
             document.add(new Field("company",company,TextField.TYPE_STORED));
         }
         if(isDocument(file)){
            FieldType fieldType = new FieldType();
            fieldType.setStoreTermVectors(true);
            fieldType.setStoreTermVectorPositions(true);
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
            fieldType.setStored(true);
            String contents=meta.getContents();
            if(contents!=null){                
                     document.add(new Field("contents",contents,fieldType));      
            }
         }
         iwriter.addDocument(document);
    }

     private boolean isDocument(File file){
         String ext=file.getName().substring(file.getName().lastIndexOf(".") + 1);
        return ext.equals("txt") || ext.equals("pdf") || ext.equals("doc") || ext.equals("docx")
                || ext.equals("xls") || ext.equals("xlsx") || ext.equals("ppt") || ext.equals("pptx");
     }
     
     private void tfidf() throws IOException{
        HashMap<String,String> finalMap=new HashMap<>();
        try (DirectoryReader ireader = DirectoryReader.open(directory)) {
            IndexSearcher isearcher=new IndexSearcher(ireader);
            for(int i=0;i<ireader.maxDoc();i++){
                int j=0;
                float totalTfIdf=0;
                float maxTfIdf=0;
                HashMap<String,Float> contents=new HashMap<>();
                Terms terms = ireader.getTermVector(i,"contents");
                if (terms != null && terms.size() > 0){
                    TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
                    TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
                    BytesRef term = null;
                    while ((term = termsEnum.next()) != null) {
                        DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
                        int docIdEnum;
                        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                            org.apache.lucene.index.Term termInstance = new org.apache.lucene.index.Term("contents", term);
                            long indexDf = ireader.docFreq(termInstance);
                            float tf = tfidfSIM.tf(docsEnum.freq());
                            float idf = tfidfSIM.idf(indexDf, ireader.getDocCount("contents"));
                            float tfidf=tf*idf;
                            if(tfidf>maxTfIdf){
                                maxTfIdf=tfidf;
                            }
                            totalTfIdf=totalTfIdf+tfidf;
                            j++;
                            contents.put(term.utf8ToString(), tfidf);
                        }
                    }
                }
                String hightfidfwords="";
                if(j>0){
                    float mTfIdf=totalTfIdf/j;
                    float limit=(mTfIdf+maxTfIdf)/2;
                    for (Map.Entry pair : contents.entrySet()) {
                        if((Float)pair.getValue()>limit){
                            hightfidfwords+=" "+(String)pair.getKey();
                        }
                    }
                }
                finalMap.put(isearcher.doc(i).get("path"),hightfidfwords);
            }
        }
        for(Map.Entry pair:finalMap.entrySet()){
            Document doc=new Document();
            doc.add(new Field("path",(String)pair.getKey(),TextField.TYPE_STORED));
            doc.add(new Field("hightfidfcontents",(String)pair.getValue(),TextField.TYPE_STORED));
            StandardAnalyzer analyzer=new StandardAnalyzer();
            IndexWriterConfig config=new IndexWriterConfig(analyzer);
            try (IndexWriter writer = new IndexWriter(directory,config)) {
                writer.addDocument(doc);
            }
            
        }
     }
     
}
