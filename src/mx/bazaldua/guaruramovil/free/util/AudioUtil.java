package
mx.bazaldua.guaruramovil.free.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class AudioUtil {

	private AudioManager audioManager;
	private MediaPlayer mediaPlayer;
	private int volumeMedia;
	
	/**
	 * 
	 * @param context
	 * @param resId
	 * @param volumeLeft
	 * @param volumeRight
	 */
	public AudioUtil( Context context, int resId, float volumeLeft, float volumeRight ) {
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mediaPlayer  = MediaPlayer.create(context, resId);
		mediaPlayer.setVolume( volumeLeft , volumeRight);
	}

	/**
	 * 
	 */
	public void start () {
		volumeMedia = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		audioManager.setStreamVolume( AudioManager.STREAM_MUSIC , audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		mediaPlayer.start();
	}
	
	public void stop() {
		restoreVolume();
		mediaPlayer.stop();
		mediaPlayer.release();
	}
	
	public void restoreVolume() {
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeMedia, 0);
	}
	
	/**
	 * 
	 * @param loop
	 */
	public void setLoop( boolean loop ) {
		mediaPlayer.setLooping( loop );
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		boolean result = false;
		try {
			result = mediaPlayer == null ? false : mediaPlayer.isPlaying();
		} catch (Exception e) {
			//Nada
		}
		return result;
	}
}
