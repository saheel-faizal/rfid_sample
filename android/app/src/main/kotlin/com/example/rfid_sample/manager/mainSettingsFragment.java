
package com.example.rfid_sample.manager;

import static android.app.Activity.RESULT_OK;
import static com.zebra.demo.application.Application.DEVICE_PREMIUM_PLUS_MODE;
import static com.zebra.demo.application.Application.RFD_DEVICE_MODE;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedDevice;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class mainSettingsFragment extends Fragment {
    /* Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AdvancedFragment.
     */
    private View mainSettingsFragmentView;
    private TableRow scanSettingRow;
    private TableRow wifiSettingsRow;

    public static mainSettingsFragment newInstance() {
        return new mainSettingsFragment();
    }

    private TableRow ShareFile;
    private static final int CHOOSE_FILE_FROM_DEVICE = 1001;
    private Uri filePath;
    private ArrayList<Uri> multipleFiles;
    private TableRow generalRow, rfidRow,applicationRow, endpointConfigRow,certificateMgmtRow;

    public mainSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_scan_settings, menu);
//        menu.findItem(R.id.action_scan).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                ((ActiveDeviceActivity) getActivity()).loadNextFragment(SCAN_DATAVIEW_TAB);
//                return true;
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainSettingsFragmentView = inflater.inflate(R.layout.fragment_mainsettings, container, false);
        generalRow = mainSettingsFragmentView.findViewById(R.id.generalrow);
        rfidRow = mainSettingsFragmentView.findViewById(R.id.rfidrow);
        applicationRow = mainSettingsFragmentView.findViewById(R.id.applicationrow);
        scanSettingRow = mainSettingsFragmentView.findViewById(R.id.scansettingrow);
        wifiSettingsRow = mainSettingsFragmentView.findViewById(R.id.wifisettingrow);
        View scanDivider = mainSettingsFragmentView.findViewById(R.id.scan_row_divider);
        View wifiDivider = mainSettingsFragmentView.findViewById(R.id.wifi_row_divider);
        View certMgmtDivider = mainSettingsFragmentView.findViewById(R.id.certificate_row_divider);
        View endPointDivider = mainSettingsFragmentView.findViewById(R.id.endpoint_row_divider);
        certificateMgmtRow = mainSettingsFragmentView.findViewById(R.id.wificertificaterow);
        endpointConfigRow = mainSettingsFragmentView.findViewById(R.id.endpointrow);
        if(RFD_DEVICE_MODE == DEVICE_PREMIUM_PLUS_MODE) {
            scanSettingRow.setVisibility(View.VISIBLE);
            scanDivider.setVisibility(View.VISIBLE);
        }else{
            scanSettingRow.setVisibility(View.GONE);
            scanDivider.setVisibility(View.GONE);
        }

        if(mConnectedDevice != null && mConnectedReader != null) {
            if (mConnectedDevice.getDeviceCapability(mConnectedReader.getHostName()).contains("WiFi")) {
                wifiSettingsRow.setVisibility(View.VISIBLE);
                wifiDivider.setVisibility(View.VISIBLE);
                certMgmtDivider.setVisibility(View.VISIBLE);
                certificateMgmtRow.setVisibility(View.VISIBLE);
                endPointDivider.setVisibility(View.VISIBLE);
                endpointConfigRow.setVisibility(View.VISIBLE);
            }

//            if (mConnectedDevice.getTransport().contains("BLUETOOTH")) {
//                wifiSettingsRow.setVisibility(View.GONE);
//                wifiDivider.setVisibility(View.GONE);
//            }
        }

        return mainSettingsFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        if(RFIDController.mConnectedReader == null) {
            generalRow.setEnabled(false);
            generalRow.setBackgroundColor(Color.LTGRAY);
            rfidRow.setEnabled(false);
            rfidRow.setBackgroundColor(Color.LTGRAY);
            scanSettingRow.setEnabled(false);
            scanSettingRow.setBackgroundColor(Color.LTGRAY);
            wifiSettingsRow.setEnabled(false);
            wifiSettingsRow.setBackgroundColor(Color.LTGRAY);
            if(endpointConfigRow.getVisibility() == View.VISIBLE) {
                endpointConfigRow.setEnabled(false);
                endpointConfigRow.setBackgroundColor(Color.LTGRAY);
            }
            if(certificateMgmtRow.getVisibility() == View.VISIBLE) {
                certificateMgmtRow.setEnabled(false);
                certificateMgmtRow.setBackgroundColor(Color.LTGRAY);
            }
        }

    }

    private void initialize() {
//        multipleFiles = new ArrayList<>();
//        ShareFile = (TableRow) getActivity().findViewById(R.id.ShareFiles);
//        ShareFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                callChooseFileFromDevice();
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("Share", "onActivityResult called");

        if (requestCode == CHOOSE_FILE_FROM_DEVICE && resultCode == RESULT_OK) {
            Log.d("Share", "requestCode == CHOOSE_PDF_FROM_DEVICE && RESULT_OK");

            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        filePath = data.getClipData().getItemAt(i).getUri();
                        Log.d("Share", "filePath = " + filePath);
                        multipleFiles.add(filePath);
                        Log.d("Share", "file added = " + i);
                    }
                    shareFile();
                } else {
                    filePath = data.getData();
                    multipleFiles.add(filePath);
                    Log.d("Share", "filePath = " + filePath);
                    shareFile();

                }
            }
        }
    }




    public void callChooseFileFromDevice(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("*/*");
        //  startActivityForResult(Intent.createChooser(intent,"Selecting multiple files"),CHOOSE_PDF_FROM_DEVICE);
        startActivityForResult(Intent.createChooser(intent,"Selecting multiple files"),CHOOSE_FILE_FROM_DEVICE);
        //  Log.d("Share"," selecting ");
    }

    public void shareFile(){

        Log.d("Share","before sharing");
        Intent intentShare = new Intent(Intent.ACTION_SEND_MULTIPLE);

        intentShare.setType("*/*");
        intentShare.putParcelableArrayListExtra(Intent.EXTRA_STREAM, multipleFiles);
        startActivity(Intent.createChooser(intentShare,"Share the file ..."));
        Log.d("Share","after sharing");
        multipleFiles.clear();
    }

}
