package com.example.pablo.proyecto_canvas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class MainActivity extends Activity {
    private Vista vista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vista=(Vista)findViewById(R.id.drawing);


    }

    public void pincel(View v){
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle(R.string.tamanopin);
        brushDialog.setContentView(R.layout.seleccionatamanio);

        //Para seleccionar el tamaño del picel
        ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                vista.setErase(false);
                vista.setCirculo(false);
                vista.setCuadrado(false);
                vista.setTamanoPincel(8);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista.setErase(false);
                vista.setCirculo(false);
                vista.setCuadrado(false);
                vista.setTamanoPincel(16);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista.setErase(false);
                vista.setCirculo(false);
                vista.setCuadrado(false);
                vista.setTamanoPincel(32);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    public void borrar(View v){
        //Para borrar. En la aplicacion lo que se vaya a borrar se verá de color negro antes de borrarse
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle(R.string.tamanobor);
        brushDialog.setContentView(R.layout.seleccionatamanio);
        //Elegir el tamaño de la goma
        ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista.setCirculo(false);
                vista.setCuadrado(false);
                vista.setErase(true);
                vista.setTamanoPincel(8);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista.setCirculo(false);
                vista.setCuadrado(false);
                vista.setErase(true);
                vista.setTamanoPincel(16);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vista.setCirculo(false);
                vista.setCuadrado(false);
                vista.setErase(true);
                vista.setTamanoPincel(32);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    public void nuevo(View v){
        //Borrar todo y empezar un canvas nuevo
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle(R.string.titulonuevo);
        newDialog.setMessage(R.string.preguntanuevo);
        newDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vista.nuevo();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    public void guardar(View v){
        //Guardar la imagen en la memoria externa
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle(R.string.tituloGuardar);
        saveDialog.setMessage(R.string.preguntaGuardar);
        saveDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                vista.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), vista.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png", "drawing");
                if (imgSaved != null) {
                    Toast savedToast = Toast.makeText(getApplicationContext(),
                            R.string.guardado, Toast.LENGTH_SHORT);
                    savedToast.show();
                } else {
                    Toast unsavedToast = Toast.makeText(getApplicationContext(),
                            R.string.noguardado, Toast.LENGTH_SHORT);
                    unsavedToast.show();
                }
                vista.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    public void abrir(View v){
        //Metodo para abrir una imagen de la galeria y poder pintar sobre ella
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = getPathFromCameraData(data, this);
                    File imgFile = new File(filePath);
                    if (imgFile.exists()) {
                        vista.abreImagen(filePath);
                    }
                }
        }
    }
    public static String getPathFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    //Cambiar color
    public void color(View v){
        vista.setErase(false);
        vista.setTamanoPincel(16);
        String color = v.getTag().toString();
        System.out.println(color);
        vista.setColor(color);
    }

    //Cambiar del pincel a dibujar circulos
    public void circulo(View v) {
        vista.setCirculo(true);
        vista.setCuadrado(false);
    }

    //Cambiar del pincel a dibujar cuadrados
     public void cuadrado(View v){
         vista.setCirculo(false);
         vista.setCuadrado(true);
     }

}
