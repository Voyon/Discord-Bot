package vendor.modules;

import vendor.abstracts.ModuleOutputtable;
import vendor.interfaces.Auditable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Audit extends ModuleOutputtable {
	
	private static ArrayList<Auditable> outputs;
	
	private static boolean hasIssuedWarning;
	
	private static String separator;
	
	@Override
	public void build() throws Exception{
		outputs = new ArrayList<>();
		hasIssuedWarning = false;
		separator = "-";
	}
	
	/**
	 * Sets the separator for logs that has a prefix before the message.
	 * <p>
	 * Set to <code>null</code> if you don't want any separator.
	 * 
	 * @param newSeparator
	 */
	public static void setSeparator(String newSeparator){
		separator = newSeparator;
	}
	
	/**
	 * Overwrite the output list with those passed as parameters.
	 * 
	 * @param outputs
	 *            Objects implementing the {@link Auditable
	 *            Auditable} interface which are meant to receive logs from the
	 *            Audit module.
	 */
	public static void setOutputs(Auditable... outputs){
		if(outputs != null)
			Audit.outputs = new ArrayList<>(Arrays.asList(outputs));
	}
	
	/**
	 * Adds an output to the already existing output list for the Audit module.
	 * 
	 * @param output
	 *            Object implementing the {@link Auditable
	 *            Auditable} interface which is meant to receive logs from the
	 *            Audit module.
	 * @return <code>true</code> if the output was added successfully,
	 *         <code>false</code> if the output is already in the outputs list.
	 */
	public static boolean addOutput(Auditable output){
		
		if(outputs.contains(output)){
			return false;
		}
		
		return outputs.add(output);
		
	}
	
	/**
	 * Adds multiple outputs to the already existing output list for the Audit
	 * module.
	 * 
	 * @param outputs
	 *            Objects implementing the {@link Auditable
	 *            Auditable} interface which are meant to receive logs from the
	 *            Audit module.
	 */
	public static void addOutputs(Auditable... outputs){
		for(Auditable output : outputs){
			Audit.addOutput(output);
		}
	}
	
	/**
	 * Removes an output from the output list for the Audit module.
	 * 
	 * @param output
	 *            Object implementing the {@link Auditable
	 *            Auditable} interface.
	 * @return <code>true</code> if the output was in the list and has been
	 *         removed, <code>false</code> if the output is not in the list.
	 */
	public static boolean removeOutput(Auditable output){
		return outputs.remove(output);
	}
	
	/**
	 * Removes an output from the output list by it's index for the Audit
	 * module.
	 * 
	 * @param index
	 *            The position of the output in the internal list.
	 * @return The {@link Auditable Auditable} object found at
	 *         this position, or <code>null</code> if the index is out of
	 *         bounds.
	 */
	public static Auditable removeOutput(int index){
		try{
			return outputs.remove(index);
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
	}
	
	public static void audit(Exception e){
		audit(e.getMessage());
	}
	
	public static void audit(String message){
		audit(message, true);
	}
	
	public static void audit(String message, final boolean appendDate){
		
		if(message != null && message.length() != 0){
			
			StringBuilder builder = new StringBuilder();
			
			Matcher matcher = Pattern.compile("^\\^?(\\s+\\^)?\\s+").matcher(
					message);
			
			if(matcher.find()){
				
				int whitespaceStartIndex = matcher.start();
				int whitespaceEndIndex = matcher.end();
				
				String beforehandWhitespace = message.substring(
						whitespaceStartIndex, whitespaceEndIndex);
				
				if(!message.startsWith("^")){
					message = message.substring(whitespaceEndIndex);
					
					builder.append(beforehandWhitespace);
				}
				else{
					message = message.substring(1);
					
					int secondCaretPos = message.indexOf("^");
					
					// No second caret
					if(secondCaretPos != -1){
						
						message = message.substring(secondCaretPos + 1);
						
						builder.append(beforehandWhitespace.substring(1,
								secondCaretPos + 1));
						
					}
					
				}
				
			}
			
			boolean hasAddedPrefix = false;
			
			if(appendDate){
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				
				builder.append("[").append(dateFormat.format(date)).append("]");
				
				hasAddedPrefix = true;
			}
			
			if(hasAddedPrefix){
				
				if(separator != null){
					builder.append(" ").append(separator);
				}
				
				builder.append(" ");
				
			}
			
			builder.append(message);
			
			String auditText = builder.toString();
			
			hasIssuedWarning = handleMessageAndWarning(auditText, outputs,
					hasIssuedWarning,
					(output) -> output.audit(auditText, appendDate));
			
		}
		
	}
	
}
