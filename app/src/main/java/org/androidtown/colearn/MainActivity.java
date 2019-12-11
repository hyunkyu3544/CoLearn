package org.androidtown.colearn;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth2";

    ImageButton btnState;
    LinearLayout btnOpen,btnClose;
    TextView txtArduino;
    Handler h;
    TextView txt1,txt2,txt3,txt4,txta,txtb,txtc,txtd;
    Button btnBack;

    final int RECIEVE_MESSAGE = 1;        // Status  for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Mac주소를 활용한 직접 열결 부분 BlueTooth mac 번호 입력부분
    private static String address = "00:21:13:04:0B:19";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnState=(ImageButton) findViewById(R.id.btnState);
        txtArduino = (TextView) findViewById(R.id.txtArduino);      // for display the received data from the Arduino
        txt1=(TextView)findViewById(R.id.text1);
        txt2=(TextView)findViewById(R.id.text2);
        txt3=(TextView)findViewById(R.id.text3);
        txt4=(TextView)findViewById(R.id.text4);
        txta=(TextView)findViewById(R.id.textA);
        txtb=(TextView)findViewById(R.id.textB);
        txtc=(TextView)findViewById(R.id.textC);
        txtd=(TextView)findViewById(R.id.textD);
        btnBack=(Button)findViewById(R.id.btnBack);
        btnOpen=(LinearLayout)findViewById(R.id.lOpen);
        btnClose=(LinearLayout)findViewById(R.id.lClose);

        // txt설명에 따른 visivle활용
        txt1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txt1.setVisibility(View.INVISIBLE);
                txt2.setVisibility(View.INVISIBLE);
                txt3.setVisibility(View.INVISIBLE);
                txt4.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                txta.setVisibility(View.VISIBLE);
                txtb.setVisibility(View.INVISIBLE);
                txtc.setVisibility(View.INVISIBLE);
                txtd.setVisibility(View.INVISIBLE);
            }
        });
        txt2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txt1.setVisibility(View.INVISIBLE);
                txt2.setVisibility(View.INVISIBLE);
                txt3.setVisibility(View.INVISIBLE);
                txt4.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                txta.setVisibility(View.INVISIBLE);
                txtb.setVisibility(View.VISIBLE);
                txtc.setVisibility(View.INVISIBLE);
                txtd.setVisibility(View.INVISIBLE);
            }
        });
        txt3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txt1.setVisibility(View.INVISIBLE);
                txt2.setVisibility(View.INVISIBLE);
                txt3.setVisibility(View.INVISIBLE);
                txt4.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                txta.setVisibility(View.INVISIBLE);
                txtb.setVisibility(View.INVISIBLE);
                txtc.setVisibility(View.VISIBLE);
                txtd.setVisibility(View.INVISIBLE);
            }
        });
        txt4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txt1.setVisibility(View.INVISIBLE);
                txt2.setVisibility(View.INVISIBLE);
                txt3.setVisibility(View.INVISIBLE);
                txt4.setVisibility(View.INVISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                txta.setVisibility(View.INVISIBLE);
                txtb.setVisibility(View.INVISIBLE);
                txtc.setVisibility(View.INVISIBLE);
                txtd.setVisibility(View.VISIBLE);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                txt3.setVisibility(View.VISIBLE);
                txt4.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.INVISIBLE);
                txta.setVisibility(View.INVISIBLE);
                txtb.setVisibility(View.INVISIBLE);
                txtc.setVisibility(View.INVISIBLE);
                txtd.setVisibility(View.INVISIBLE);
            }
        });
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                            sb.delete(0, sb.length());                                      // and clear
                            txtArduino.setText("Data from Arduino: " + sbprint);            // update TextView
                            btnState.setEnabled(true);
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        //Button을 통한 char를 전송하고 이를 활용한 행동조작 ( 추후 아두이노 스케치 내에서 활용한다.)
        btnState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnState.setEnabled(false);
                mConnectedThread.write("2");
            }
        });

        btnOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnState.setEnabled(false);
                mConnectedThread.write("1");
            }
        });
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnState.setEnabled(false);
                mConnectedThread.write("0");
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}