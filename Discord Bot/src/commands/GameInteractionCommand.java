package commands;

import java.util.ArrayList;
import java.util.Random;

import errorHandling.BotError;
import framework.Command;
import framework.specifics.GamePool;
import ressources.Ressources;

public class GameInteractionCommand extends Command {
	
	public enum CommandType{
		INITIAL, ADD, REMOVE, ROLL, LIST
	}
	
	CommandType commandType;
	
	public GameInteractionCommand(CommandType commandGameType){
		commandType = commandGameType;
	}
	
	@Override
	public void action(){
		
		switch(commandType){
		case INITIAL:
			initial();
			break;
		case ADD:
			add();
			break;
		case REMOVE:
			remove();
			break;
		case ROLL:
			roll();
			break;
		case LIST:
			list();
			break;
		}
		
	}
	
	private void initial(){
		
		if(getContent() == null){
			error();
		}
		else{
			
			String[] jeux = getContent().split(",");
			
			for(int i = 0; i < jeux.length; i++)
				jeux[i] = jeux[i].trim();
			
			GamePool gamepool = new GamePool(jeux);
			
			getBuffer().push(gamepool, Ressources.BUFFER_GAMEPOOL);
			
			ArrayList<String> messages = new ArrayList<>();
			
			messages.add(getStringEz("GamesReadyToBeRolledMessage"));
			
			for(int i = 1; i <= gamepool.size(); i++)
				messages.add(i + ". `" + gamepool.get(i - 1) + "`");
			
			groupAndSendMessages(messages);
			
			sendInfoMessage(getStringEz("InitialViewListAgainMessage"),
					buildVCommand(GAME_LIST));
			
		}
		
	}
	
	private void add(){
		
		if(getContent() == null){
			error();
		}
		else{
			
			try{
				
				GamePool gamepool = (GamePool)getBuffer().get(
						Ressources.BUFFER_GAMEPOOL);
				
				gamepool.add(getContent());
				
				sendMessage(getStringEz("AddedGameSuccessMessage"),
						getContent());
				
			}
			catch(NullPointerException e){
				
				sendInfoMessage(getStringEz("AddErrorNoPoolCreated"),
						buildVCommand(GAME + " [game 1],[game 2],[...]"));
				
			}
			
		}
		
	}
	
	private void remove(){
		
		if(getContent() == null){
			error();
		}
		else{
			
			try{
				
				GamePool gamepool = (GamePool)getBuffer().get(
						Ressources.BUFFER_GAMEPOOL);
				
				String content = getContent();
				
				if(!content.equals("-all")){
					
					if(gamepool.remove(getContent()))
						sendMessage(getStringEz("RemovedGameSuccessMessage"),
								getContent());
					else
						sendInfoMessage(getStringEz("RemoveErrorNoSuchGame"),
								buildVCommand(GAME_LIST));
					
				}
				else{
					
					do{
						gamepool.remove(0);
					}while(!gamepool.isEmpty());
					
				}
				
				if(gamepool.isEmpty()){
					
					getBuffer().remove(Ressources.BUFFER_GAMEPOOL);
					sendMessage(getStringEz("RemoveIsNowEmpty"));
					
				}
				
			}
			catch(NullPointerException e){
				sendInfoMessage(getStringEz("RemoveErrorEmptyPool"));
			}
			
		}
		
	}
	
	private void roll(){
		
		try{
			
			GamePool gamepool = (GamePool)getBuffer().get(
					Ressources.BUFFER_GAMEPOOL);
			
			int wantedRoll = 1;
			
			if(getContent() != null)
				try{
					wantedRoll = Integer.parseInt(getContent());
				}
				catch(NumberFormatException e){}
			
			Random ran = new Random();
			int num;
			
			if(wantedRoll < 1)
				new BotError(getStringEz("RollNumberIsNotValid"));
			else if(wantedRoll == 1){
				
				num = ran.nextInt(gamepool.size());
				
				sendMessage(getStringEz("RolledGameMessage"), gamepool.get(num));
				
			}
			else{
				
				for(int i = 1; i <= wantedRoll; i++){
					
					num = ran.nextInt(gamepool.size());
					
					sendMessage(getStringEz("RolledMultipleGamesMessage"), i,
							gamepool.get(num));
					
				}
				
			}
			
		}
		catch(NullPointerException e){
			sendInfoMessage(getStringEz("RollErrorPoolEmpty"),
					buildVCommand(GAME + " [game 1],[game 2],[...]"));
		}
		catch(IllegalArgumentException e){
			sendInfoMessage("Usage :\n" + buildVCommand(GAME_ROLL) + " OR "
					+ buildVCommand(GAME_ROLL_ALT)
					+ " : Roll the dice once to get a random game.\n"
					+ buildVCommand(GAME_ROLL + " [positive number]") + " OR "
					+ buildVCommand(GAME_ROLL_ALT + " [positive number]")
					+ " : Roll a random game for the inputted number of time.",
					false);
		}
		
	}
	
	private void list(){
		
		GamePool gamepool = null;
		
		try{
			gamepool = (GamePool)getBuffer().get(Ressources.BUFFER_GAMEPOOL);
		}
		catch(NullPointerException e){}
		
		if(gamepool == null){
			sendMessage("No games in your pool mate!");
		}
		else{
			
			ArrayList<String> messages = new ArrayList<>();
			
			messages.add("Here are the games in your game pool :");
			
			for(int i = 0; i < gamepool.size(); i++)
				messages.add((i + 1) + ". `" + gamepool.get(i) + "`");
			
			groupAndSendMessages(messages);
			
		}
		
	}
	
	private void error(){
		
		String message;
		
		switch(commandType){
		case INITIAL:
			message = "Usage : "
					+ buildVCommand(GAME + " [game 1],[game 2],[game 3],[...]")
					+ ".\nSeparate games using commas.";
			break;
		case ADD:
			message = "Usage : " + buildVCommand(GAME_ADD + " [game name]")
					+ ".";
			break;
		case REMOVE:
			message = "Usage : `" + buildVCommand(GAME_REMOVE + " [game name]")
					+ ".\nYou could also input "
					+ buildVCommand(GAME_REMOVE + " -all")
					+ " to remove all the games in the current pool.";
			break;
		default:
			message = "An unsuspected error happened. What have you done.";
			break;
		}
		
		sendInfoMessage(message, false);
		
	}
	
}
