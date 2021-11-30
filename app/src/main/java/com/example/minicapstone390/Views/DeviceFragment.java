package com.example.minicapstone390.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.Controllers.DatabaseEnv;
import com.example.minicapstone390.Controllers.SharedPreferenceHelper;
import com.example.minicapstone390.Models.Device;
import com.example.minicapstone390.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Device Fragment
public class DeviceFragment extends DialogFragment {
    private static final String TAG = DeviceFragment.class.getSimpleName();
    private static final String DEVICES = DatabaseEnv.USERDEVICES.getEnv();

    // Declare variables
    private final Database dB = new Database();
    protected SharedPreferenceHelper sharePreferenceHelper;

    protected Button cancelButton, saveButton;
    protected EditText wifiNameInput, wifiPasswordInput;
    protected PopupWindow popupWindow;
    public HomeActivity activity;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

//    public static final int REQUEST_ENABLE_BT=1;
//    ListView lv_paired_devices;
//    Set<BluetoothDevice> set_pairedDevices;
//    ArrayAdapter adapter_paired_devices;
//    BluetoothAdapter bluetoothAdapter;
//    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    public static final int MESSAGE_READ=0;
//    public static final int MESSAGE_WRITE=1;
//    public static final int CONNECTING=2;
//    public static final int CONNECTED=3;
//    public static final int NO_SOCKET_FOUND=4;

    String bluetooth_message = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (HomeActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            closeBT();
        }
        catch (IOException ex) { }

    }

    // TODO: Replace with check for device ID in database and add it to user
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_device, container, false);

        // Initialize SharedPref and check theme
        sharePreferenceHelper = new SharedPreferenceHelper(getContext());
        // TODO CHECK THEME

        // Input Fields for Student Profile Data
        wifiNameInput = (EditText) view.findViewById(R.id.wifi_name);
        wifiPasswordInput = (EditText) view.findViewById(R.id.wifi_password);

        View popupView = inflater.inflate(R.layout.popup_window, null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        //Create Object and Listener for Cancel and Save Buttons


        cancelButton = (Button) view.findViewById(R.id.cancel_add_device_button);
        saveButton = (Button) view.findViewById(R.id.save_add_device_button);


        try {
            findBT();
            openBT();
        }
        catch (IOException ex) { }

//        lv_paired_devices = (ListView) view.findViewById(R.id.lv_paired_devices);
//        adapter_paired_devices = new ArrayAdapter(getActivity().getApplicationContext(),R.layout.support_simple_spinner_dropdown_item);
//        lv_paired_devices.setAdapter(adapter_paired_devices);
//        lv_paired_devices.setVisibility(View.GONE);

//        initialize_bluetooth();
//        start_accepting_connection()
//        initialize_clicks();

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(view1 -> {


            // Get Input Responses
            String wifiName = wifiNameInput.getText().toString();
            String wifiPassword = wifiPasswordInput.getText().toString();

            // Validate Inputs
            if (wifiName.equals("")) {
                wifiNameInput.setError("Please enter a wifi name");
                wifiNameInput.requestFocus();
                return;
            }
            if (wifiPassword.equals("")) {
                wifiPasswordInput.setError("Please enter a wifi password");
                wifiPasswordInput.requestFocus();
                return;
            }
            wifiNameInput.setVisibility(View.GONE);
            wifiPasswordInput.setVisibility(View.GONE);
            bluetooth_message=wifiName+"\n"+wifiPassword;

            try {
                sendData();
            }
            catch (IOException ex) {

                Toast.makeText(getActivity().getApplicationContext(),"SEND FAILED",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(getActivity().getApplicationContext(),"No bluetooth adapter available",Toast.LENGTH_SHORT).show();
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("GasGaurd")) {
                    mmDevice = device;
                    break;
                }
            }
        }
        Toast.makeText(getActivity().getApplicationContext(),"Bluetooth Device Found",Toast.LENGTH_SHORT).show();
    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
    }
    void beginListenForData() {

        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {

                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException {

        mmOutputStream.write(bluetooth_message.getBytes());
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }









   /* @SuppressLint("HandlerLeak")
    Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg_type) {
            super.handleMessage(msg_type);

            switch (msg_type.what){
                case MESSAGE_READ:

                    byte[] readbuf=(byte[])msg_type.obj;
                    String string_recieved=new String(readbuf);

                    //do some task based on recieved string

                    break;
                case MESSAGE_WRITE:

                    if(msg_type.obj!=null){
                        ConnectedThread connectedThread=new ConnectedThread((BluetoothSocket)msg_type.obj);
                        connectedThread.write(bluetooth_message.getBytes());

                    }
                    break;

                case CONNECTED:
                    Toast.makeText(getActivity().getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    break;

                case CONNECTING:
                    Toast.makeText(getActivity().getApplicationContext(),"Connecting...",Toast.LENGTH_SHORT).show();
                    break;

                case NO_SOCKET_FOUND:
                    Toast.makeText(getActivity().getApplicationContext(),"No socket found",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void initialize_clicks()
    {
        lv_paired_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object[] objects = set_pairedDevices.toArray();
                BluetoothDevice device = (BluetoothDevice) objects[position];

                ConnectThread connectThread = new ConnectThread(device);
                connectThread.start();

                Toast.makeText(getActivity().getApplicationContext(),"Device Chosen: "+device.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initialize_bluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(getActivity().getApplicationContext(),"Your Device doesn't support bluetooth.",Toast.LENGTH_SHORT).show();
        }

        //Add these permisions before
//        <uses-permission android:name="android.permission.BLUETOOTH" />
//        <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
//        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
//        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        else {
            set_pairedDevices = bluetoothAdapter.getBondedDevices();

            if (set_pairedDevices.size() > 0) {

                for (BluetoothDevice device : set_pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    adapter_paired_devices.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
    }

    public void start_accepting_connection()
    {
        //call this on button click as suited by you

        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
        Toast.makeText(getActivity().getApplicationContext(),"accepting",Toast.LENGTH_SHORT).show();
    }

    public class AcceptThread extends Thread
    {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NAME",MY_UUID);
            } catch (IOException e) { }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null)
                {
                    // Do work to manage the connection (in a separate thread)
                    mHandler.obtainMessage(CONNECTED).sendToTarget();
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mHandler.obtainMessage(CONNECTING).sendToTarget();

                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
//            bluetooth_message = "Initial message"
//            mHandler.obtainMessage(MESSAGE_WRITE,mmSocket).sendToTarget();
        }

        *//** Will cancel an in-progress connection, and close the socket *//*
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
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
            byte[] buffer = new byte[2];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    break;
                }
            }
        }

        *//* Call this from the main activity to send data to the remote device *//*
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        *//* Call this from the main activity to shutdown the connection *//*
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
*/

}