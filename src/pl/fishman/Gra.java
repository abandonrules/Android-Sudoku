package pl.fishman;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Gra extends Activity {
	private static final String ZNACZNIK = "Sudoku";
	
	public static final String TRUDNOSC_KLUCZ = "pl.fishman.trudnosc";
	public static final int TRUDNOSC_LATWY = 0;
	public static final int TRUDNOSC_SREDNI = 1;
	public static final int TRUDNOSC_TRUDNY = 2;
	public static final int TRUDNOSC_KONTYNUACJA = -1;
	
	public static final String PREF_PUZZLE = "puzzle";
	
	private int puzzle[] = new int[9*9];
	private WidokPuzle widokPuzzle;
	
	private final int wykorzystane[][][] = new int[9][9][];
	
	private final String puzzleLatwe = 
				"360000000004230800000004200" +
				"070460003820000014500013020" +
				"001900000007048300000000045";
	private final String puzzleSrednie =
				"650000070000506000014000005" +
				"007009000002314700000700800" +
				"500000630000201000030000097";
	private final String puzzleTrudne =
				"009000000080605020501078000" +
				"000000700706040102004000000" +
				"000720903090301080000000600";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(ZNACZNIK,"onCreate");
		
		int trud = getIntent().getIntExtra(TRUDNOSC_KLUCZ, TRUDNOSC_LATWY);
		puzzle = wezPuzzle(trud);
		obliczUzytePola();
		
		widokPuzzle = new WidokPuzle(this);
		setContentView(widokPuzzle);
		widokPuzzle.requestFocus();
		
		// Jeżeli aktywność zostanie zresetowana, przy następnym uruchomieniu gra będzie kontynowana
		getIntent().putExtra(TRUDNOSC_KLUCZ, TRUDNOSC_KONTYNUACJA);
	}
	
	protected boolean pokazKlawiatureLubBlad(int x, int y) {
		int pola[] = wezUzytePola(x,y);
		if(pola.length == 9) {
			Toast toast = Toast.makeText(this, R.string.etykieta_brak_ruchow, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else {
			Log.d(ZNACZNIK, "pokazKlawiature: uzyte=" + doZnakowPuzzle(pola));
			Dialog v = new Klawiatura(this, pola, widokPuzzle);
			v.show();
		}
		return true;
	}
	
	protected boolean ustawPoleJesliPoprawne(int x, int y, int wartosc) {
		int pola[] = wezUzytePola(x,y);
		if(wartosc != 0) {
			for(int pole : pola) {
				if(pole == wartosc)
					return false;
			}
		}
		ustawPole(x, y, wartosc);
		obliczUzytePola();
		return true;
	}
	
	protected int[] wezUzytePola(int x, int y) {
		return wykorzystane[x][y];
	}
	
	private void obliczUzytePola() {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				wykorzystane[x][y] = obliczUzytePola(x,y);
				// Log.d(ZNACZNIK, "wykorzystane[" + x + "][" + y + "] = " + doZnakowPuzzle(wykorzystane[x][y]));
			}
		}
	}
	
	private int[] obliczUzytePola(int x, int y) {
		int c[] = new int[9];
		
		// poziomo
		for(int i = 0; i < 9; i++) {
			if(i == y)
				continue;
			int t = wezPole(x, i);
			if(t != 0)
				c[t-1] = t;
		}
		
		// pionowo
		for(int i = 0; i < 9; i++) {
			if(i == x)
				continue;
			int t = wezPole(i, y);
			if(t != 0)
				c[t-1] = t;
		}
		
		// w zakresie bloku
		int startx = (x / 3) * 3;
		int starty = (y / 3) * 3;
		for(int i = startx; i < startx + 3; i++) {
			for(int j = starty; j < starty + 3; j++) {
				if(i == x && j == y)
					continue;
				int t = wezPole(i,j);
				if(t != 0)
					c[t-1] = t;
			}
		}
		
		// kompresja
		int nuzyty = 0;
		for(int t : c) {
			if(t != 0)
				nuzyty++;
		}
		int c1[] = new int[nuzyty];
		nuzyty = 0;
		for(int t: c) {
			if(t != 0)
				c1[nuzyty++] = t;
		}
		return c1;
	}
	
	private int[] wezPuzzle(int trud) {
		String puz;
		switch(trud) {
		case TRUDNOSC_KONTYNUACJA:
			puz = getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE, puzzleLatwe);
			break;
		case TRUDNOSC_TRUDNY:
			puz = puzzleTrudne;
			break;
		case TRUDNOSC_SREDNI:
			puz = puzzleSrednie;
			break;
		case TRUDNOSC_LATWY:
			puz = puzzleLatwe;
			break;
		default:
			puz = puzzleLatwe;
		}
		return odZnakowPuzzle(puz);
	}
	
	static private String doZnakowPuzzle(int[] puz) {
		StringBuilder buf = new StringBuilder();
		for(int element : puz) {
			buf.append(element);
		}
		return buf.toString();
	}
	
	static protected int[] odZnakowPuzzle(String string) {
		int[] puz = new int[string.length()];
		for(int i = 0; i < puz.length; i++) {
			puz[i] = string.charAt(i) - '0';
		}
		return puz;
	}
	
	private int wezPole(int x, int y) {
		return puzzle[y * 9 + x];
	}
	
	private void ustawPole(int x, int y, int wartosc) {
		puzzle[y*9 + x] = wartosc;
	}
	
	protected String wezZnakPola(int x, int y) {
		int v = wezPole(x, y);
		if (v == 0)
			return "";
		else
			return String.valueOf(v);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Muzyka.play(this, R.raw.gra);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(ZNACZNIK, "onPause");
		Muzyka.stop(this);
		
		// Zapisuje bieżącą łamigłówkę
		getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE, doZnakowPuzzle(puzzle)).commit();
	}
}
