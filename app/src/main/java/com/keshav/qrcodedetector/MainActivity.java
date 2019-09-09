package com.keshav.qrcodedetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;

import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_AZTEC;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_CODABAR;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_CODE_128;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_CODE_39;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_CODE_93;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_DATA_MATRIX;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_EAN_13;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_EAN_8;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_ITF;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_PDF417;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_QR_CODE;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_UPC_A;
import static com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode.FORMAT_UPC_E;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button camera, detect;

    Bitmap imageD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init of widgets
        image = findViewById(R.id.image);
        camera = findViewById(R.id.camera);
        detect = findViewById(R.id.detect);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,100);
            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    detectCode();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No Image Clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }//oncreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 100) {
                imageD = (Bitmap) data.getExtras().get("data");
                image.setImageBitmap(imageD);
            }
        }catch (Exception e){
            Toast.makeText(this, "No Image Clicked", Toast.LENGTH_SHORT).show();
        }
    }//onActivityResult

    //detection part
    void detectCode() {

        //convert simple bitmap image to firebase image format
        FirebaseVisionImage imageF = FirebaseVisionImage.fromBitmap(imageD);

        //detector format options
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE, FirebaseVisionBarcode.FORMAT_AZTEC,
                        FirebaseVisionBarcode.FORMAT_CODE_128, FirebaseVisionBarcode.FORMAT_CODE_39,
                        FirebaseVisionBarcode.FORMAT_CODE_93, FirebaseVisionBarcode.FORMAT_CODABAR,
                        FirebaseVisionBarcode.FORMAT_EAN_13, FirebaseVisionBarcode.FORMAT_EAN_8,
                        FirebaseVisionBarcode.FORMAT_ITF, FirebaseVisionBarcode.FORMAT_UPC_A,
                        FirebaseVisionBarcode.FORMAT_UPC_E, FirebaseVisionBarcode.FORMAT_PDF417,
                        FirebaseVisionBarcode.FORMAT_AZTEC, FirebaseVisionBarcode.FORMAT_DATA_MATRIX).build();

        //creating the detector with local model
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        //now we r good to go with detection
        detector.detectInImage(imageF).addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionBarcode>>() {
            @Override
            public void onComplete(@NonNull Task<List<FirebaseVisionBarcode>> task) {

                String out="";
                for(FirebaseVisionBarcode bar: task.getResult()) {
                    out+="\n"+bar.getRawValue()+"\n";
                }
                Toast.makeText(MainActivity.this, ""+out, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


            }
        });


    }


}//MainActivity
