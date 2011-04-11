/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import com.mallardsoft.tuple.*;

/**
 * If duplicate mods are found this exception is thrown.
 * @author Penn
 */
public class ModDuplicateException extends Exception {

	private ArrayList<Pair<String, String>> _mods;

    /**
     * @param name of the mod that was enabled.
     * @param version of the mod that was enabled.
     */
    public ModDuplicateException(ArrayList<Pair<String, String>> mods) {
        super();
        _mods = mods;
    }

    /**
     * @return the list of mods that depends on the mod that are not disabled
     */
    public ArrayList<Pair<String, String>> getMods() {
        return _mods;
    }
    
    /**
     * @return a string of mod names separated by comma in array _deps
     */
    public String toString() {
    	String ret = "";
    	Enumeration e = Collections.enumeration(_mods);
    	while (e.hasMoreElements()) {
    		ret += Tuple.get1((Pair<String, String>)e.nextElement());
    		ret += ", ";
    	}
    	
    	return ret.substring(0, ret.length()-2);
    }

}
