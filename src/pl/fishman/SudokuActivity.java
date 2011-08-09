package pl.fishman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SudokuActivity extends Activity implements OnClickListener {
	private static final String ZNACZNIK = "Sudoku";
	
	private void otworzDialogNowaGra() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.tytul_nowa_gra)
			.setItems(R.array.trudnosc, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					uruchomGre(which);
				}
			})
			.show();
	}
	
	private void uruchomGre(int which) {
		Log.d(ZNACZNIK, "kliknieto " + which);
		Intent intencja = new Intent(SudokuActivity.this, Gra.class);
		intencja.putExtra(Gra.TRUDNOSC_KLUCZ, which);
		startActivity(intencja);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Konfigurujemy obiekty nasłuchujące kliknięć dla wszystkich przycisków
        View przyciskKontynuacja = findViewById(R.id.przycisk_kontynuacja);
        przyciskKontynuacja.setOnClickListener(this);
        
        View przyciskNowa = findViewById(R.id.przycisk_nowa_gra);
        przyciskNowa.setOnClickListener(this);
        
        View przyciskInformacje = findViewById(R.id.przycisk_informacje);
        przyciskInformacje.setOnClickListener(this);
        
        View przyciskWyjscie = findViewById(R.id.przycisk_wyjscie);
        przyciskWyjscie.setOnClickListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater wypelniacz = getMenuInflater();
    	wypelniacz.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.ustawienia:
    		startActivity(new Intent(this, Preferencje.class));
    		return true;
    	}
    	return false;
    }
    
    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.przycisk_kontynuacja:
    		uruchomGre(Gra.TRUDNOSC_KONTYNUACJA);
    		break;
		case R.id.przycisk_informacje:
			Intent i = new Intent(this, Informacje.class);
			startActivity(i);
			break;
		case R.id.przycisk_nowa_gra:
			otworzDialogNowaGra();
			break;
		case R.id.przycisk_wyjscie:
			finish();
			break;
		default:
			break;
		}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	//Muzyka.play(this, R.raw.glowna);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	Muzyka.stop(this);
    }
}