package br.com.luismatos.util;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="ConverterEmptyToNull")
public class ConverterEmptyToNull implements Converter{
	
	 public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
	        if (value == null || value.trim().length() == 0) {
	            if (component instanceof EditableValueHolder) {
	                ((EditableValueHolder) component).setSubmittedValue(null);
	            }
	            return null;
	        }
	        return value;
	    }
	    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
	    	return value == null ? null : value.toString();
	    }	
}