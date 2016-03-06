package com.example.nroy8.simplesensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private SensorStruct currData;

    private static final String COMMA = ",";
    private static final String NEW_LINE = "\n";
    private SimpleDateFormat sdf;

    private FileWriter fw;
    private ArrayList<SensorStruct> arData;

    private TextView tvTime;
    private TextView tvAccelX;
    private TextView tvAccelY;
    private TextView tvAccelZ;
    private TextView tvMagX;
    private TextView tvMagY;
    private TextView tvMagZ;
    private TextView tvGyroX;
    private TextView tvGyroY;
    private TextView tvGyroZ;
    private TextView tvLightL;

    private double mean = 0;
    private final double alpha = .2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currData = new SensorStruct();
        arData = new ArrayList<SensorStruct>();
        sdf = new SimpleDateFormat("_dd-MM-yy-HH:mm:ss_");

        tvTime = (TextView)findViewById(R.id.tvTime);
        tvAccelX = (TextView)findViewById(R.id.tvAccelX);
        tvAccelY = (TextView)findViewById(R.id.tvAccelY);
        tvAccelZ = (TextView)findViewById(R.id.tvAccelZ);
        tvMagX = (TextView)findViewById(R.id.tvMagX);
        tvMagY = (TextView)findViewById(R.id.tvMagY);
        tvMagZ = (TextView)findViewById(R.id.tvMagZ);
        tvGyroX = (TextView)findViewById(R.id.tvGyroX);
        tvGyroY = (TextView)findViewById(R.id.tvGyroY);
        tvGyroZ = (TextView)findViewById(R.id.tvGyroZ);
        tvLightL = (TextView)findViewById(R.id.tvLightL);


        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //currData.time = event.timestamp;
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            // assign directions
            currData.accelX=event.values[0];
            currData.accelY=event.values[1];
            currData.accelZ=event.values[2];
        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            // assign directions
            currData.gyroX=event.values[0];
            currData.gyroY=event.values[1];
            currData.gyroZ=event.values[2];
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            // assign directions
            currData.magX=event.values[0];
            currData.magY=event.values[1];
            currData.magZ=event.values[2];
        }
        if(event.sensor.getType()==Sensor.TYPE_LIGHT){
            // assign directions
            currData.light=event.values[0];
        }
        currData.updateDisplay();
        if(event.timestamp > currData.time) {
            currData.time = event.timestamp;
            mean = alpha*(Math.sqrt(currData.accelX*currData.accelX+currData.accelY*currData.accelY+currData.accelZ*currData.accelZ))+(1-alpha)*mean;

            try {
                fw.write(currData.toString() + NEW_LINE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onButtonClick(View v) {
        if(v == findViewById(R.id.bStart)) {
            ((RadioGroup)findViewById(R.id.rbg)).setEnabled(false);
            ((Button)findViewById(R.id.bStart)).setEnabled(false);
            ((Button)findViewById(R.id.bStop)).setEnabled(true);
            int rbId = ((RadioGroup)findViewById(R.id.rbg)).getCheckedRadioButtonId();
            String prefix = (String)((RadioButton)findViewById(rbId)).getText();
            prefix = prefix + sdf.format(new Date());
            prefix = prefix + ((EditText)findViewById(R.id.tbCm)).getText()+"cm";
            try {
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"ece498mp1");
                f = new File(f,prefix+".csv");
                fw = new FileWriter(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                    SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else if (v == findViewById(R.id.bStop)){
            ((RadioGroup)findViewById(R.id.rbg)).setEnabled(true);
            ((Button)findViewById(R.id.bStart)).setEnabled(true);
            ((Button)findViewById(R.id.bStop)).setEnabled(false);
            sensorManager.unregisterListener(this);
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class SensorStruct {
        public long time;
        public float accelX;
        public float accelY;
        public float accelZ;
        public float gyroX;
        public float gyroY;
        public float gyroZ;
        public float magX;
        public float magY;
        public float magZ;
        public float light;

        public SensorStruct() {
            time = 0L;
            accelX = 0;
            accelY = 0;
            accelZ = 0;
            gyroX = 0;
            gyroY = 0;
            gyroZ = 0;
            magX = 0;
            magY = 0;
            magZ = 0;
            light = 0;
        }

        public void updateDisplay() {
            tvTime.setText(""+time);
            tvAccelX.setText(""+accelX);
            tvAccelY.setText(""+accelY);
            tvAccelZ.setText(""+accelZ);
            tvMagX.setText(""+magX);
            tvMagY.setText(""+magY);
            tvMagZ.setText(""+magZ);
            tvGyroX.setText(""+gyroX);
            tvGyroY.setText(""+gyroY);
            tvGyroZ.setText(""+gyroZ);
            tvLightL.setText(""+mean);
        }

        public String toString() {
            return ""+time+COMMA+accelX+COMMA+accelY+COMMA+accelZ+
                    COMMA+gyroX+COMMA+gyroY+COMMA+gyroZ+COMMA+
                    magX+COMMA+magY+COMMA+magZ+COMMA+light;
        }
    }
}
