package br.com.luismatos.adapter.shared;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "ConverterEmptyToNull")
public class ConverterEmptyToNull implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            if (component instanceof EditableValueHolder) {
                ((EditableValueHolder) component).setSubmittedValue(null);
            }
            return null;
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : value.toString();
    }
}
