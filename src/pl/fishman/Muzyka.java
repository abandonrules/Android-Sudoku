package pl.fishman;

import android.content.Context;
import android.media.MediaPlayer;

public class Muzyka {
	private static MediaPlayer mp = null;

	/** Zatrzymuje stary utwór i rozpoczyna odtwarzanie nowego **/
	public static void play(Context kontekst, int zasob) {
		stop(kontekst);

		// Odtwarza muzykę pod warunkiemm że nie została wyłączona w
		// preferencjach
		if (Preferencje.wezMuzyka(kontekst)) {
			mp = MediaPlayer.create(kontekst, zasob);
			mp.setLooping(true);
			mp.start();
		}
	}

	/** Zatrzymuje odtwarzanie muzyki **/
	public static void stop(Context kontekst) {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
	}
}
