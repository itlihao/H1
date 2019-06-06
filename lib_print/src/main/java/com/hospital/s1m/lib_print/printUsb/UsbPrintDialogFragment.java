package com.hospital.s1m.lib_print.printUsb;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterInstance;
import com.hospital.s1m.lib_print.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * USb
 * Created by Achen on 2017/1/16.
 */
public class UsbPrintDialogFragment extends DialogFragment {

    private ListView mFoundDevicesListView;
    private ArrayAdapter<String> deviceArrayAdapter;
    private TextView button_scan;
    private TextView tv_cancle;
    private List<UsbDevice> deviceList;
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";

    private OnClickListenerMiss listener;

    public interface OnClickListenerMiss {
        void setCancle();
    }

    public static UsbPrintDialogFragment newInstance(OnClickListenerMiss listener) {
        UsbPrintDialogFragment doctorAdviceDialogFragment = new UsbPrintDialogFragment();
        doctorAdviceDialogFragment.listener = listener;
        return doctorAdviceDialogFragment;
    }

    public void setOnOnClickListenerMiss(OnClickListenerMiss listener) {
        this.listener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE;
        setStyle(style, 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.devistlist_dialog, null);
        setCancelable(true);
        findView(view);
        setListener();
        return view;
    }

    private void setListener() {
        mFoundDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mUSBDevice = deviceList.get(position);
                returnToPreviousActivity(deviceList.get(position));
            }
        });
        deviceArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_item);
        mFoundDevicesListView.setAdapter(deviceArrayAdapter);
        doDiscovery();
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void returnToPreviousActivity(UsbDevice mUSBDevice) {
        myPrinter = new PrinterInstance(getActivity(), mUSBDevice, mHandler);
        UsbManager mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        if (mUsbManager.hasPermission(mUSBDevice)) {
            myPrinter.openConnection();
        } else {
            // 没有权限询问用户是否授予权限
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            getActivity().registerReceiver(mUsbReceiver, filter);
            // 该代码执行后，系统弹出一个对话框
            mUsbManager.requestPermission(mUSBDevice, pendingIntent);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    System.out.println("SUCCESS");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(myPrinter);
                    listener.setCancle();
                    dismiss();
                    break;
                case PrinterConstants.Connect.FAILED:
                    System.out.println("FAILED");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(null);
                    break;
                case PrinterConstants.Connect.CLOSED:
                    System.out.println("CLOSED");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(null);
                    break;
                case PrinterConstants.Connect.NODEVICE:
                    System.out.println("NODEVICE");
                    PrinterHelper.getInstance().setPrinterInstanceUSB(null);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        @SuppressLint("NewApi")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    getActivity().unregisterReceiver(mUsbReceiver);
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUSBDevice.equals(device)) {
                        myPrinter.openConnection();
                    } else {
                        mHandler.obtainMessage(PrinterConstants.Connect.FAILED).sendToTarget();
                    }
                }
            }
        }
    };

    private PrinterInstance myPrinter;
    private UsbDevice mUSBDevice;

    private void doDiscovery() {
        deviceArrayAdapter.clear();
        UsbManager manager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        deviceList = new ArrayList<>();
        for (UsbDevice device : devices.values()) {
            //只显示打印机
            UsbInterface anInterface = device.getInterface(0);
            if (7 == anInterface.getInterfaceClass()) {
                deviceArrayAdapter.add(device.getDeviceName() + "\nvid: "
                        + device.getVendorId() + " pid: "
                        + device.getProductId());
                deviceList.add(device);
            }
        }
    }

    private void findView(View view) {
        mFoundDevicesListView = view.findViewById(R.id.paired_devices);
        button_scan = view.findViewById(R.id.button_scan);
        tv_cancle = view.findViewById(R.id.tv_cancle);
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        int screen_w = wm.getDefaultDisplay().getWidth();
        int screen_h = wm.getDefaultDisplay().getHeight();
        int max = Math.max(screen_h, screen_w);
        getDialog().getWindow().setLayout((int) (max * 4 / 10), ViewGroup.LayoutParams.WRAP_CONTENT);
        applyCompat();
    }

    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getDialog().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
