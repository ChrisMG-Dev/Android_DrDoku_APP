package com.project.drdoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.SAXException;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class Ranking extends ActionBarActivity {

	private List<Score> puntuaciones = new ArrayList<Score>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking_screen);
		try {
			leerPuntuaciones();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		popularScoreItemView();
	}
	

	private void popularScoreItemView() {
		ArrayAdapter<Score> adaptador = new AdaptadorScore();
		ListView lista = (ListView) findViewById(R.id.listView1);
		lista.setAdapter(adaptador);
	}
	
	private class AdaptadorScore extends ArrayAdapter<Score> {

		boolean topPuesto = false;
		
		public AdaptadorScore() {
			super(Ranking.this, R.layout.score_item_view, puntuaciones);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
				if (itemView == null) {
					itemView = getLayoutInflater().inflate(R.layout.score_item_view,  parent, false);
				}
			Score puntuacionActual = puntuaciones.get(position);
			
			TextView tv = (TextView) itemView.findViewById(R.id.tvTime);
			int tiempo = Integer.valueOf(puntuacionActual.getTiempo());
			int segundos = (tiempo / 1000);
			int minutos = segundos / 60;
			segundos = segundos % 60;
			tv.setText(String.format("%02d:%02d", minutos, segundos));
			TextView tvDif = (TextView) itemView.findViewById(R.id.tvDificulty);
			tvDif.setText(puntuacionActual.getDificultad());
			ImageView iv = (ImageView) itemView.findViewById(R.id.ivTrophy);
			
			if (Integer.valueOf(puntuacionActual.getTiempo()) < 360000)
				iv.setImageResource(R.drawable.oro);
			else if (Integer.valueOf(puntuacionActual.getTiempo()) < 720000)
				iv.setImageResource(R.drawable.plata);
			else {
				iv.setImageResource(R.drawable.bronce);
			}	
			
			return itemView;
		}
	}
	
	public void leerPuntuaciones() throws SAXException, IOException {
		File file = new File(this.getFilesDir().getPath().toString() + "ranking.csv");
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	Log.d("Línea", line);
		    	String[] sp = line.split(",");
		    	if (sp.length > 1)
		    		puntuaciones.add(new Score(0, sp[1], sp[0]));
		    }
		    br.close();
		    Collections.sort(puntuaciones);
		}
		catch (IOException e) {
		    Log.d("e", e.getMessage());
		}
	}

}
