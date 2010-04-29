package business.actions;

/**
 *  Copies a supportive file from the mod archive.<br/>If "path2" is not specified the file "path1" is copied, if it is "path2" is copied and renamed to "path1".<br/>
 *        Overwrite specifies a controlled behaviour in case the target file already exists:<br/>
 *        "yes"   -> target file is overwritten<br/>
 *        "no"    -> target file is left as is<br/>
 *        "newer" -> target file is overwritten if its version is lower than the one specified by the version attribute<br/>
 *       If the condition attribute is specified the copying is only performed if the given condition is true. A condition can consist of another mod being enabled or disabled or a boolean expression combining multiple such conditions.Examples of valid condition strings:<br/>
 *       'Tiny UI'<br/>
 *       not 'Tiny UI'<br/>
 *       'Tiny UI[v3.0]' and 'Automatic Ability Learner[v1.1-1.5]'<br/>
 *       ('BardUI' or ('Improved UI by Barter[v1.08]' and 'Improved UI Addon - Juking Map')) and not 'Tiny UI'<br/>
 * @author Shirkit
 */
public class ActionCopyFile extends Action {

    private String name; // Path1
    private String source; // Path2
    private String overwrite; // yes | no | newer
    private String version;
    private String condition;

    /**
     * @return Path1
     */
    public String getName() {
        return name;
    }


    /**
     * @param Path1
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Path2
     */
    public String getSource() {
        return source;
    }

    /**
     * @param Path2
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the overwrite
     */
    public String getOverwrite() {
        return overwrite;
    }

    /**
     * @param overwrite the overwrite to set
     */
    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

}
