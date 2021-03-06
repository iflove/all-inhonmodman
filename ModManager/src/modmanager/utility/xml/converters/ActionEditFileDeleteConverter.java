/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package modmanager.utility.xml.converters;

import modmanager.business.modactions.ActionEditFileDelete;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 * @author Shirkit
 */
public class ActionEditFileDeleteConverter implements Converter {

    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        ActionEditFileDelete value = (ActionEditFileDelete) o;
        //writer.setValue(value.getContent());
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        ActionEditFileDelete value = new ActionEditFileDelete();
        value.setLineStart(reader.getAttribute("lineStart"));
        value.setLineEnd(reader.getAttribute("lineEnd"));
        //value.setContent("<![CDATA[" + reader.getValue() + "]]>");
        return value;
    }

    public boolean canConvert(Class type) {
        return type.equals(ActionEditFileDelete.class);
    }

}
