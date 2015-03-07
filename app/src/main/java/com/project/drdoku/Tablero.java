package com.project.drdoku;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.View;

public class Tablero extends View {
	private final Juego juego;
	private float width;
	private float height;
	private int selX;
	private int selY;
	private int selRow;
	private int selCol;
	private int selPanelX = -1;
	private final Rect selRect = new Rect();
	private float panelY;
	private Canvas canvas;
	private final int BTN_PANEL_ROW = 10;
	private Paint dark = new Paint();
	private Paint light = new Paint();
	private Paint hilite = new Paint();
	private Paint fg = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint seleccionado = new Paint();
	private boolean lleno;
	private boolean completado;
	private boolean unaVez = false;
	private boolean guardado = false;
	/**
	 * Altura del tablero, 100% = 1, 50% = 2;
	 */
	private float heightRatio = 1.5f;

	public Tablero(Context context) {
		super(context);
		this.juego = (Juego) context;
		dark.setColor(getResources().getColor(R.color.darkLines));
		dark.setStrokeWidth(5);
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		light.setColor(getResources().getColor(R.color.puzzle_light));
		light.setStrokeWidth(5);
		light.setStyle(Style.FILL_AND_STROKE);
		// mHandler.postDelayed(this, 1000L);
		setFocusable(true);
		setFocusableInTouchMode(true);

	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		height /= heightRatio;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		if (!unaVez){
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					if (juego.getCelda(i, j) == 0) {
						seleccionar(i, j);
						unaVez = true;
						break;
					}
				}
				if(unaVez)
					break;
			}
		}
		panelY = getHeight() / heightRatio + height;
		dibujarFondo(canvas);

		// Línea inferior del tablero
		canvas.drawLine(0, panelY - height, getWidth(), panelY - height, dark);

		fg.setColor(getResources().getColor(R.color.static_cell));
		fg.setTextAlign(Paint.Align.CENTER);
		fg.setStyle(Style.FILL);
		fg.setTextSize(height / 2 * 1.25f);
		fg.setTextScaleX(width / (height) / 1.6f);
		FontMetrics fm = fg.getFontMetrics();

		dibujarLineasMenores(canvas, light, hilite);
		dibujarGuia(canvas);
		dibujarCoincidencias(canvas);
		dibujarSeleccionado(canvas);
		dibujarPanel(canvas, fg, fm);
		dibujarLineasMayores(canvas, dark, hilite);
		dibujarNumeros(canvas, fg, fm);

		invalidate();
	}

	private void dibujarGuia(Canvas canvas) {
		for (int i = 0; i < 9; i++) {
			if (i != selRow) {
				canvas.drawRect(i * width, height * selCol, i * width + width,
						height * selCol + height, light);
			}
			if (i != selCol) {
				canvas.drawRect(selRow * width, i * height, selRow * width
						+ width, i * height + height, light);
			}

			for (int j = 0; j < 9; j++) {
				if (i / 3 == selX / 3 && j / 3 == selY / 3) {
					canvas.drawRect(i * width, j * height, i * width + width, j
							* height + height, light);
				}
			}
		}
	}

	private void dibujarCoincidencias(Canvas canvas) {
		for (int i = 0; i < juego.getSudoku1().length; i++) {
			for (int j = 0; j < juego.getSudoku1()[i].length; j++) {
				if (juego.getCelda(i, j) == juego.getCelda(selX, selY)
						&& juego.getCelda(i, j) != 0) {

					if (selX == i || selY == j
							|| (i / 3 == selX / 3 && j / 3 == selY / 3)) {
						Paint seleccionado = new Paint();
						seleccionado.setStyle(Style.FILL);
						seleccionado.setColor(getResources().getColor(
								R.color.red));
						canvas.drawRect(i * width, j * height, i * width
								+ width, j * height + height, seleccionado);
						seleccionado.setStyle(Style.STROKE);
						seleccionado.setStrokeWidth(2);
						seleccionado.setColor(getResources().getColor(
								R.color.puzzle_light));
						canvas.drawRect(i * width, j * height, i * width
								+ width, j * height + height, seleccionado);
						continue;
					}

					Paint seleccionado = new Paint();
					seleccionado.setStyle(Style.FILL);
					seleccionado.setColor(getResources().getColor(
							R.color.puzzle_selected));
					canvas.drawRect(i * width, j * height, i * width + width, j
							* height + height, seleccionado);
				}
			}
		}
	}

	private void dibujarPanel(Canvas canvas, Paint fg, FontMetrics fm) {
		float x = width / 2;
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		fg.setColor(getResources().getColor(R.color.panel_cell));
		for (int i = 0; i < 9; i++) {
			canvas.drawRect(width * i, panelY, (width * i) + width,
					(panelY + height), light);
			canvas.drawText(String.valueOf(i + 1), i * width + x, panelY + y,
					fg);
		}
	}

	private void dibujarSeleccionado(Canvas canvas) {
		seleccionado.setStyle(Style.FILL);
		seleccionado.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, seleccionado);
		seleccionado.setStyle(Style.STROKE);
		seleccionado.setColor(getResources().getColor(R.color.black));
		seleccionado.setStrokeWidth(8);
		canvas.drawRect(selRect, seleccionado);
	}

	private void dibujarNumeros(Canvas canvas, Paint fg, FontMetrics fm) {
		float x = width / 2;
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (juego.getCelda(i, j) != juego.getSudoku1()[i][j]) {
					fg.setColor(getResources().getColor(R.color.dynamic_cell));
				} else {
					fg.setColor(getResources().getColor(R.color.static_cell));
				}
				if (juego.getCelda(i, j) != 0) {
					canvas.drawText(String.valueOf(juego.getCelda(i, j)), i
							* width + x, j * height + y, fg);
				}
			}
		}
	}

	private void dibujarFondo(Canvas canvas) {
		Paint bg = new Paint();
		bg.setColor(getResources().getColor(R.color.white));
		canvas.drawRect(0, 0, getWidth(), getHeight(), bg);
	}

	private void dibujarLineasMayores(Canvas canvas, Paint dark, Paint hilite) {
		for (int i = 0; i < 9; i++) {
			if (i % 3 != 0) {
				continue;
			}

			canvas.drawLine(0, i * height, getWidth(), i * height, dark);
			canvas.drawLine(i * width, 0, i * width, getHeight() / heightRatio,
					dark);
		}
	}

	private void dibujarLineasMenores(Canvas canvas, Paint light, Paint hilite) {
		for (int i = 0; i < 9; i++) {

			canvas.drawLine(0, i * height, getWidth(), i * height, light);
			canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1,
					hilite);
			canvas.drawLine(i * width, 0, i * width, getHeight() / heightRatio,
					light);
			canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight()
					/ heightRatio, hilite);

		}
	}

	private void setRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height),
				(int) (x * width + width), (int) (y * height + height));
	}

	private void seleccionar(int x, int y) {
		boolean suena = false;
		if (x != selX || y != selY)
			suena = true;
		if (x < 9 && y < 9) {
			if (juego.getSudoku1()[x][y] == 0) {
				this.selX = x;
				this.selY = y;
				this.selRow = x;
				this.selCol = y;
				juego.selX = x;
				juego.selY = y;
				selPanelX = -1;
				setRect(x, y, selRect);
				if(suena)
					juego.sp.play(juego.soundIds[0], 0.5f, 0.5f, 1, 0, 1f);
				
				Paint seleccionado = new Paint();
				seleccionado.setColor(getResources().getColor(
						R.color.puzzle_selected));
			}
		} else if (x == BTN_PANEL_ROW || y == BTN_PANEL_ROW) {
			if (juego.getSudoku1()[selX][selY] == 0) {
				if (x == selPanelX)
					suena = false;
				
				selPanelX = x;
				juego.setCelda(selX, selY, x + 1);
				
				if(suena)
					juego.sp.play(juego.soundIds[0], 1, 1, 1, 0, 1f);
				
				lleno = true;
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						if (juego.getCelda(i, j) == 0)
							lleno = false;
					}
					if (!lleno)
						break;
				}

				if (lleno) {
					completado = true;
					for (int i = 0; i < 9; i++) {
						if (!comprobarFila(i) || !comprobarColumna(i))
							completado = false;
					}
					if (completado) {
						if (!comprobarBloque())
							completado = false;
						else {
							if (!guardado) {
								juego.ganar();
								guardado = true;
							}
						}
					}
				}
			}
		}
	}

	private boolean comprobarBloque() {
		for (int i = 0; i < 9; i++) {
			for (int j = i + 1; j < 9; j++) {

				if (juego.getCelda(i / 3, i % 3) == juego
						.getCelda(j / 3, j % 3))
					return false;

			}
		}
		return true;
	}

	private boolean comprobarFila(int nrow) {
		for (int i = 0; i < 9; i++) {
			for (int j = i + 1; j < 9; j++) {
				if (juego.getCelda(nrow, i) == juego.getCelda(nrow, j))
					return false;
			}
		}
		return true;
	}

	private boolean comprobarColumna(int ncol) {
		for (int i = 0; i < 9; i++) {
			for (int j = i + 1; j < 9; j++) {
				if (juego.getCelda(i, ncol) == juego.getCelda(j, ncol))
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			seleccionar((int) (event.getX() / width), (int) (event.getY() / height));
		}
		return true;
		
	}

}
