package pl.fishman;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preferencje extends PreferenceActivity {
	//Nazwy opcji i parametry domyslne
	private static final String OPC_MUZYKA = "muzyka";
	private static final boolean OPC_MUZYKA_DOM = true;
	private static final String OPC_PODPOWIEDZI = "podpowiedzi";
	private static final boolean OPC_PODPOWIEDZI_DOM = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ustawienia);
	}
	
	/** Pobiera bieżącą wartość opcji Muzyka **/
	public static boolean wezMuzyka(Context kontekst) {
		return PreferenceManager.getDefaultSharedPreferences(kontekst)
				.getBoolean(OPC_MUZYKA, OPC_MUZYKA_DOM);
	}
	
	/** Pobiera bieżącą wartość opcji Podpowiedzi **/
	public static boolean wezPodpowiedzi(Context kontekst) {
		return PreferenceManager.getDefaultSharedPreferences(kontekst)
				.getBoolean(OPC_PODPOWIEDZI, OPC_PODPOWIEDZI_DOM);
	}
}
