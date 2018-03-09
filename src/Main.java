import javax.security.auth.login.LoginException;

import vendor.Framework;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

/**
 * 
 * @author Stephano
 *
 */
public class Main {
	
	private static JDA jda;
	
	public static void main(String[] args){
		
		try{
			
			Framework.build();
			
			try{
				
				String botToken = Framework.getEnvVar("BOT_TOKEN");
				
				jda = new JDABuilder(AccountType.BOT).setToken(botToken)
						.buildBlocking();
				jda.addEventListener(new MessageListener());
				jda.setAutoReconnect(true);
				
				boolean isDebug = Framework.getEnvVar("DEBUG");
				if(isDebug){
					String clientId = Framework.getEnvVar("CLIENT_ID", null);
					
					if(clientId != null){
						System.out
								.println("\nLink to join the bot to a server : "
										+ "https://discordapp.com/oauth2/authorize?&client_id="
										+ clientId
										+ "&scope=bot&permissions=0\n");
					}
				}
				
			}
			catch(LoginException | IllegalArgumentException
					| InterruptedException e){
				e.printStackTrace();
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
