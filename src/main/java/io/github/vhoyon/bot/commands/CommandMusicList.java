package io.github.vhoyon.bot.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.github.vhoyon.bot.errorHandling.BotError;
import io.github.vhoyon.bot.utilities.abstracts.MusicCommand;
import io.github.vhoyon.bot.utilities.music.MusicManager;

/**
 * Command that sends a message containing a list of the tracks that are
 * currently in the playlist of the MusicPlayer of the VoiceChannel that is
 * currently connected to.
 * 
 * @version 1.0
 * @since v0.5.0
 * @author V-ed (Guillaume Marcoux)
 */
public class CommandMusicList extends MusicCommand {
	
	@Override
	public void musicAction(){
		
		if(!isPlaying()){
			new BotError(this, lang("NoList", buildVCommand(MUSIC_PLAY
					+ " [music]")));
		}
		else{
			
			StringBuilder sb = new StringBuilder();
			
			AudioTrack currentTrack = MusicManager.get().getPlayer(this)
					.getAudioPlayer().getPlayingTrack();
			
			sb.append(lang("CurrentTrack", code(currentTrack.getInfo().title)));
			
			if(MusicManager.get().getPlayer(this).getListener().getTrackSize() != 0){
				
				sb.append("\n\n").append(lang("Header")).append("\n\n");
				
				int i = 1;
				
				for(AudioTrack track : MusicManager.get().getPlayer(this)
						.getListener().getTracks()){
					
					sb.append(
							lang("TrackInfo", code(i++),
									code(track.getInfo().title))).append("\n");
					
				}
				
			}
			
			sendMessage(sb.toString());
			
		}
		
	}
	
	@Override
	public String getCall(){
		return MUSIC_LIST;
	}
	
	@Override
	public String getCommandDescription(){
		return lang("Description");
	}
	
}
