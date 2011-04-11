package business.modactions;

import utility.xml.converters.ActionEditFileDeleteConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Deletes the string pointed to by the "cursor". Does not require a source string.
 * @author Shirkit
 */
@XStreamAlias("delete")
@XStreamConverter(ActionEditFileDeleteConverter.class)
public class ActionEditFileDelete extends Action implements ActionEditFileActions {

    public ActionEditFileDelete() {
        setType(DELETE);
    }

    private String content;

    /**
     * @deprecated This method is not used by this action. It doesn't contain a content.
     */
    public String getContent() {
        return content;
    }

    /**
     * @deprecated This method is not used by this action. It doesn't contain a content.
     */
    public void setContent(String content) {
        this.content = content;
    }

}