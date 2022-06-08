package ar.com.andessalud.andes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import androidx.appcompat.app.AppCompatActivity;


public class mostrar_archivos extends AppCompatActivity {
    static Dialog dialog;
    static String rutaAlServidorDeArchivos = "http://www.quan.com.ar/";
    static String archivoABajar="";
    static String nombreLocal="";
    String mensajeErrorLocal, archivoAMostrar, accion;

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(android.R.style.Theme_Translucent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_archivos);
        Bundle extras = getIntent().getExtras();
        String folder_main = "myApp";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        if (extras != null) {
            rutaAlServidorDeArchivos= extras.getString("rutaServidor");
            archivoAMostrar = extras.getString("docAbrir");
            mensajeErrorLocal = extras.getString("mensajeError");
            accion = extras.getString("accion");
            String dstPath = Environment.getExternalStorageDirectory() + File.separator + "myApp" + File.separator;
            File archivoLocal = new File(dstPath, archivoAMostrar);
            if (archivoLocal.exists()==true) {
                if (accion.equals("ver")) {
                    verPDFLocal(archivoAMostrar);
                }else {
                    enviarPDF(archivoAMostrar);
                }
            } else{
                archivoABajar=rutaAlServidorDeArchivos+archivoAMostrar;
                nombreLocal=archivoAMostrar;
                new DownloadFileFromURL().execute(archivoABajar);
            }
        }
    }

    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Descargando documento. Por favor espere...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                String SDCardRoot = Environment.getExternalStorageDirectory() + File.separator + "myApp";
                File file = new File(SDCardRoot,nombreLocal);
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Toast.makeText(mostrar_archivos.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String nombreArchivo) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            verPDFLocal(archivoAMostrar);
        }
    }

    void verPDFLocal(String nombreArchivo){
        String dstPath = Environment.getExternalStorageDirectory() + File.separator + "myApp" + File.separator;
        File dst = new File(dstPath);
        File file = new File(dstPath, nombreArchivo);
        try {
            Uri path = Uri.fromFile(file);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }catch (Exception ex){
            dialog = new Dialog(mostrar_archivos.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dlg_modal_error);
            TextView textMsgError = (TextView) dialog.findViewById(R.id.lblMsg);
            textMsgError.setText("Error al mostrar el archivo:\n" + ex.getMessage());
            LinearLayout lnMsgTotal=(LinearLayout)dialog.findViewById(R.id.lnMsgTotal);
            lnMsgTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    void enviarPDF(String nombreArchivo){
        String dstPath = Environment.getExternalStorageDirectory() + File.separator + "myApp" + File.separator;
        File dst = new File(dstPath);
        File file = new File(dstPath, nombreArchivo);
        Uri u = null;
        u = Uri.fromFile(file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/*");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Envio de formulario especial");
        // + "\n\r" + "\n\r" +
        // feed.get(Selectedposition).DETAIL_OBJECT.IMG_URL
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Formulario especial");
        emailIntent.putExtra(Intent.EXTRA_STREAM, u);
        startActivity(Intent.createChooser(emailIntent, "Enviar formulario especial"));
        finish();
    }
}
