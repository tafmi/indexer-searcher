# indexer-searcher
Indexing files and searching index
## features
Project Indexer stores files' metadata, contents and high tf*idf content's terms in an index.

Project Searcher searches for a word or a phrase in file's metadata and contents' terms with

high tf*idf value, that have been indexed by project indexer. You could schedule project 

indexer to run automatically and integrade project searcher in your application to search the index.

## usage

#### Indexer: 

in windows cmd:

cd in project Indexer
#### compile

path\to\Indexer>javac -d bin -sourcepath src -cp lib\lucene-analy
zers-common-5.0.0.jar;lib\lucene-core-5.0.0.jar;lib\tika-app-1.7.jar src/indexer/Indexer.java 
#### run

args[0]: directory to be indexed

args[1]: directory of index to be stored

path\to\Indexer>java -cp bin;lib\lucene-analy
zers-common-5.0.0.jar;lib\lucene-core-5.0.0.jar;lib\tika-app-1.7.jar indexer.Ind
exer directory index_directory

#### Searcher:

in windows cmd:

cd in project Searcher
#### compile

path\to\Searcher>javac -d bin -sourcepath src -cp lib\lucene-analy
zers-common-5.0.0.jar;lib\lucene-core-5.0.0.jar;lib\lucene-queryparser-5.0.0.jar src/searcher/Searcher.java

#### run

args[0]: query

args[1]: directory of stored index 

path\to\Searcher>java -cp bin;lib\lucene-analy
zers-common-5.0.0.jar;lib\lucene-core-5.0.0.jar;lib\lucene-queryparser-5.0.0.jar
 searcher.Searcher query index_directory
 
###### note: indexer's args[1] and searcher's args[1] must be the same directory
 

