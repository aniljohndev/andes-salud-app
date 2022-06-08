package ar.com.andessalud.andes;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.zxing.Result;

import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class soy_afiliado_scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    Dialog dialog;
    private ZXingScannerView mScannerView;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        this.mScannerView = new ZXingScannerView(this);
        setContentView((View) this.mScannerView);
    }

    public void onResume() {
        super.onResume();
        this.mScannerView.setResultHandler(this);
        this.mScannerView.startCamera();
    }

    public void onPause() {
        super.onPause();
        this.mScannerView.stopCamera();
    }

    public void handleResult(Result rawResult) {
        showBottomSheetDialog(rawResult);
    }

    /* access modifiers changed from: package-private */
    public void showBottomSheetDialog(Result rawResult) {
        final String codeValue = rawResult.getText();
        soy_afiliado_scanner.this.finishBarcodeScan(codeValue);

        /*Da error Could not inflate Behavior subclass android.support.design.widget.BottomSheetBehavior
        * cuando lo pase a androidx*/
        /*this.dialog = new BottomSheetDialog(this);
        this.dialog.setContentView(R.layout.dlg_modal_pdf417);
        ((TextView) this.dialog.findViewById(R.id.tvCodeValue)).setText(rawResult.getText());
        ((Button) this.dialog.findViewById(R.id.btnDone)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                soy_afiliado_scanner.this.finishBarcodeScan(codeValue);
            }
        });
        this.dialog.show();*/
    }

    /* access modifiers changed from: package-private */
    public void finishBarcodeScan(String value) {
        //this.dialog.dismiss();
        Intent intent = new Intent();
        intent.putExtra("codeValue", value);
        setResult(-1, intent);
        finish();
    }
}