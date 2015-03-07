package com.project.drdoku;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainScreen extends ActionBarActivity {

	private final int PETICION_JUEGO = 1001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			lanzarOpciones(null);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void lanzarJuego(View view) {
		Intent intent = new Intent(this, Juego.class);
		startActivityForResult(intent, PETICION_JUEGO);
	}
	
	public void lanzarOpciones(View view) {
		Intent intent = new Intent(this, Opciones.class);
		startActivity(intent);
	}
	
	public void lanzarRanking(View view) {
		Intent intent = new Intent(this, Ranking.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("Result", "1");
	    if (requestCode == PETICION_JUEGO) {
	    	Log.d("Result", "2");
	        if (resultCode == RESULT_OK) {
	        	Log.d("Result", "3");
	            Toast.makeText(this, "Felicidades! Has tardado " + data.getExtras().getString("tiempo"), Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	public void salir(View view) {
		finish();
	}
}
