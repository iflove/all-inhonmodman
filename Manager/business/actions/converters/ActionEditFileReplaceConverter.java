/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package business.actions.converters;

import business.actions.ActionEditFileReplace;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 * @author Shirkit
 */
public class ActionEditFileReplaceConverter implements Converter {

    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        ActionEditFileReplace value = (ActionEditFileReplace) o;
        writer.setValue(value.getContent());
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        ActionEditFileReplace value = new ActionEditFileReplace();
        value.setContent("<![CDATA[" + reader.getValue() + "]]>");
        return value;
    }

    public boolean canConvert(Class type) {
        return type.equals(ActionEditFileReplace.class);
    }
}
