package ar.com.andessalud.andes;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class inicio extends AppCompatActivity {

    Activity actActual=this;
    ImageView btnSoyAfiliado, btnSoyPrestador, btnSocialInstagram, btnSocialYoutube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actActual.overridePendingTransition(R.anim.entrar_derecha, R.anim.salir_izquierda);
        setContentView(R.layout.inicio);

        mostrarMenuRedesSociales();

        btnSoyAfiliado=(ImageView) findViewById(R.id.btnSoyAfiliado);
        btnSoyAfiliado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inent = new Intent(getBaseContext(), soy_afiliado.class);
                startActivity(inent);
            }
        });

        btnSoyPrestador=(ImageView) findViewById(R.id.btnSoyPrestador);
        btnSoyPrestador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inent = new Intent(getBaseContext(), soy_prestador.class);
                startActivity(inent);
            }
        });
    }

    private void mostrarMenuRedesSociales(){
        Bundle args = new Bundle();
        args.putString("muestraContactCenter", "NO");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment_menu_sociales contentFragment = new fragment_menu_sociales();
        contentFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .add(R.id.frg_sociales, contentFragment)
                .commit();
        //ImageView btnContactCenter= (ImageView) contentFragment.findViewById(R.id.btnContactCenter);
        //btnContactCenter.setVisibility(View.GONE);
    }


    //region otros
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        actActual.overridePendingTransition(R.anim.entrar_izquierda, R.anim.salir_derecha);
    }
    //endregion
}
