package com.project.drdoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import org.xml.sax.SAXException;

public class Juego extends ActionBarActivity implements Runnable {
/*
	private int[][] sudoku1 = { { 0, 4, 5, 8, 0, 3, 7, 1, 0 },
			{ 8, 1, 0, 0, 0, 0, 0, 2, 4 }, { 7, 0, 9, 0, 0, 0, 5, 0, 8 },
			{ 0, 0, 0, 9, 0, 7, 0, 0, 0 }, { 0, 0, 0, 0, 6, 0, 0, 0, 0 },
			{ 0, 0, 0, 4, 0, 2, 0, 0, 0 }, { 6, 0, 4, 0, 0, 0, 3, 0, 5 },
			{ 3, 2, 0, 0, 0, 0, 0, 8, 7 }, { 0, 5, 7, 3, 0, 8, 2, 6, 0 } };
			*/

    private int[][] sudoku1 = new int[9][9];

//	 private int[][] sudoku1 = { { 2, 4, 5, 8, 9, 3, 7, 1, 6 },
//	{ 8, 1, 3, 5, 7, 6, 9, 2, 4 }, { 7, 6, 9, 2, 1, 4, 5, 3, 8 },
//	 { 5, 3, 6, 9, 8, 7, 1, 4, 2 }, { 4, 9, 2, 1, 6, 5, 8, 7, 3 },
//	{ 1, 7, 8, 4, 3, 2, 6, 5, 9 }, { 6, 8, 4, 7, 2, 1, 3, 9, 5 },
//	 { 3, 2, 1, 6, 5, 9, 4, 8, 7 }, { 9, 5, 7, 3, 4, 8, 2, 6, 0 } };

	private int[][] celdas = new int[9][9];
	public int selX;
	public int selY;
	public long timeInMilliseconds = 0L;
	public long startTime = SystemClock.uptimeMillis();
	public int seconds;
	public int minutes;
	Handler mHandler = new Handler();
	public SoundPool sp = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
	public int soundIds[] = new int[10];
	public String dificultad;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			this.selX = savedInstanceState.getInt("selX");
			this.selY = savedInstanceState.getInt("selY");
			recuperarSudoku(savedInstanceState);
			recuperarCeldas(savedInstanceState);
		} else {
			inicializarCeldas();
			
		}
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		dificultad = pref.getString("dificultad", "easy");
        try {
            recogerSudoku(dificultad);
        } catch (Exception e) {
            Log.e("Error", "No se pudo leer el fichero");
        }

		Tablero tablero = new Tablero(this);
		mHandler.postDelayed(this, 0L);
		setContentView(tablero);
		tablero.requestFocus();

		soundIds[0] = sp.load(this, R.raw.selected, 1);
		//soundIds[1] = sp.load(this, R.raw.selected, 1);
	}

    public void recogerSudoku(String dificultad) throws SAXException, IOException {

        int i = (int) Math.round(Math.random() * (4 - 1) + 1);
        Resources res = getResources();
        Log.d("Dificultad", "Dificultad: " + dificultad);
        InputStream file = getResources().openRawResource(
                getResources().getIdentifier("raw/" + dificultad + i,
                        "raw", getPackageName()));

        BufferedReader r = new BufferedReader(new InputStreamReader(file));
        StringBuilder text = new StringBuilder();
        try {
            StringBuilder total = new StringBuilder();
            String line;
            int j = 0;
            while ((line = r.readLine()) != null) {
                String[] casillas = line.split(",");
                for (int k = 0; k < casillas.length; k++) {
                    setSudoku1Celda(k, j, Integer.valueOf(casillas[k]));
                    setCelda(k, j, Integer.valueOf(casillas[k]));
                }
                j++;
            }
        }
        catch (IOException e) {
            Log.d("e", e.getMessage());
        }
    }

	public void guardarPuntuacion() {
		FileOutputStream fop = null;
		File file;
		try {

			file = new File(this.getFilesDir().getPath().toString()
					+ "ranking.csv");
			fop = new FileOutputStream(file, true);

			if (!file.exists()) {
				file.createNewFile();
			}
			String content = timeInMilliseconds + ", " + dificultad + "\n";
			fop.write(content.getBytes());
			fop.flush();
			fop.close();
		} catch (Exception e) {
			Log.d("Error", e.getMessage());
		}
	}

	private void inicializarCeldas() {
		for (int i = 0; i < sudoku1.length; i++) {
			for (int j = 0; j < sudoku1.length; j++) {
				celdas[i][j] = sudoku1[i][j];
			}
		}
	}

	private void recuperarCeldas(Bundle savedInstanceState) {
		ArrayList<Integer> alCeldas = savedInstanceState
				.getIntegerArrayList("celdas");
		for (int i = 0, j = 0, k = 0; i < alCeldas.size() - 1; i++) {
			if (i % (9) == 0 && i != 0) {
				j++;
				k = 0;
			}
			celdas[j][k++] = alCeldas.get(i);
		}
	}

	private void recuperarSudoku(Bundle savedInstanceState) {
		int rows = savedInstanceState.getInt("sudokuRows");
		ArrayList<Integer> alSudoku = savedInstanceState
				.getIntegerArrayList("sudoku");
		for (int i = 0, j = 0, k = 0; i < alSudoku.size() - 1; i++) {
			if (i % (9) == 0 && i != 0) {
				j++;
				k = 0;
			}
			sudoku1[j][k++] = alSudoku.get(i);
		}
	}

	public int[][] getSudoku1() {
		return sudoku1;
	}
	public void setSudoku1(int[][] sudoku1) {
		this.sudoku1 = sudoku1;
	}
    public void setSudoku1Celda(int i, int j, int value) {
        this.sudoku1[i][j] = value;
    }
	public int[][] getCeldas() {
		return celdas;
	}
	public int getCelda(int i, int j) {
		return celdas[i][j];
	}
	public void setCelda(int i, int j, int value) {
		this.celdas[i][j] = value;
	}

    /*
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		ArrayList<Integer> fldSudoku = new ArrayList<Integer>();
		int sudokuRows = 9;

		for (int i = 0; i < sudoku1.length; i++) {
			for (int j = 0; j < sudoku1[i].length; j++) {
				fldSudoku.add(sudoku1[i][j]);
			}
		}

		ArrayList<Integer> fldCeldas = new ArrayList<Integer>();
		for (int i = 0; i < celdas.length; i++) {
			for (int j = 0; j < celdas[i].length; j++) {
				fldCeldas.add(celdas[i][j]);
			}
		}

		savedInstanceState.putIntegerArrayList("sudoku", fldSudoku);
		savedInstanceState.putIntegerArrayList("celdas", fldCeldas);
		savedInstanceState.putInt("selX", selX);
		savedInstanceState.putInt("selY", selY);
		savedInstanceState.putInt("sudokuRows", sudokuRows);
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}
	*/

	@Override
	public void run() {
		timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
		seconds = (int) (timeInMilliseconds / 1000);
		minutes = seconds / 60;
		seconds = seconds % 60;

		setTitle(String.format("%02d:%02d", minutes, seconds));
		mHandler.postDelayed(this, 1000L);
	}

	public void ganar() {
		guardarPuntuacion();
		Intent returnIntent = new Intent();
		returnIntent.putExtra("tiempo",
				String.format("%02d:%02d", minutes, seconds));
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			sp.release();
			sp = null;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
