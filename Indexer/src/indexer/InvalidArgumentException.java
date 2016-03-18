/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexer;

/**
 *
 * @author Teo
 */
public class InvalidArgumentException extends Exception {
    
    public InvalidArgumentException(){
        super();
    }
    
    public InvalidArgumentException(String message) {
        super(message);
    }
}
