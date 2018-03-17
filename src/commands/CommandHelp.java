package commands;

import utilities.Command;

/**
 * 
 * @author Stephano
 *
 * <br>
 * <br>
 *         Classe qui envois un message a l'utilisateur qui demande de l'aide
 *         avec une liste compl�te des commandes
 *
 */

public class CommandHelp extends Command {
	
	@Override
	public void action(){
		
		sendPrivateMessage(lang("Content"));
		sendInfoMessage(lang("HelpSentMessage"));
		
	}
	
}
