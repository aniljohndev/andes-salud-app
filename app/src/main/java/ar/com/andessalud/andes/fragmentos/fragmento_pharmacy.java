package ar.com.andessalud.andes.fragmentos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.JsonObject;
import com.itextpdf.text.pdf.parser.Line;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ar.com.andessalud.andes.Interfaces.ErrorResponseHandler;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;
import ar.com.andessalud.andes.Interfaces.SuccessResponseHandler;
import ar.com.andessalud.andes.MapActivity;
import ar.com.andessalud.andes.R;
import ar.com.andessalud.andes.SQLController;
import ar.com.andessalud.andes.adaptadores.HorizontalRecyclerviewAdapter;
import ar.com.andessalud.andes.fabricas.fabrica_WS;
import ar.com.andessalud.andes.fabricas.fabrica_dialogos;
import ar.com.andessalud.andes.models.PharmacyModelResponse;
import ar.com.andessalud.andes.network.ApiConstants;
import ar.com.andessalud.andes.network.ServiceInterface;
import ar.com.andessalud.andes.principal;
import ar.com.andessalud.andes.twilio.VideoActivity;
import ar.com.andessalud.andes.util.FileUtil;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class fragmento_pharmacy extends Fragment implements View.OnClickListener,OnMapReadyCallback {
    GoogleMap googleMapObj;
    Marker marker;
    private Dialog dialogo;
    boolean especialidadHabilitado = false;
    FragmentChangeTrigger trigger;
    LinearLayout mapLayout;
    MapActivity mapActivity;
    Button addLocation;
    private float ultimoClick = 0, tiempoMulticlick = 2000;
    TextView lblAfiliadoSeleccionado;
    LinearLayout lnAfiliado, lnAfiliadoResultados, listaAfiliado, lnEspacialidad;
    String _idAfiliado = "", _nombreAfiliado = "", _idEspecialidad = "";
    TextView lblSeleccionarEspecialidadPaso, lblEspecialidadSeleccionado, lblSeleccionarEspecialidad, lblSeleccionarEspecialidadProfesional, lblSeleccionarEspecialidadProfesionalPaso, lblEspecialidadProfesionalSeleccionado;
    ListView lstEspecialiad;
    LinearLayout galleryuploadlay, lnPrestadorResultado, secondlayoutpharmacyly,lnPrestador3rdmain,map3rdphaselnlay;
    ImageView imagefromgallery, imagefromcamera;
    Button add_location_map;
    Bitmap bitmap;
    String encodedImageString;
    ArrayList<Uri> uri;
    RecyclerView recyclerView;
    HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;
    principal pantPrincipal;
    JSONObject sendingJsonObj;
    LinearLayout lnPrestador;
    AppCompatEditText exactlocation1;
    Button dateselect2;
    private Dialog dialog;
    EditText selecteddate;
    View v;
    CircularProgressButton btnSubmit;
    ServiceInterface serviceInterface;
//    List<Uri> files = new ArrayList<>();
    String latitude,longitude;
     LinearLayout parentLinearLayout;
    ImageView selectedImage ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void showSuccessDialog(String title, String body) {
        dialog.dismiss();
        dialog.setContentView(R.layout.appointment_dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView titleString, bodyString;
        titleString = dialog.findViewById(R.id.appointment_title);
        bodyString = dialog.findViewById(R.id.appointment_body);
        titleString.setText(title);
        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.appointment_Yesbtn);
        noBtn = dialog.findViewById(R.id.appointment_nobtn);
        yesBtn.setText("OK");
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showCalender(id);
                dialog.dismiss();
                dialogo.dismiss();
            }


        });
        noBtn.setVisibility(View.GONE);
        dialog.show();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_fragmento_pharmacy, container, false);
        initialize(v);
        SupportMapFragment mapFragment =  SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_mapFragmentId_activity_get_location_filter1,mapFragment).commit();
        mapFragment.getMapAsync(this);



        return v;

    }
     private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Choose a Media");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void initialize(View v) {
        parentLinearLayout= v.findViewById(R.id.parent_linear_layout);

        btnSubmit = v.findViewById(R.id.submit_button);
        selectedImage  = v.findViewById(R.id.number_edit_text);
        uri = new ArrayList<>();
        dialog = new Dialog(getContext());
        /*mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.fragment_mapFragmentId_activity_get_location_filter1);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }
*/

        exactlocation1 = v.findViewById(R.id.exactlocation1);
        dateselect2 = v.findViewById(R.id.dateselect2);
//        dateselect2.setEnabled(false);
        selecteddate = v.findViewById(R.id.selecteddate);
//        recyclerView = v.findViewById(R.id.selectedImageRecyclerview);
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(uri);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true));
//        recyclerView.setAdapter(horizontalRecyclerviewAdapter);
        lnAfiliado = v.findViewById(R.id.lnAfiliado);
        lnAfiliado.setOnClickListener(this);
        lnPrestador3rdmain = v.findViewById(R.id.lnPrestador);
        lblAfiliadoSeleccionado = (TextView) v.findViewById(R.id.lblAfiliadoSeleccionados);
        lblSeleccionarEspecialidad = (TextView) v.findViewById(R.id.lblSeleccionarEspecialidad);
        map3rdphaselnlay = v.findViewById(R.id.map3rdphaselnlay);
        lnPrestador = v.findViewById(R.id.lnPrestador);
        secondlayoutpharmacyly = (LinearLayout) v.findViewById(R.id.secondlayoutpharmacyly);
//        lnEspacialidad.setVisibility(View.VISIBLE);
        lstEspecialiad = v.findViewById(R.id.lstEspecialiad);
        lstEspecialiad.setVisibility(View.GONE);
//        btnEspecialidad = v.findViewById(R.id.lnEspecialidad);
        lnAfiliadoResultados = v.findViewById(R.id.lnAfiliadoResultadoss);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lnPrestador3rdmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                lnPrestadorResultado.setVisibility(View.VISIBLE);
                add_location_map.setVisibility(View.VISIBLE);
//                map3rdphaselnlay.setVisibility(View.VISIBLE);
            }
        });
//        addImage();
        lnAfiliadoResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAfiliado();
            }
        });
        listaAfiliado = v.findViewById(R.id.listaAfiliado);
        imagefromcamera = v.findViewById(R.id.imagefromcamera);
        imagefromcamera.setOnClickListener(v12 -> Dexter.withContext(getContext()).withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        /*Intent intent = new Intent();
                        intent.setType("images/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);*/
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                        ;
//                        startActivityForResult(Intent.createChooser(intent, "Pictures:"), 1);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check());
        imagefromgallery = v.findViewById(R.id.imagefromgallery);
        imagefromgallery.setOnClickListener(v12 -> Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View rowView=inflater.inflate(R.layout.image, null);
                        // Add the new row before the add field button.
                        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
                        parentLinearLayout.isFocusable();

                        selectedImage = rowView.findViewById(R.id.number_edit_text);
//=======

//>>>>>>> Stashed changes
                       /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(Intent.createChooser(intent, "Pictures: "), 1);*/
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                       /* ImagePicker.with(Objects.requireNonNull(getActivity()))
                                .galleryOnly().
                                compress(1024)
                                .start()
                        ;*/

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                            Toast.makeText(getActivity(),"")
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check());
        listaAfiliado.setVisibility(View.GONE);
        lblSeleccionarEspecialidadPaso = v.findViewById(R.id.lblSeleccionarEspecialidadPaso);
        lblSeleccionarEspecialidadPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lnPrestadorResultado = v.findViewById(R.id.lnPrestadorResultado);
        lnPrestadorResultado.setVisibility(View.GONE);
        add_location_map = v.findViewById(R.id.add_location_map);
        add_location_map.setVisibility(View.GONE);
//        lnAfiliado.setOnClickListener(this);
        mapLayout = v.findViewById(R.id.map_layout);
        mapActivity = new MapActivity();
       /* addLocation = v.findViewById(R.id.add_location_map);
        addLocation.setOnClickListener(this);
       */
        add_location_map.setOnClickListener(this);

        lnEspacialidad = v.findViewById(R.id.secondLayoutNumber);
        lnEspacialidad.setOnClickListener(v1 -> {

            if (_idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Antes de seleccionar la especialidad debe seleccionar el afiliado");
                dialogo.show();
                return;
            } else {
                secondlayoutpharmacyly.setVisibility(View.VISIBLE);

                lnEspacialidad.setVisibility(View.GONE);

            }
        });
    }

    /*  public String getPath(Uri uri) {
          int  column_index;
          String imagePath ;
          String[] projection = {MediaStore.MediaColumns.DATA};
         Cursor cursor = getContext().getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA, null, null);
          column_index = cursor
                  .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
          cursor.moveToFirst();
          imagePath = cursor.getString(column_index);
          cursor.close();
          return cursor.getString(column_index);
      }
  */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "intuenty", null);
        Log.d("image uri",path);
        return Uri.parse(path);
    }

   /* @SuppressLint("NotifyDataSetChanged")
    @Override*/
    /*public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if (data != null) {
                if (requestCode == 100)
                {
                try {
                    gettingMapInfo(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                }
                if (data.getClipData() != null) {
*//*                    int count = data.getClipData().getItemCount();
//                    for (int i = 0; i < count; i++) {
//                        uri.add(data.getClipData().getItemAt(i).getUri());
//                        Log.d("iamamgePath", uri.get(i).getPath());
//
//                    }
      *//*
                    Bitmap img = (Bitmap) data.getExtras().get("data");

                    String imgPath = FileUtil.getPath(getActivity(),getImageUri(getContext(),img));

                    uri.add(Uri.parse(imgPath));
//                    recyclerView.setVisibility(View.VISIBLE);
//                    horizontalRecyclerviewAdapter.notifyDataSetChanged();
                    parentLinearLayout.setVisibility(View.VISIBLE);
                } else if (data.getData() != null) {
                    String imagePath = data.getData().getPath();
                    Log.d("iamamgePath", imagePath);
                    Uri img = data.getData();
                    String imgPath = FileUtil.getPath(getContext(),img);

                    uri.add(Uri.parse(imgPath));
                } else {
//                    Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
                }
            }



    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        parentLinearLayout.setVisibility(View.VISIBLE);
                        Bitmap img = (Bitmap) data.getExtras().get("data");
                        selectedImage.setImageBitmap(img);
                        Picasso.get().load(getImageUri(getContext(),img)).into(selectedImage);

                        String imgPath = FileUtil.getPath(getContext(),getImageUri(getContext(),img));

                        uri.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri img = data.getData();
                        Picasso.get().load(img).into(selectedImage);
                        parentLinearLayout.setVisibility(View.VISIBLE);
                        String imgPath = FileUtil.getPath(getContext(),img);

                        uri.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);

                    }
                    break;
            }
        }
        if (requestCode == 100)
        {
            try {
                gettingMapInfo(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_location_map:
                startActivityForResult(new Intent(getActivity(), MapActivity.class),100);
                /*Intent intentforsend = new Intent(getContext(), MapActivity.class);
                intentforsend.putExtra("connectMobileToWeb","connectedcall");
                startActivityForResult(intentforsend, RESULT_OK);
                */pantPrincipal.overridePendingTransition(R.anim.entrar_arriba, R.anim.entrar_de_abajo);

                break;
            case R.id.lnAfiliado:
                clickAfiliado();

                break;
            case R.id.lnAfiliadoResultadoss:
                clickAfiliado();

                break;

        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }

    private void buscarAfiliado() {
        try {
            SQLController database = new SQLController(getContext());
            final String idAfiliado = database.obtenerIDAfiliado();
            if (idAfiliado.equals("")) {
                dialogo = fabrica_dialogos.dlgError(getContext(), "Error al buscar la identificación del afiliado");
                return;
            }

            dialogo = fabrica_dialogos.dlgBuscando(getContext(), "Buscando el grupo familiar");


            fabrica_WS.APPBuscarGrupoFamiliar(getActivity(), idAfiliado, new SuccessResponseHandler<Document>() {
                @Override
                public void onSuccess(Document valores) {
                    dialogo.dismiss();
                    String[] grupoFam = new String[valores.getElementsByTagName("apellidoYNombre").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("apellidoYNombre").getLength(); i++) {
                        grupoFam[i] = valores.getElementsByTagName("apellidoYNombre").item(i).getTextContent();
                    }
                    String[] numAfil = new String[valores.getElementsByTagName("nroAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("nroAfiliado").getLength(); i++) {
                        numAfil[i] = valores.getElementsByTagName("nroAfiliado").item(i).getTextContent();
                    }
                    String[] idAfiliados = new String[valores.getElementsByTagName("idAfiliado").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("idAfiliado").getLength(); i++) {
                        idAfiliados[i] = valores.getElementsByTagName("idAfiliado").item(i).getTextContent();
                    }
                    String[] tipoMensaje = new String[valores.getElementsByTagName("tipoMensaje").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("tipoMensaje").getLength(); i++) {
                        tipoMensaje[i] = valores.getElementsByTagName("tipoMensaje").item(i).getTextContent();
                    }
                    String[] mensaje = new String[valores.getElementsByTagName("mensaje").getLength()];
                    for (int i = 0; i < valores.getElementsByTagName("mensaje").getLength(); i++) {
                        mensaje[i] = valores.getElementsByTagName("mensaje").item(i).getTextContent();
                    }
                    mostrarGrupoFam(idAfiliados, grupoFam, numAfil, tipoMensaje, mensaje);
                }
            }, new ErrorResponseHandler() {
                @Override
                public void onError(String msg) {
                    dialogo.dismiss();
                    dialogo = fabrica_dialogos.dlgError(getContext(), msg);
                }
            });
        } catch (Exception ex) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al buscar el grupo familiar:\n" + ex.getMessage());
        }

    }

    private void mostrarGrupoFam(String[] idAfiliados, String[] grupoFam, String[] numAfil, String[] tipoMensaje, String[] mensaje) {
        try {
            lnAfiliado.setVisibility(View.GONE);
            lnAfiliadoResultados.setVisibility(View.VISIBLE);
            listaAfiliado.removeAllViews();
            for (int i = 0; i < idAfiliados.length; i++) {
                LayoutInflater inflater = this.getLayoutInflater();
                View lnPrestador = inflater.inflate(R.layout.lista_grupo_fam, null);

                final String idAfiliadoLocal = idAfiliados[i];
                final String grupoFamLocal = grupoFam[i];
                final String numAfilLocal = numAfil[i];
                final String mensajeLocal = mensaje[i];

                TextView idFila = (TextView) lnPrestador.findViewById(R.id.idFila);
                TextView oculto1 = (TextView) lnPrestador.findViewById(R.id.oculto1);
                TextView linea1 = (TextView) lnPrestador.findViewById(R.id.linea1);
                TextView linea2 = (TextView) lnPrestador.findViewById(R.id.linea2);

                idFila.setText(idAfiliadoLocal);
                oculto1.setText(mensajeLocal);
                linea1.setText(grupoFamLocal);
                linea2.setText(numAfilLocal);

                lnPrestador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mensajeLocal.equals("--")) {
                            seleccionarAfiliado(idAfiliadoLocal, grupoFamLocal);
                        } else {
                            dialogo = fabrica_dialogos.dlgError(getContext(), mensajeLocal);
                        }
                    }
                });
                listaAfiliado.addView(lnPrestador);
            }
            listaAfiliado.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            dialogo.dismiss();
            dialogo = fabrica_dialogos.dlgError(getContext(), "Error general al mostrar los prestadores:\n" + e.getMessage());
        }
    }

    private void seleccionarAfiliado(String idAfiliado, String apellNomb) {
        _idAfiliado = idAfiliado;
        _nombreAfiliado = apellNomb;
        lblAfiliadoSeleccionado.setText(_nombreAfiliado);
        listaAfiliado.setVisibility(View.GONE);
        habilitaEspecialidad();
    }

    private void clickAfiliado() {
        if (SystemClock.elapsedRealtime() - ultimoClick < tiempoMulticlick) {
            return;
        }
        ultimoClick = SystemClock.elapsedRealtime();
        lnAfiliado.setVisibility(View.VISIBLE);
        lnAfiliadoResultados.setVisibility(View.GONE);
        lblAfiliadoSeleccionado.setText("Seleccionar Afiliado");
//        listaAfiliado.setVisibility(View.GONE);
        _idAfiliado = "";
        _nombreAfiliado = "";
        deshabilitaEspecialidad();
//        lnCuestionario.setVisibility(View.GONE);
        _idEspecialidad = "";
//        btnSiguiente.setVisibility(View.GONE);
//        btnAnterior.setVisibility(View.GONE);
//        deshabilitaEspecialidadProfesional();
        buscarAfiliado();
    }

    private void habilitaEspecialidad() {
        especialidadHabilitado = true;
        lblSeleccionarEspecialidadPaso.setTextColor(getResources().getColor(R.color.colorFuente));
        lblSeleccionarEspecialidad.setTextColor(getResources().getColor(R.color.colorFuente));
    }

    private void deshabilitaEspecialidad() {
        especialidadHabilitado = false;
        lblSeleccionarEspecialidadPaso.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
        lblSeleccionarEspecialidad.setTextColor(getResources().getColor(R.color.colorFuenteDesactivada));
//        lnEspacialidad.setVisibility(View.VISIBLE);
//        lnEspacialidad.setVisibility(View.VISIBLE);
        secondlayoutpharmacyly.setVisibility(View.GONE);
        lnEspacialidad.setVisibility(View.VISIBLE);
        lstEspecialiad.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pantPrincipal = (principal) getActivity();
        sendingJsonObj = new JSONObject();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });

    }
    private void gettingMapInfo(Intent data) throws JSONException {
//        sendingJsonObj.put("date",new Date().getDate());
        try {
            latitude = data.getStringExtra("lat");
            longitude = data.getStringExtra("lng");
            sendingJsonObj.put("longitude",longitude);
            sendingJsonObj.put("latitude",latitude);
            sendingJsonObj.put("exact_location",data.getStringExtra("addressLine"));
            exactlocation1.setText(data.getStringExtra("addressLine"));

        }
        catch (Exception e)
        {

        }
        lnPrestadorResultado.setVisibility(View.VISIBLE);
        lnPrestador.setVisibility(View.GONE);
        map3rdphaselnlay.setVisibility(View.VISIBLE);
//        setupAutoSuggestComplete();

        LatLng        location = new LatLng(Double.parseDouble(sendingJsonObj.getString("longitude")), Double.parseDouble(sendingJsonObj.getString("longitude")));
        Log.d("checkingLatLong",sendingJsonObj.get("longitude") + " "+ sendingJsonObj.get("latitude"));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(location.latitude + " : "+location.longitude);

        Log.d("finaljson", String.valueOf(sendingJsonObj));
        dateselect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalender();
            }
        });
    }

    public void showCalender() {
        final String[] dates = new String[1];
        dialog.dismiss();
        dialog.setContentView(R.layout.fragment_fragmento_calenderio);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//           TextView titleString,bodyString;
//        titleString = dialog.findViewById(R.id.appointment_title);
//        bodyString = dialog.findViewById(R.id.appointment_body);
//        titleString.setText(title);
//        bodyString.setText(body);
        TextView yesBtn, noBtn;
        yesBtn = dialog.findViewById(R.id.calender_yesBtn);
        noBtn = dialog.findViewById(R.id.calender_nobtn);
        CalendarView calendarView = dialog.findViewById(R.id.calendarView);
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                dates[0] = eventDay.getCalendar().get(Calendar.YEAR)
                        + "-" + Integer.parseInt(String.valueOf(eventDay.getCalendar().get(Calendar.MONTH)+1)) +
                        "-" + eventDay.getCalendar().get(Calendar.DATE)
                ;
                Log.d("finaljson",dates[0]);
//                Toast.makeText(this,dates[0].toString(),Toast.LENGTH_LONG).show();
//Toast.makeText(this,dates[0],Toast.LENGTH_LONG).show();
//            Log.d("datesvalue",dates[0]);
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("datesvalue", dates[0]);

                try {
                    sendingJsonObj.put("date", dates[0]);
             Log.d("finaljson", String.valueOf(sendingJsonObj));
                    selecteddate.setText(MessageFormat.format("{0}", dates[0]));
                    dateselect2.setVisibility(View.GONE);
                    dialog.dismiss();
                    add_location_map.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMapObj = googleMap;
        try {
            LatLng location =  new LatLng(Double.parseDouble(sendingJsonObj.getString("latitude")),Double.parseDouble(sendingJsonObj.getString("longitude")));
            Log.d("checkdataad", String.valueOf(location));
            googleMap.addMarker(new MarkerOptions().position(location).title(sendingJsonObj.getString("addressLine")));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = new File(fileUri.getPath());
        Log.i("here is error",file.getAbsolutePath());
        // create RequestBody instance from file

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);


    }

    public void uploadImages(){

        btnSubmit.startAnimation();

        List<MultipartBody.Part> list = new ArrayList<>();

        for (Uri uris:this.uri) {

            Log.i("uris",uris.getPath());

            list.add(prepareFilePart("images[]", uris));

        }
        for (int i = 0 ; i < list.size();i++)
        {
            Log.d("adada", list.get(i).toString());
        }
        RequestBody idAfilliado = RequestBody.create(MediaType.parse("multipart/form-data"),_idAfiliado);
        RequestBody dates = RequestBody.create(MediaType.parse("multipart/form-data"),selecteddate.getText().toString());
        RequestBody exactloc = RequestBody.create(MediaType.parse("multipart/form-data"), exactlocation1.getText().toString());
        RequestBody longitudes,latitudes;
             longitudes = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longitude));
             latitudes = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latitude));
        Log.d("checkinada",_idAfiliado + " "+ selecteddate.getText().toString() + " " + latitude + " "+ longitude + " "+exactlocation1.getText().toString());

        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);


        Call<PharmacyModelResponse> call = serviceInterface.updateProfile(dates,exactloc,longitudes,latitudes,list,idAfilliado);
        call.enqueue(new Callback<PharmacyModelResponse>() {
            @Override
            public void onResponse(@NonNull Call<PharmacyModelResponse> call, @NonNull Response<PharmacyModelResponse> response) {
                btnSubmit.revertAnimation();
                try {


                    Toast.makeText(getContext(),response.body().getResponse().getMessage(),Toast.LENGTH_LONG).show();
                    Log.d("Exception1", "the status is ----> " + response.code());
                    showSuccessDialog("Exito",response.body().getResponse().getMessage());
                }
                catch (Exception e){
                    Log.d("Exception","|=>"+e.getMessage());
//
                }
            }

            @Override
            public void onFailure(Call<PharmacyModelResponse> call, Throwable t) {
                btnSubmit.revertAnimation();
                Log.i("Exception",t.getMessage());
            }
        });
    }
    // this is all you need to grant your application external storage permision
   /* private void requestPermission(){
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getContext(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
        }
    }*/

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    uploadImages();
                }
                else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
*/
}