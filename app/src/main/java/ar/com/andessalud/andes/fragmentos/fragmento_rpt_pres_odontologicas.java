package ar.com.andessalud.andes.fragmentos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.BuildConfig;
import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.datosPrestador;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.funciones;

public class fragmento_rpt_pres_odontologicas extends Fragment {

    FragmentChangeTrigger trigger;
    private Dialog dialogo;
    LinearLayout lnDesdeFecha, lnHastaFecha, lnRptPrestOdonto;
    String _datoFecDesde = "---", _datoFecHasta = "---";
    TextView txtDesdeFecha, txtHastaFecha;
    String prestador="Prestador de prueba";
    String[] idAfiliado, fecPrestacion, prestacion, diente, cara, codPrestacionTot, prestacionTot, cantidadTot
        ,idAfiliadoGen, apellNombAfiliadoGen, nroAfiliadoGen, coseguro, montoTotal, coseguroTot, montoTot, planPrestacionalGen;
    private File pdfFile;
    private static BaseColor colorAzul=new BaseColor(84,91,119);
    private static BaseColor colorRojo=new BaseColor(255,00,00);
    private Font smallBold = new Font(Font.FontFamily.COURIER, 10,
            Font.BOLD);
    private Font smallBlue = new Font(Font.FontFamily.COURIER, 10,
            Font.NORMAL, colorAzul);
    private Font logoFont = new Font(Font.FontFamily.COURIER, 8,
            Font.NORMAL,colorAzul);
    private Font logoFontBold = new Font(Font.FontFamily.COURIER, 10,
            Font.BOLD,colorAzul);
    private Font titDetPrestacionesBold = new Font(Font.FontFamily.COURIER, 10,
            Font.BOLD, colorAzul);
    private Font titNoDatosBold = new Font(Font.FontFamily.COURIER, 12,
            Font.BOLD, colorRojo);

    public fragmento_rpt_pres_odontologicas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_rpt_pres_odontologicas, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lnDesdeFecha = (LinearLayout) view.findViewById(R.id.lnDesdeFecha);
        lnHastaFecha = (LinearLayout) view.findViewById(R.id.lnHastaFecha);
        lnRptPrestOdonto = (LinearLayout) view.findViewById(R.id.lnRptPrestOdonto);
        txtDesdeFecha = (TextView) view.findViewById(R.id.txtDesdeFecha);
        txtHastaFecha = (TextView) view.findViewById(R.id.txtHastaFecha);

        lnDesdeFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dia = "00" + String.valueOf(dayOfMonth);
                        String diaDosDigitos = dia.substring(dia.length() - 2, dia.length());
                        String mes = "00" + String.valueOf(monthOfYear+1);
                        String mesDosDigitos = mes.substring(mes.length() - 2, mes.length());

                        _datoFecDesde=diaDosDigitos+'/'+mesDosDigitos+'/'+String.valueOf(year);
                        txtDesdeFecha.setText(_datoFecDesde);
                    }
                }, yy, mm, dd);

                datePicker.show();
            }
        });

        lnHastaFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendario = Calendar.getInstance();
                int yy = calendario.get(Calendar.YEAR);
                int mm = calendario.get(Calendar.MONTH);
                int dd = calendario.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dia = "00" + String.valueOf(dayOfMonth);
                        String diaDosDigitos = dia.substring(dia.length() - 2, dia.length());
                        String mes = "00" + String.valueOf(monthOfYear+1);
                        String mesDosDigitos = mes.substring(mes.length() - 2, mes.length());

                        _datoFecHasta=diaDosDigitos+'/'+mesDosDigitos+'/'+String.valueOf(year);
                        txtHastaFecha.setText(_datoFecHasta);
                    }
                }, yy, mm, dd);

                datePicker.show();
            }
        });

        lnRptPrestOdonto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarReporte();
            }
        });
    }

    //region armar reporte
    public void solicitarReporte() {
        try{
            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Armando el reporte");

            Map<String, String> params = new HashMap<String, String>();
            params.put("idUsuario", datosPrestador.leerIdUsuario());
            params.put("fechaDesde", _datoFecDesde);
            params.put("fechaHasta", _datoFecHasta);
            Log.d("checkingdata", String.valueOf(params));
            fabrica_WS.APPEXTRptPrestacionesOdonto(getContext(), params, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    prestador = valores.getElementsByTagName("nombreConvenio").item(0).getTextContent();

                    idAfiliado = new String[valores.getElementsByTagName("idAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliado").getLength(); i++) {
                        idAfiliado[i] = valores.getElementsByTagName("idAfiliado").item(i).getTextContent();
                    }

                    fecPrestacion = new String[valores.getElementsByTagName("fecPrestacion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("fecPrestacion").getLength(); i++) {
                        fecPrestacion[i] = valores.getElementsByTagName("fecPrestacion").item(i).getTextContent();
                    }

                    prestacion = new String[valores.getElementsByTagName("prestacion").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("prestacion").getLength(); i++) {
                        prestacion[i] = valores.getElementsByTagName("prestacion").item(i).getTextContent();
                    }

                    diente = new String[valores.getElementsByTagName("diente").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("diente").getLength(); i++) {
                        diente[i] = valores.getElementsByTagName("diente").item(i).getTextContent();
                    }

                    cara = new String[valores.getElementsByTagName("cara").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("cara").getLength(); i++) {
                        cara[i] = valores.getElementsByTagName("cara").item(i).getTextContent();
                    }

                    coseguro = new String[valores.getElementsByTagName("coseguro").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("coseguro").getLength(); i++) {
                        coseguro[i] = valores.getElementsByTagName("coseguro").item(i).getTextContent();
                    }

                    montoTotal = new String[valores.getElementsByTagName("montoTotal").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("montoTotal").getLength(); i++) {
                        montoTotal[i] = valores.getElementsByTagName("montoTotal").item(i).getTextContent();
                    }

                    codPrestacionTot = new String[valores.getElementsByTagName("codPrestacionTot").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("codPrestacionTot").getLength(); i++) {
                        codPrestacionTot[i] = valores.getElementsByTagName("codPrestacionTot").item(i).getTextContent();
                    }

                    prestacionTot = new String[valores.getElementsByTagName("prestacionTot").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("prestacionTot").getLength(); i++) {
                        prestacionTot[i] = valores.getElementsByTagName("prestacionTot").item(i).getTextContent();
                    }

                    cantidadTot = new String[valores.getElementsByTagName("cantidadTot").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("cantidadTot").getLength(); i++) {
                        cantidadTot[i] = valores.getElementsByTagName("cantidadTot").item(i).getTextContent();
                    }

                    coseguroTot = new String[valores.getElementsByTagName("coseguroTot").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("coseguroTot").getLength(); i++) {
                        coseguroTot[i] = valores.getElementsByTagName("coseguroTot").item(i).getTextContent();
                    }

                    montoTot = new String[valores.getElementsByTagName("montoTot").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("montoTot").getLength(); i++) {
                        montoTot[i] = valores.getElementsByTagName("montoTot").item(i).getTextContent();
                    }

                    idAfiliadoGen = new String[valores.getElementsByTagName("idAfiliadoGen").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliadoGen").getLength(); i++) {
                        idAfiliadoGen[i] = valores.getElementsByTagName("idAfiliadoGen").item(i).getTextContent();
                    }

                    apellNombAfiliadoGen = new String[valores.getElementsByTagName("apellNombAfiliadoGen").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("apellNombAfiliadoGen").getLength(); i++) {
                        apellNombAfiliadoGen[i] = valores.getElementsByTagName("apellNombAfiliadoGen").item(i).getTextContent();
                    }

                    nroAfiliadoGen = new String[valores.getElementsByTagName("nroAfiliadoGen").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nroAfiliadoGen").getLength(); i++) {
                        nroAfiliadoGen[i] = valores.getElementsByTagName("nroAfiliadoGen").item(i).getTextContent();
                    }

                    planPrestacionalGen = new String[valores.getElementsByTagName("planPrestacionalGen").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("planPrestacionalGen").getLength(); i++) {
                        planPrestacionalGen[i] = valores.getElementsByTagName("planPrestacionalGen").item(i).getTextContent();
                    }
                    crearPDF();
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        }catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al armar el reporte de prestaciones odontologicas:\n" + e.getMessage());
        }
    }
    private void crearPDF() {
        try {
            File FILE = new File(Environment.getExternalStorageDirectory() + File.separator + "myApp" + File.separator);
            if (!FILE.exists()) {
                FILE.mkdir();
            }
            pdfFile = new File(FILE.getAbsolutePath(), "Liq_"+prestador.replace(" ","_")+_datoFecDesde.replace("/","_")+_datoFecHasta.replace("/","_")+".pdf");
            Log.d("dadart", String.valueOf(pdfFile));
            if(pdfFile.exists()){
                pdfFile.delete();
            }
            com.itextpdf.text.Document documento = new com.itextpdf.text.Document(PageSize.A4, 36, 36, 50, 55);
            PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(pdfFile));
            writer.setPageEvent(new MyFooter());

            documento.open();
            addMetaData(documento);
            addEncabezado(documento);
            addSubEncabezado(documento);

            if (idAfiliado.length>0) {
                addTblTotales(documento);
                addTblPorAfiliado(documento);
            }else {
                addSinDatos(documento);
            }
            documento.close();
            previewPdf();
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMetaData(com.itextpdf.text.Document document) {
        document.addTitle("Liquidación de prestadores odontológicos");
        document.addSubject("Desde el "+_datoFecDesde+" hasta el "+_datoFecHasta);
        document.addKeywords("Liquidacion, odontología");
        document.addAuthor("APP de Andes salud");
        document.addCreator("Andes salud");
    }

    private void addEncabezado(com.itextpdf.text.Document document) {
        try {
            InputStream ims = getActivity().getAssets().open("rpt_odonto_enc_500_1.png");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            //image.setAbsolutePosition(0, 0);
            document.add(image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addSubEncabezado(com.itextpdf.text.Document document){
        try {
            Paragraph lblPrestadorTitulo = new Paragraph("Prestador: ", logoFont);
            lblPrestadorTitulo.add(new Chunk(prestador, logoFontBold));
            lblPrestadorTitulo.setIndentationLeft(100);
            document.add(lblPrestadorTitulo);
            Paragraph lblDesdeHastaTitulo = new Paragraph("Desde / Hasta: ", logoFont);
            lblDesdeHastaTitulo.add(new Chunk(_datoFecDesde+" / "+_datoFecHasta, logoFontBold));
            lblDesdeHastaTitulo.setIndentationLeft(100);
            addEmptyLine(lblDesdeHastaTitulo, 1);
            document.add(lblDesdeHastaTitulo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addTblTotales(com.itextpdf.text.Document document) {
        try {
            int totalPrestaciones=0;
            float totalCoseguro=0, totalMonto=0;
            PdfPTable table = new PdfPTable(5);
            table.setTotalWidth(new float[]{40, 215, 35, 55,55 });
            table.setLockedWidth(true);

            PdfPCell c1 = new PdfPCell(new Phrase("Cod. Prest.", logoFontBold));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1.setBorderColor(colorAzul);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Descripción", logoFontBold));
            c1.setBorderColor(colorAzul);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);

            c1 = new PdfPCell(new Phrase("Cant.", logoFontBold));
            c1.setBorderColor(colorAzul);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.setHeaderRows(1);

            c1 = new PdfPCell(new Phrase("Coseg.", logoFontBold));
            c1.setBorderColor(colorAzul);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.setHeaderRows(1);

            c1 = new PdfPCell(new Phrase("Arancel", logoFontBold));
            c1.setBorderColor(colorAzul);
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.setHeaderRows(1);

            //Comenzamos con el detalle de los totales
            for (int i = 0; i < codPrestacionTot.length; i++) {
                c1 = new PdfPCell(new Phrase(codPrestacionTot[i], smallBlue));
                c1.setBorderColor(colorAzul);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(prestacionTot[i], smallBlue));
                c1.setBorderColor(colorAzul);
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(cantidadTot[i], smallBlue));
                c1.setBorderColor(colorAzul);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(coseguroTot[i], smallBlue));
                c1.setBorderColor(colorAzul);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);

                c1 = new PdfPCell(new Phrase(montoTot[i], smallBlue));
                c1.setBorderColor(colorAzul);
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);

                totalPrestaciones+= Integer.parseInt(cantidadTot[i]);
                totalCoseguro+= Float.parseFloat(coseguroTot[i]);
                totalMonto+= Float.parseFloat(montoTot[i]);
            }
            document.add(table);

            PdfPTable tableTotalGen = new PdfPTable(4);
            tableTotalGen.setTotalWidth(new float[]{255, 35, 55,55 });
            tableTotalGen.setLockedWidth(true);
            PdfPCell c1TotalGen = new PdfPCell(new Phrase("TOTAL", logoFontBold));
            c1TotalGen.setHorizontalAlignment(Element.ALIGN_CENTER);
            c1TotalGen.setBorderColor(colorAzul);
            tableTotalGen.addCell(c1TotalGen);

            c1TotalGen = new PdfPCell(new Phrase(String.valueOf(totalPrestaciones), logoFontBold));
            c1TotalGen.setBorderColor(colorAzul);
            c1TotalGen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotalGen.addCell(c1TotalGen);

            c1TotalGen = new PdfPCell(new Phrase(String.valueOf(totalCoseguro), logoFontBold));
            c1TotalGen.setBorderColor(colorAzul);
            c1TotalGen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotalGen.addCell(c1TotalGen);

            c1TotalGen = new PdfPCell(new Phrase(String.valueOf(totalMonto), logoFontBold));
            c1TotalGen.setBorderColor(colorAzul);
            c1TotalGen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableTotalGen.addCell(c1TotalGen);

            document.add(tableTotalGen);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addTblPorAfiliado(com.itextpdf.text.Document document) {
        try {
            int totalPrestaciones=0;
            Paragraph titDetPrestaciones = new Paragraph();
            titDetPrestaciones.setAlignment(Element.ALIGN_CENTER);
            titDetPrestaciones.setIndentationLeft(200);
            addEmptyLine(titDetPrestaciones, 1);
            titDetPrestaciones.add(new Paragraph("Detalle de prestaciones", titDetPrestacionesBold));
            addEmptyLine(titDetPrestaciones, 1);
            document.add(titDetPrestaciones);

            //Comenzamos con el listado de los afiliados
            for (int i = 0; i < idAfiliadoGen.length; i++) {
                //Encabezado de los afiliados
                PdfPTable tblDatosAfiliadoEnc = new PdfPTable(3);
                tblDatosAfiliadoEnc.setTotalWidth(new float[]{60, 160, 180 });
                tblDatosAfiliadoEnc.setLockedWidth(true);

                PdfPCell celAfiliadoTot = new PdfPCell(new Phrase(planPrestacionalGen[i], logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_LEFT);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase(apellNombAfiliadoGen[i], logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_LEFT);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase(nroAfiliadoGen[i], logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_LEFT);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoEnc.addCell(celAfiliadoTot);

                document.add(tblDatosAfiliadoEnc);

                PdfPTable tblDatosAfiliadoSubEnc = new PdfPTable(6);
                tblDatosAfiliadoSubEnc.setTotalWidth(new float[]{60, 160, 40,40, 50, 50});
                tblDatosAfiliadoSubEnc.setLockedWidth(true);

                celAfiliadoTot = new PdfPCell(new Phrase("Fecha Prest.", logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoSubEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase("Cod. Prest - Descripción", logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoSubEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase("Diente", logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoSubEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase("Cara", logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoSubEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase("Coseg.", logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoSubEnc.addCell(celAfiliadoTot);

                celAfiliadoTot = new PdfPCell(new Phrase("Aranc.", logoFontBold));
                celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                celAfiliadoTot.setBorderColor(colorAzul);
                tblDatosAfiliadoSubEnc.addCell(celAfiliadoTot);

                document.add(tblDatosAfiliadoSubEnc);

                //Detalle de los afiliados
                PdfPTable tblDatosAfiliadoDet = new PdfPTable(6);
                tblDatosAfiliadoDet.setTotalWidth(new float[]{60, 160, 40, 40, 50, 50});
                tblDatosAfiliadoDet.setLockedWidth(true);

                for (int j = 0; j < idAfiliado.length; j++) {
                    if (idAfiliadoGen[i].equals(idAfiliado[j])){
                        celAfiliadoTot = new PdfPCell(new Phrase(fecPrestacion[j], logoFont));
                        celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celAfiliadoTot.setBorderColor(colorAzul);
                        tblDatosAfiliadoDet.addCell(celAfiliadoTot);

                        celAfiliadoTot = new PdfPCell(new Phrase(prestacion[j], logoFont));
                        celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_LEFT);
                        celAfiliadoTot.setBorderColor(colorAzul);
                        tblDatosAfiliadoDet.addCell(celAfiliadoTot);

                        celAfiliadoTot = new PdfPCell(new Phrase(diente[j], logoFont));
                        celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celAfiliadoTot.setBorderColor(colorAzul);
                        tblDatosAfiliadoDet.addCell(celAfiliadoTot);

                        celAfiliadoTot = new PdfPCell(new Phrase(cara[j], logoFont));
                        celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celAfiliadoTot.setBorderColor(colorAzul);
                        tblDatosAfiliadoDet.addCell(celAfiliadoTot);

                        celAfiliadoTot = new PdfPCell(new Phrase(coseguro[j], logoFont));
                        celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celAfiliadoTot.setBorderColor(colorAzul);
                        tblDatosAfiliadoDet.addCell(celAfiliadoTot);

                        celAfiliadoTot = new PdfPCell(new Phrase(montoTotal[j], logoFont));
                        celAfiliadoTot.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        celAfiliadoTot.setBorderColor(colorAzul);
                        tblDatosAfiliadoDet.addCell(celAfiliadoTot);
                    }
                }
                document.add(tblDatosAfiliadoDet);

                Paragraph lblEspacioEntreTablas = new Paragraph();
                lblEspacioEntreTablas.setAlignment(Element.ALIGN_CENTER);
                addEmptyLine(lblEspacioEntreTablas, 2);
                document.add(lblEspacioEntreTablas);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class MyFooter extends PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, com.itextpdf.text.Document document) {
            try {
                InputStream ims = getActivity().getAssets().open("rpt_odonto_pie_500.png");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.setAbsolutePosition(0, document.bottom()-30);
                document.add(image);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addSinDatos(com.itextpdf.text.Document document) {
        try {
            Paragraph titDetPrestaciones = new Paragraph();
            titDetPrestaciones.setAlignment(Element.ALIGN_CENTER);
            titDetPrestaciones.setIndentationLeft(100);
            addEmptyLine(titDetPrestaciones, 1);
            titDetPrestaciones.add(new Paragraph("No hay prestaciones para mostrar en el periodo indicado", titNoDatosBold));
            addEmptyLine(titDetPrestaciones, 1);
            document.add(titDetPrestaciones);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void previewPdf() {
        Uri uri;
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Objects.requireNonNull(getActivity()),
                    BuildConfig.APPLICATION_ID + ".provider",
                    pdfFile);
        } else{
            uri = Uri.fromFile(pdfFile);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/pdf");

// validate that the device can open your File!
        PackageManager pm = Objects.requireNonNull(getActivity()).getPackageManager();
        if (intent.resolveActivity(pm) != null) {
            startActivity(intent);
        }else {
            Intent intentPDF = new Intent();

            intent.setPackage("com.adobe.reader");
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intentPDF);
        }
    }
    //endregion

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}