package vendor.utilities.settings;

import java.util.ArrayList;
import java.util.IllegalFormatException;

public class EnumField extends TextField {
	
	protected class CaseArrayList extends ArrayList<String>{
        @Override
        public boolean contains(Object o){
            if(o == null || o instanceof String){
                
                String e = (String)o;
                
                for(String s : this){
                    boolean val = e == null ? s == null : e.equalsIgnoreCase(s);
                    
                    if(val)
                        return true;
                }
                
            }
            
            return false;
        }
    }
	
	protected ArrayList<String> values;
	
	public EnumField(String name, String env, Object defaultValue,
			Object... otherValues){
		super(name, env, defaultValue.toString());
		
		this.values = this.getValuesArrayList(defaultValue, otherValues);
	}
	
	@Override
	protected String sanitizeValue(Object value)
			throws IllegalArgumentException{
		
		String stringValue = super.sanitizeValue(value);
		
		if(!this.values.contains(stringValue)){
			throw new IllegalArgumentException("The value " + stringValue
					+ " is not a choice for this setting!");
		}
		
		return stringValue;
		
	}
	
	public ArrayList<String> getPossibleValues(){
		return this.values;
	}
	
	@Override
	protected String formatEnvironment(String envValue)
			throws IllegalFormatException{
		String[] possibleValues = envValue.split("\\s*\\|\\s*");
		
		String envDefaultValue = possibleValues[0];
		
		this.values = this.getValuesArrayList(envDefaultValue,
				(Object[])possibleValues);
		
		return envDefaultValue;
	}
	
	private ArrayList<String> getValuesArrayList(Object defaultValue,
			Object... otherValues){
		
		ArrayList<String> newValues = new CaseArrayList();
		
		newValues.add(defaultValue.toString());
		
		for(Object otherValue : otherValues){
			if(!defaultValue.equals(otherValue)){
				try{
					newValues.add(otherValue.toString());
				}
				catch(NullPointerException e){}
			}
		}
		
		return newValues;
		
	}
	
}
