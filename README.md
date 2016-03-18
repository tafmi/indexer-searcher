# indexer-searcher
Indexing files and searching index
## features
Project Indexer stores files' metadata, contents and high tf*idf content's terms in an index.

Project Searcher searches for a word or a phrase in file's metadata and contents' terms with

high tf*idf value, that have been indexed by project indexer. You could schedule project 

indexer to run automatically and integrade project searcher in your application to search the index.

## usage

indexer: 

args[0]: directory to be indexed

args[1]: directory of index to be stored

in windows cmd:

1. cd in project indexer

2. path\to\Indexer>java -cp bin;lib\lucene-analy
zers-common-5.0.0.jar;lib\lucene-core-5.0.0.jar;lib\tika-app-1.7.jar indexer.Ind
exer directory index_directory

searcher:

args[0]: query

args[1]: directory of stored index 

in windows cmd:

1. cd in project searcher

2. path\to\Searcher>java -cp bin;lib\lucene-analy
zers-common-5.0.0.jar;lib\lucene-core-5.0.0.jar;lib\lucene-queryparser-5.0.0.jar
 searcher.Searcher query index_directory
 
###### note: indexer's args[1] and searcher's args[1] must be the same directory
 

