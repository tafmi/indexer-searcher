/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author Teo
 */
public class Indexer {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws org.apache.tika.exception.TikaException
     */
    public static void main(String[] args) throws IOException, SAXException, TikaException {
        // TODO code application logic here
         try{
            if(args.length!=2){
                throw new InvalidArgumentException();
            }
            else{
                 String inputDirectory=args[0];
                 String inputStore=args[1];
                 File directory= new File(inputDirectory);
                 File store=new File(inputStore);
                 if(!directory.exists()){
                    throw new FileNotFoundException("Input directory does not exist."
                            + " Please specify a valid input directory.");
                 }
                 if(!directory.isDirectory()){
                    throw new IOException("The input path that was specified is not a directory."
                            + " Please specify a valid directory.");
                 }
                 if(!store.exists()){
                    throw new FileNotFoundException("Input index directory does not exist."
                            + " Please specify a valid input directory.");
                 }
                 if(!store.isDirectory()){
                    throw new IOException("The input index path that was specified is not a directory."
                            + " Please specify a valid directory.");
                 }
                 
                 start(directory,store);  
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
    
    private static void start(File directory,File store) throws IOException, IOException, FileNotFoundException, SAXException, TikaException{
        
        Logger.getRootLogger().setLevel(Level.OFF);
        Index index=new Index(directory,store);
    }
    
}
