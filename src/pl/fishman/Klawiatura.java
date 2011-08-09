package pl.fishman;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class Klawiatura extends Dialog {
	
	protected static final String ZNACZNIK = "Sudoku";
	
	private final View klawisze[] = new View[9];
	private View klawiatura;
	
	private final int uzyte[];
	private final WidokPuzle widokPuzzle;
	
	public Klawiatura(Context kontekst, int uzyte[], WidokPuzle widokPuzzle) {
		super(kontekst);
		this.uzyte = uzyte;
		this.widokPuzzle = widokPuzzle;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.tytul_klawiatura);
		setContentView(R.layout.klawiatura);
		znajdzWidoki();
		for(int element : uzyte) {
			if(element != 0)
				klawisze[element-1].setVisibility(View.INVISIBLE);
		}
		ustawObiektyNasluchujace();
	}
	
	private void znajdzWidoki() {
		klawiatura = findViewById(R.id.klawiatura);
		klawisze[0] = findViewById(R.id.klawiatura_1);
		klawisze[1] = findViewById(R.id.klawiatura_2);
		klawisze[2] = findViewById(R.id.klawiatura_3);
		klawisze[3] = findViewById(R.id.klawiatura_4);
		klawisze[4] = findViewById(R.id.klawiatura_5);
		klawisze[5] = findViewById(R.id.klawiatura_6);
		klawisze[6] = findViewById(R.id.klawiatura_7);
		klawisze[7] = findViewById(R.id.klawiatura_8);
		klawisze[8] = findViewById(R.id.klawiatura_9);
	}
	
	private void ustawObiektyNasluchujace() {
		for (int i = 0; i < klawisze.length; i++) {
			final int t = i+1;
			klawisze[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					zwrocWynik(t);
				}
			});
		}
		klawiatura.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				zwrocWynik(0);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int kodKlaw, KeyEvent zdarzenie) {
		int pole = 0;
		switch (kodKlaw) {
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE: pole = 0; break;
		case KeyEvent.KEYCODE_1: pole = 1; break;
		case KeyEvent.KEYCODE_2: pole = 2; break;
		case KeyEvent.KEYCODE_3: pole = 3; break;
		case KeyEvent.KEYCODE_4: pole = 4; break;
		case KeyEvent.KEYCODE_5: pole = 5; break;
		case KeyEvent.KEYCODE_6: pole = 6; break;
		case KeyEvent.KEYCODE_7: pole = 7; break;
		case KeyEvent.KEYCODE_8: pole = 8; break;
		case KeyEvent.KEYCODE_9: pole = 9; break;
		default:
			return super.onKeyDown(kodKlaw, zdarzenie);
		}
		if(isValid(pole)) {
			zwrocWynik(pole);
		}
		return true;
	}
	
	private boolean isValid(int pole) {
		for(int t : uzyte) {
			if(pole == t)
				return false;
		}
		return true;
	}
	
	private void zwrocWynik(int pole) {
		widokPuzzle.ustawWybranePole(pole);
		dismiss();
	}
}
