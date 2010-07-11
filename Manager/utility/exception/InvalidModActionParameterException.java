/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.exception;

import business.actions.Action;

/**
 *
 * @author Shirkit
 */
public class InvalidModActionParameterException extends Exception {

    private String name;
    private String version;
    private Action action;

    public InvalidModActionParameterException(String name, String version, Action action) {
        super();
        this.name = name;
        this.version = version;
        this.action = action;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Action getAction() {
        return action;
    }
}