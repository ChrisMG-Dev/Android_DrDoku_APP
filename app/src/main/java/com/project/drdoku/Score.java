package com.project.drdoku;

public class Score implements Comparable<Score> {
	private int imgId;
	private String dificultad;
	private String tiempo;

	public Score(int imgId, String dificultad, String tiempo) {
		super();
		this.imgId = imgId;
		this.dificultad = dificultad;
		this.tiempo = tiempo;
	}

	public int getImgId() {
		return imgId;
	}

	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	public String getDificultad() {
		return dificultad;
	}

	public void setDificultad(String dificultad) {
		this.dificultad = dificultad;
	}

	public String getTiempo() {
		return tiempo;
	}

	public void setTiempo(String tiempo) {
		this.tiempo = tiempo;
	}

	public int compareTo(Score otro) {
		return Integer.valueOf(tiempo).compareTo(Integer.valueOf(otro.tiempo));
	}

}
