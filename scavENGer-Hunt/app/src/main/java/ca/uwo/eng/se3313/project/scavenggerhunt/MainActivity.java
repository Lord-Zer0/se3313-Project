package ca.uwo.eng.se3313.project.scavenggerhunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Initialize Visual Recognition client
    VisualRecognition service=new VisualRecognition(
            VisualRecognition.VERSION_DATE_2016_05_20,
            getString(R.string.api_key)

    );









}
