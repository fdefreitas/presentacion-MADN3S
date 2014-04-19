package org.madn3s.controller.fragments;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.madn3s.controller.MADN3SController;
import org.madn3s.controller.R;
import org.madn3s.controller.components.NXTTalker;
import org.madn3s.controller.io.BTConnection;
import org.madn3s.controller.io.HiddenMidgetAttackAsyncTask;
import org.madn3s.controller.models.DevicesAdapter;

import java.util.ArrayList;

/**
 * Created by inaki on 26/01/14.
 */
public class ConnectionFragment extends BaseFragment {

    private ArrayList<BluetoothDevice> device;
    private NXTTalker talker;
    int mState;
    public static final int MESSAGE_STATE_CHANGE = 2;
    public static final int MESSAGE_TOAST = 1;
    public static final String TOAST = "toast";
    private BroadcastReceiver mReceiver;
    private Handler mHandler;

    ListView devicesListView;
    DevicesAdapter devicesAdapter;
    private TextView nxtNameTextView;
    private TextView nxtAddressTextView;
    private ProgressBar nxtConnectingProgressBar;
    private ImageView nxtConnectedImageView;
    private ImageView nxtNotConnectedImageView;

    private TextView camera1NameTextView;
    private TextView camera1AddressTextView;
    private ProgressBar camera1ConnectingProgressBar;
    private ImageView camera1ConnectedImageView;
    private ImageView camera1NotConnectedImageView;


    private TextView camera2NameTextView;
    private TextView camera2AddressTextView;
    private ProgressBar camera2ConnectingProgressBar;
    private ImageView camera2ConnectedImageView;
    private ImageView camera2NotConnectedImageView;

    private ConnectionFragment(){
        TAG = this.getClass().getName();
    }

    public ConnectionFragment(ArrayList<BluetoothDevice>  device){
        this();
        this.device = (ArrayList<BluetoothDevice>)device.clone();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nxtNameTextView = (TextView) view.findViewById(R.id.nxt_name_connection_textView);
        nxtNameTextView.setText(MADN3SController.nxt.getName());
        nxtAddressTextView = (TextView) view.findViewById(R.id.nxt_address_connection_textView);
        nxtAddressTextView.setText(MADN3SController.nxt.getAddress());
        nxtConnectingProgressBar = (ProgressBar) view.findViewById(R.id.nxt_connecting_progressBar);
        nxtConnectedImageView = (ImageView) view.findViewById(R.id.nxt_connected_imageView);
        nxtNotConnectedImageView = (ImageView) view.findViewById(R.id.nxt_not_connected_imageView);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_TOAST:
                        Toast.makeText(getActivity().getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_STATE_CHANGE:
                        mState = msg.arg1;
                        switch(mState){
                            case NXTTalker.STATE_CONNECTED:
                                nxtConnectingProgressBar.setVisibility(View.GONE);
                                nxtConnectedImageView.setVisibility(View.VISIBLE);
                                break;
                            case NXTTalker.STATE_NONE:
                                nxtConnectingProgressBar.setVisibility(View.GONE);
                                nxtNotConnectedImageView.setVisibility(View.VISIBLE);
                                nxtConnectedImageView.setVisibility(View.GONE);
                                break;
                            case NXTTalker.STATE_CONNECTING:
                                nxtConnectingProgressBar.setVisibility(View.VISIBLE);
                                nxtNotConnectedImageView.setVisibility(View.GONE);
                                nxtConnectedImageView.setVisibility(View.GONE);
                                break;
                        }
                        break;
                }
            }
        };

        for(BluetoothDevice b : device){
            if(b.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT){
                talker = new NXTTalker(mHandler);
                talker.connect(b);
            } else {
                HiddenMidgetAttackAsyncTask task = new HiddenMidgetAttackAsyncTask(b);
                task.execute();
                //establecer conexion bluetooth con las camaras
            }
            Log.d(TAG, b.getName());
        }

        camera1NameTextView = (TextView) view.findViewById(R.id.camera1_name_connection_textView);
        camera1NameTextView.setText(MADN3SController.cameras.get(0).getName());
        camera1AddressTextView = (TextView) view.findViewById(R.id.camera1_address_connection_textView);
        camera1AddressTextView.setText(MADN3SController.cameras.get(0).getAddress());
        camera1ConnectingProgressBar = (ProgressBar) view.findViewById(R.id.camera1_connecting_progressBar);
        camera1ConnectedImageView = (ImageView) view.findViewById(R.id.camera1_connected_imageView);
        camera1NotConnectedImageView = (ImageView) view.findViewById(R.id.camera1_not_connected_imageView);

        camera2NameTextView = (TextView) view.findViewById(R.id.camera2_name_connection_textView);
        camera2NameTextView.setText(MADN3SController.cameras.get(1).getName());
        camera2AddressTextView = (TextView) view.findViewById(R.id.camera2_address_connection_textView);
        camera2AddressTextView.setText(MADN3SController.cameras.get(1).getAddress());
        camera2ConnectingProgressBar = (ProgressBar) view.findViewById(R.id.camera2_connecting_progressBar);
        camera2ConnectedImageView = (ImageView) view.findViewById(R.id.camera2_connected_imageView);
        camera2NotConnectedImageView = (ImageView) view.findViewById(R.id.camera2_not_connected_imageView);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "EL COÑO DE LA MADRE");
                BTConnection conn = BTConnection.getInstance();
                BluetoothSocket mSocket1 = conn.getCam1Socket();
                BluetoothSocket mSocket2 = conn.getCam2Socket();
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                    String addressCamera1 = MADN3SController.cameras.get(0).getAddress();
                    String addressCamera2 = MADN3SController.cameras.get(1).getAddress();

                    //Camera 1
                    if(device != null && device.getAddress().equals(addressCamera1) && mSocket1 != null){
                        //if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                        if (mSocket1.isConnected()){
                            camera1ConnectedImageView.setVisibility(View.VISIBLE);
                            camera1ConnectingProgressBar.setVisibility(View.GONE);
                        //}else if (device.getBondState() == BluetoothDevice.BOND_NONE){
                        }else if (!mSocket1.isConnected()){
                            camera1NotConnectedImageView.setVisibility(View.VISIBLE);
                            camera1ConnectingProgressBar.setVisibility(View.GONE);
                        }
                    }

                    //Camera 2
                    if(device != null && device.getAddress().equals(addressCamera2) && mSocket2 != null){
                    //    if (device.getBondState() == BluetoothDevice.BOND_BONDED){
                        if (mSocket2.isConnected()){
                            camera2ConnectedImageView.setVisibility(View.VISIBLE);
                            camera2ConnectingProgressBar.setVisibility(View.GONE);
                        ///}else if (device.getBondState() == BluetoothDevice.BOND_NONE){
                        }else if (!mSocket2.isConnected()){
                            camera2NotConnectedImageView.setVisibility(View.VISIBLE);
                            camera2ConnectingProgressBar.setVisibility(View.GONE);
                        }
                    }


                }
                if(mSocket1 == null){
                    Log.d(TAG, "mSocket1 es null");
                } else {
                    if (mSocket1.isConnected()){
                        Log.d(TAG, "mSocket1 conectado");
                    } else {
                        Log.d(TAG, "mSocket1 NO conectado");
                    }
                }

                if(mSocket2 == null){
                    Log.d(TAG, "mSocket2 es null");
                } else {
                    if (mSocket2.isConnected()){
                        Log.d(TAG, "mSocket2 conectado");
                    } else {
                        Log.d(TAG, "mSocket2 NO conectado");
                    }
                }
            }
        };
    }

}