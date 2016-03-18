/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 *
 * @author Teo
 */
public class Searcher {

    /**
     * @param args the command line arguments
     * @throws org.apache.lucene.queryparser.classic.ParseException
     */
    public static void main(String[] args) throws ParseException {
        // TODO code application logic here
         try{
            if(args.length!=2){
                throw new InvalidArgumentException();
            }
            else{
                 String query=args[0];
                 String inputStore=args[1];
                 File store=new File(inputStore);
                 if(!store.exists()){
                    throw new FileNotFoundException("Input index directory does not exist."
                            + " Please specify a valid input directory.");
                 }
                 if(!store.isDirectory()){
                    throw new IOException("The input index path that was specified is not a directory."
                            + " Please specify a valid directory.");
                 }
                 
                 start(query,store);  
            }         
        }
        catch(InvalidArgumentException iaex){
            System.out.println("Application usage:");
            System.out.println("<executble_name> <directory> <index_directory>");
        } catch (FileNotFoundException fnfex) {
            System.out.println("Error: "+fnfex.getMessage());
        } catch (IOException ioex) {
            System.out.println("Error"+ioex.getMessage());
        }
    }
    
    private static void start(String query,File store) throws IOException, ParseException{
        
        System.out.println("Searching for term \""+query+"\"...");
        Index index=new Index(store);
        index.searchQuery(query);
        
    }
}
