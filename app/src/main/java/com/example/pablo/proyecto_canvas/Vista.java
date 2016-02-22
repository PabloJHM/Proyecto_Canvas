package com.example.pablo.proyecto_canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class Vista extends View {
    private Paint pincel;
    int ancho,alto;
    private Canvas lienzoFondo;
    private Bitmap mapaDeBits;
    private Path camino;
    private int colorp = 0xff000000;
    private float tamanoPincel;
    private boolean erase=false, circulo=false,cuadrado=false;
    private Path rectaPoligonal =new Path();

    public Vista(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        tamanoPincel=16;
        camino=new Path();
        pincel=new Paint();
        pincel.setColor(colorp);
        pincel.setAntiAlias(true);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeWidth(tamanoPincel);
        pincel.setStrokeJoin(Paint.Join.ROUND);
        pincel.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas lienzo) {
        super.onDraw(lienzo);
        lienzo.drawBitmap(mapaDeBits, 0, 0, null);
        lienzo.drawPath(camino, pincel);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ancho=w;
        alto=h;
        mapaDeBits = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);

    }
    float x0,y0,xi,yi;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(x0<1){
            x0= event.getX();
        }
        if(y0<1){
            y0= event.getY();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                camino.reset();
                camino.moveTo(x, y);
                rectaPoligonal.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                xi=x;
                yi=y;
                rectaPoligonal.lineTo(x, y);
                if(!cuadrado && !circulo){
                    camino.lineTo(x, y);
                } else if(cuadrado){
                    rectaPoligonal.lineTo(x, y);
                    lienzoFondo.drawRect(x0, y0, xi, yi, pincel);
                } else {
                    float radio = (float) Math.sqrt(Math.pow(xi-x0, 2) + Math.pow(yi-y0, 2));
                    lienzoFondo.drawCircle(x0,y0,radio,pincel);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(circulo) {
                    float radio = (float) Math.sqrt(Math.pow(xi-x0, 2) + Math.pow(yi-y0, 2));
                    lienzoFondo.drawCircle(x0,y0,radio,pincel);
                } else if(cuadrado){
                    lienzoFondo.drawRect(x0, y0, xi, yi, pincel);
                } else {
                    lienzoFondo.drawPath(camino, pincel);
                }
                camino.reset();
                x0=y0=0;
                break;
        }
        invalidate();
        return true;
    }

    public void setColor(String c){
        invalidate();
        System.out.println(c);
        colorp= Color.parseColor(c);
        pincel.setColor(colorp);
    }

    public void setTamanoPincel(float tamano){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                tamano, getResources().getDisplayMetrics());
        tamanoPincel=pixelAmount;
        pincel.setStrokeWidth(tamanoPincel);
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) pincel.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else pincel.setXfermode(null);
    }

    //Para saber si se ha seleccionado o no el cuadrado
    public void setCirculo(boolean circulo) {
        this.circulo = circulo;
    }

    //Para saber si se ha seleccionado o no el circulo
    public void setCuadrado(boolean cuadrado) {
        this.cuadrado = cuadrado;
    }

    //Borramos el lienzo
    public void nuevo(){
        lienzoFondo.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    //Cargar la imagen que hemos abierto
    public void abreImagen(String path){
        lienzoFondo.drawBitmap(BitmapFactory.decodeFile(path),FOCUS_LEFT,FOCUS_UP,pincel);
    }

}
