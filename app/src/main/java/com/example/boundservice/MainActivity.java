package com.example.boundservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * La actividad requiere un servicio concreto, que ofrece en este caso el service ServiceBound.
 */

public class MainActivity extends AppCompatActivity {

    private Button btnTiempo;
    private Button btnStopTiempo;
    private TextView txvTiempo;
    //Service que nos ofrece un contador de tiempo
    BoundService boundService;
    //Indica si estamos vinculados al service
    boolean isBound = false;
    //Necesitamos un objeto ServiceConnection que controla la conexión con el
    //servidor (servicio)
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("HOLA", "onServiceConnected: HOLA");
            //Cojo el IBinder que se devuelve del onBind del service
            BoundService.MyBinder myBinder = (BoundService.MyBinder) iBinder;
            boundService = myBinder.getService();
            //Ya estaria conectado, ya tengo el service.

            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTiempo = findViewById(R.id.btnTiempo);
        btnStopTiempo = findViewById(R.id.btnStopTiempo);
        txvTiempo = findViewById(R.id.txvTiempo);
        btnTiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBound)
                    startBoundService();
                if (isBound) {
                    txvTiempo.setText(boundService.getTimestamp());
                    txvTiempo.setEnabled(true);
                    //Los servicios han de ser finitos, si he obtenido lo que necesito de ti, te termino.
                    btnStopTiempo.setEnabled(true);
                    btnTiempo.setEnabled(false);
                }
            }
        });

        btnStopTiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound) {
                    StopBoundService();
                    txvTiempo.setText("Se ha parado el servicio");
                    btnStopTiempo.setEnabled(false);
                    btnTiempo.setEnabled(true);
                }
            }
        });
    }

    /**
     * Método que para el servidor (service). Es obligatorio desvincular el servicio de la activity.
     */
    private void StopBoundService() {
        Intent intent = new Intent(MainActivity.this, BoundService.class);
        //Desvinculamos el servicio siempre
        if (isBound)
            unbindService(serviceConnection);
        //Despues de desvincular paramos
        stopService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Desvinculamos el servicio siempre
        if (isBound)
            unbindService(serviceConnection);
    }

    /**
     * Método que inicia el servidor (service)
     */
    private void startBoundService() {
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        //Una vez iniciado el servicio lo vinculamos al ServiceConnection, que es la clase
        //que va a gestionar la conexion entre ambos dos. El flag ultimo dice que
        //se llame a los dos metodos del ServiceConnection de forma automática.
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
