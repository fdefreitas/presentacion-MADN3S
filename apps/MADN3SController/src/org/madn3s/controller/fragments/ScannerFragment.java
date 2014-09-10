package org.madn3s.controller.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.madn3s.controller.MADN3SController;
import org.madn3s.controller.MADN3SController.Device;
import org.madn3s.controller.MADN3SController.State;
import org.madn3s.controller.R;
import org.madn3s.controller.io.BraveHeartMidgetService;
import org.madn3s.controller.io.UniversalComms;
import org.madn3s.controller.models.ScanStepViewHolder;
import org.madn3s.controller.models.StatusViewHolder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressWarnings("unused")
public class ScannerFragment extends BaseFragment {

	public static final String tag = "ScannerFragment";
	public static UniversalComms bridge;
	
	private ScannerFragment mFragment;
	private ScanStepViewHolder calibrationViewHolder;
	private ScanStepViewHolder nxtActionViewHolder;
	private ScanStepViewHolder camera1ActionViewHolder;
	private ScanStepViewHolder camera2ActionViewHolder;
	private ScanStepViewHolder step1ViewHolder;
	private ScanStepViewHolder step2ViewHolder;
	private ScanStepViewHolder step3ViewHolder;
	private ProgressBar globalProgressBar;
	private ProgressBar generateModelProgressBar;
	private EditText projectNameEditText;
	private Button scanButton;
	private Button generateModelButton;
	
	public ScannerFragment() {
		mFragment = this;
		BraveHeartMidgetService.scannerBridge = new UniversalComms() {
			@Override
			public void callback(Object msg) {
				Bundle bundle = (Bundle) msg;
				final Device device = Device.setDevice(bundle.getInt("device"));
				final State state = State.setState(bundle.getInt("state"));
				int iter = MADN3SController.sharedPrefsGetInt("iter");
				Log.d(tag, device + " " + state + " " + iter);
				mFragment.getView().post(
					new Runnable() { 
						public void run() {
							//update UI
							setDeviceActionState(device, state);
						} 
					}
				); 
			}
		};
	}
	
	private void setDeviceActionState(Device device, State state){
		StatusViewHolder deviceActionViewHolder;
		switch (device) {
		case NXT:
			deviceActionViewHolder = nxtActionViewHolder;
			break;
		case CAMERA1:
			deviceActionViewHolder = camera1ActionViewHolder;		
			break;
		default:
			Log.d(tag, "Device switch unhandled default case");
		case CAMERA2:
			deviceActionViewHolder = camera2ActionViewHolder;
			break;
		}
		
		switch (state) {
		case CONNECTING:
			deviceActionViewHolder.working();
			break;
		case CONNECTED:
			deviceActionViewHolder.success();		
			break;
		case FAILED:
			deviceActionViewHolder.failure();
			break;
		default:
			Log.d(tag, "State switch unhandled default case");
			break;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_scanner, container, false);
	}

	@Override
	public void onViewCreated (View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		globalProgressBar = (ProgressBar) getView().findViewById(R.id.scanner_global_progressBar);
		projectNameEditText = (EditText) getView().findViewById(R.id.scanner_project_name_editText);
		scanButton = (Button) getView().findViewById(R.id.scan_button);
		scanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String projectName = projectNameEditText.getText().toString();
				if(projectName != null && !projectName.isEmpty()){
					projectNameEditText.setEnabled(false);
					MADN3SController.sharedPrefsPutString("project_name", projectName);
					scan(projectName);
				} else {
					Toast missingName = Toast.makeText(getActivity().getBaseContext(), "Falta el nombre del proyecto", Toast.LENGTH_LONG);
					missingName.show();
				}
			}
		});
		calibrationViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.calibration_failure_imageView), 
				getView().findViewById(R.id.calibration_success_imageView), 
				getView().findViewById(R.id.calibration_working_progressBar), 
				getView().findViewById(R.id.calibration_textView)
			);
		nxtActionViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.nxt_action_failure_imageView), 
				getView().findViewById(R.id.nxt_action_success_imageView), 
				getView().findViewById(R.id.nxt_action_working_progressBar), 
				getView().findViewById(R.id.nxt_action_textView)
			);
		camera1ActionViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.camera1_action_failure_imageView), 
				getView().findViewById(R.id.camera1_action_success_imageView), 
				getView().findViewById(R.id.camera1_action_working_progressBar), 
				getView().findViewById(R.id.camera1_action_textView)
			);
		camera2ActionViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.camera2_action_failure_imageView), 
				getView().findViewById(R.id.camera2_action_success_imageView), 
				getView().findViewById(R.id.camera2_action_working_progressBar), 
				getView().findViewById(R.id.camera2_action_textView)
			); 
		step1ViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.step1_generation_failure_imageView), 
				getView().findViewById(R.id.step1_generation_success_imageView), 
				getView().findViewById(R.id.step1_generation_working_progressBar), 
				getView().findViewById(R.id.step1_generation_textView)
			);
		step2ViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.step2_generation_failure_imageView), 
				getView().findViewById(R.id.step2_generation_success_imageView), 
				getView().findViewById(R.id.step2_generation_working_progressBar), 
				getView().findViewById(R.id.step2_generation_textView)
			);
		step3ViewHolder = new ScanStepViewHolder(
				getView().findViewById(R.id.step3_generation_failure_imageView), 
				getView().findViewById(R.id.step3_generation_success_imageView), 
				getView().findViewById(R.id.step3_generation_working_progressBar), 
				getView().findViewById(R.id.step3_generation_textView)
			);
		
		generateModelProgressBar = (ProgressBar) getView().findViewById(R.id.model_generation_progressBar);
		generateModelButton = (Button) view.findViewById(R.id.model_generation_button);
		generateModelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@SuppressLint("SimpleDateFormat")
	public void scan(String projectName){
		try{
			MADN3SController.sharedPrefsPutInt("iter", 0);
			MADN3SController.sharedPrefsPutInt("points", 6);
			int points = MADN3SController.sharedPrefsGetInt("points");
			for(int i = 0; i < points; ++i){
				MADN3SController.removeKeyFromSharedPreferences("frame-"+i);
			}
			JSONObject json = new JSONObject();
	        json.put("action", "photo");
	        json.put("project_name", projectName);
	        bridge.callback(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
