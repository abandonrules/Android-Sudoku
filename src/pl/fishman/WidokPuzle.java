package pl.fishman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class WidokPuzle extends View {
	private static final String ZNACZNIK = "SUDOKU";
	private final Gra gra;

	private float szerokosc; // szerokosc jednego pola
	private float wysokosc; // wysokosc jednego pola
	private int wybX; // wybrany indeks X
	private int wybY; // wybrany indeks Y
	private final Rect wybProst = new Rect();
	
	private static final String WYBX = "wybX";
	private static final String WYBY = "wybY";
	private static final String ZOBACZ_STAN = "zobaczStan";
	private static final int ID = 42;

	public WidokPuzle(Context kontekst) {
		super(kontekst);
		this.gra = (Gra) kontekst;
		setFocusable(true);
		setFocusableInTouchMode(true);
		setId(ID);
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable p = super.onSaveInstanceState();
		Log.d(ZNACZNIK, "onSaveInstanceState");
		Bundle zestaw = new Bundle();
		zestaw.putInt(WYBX, wybX);
		zestaw.putInt(WYBY, wybY);
		zestaw.putParcelable(ZOBACZ_STAN, p);
		return zestaw;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Log.d(ZNACZNIK, "onRestoreInstanceState");
		Bundle zestaw = (Bundle) state;
		wybierz(zestaw.getInt(WYBX), zestaw.getInt(WYBY));
		super.onRestoreInstanceState(state);
		return ;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		szerokosc = w / 9f;
		wysokosc = h / 9f;
		wezProst(wybX, wybY, wybProst);
		Log.d(ZNACZNIK, "on SizeChanged: szerokosc: " + szerokosc
				+ ", wysokosc: " + wysokosc);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void wezProst(int x, int y, Rect prost) {
		prost.set((int) (x * szerokosc), (int) (y * wysokosc), (int) (x
				* szerokosc + szerokosc), (int) (y * wysokosc + wysokosc));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Rysuje tlo...
		Paint tlo = new Paint();
		tlo.setColor(getResources().getColor(R.color.puzzle_tlo));
		canvas.drawRect(0, 0, getWidth(), getHeight(), tlo);

		// Rysuje planszę ...
		// Definiuje colory odpowiedzialne za linie siatki
		Paint ciemny = new Paint();
		ciemny.setColor(getResources().getColor(R.color.puzzle_ciemny));

		Paint podswietlenie = new Paint();
		podswietlenie.setColor(getResources().getColor(
				R.color.puzzle_podswietlenie));

		Paint jasny = new Paint();
		jasny.setColor(getResources().getColor(R.color.puzzle_jasny));

		// Rysuje pomniejsze linie siatki
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(0, i * wysokosc, getWidth(), i * wysokosc, jasny);
			canvas.drawLine(0, i * wysokosc + 1, getWidth(), i * wysokosc + 1,
					podswietlenie);
			canvas.drawLine(i * szerokosc, 0, i * szerokosc, getHeight(), jasny);
			canvas.drawLine(i * szerokosc + 1, 0, i * szerokosc + 1,
					getHeight(), podswietlenie);
		}

		// Rysuje główne linie siatki
		for (int i = 0; i < 9; i++) {
			if (i % 3 != 0) {
				continue;
			}
			canvas.drawLine(0, i * wysokosc, getWidth(), i * wysokosc, ciemny);
			canvas.drawLine(0, i * wysokosc + 1, getWidth(), i * wysokosc + 1,
					podswietlenie);
			canvas.drawLine(i * szerokosc, 0, i * szerokosc, getHeight(),
					ciemny);
			canvas.drawLine(i * szerokosc + 1, 0, i * szerokosc + 1,
					getHeight(), podswietlenie);
		}

		// Rysuje cyfry ...
		// Definiuje styl i kolor cyfr
		Paint pierwszy_plan = new Paint(Paint.ANTI_ALIAS_FLAG);
		pierwszy_plan.setColor(getResources().getColor(
				R.color.puzzle_pierwszy_plan));
		pierwszy_plan.setStyle(Style.FILL);
		pierwszy_plan.setTextSize(wysokosc * 0.75f);
		pierwszy_plan.setTextScaleX(szerokosc / wysokosc);
		pierwszy_plan.setTextAlign(Paint.Align.CENTER);

		// Rysuje cyfrę na środku pola
		FontMetrics fm = pierwszy_plan.getFontMetrics();

		// Środkowanie w osi X: stosujemy wyrównanie (oraz X w samym środku)
		float x = szerokosc / 2;
		// Środkowanie w osi Y: mierzymy najpierw wartość wzniesienia/zejścia
		float y = wysokosc / 2 - (fm.ascent + fm.descent) / 2;

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.gra.wezZnakPola(i, j), i * szerokosc + x,
						j * wysokosc + y, pierwszy_plan);
			}
		}

		// Rysuje zaznaczenie ...
		Log.d(ZNACZNIK, "wybProst=" + wybProst);
		Paint zaznaczony = new Paint();
		zaznaczony.setColor(getResources().getColor(R.color.puzzle_zaznaczony));
		canvas.drawRect(wybProst, zaznaczony);

		if (Preferencje.wezPodpowiedzi(getContext())) {
			// Rysuje podpowiedzi ...
			// Wybiera kolor podpowiedzi w zależności od liczby dostępnych
			// ruchów
			Paint podpowiedz = new Paint();
			int c[] = { getResources().getColor(R.color.puzzle_podpowiedz_0),
					getResources().getColor(R.color.puzzle_podpowiedz_1),
					getResources().getColor(R.color.puzzle_podpowiedz_2) };

			Rect r = new Rect();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					int pozostaleruchy = 9 - gra.wezUzytePola(i, j).length;
					if (pozostaleruchy < c.length) {
						wezProst(i, j, r);
						podpowiedz.setColor(c[pozostaleruchy]);
						canvas.drawRect(r, podpowiedz);
					}
				}
			}
		}

		// Rysuje wybraną wartość ...
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(ZNACZNIK, "onKeyDown: kod klawisza=" + keyCode + ", zdarzenie="
				+ event);
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			wybierz(wybX, wybY - 1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			wybierz(wybX, wybY + 1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			wybierz(wybX - -1, wybY);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			wybierz(wybX + 1, wybY);
			break;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_SPACE:
			ustawWybranePole(0);
			break;
		case KeyEvent.KEYCODE_1:
			ustawWybranePole(1);
			break;
		case KeyEvent.KEYCODE_2:
			ustawWybranePole(2);
			break;
		case KeyEvent.KEYCODE_3:
			ustawWybranePole(3);
			break;
		case KeyEvent.KEYCODE_4:
			ustawWybranePole(4);
			break;
		case KeyEvent.KEYCODE_5:
			ustawWybranePole(5);
			break;
		case KeyEvent.KEYCODE_6:
			ustawWybranePole(6);
			break;
		case KeyEvent.KEYCODE_7:
			ustawWybranePole(7);
			break;
		case KeyEvent.KEYCODE_8:
			ustawWybranePole(8);
			break;
		case KeyEvent.KEYCODE_9:
			ustawWybranePole(9);
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			gra.pokazKlawiatureLubBlad(wybX, wybY);
			break;
		default:
			return super.onKeyDown(keyCode, event);
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean czyOk = true;
		if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		wybierz((int) (event.getX() / szerokosc),
				(int) (event.getY() / wysokosc));
		czyOk = gra.pokazKlawiatureLubBlad(wybX, wybY);
		if (!czyOk)
			startAnimation(AnimationUtils.loadAnimation(gra, R.anim.wstrzasy));
		Log.d(ZNACZNIK, "onTouchEvent: x " + wybX + ", y" + wybY);
		return true;
	}

	public void ustawWybranePole(int pole) {
		if (gra.ustawPoleJesliPoprawne(wybX, wybY, pole)) {
			invalidate(); // podpowiedzi mogą ulec zmianie
		} else {
			// Niepoprawna cyfra dla tego pola
			Log.d(ZNACZNIK, "ustawWybranePole: nieprawidłowe: " + pole);
			startAnimation(AnimationUtils.loadAnimation(gra, R.anim.wstrzasy));
		}
	}

	private void wybierz(int x, int y) {
		invalidate(wybProst);
		wybX = Math.min(Math.max(x, 0), 8);
		wybY = Math.min(Math.max(y, 0), 8);
		wezProst(wybX, wybY, wybProst);
		invalidate(wybProst);
	}
}
