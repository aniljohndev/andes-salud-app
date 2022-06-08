package ar.com.andessalud.andes;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ar.com.andessalud.andes.Interfaces.FragmentChangeTrigger;

public class fragment_menu_sociales extends Fragment implements View.OnClickListener{
    FragmentChangeTrigger trigger;
    ImageView btnSocialFacebook, btnSocialInstagram, btnSocialYoutube;
    LinearLayout btnContactCenter;

    public fragment_menu_sociales() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_sociales, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSocialFacebook=(ImageView) view.findViewById(R.id.btnSocialFacebook);
        btnSocialFacebook.setOnClickListener(this);
        btnSocialInstagram=(ImageView) view.findViewById(R.id.btnSocialInstagram);
        btnSocialInstagram.setOnClickListener(this);
        btnSocialYoutube=(ImageView) view.findViewById(R.id.btnSocialYoutube);
        btnSocialYoutube.setOnClickListener(this);
        btnContactCenter=(LinearLayout) view.findViewById(R.id.btnContactCenter);
        btnContactCenter.setOnClickListener(this);

        Bundle extras = getArguments();
        if (extras != null) {
            String  muestraContacCenter;
            muestraContacCenter = extras.getString("muestraContactCenter","");
            if (muestraContacCenter.equals("NO")){
                btnContactCenter.setVisibility(View.GONE);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSocialFacebook){
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = "https://www.facebook.com/andes.salud";
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        }else if(v.getId()==R.id.btnSocialInstagram){
            Uri instagramUrl = Uri.parse("https://www.instagram.com/andes.salud/");
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, instagramUrl);
            instagramIntent.setPackage("com.instagram.android");

            try {
                startActivity(instagramIntent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        instagramUrl));
            }
        }else if(v.getId()==R.id.btnSocialYoutube){
            Uri youtubeUrl = Uri.parse("https://www.youtube.com/channel/UCScModADwaATBNZub8g4svA");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(youtubeUrl);
            startActivity(intent);
        }else if(v.getId()==R.id.btnContactCenter){
            trigger.fireChange("contactCenter_btnAbrir");
        }
    }

    public void setTrigger(FragmentChangeTrigger trigger) {
        this.trigger = trigger;
    }
}