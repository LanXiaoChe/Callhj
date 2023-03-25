package com.example.callhj;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private boolean userDeniedDefaultSmsApp = false;
    // 请求权限的请求码
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SET_DEFAULT_SMS_APP_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    // 用于显示接收到的短信的 TextView
    private TextView mReceivedSmsTextView;
    // 用于接收来电的广播接收器
    private IncomingCallReceiver incomingCallReceiver;
    // 用于接收短信的广播接收器
    private IncomingSmsReceiver incomingSmsReceiver;
    // 用于显示来电号码的 TextView
    private TextView incomingCallTextView;
    // 声明UI组件变量
    TextView tvIncomingCall, tvIncomingSms;
    EditText etPhoneNumber, etMessage;
    Button btnCall, btnSendSms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取 UI 组件
        incomingCallTextView = findViewById(R.id.incoming_call_textview);
        mReceivedSmsTextView = findViewById(R.id.received_sms_text_view);
        tvIncomingCall = findViewById(R.id.incoming_call_textview);
        tvIncomingSms = findViewById(R.id.received_sms_text_view);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etMessage = findViewById(R.id.et_message);
        btnCall = findViewById(R.id.btn_call);
        btnSendSms = findViewById(R.id.btn_send_sms);
        Button button1 = findViewById(R.id.button1);
        Button buttonDelete = findViewById(R.id.button_delete);
        checkReadPhoneStatePermission();






        button1.setOnClickListener(this);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phoneNumberEditText = findViewById(R.id.et_phone_number);
                String currentNumber = phoneNumberEditText.getText().toString();
                if (currentNumber.length() > 0) {
                    phoneNumberEditText.setText(currentNumber.substring(0, currentNumber.length() - 1));
                    phoneNumberEditText.setSelection(currentNumber.length() - 1);
                }
            }
        });

        // 注册一个新的 BroadcastReceiver，以便在接收到来电广播时更新 UI
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String incomingNumber = intent.getStringExtra(IncomingCallReceiver.EXTRA_INCOMING_NUMBER);
                incomingCallTextView.setText("接收到的电话：" + incomingNumber);
            }
        }, new IntentFilter(IncomingCallReceiver.ACTION_INCOMING_CALL));


        // 创建 IncomingSmsReceiver 实例，并注册到 Telephony.Sms.Intents.SMS_RECEIVED_ACTION 广播
        incomingSmsReceiver = new IncomingSmsReceiver(this);
        registerReceiver(incomingSmsReceiver, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));

        // 如果运行 Android 6.0 或以上版本，请求所需权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
            }, PERMISSION_REQUEST_CODE);
        }
        // 注册一个广播接收器来接收发送短信的 Intent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sender = intent.getStringExtra("sender");
                String messageBody = intent.getStringExtra("message_body");
                updateReceivedSmsTextView(sender, messageBody);
            }
        }, new IntentFilter("com.example.callhj.INCOMING_SMS"));
        // 获取传感器管理器，并注册加速度传感器的监听器
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(new ShakeListener(), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // 设置拨打电话按钮的点击事件
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhoneNumber(etPhoneNumber.getText().toString());
            }
        });

        // 设置发送短信按钮的点击事件
        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms(etPhoneNumber.getText().toString(), etMessage.getText().toString());
            }
        });

    }

    @Override
    public void onClick(View v) {
        EditText phoneNumberEditText = findViewById(R.id.et_phone_number);
        String currentNumber = phoneNumberEditText.getText().toString();

        switch (v.getId()) {
            case R.id.button1:
                currentNumber += "1";
                break;
            case R.id.button2:
                currentNumber += "2";
                break;
            case R.id.button3:
                currentNumber += "3";
                break;
            case R.id.button4:
                currentNumber += "4";
                break;
            case R.id.button5:
                currentNumber += "5";
                break;
            case R.id.button6:
                currentNumber += "6";
                break;
            case R.id.button7:
                currentNumber += "7";
                break;
            case R.id.button8:
                currentNumber += "8";
                break;
            case R.id.button9:
                currentNumber += "9";
                break;
            case R.id.button0:
                currentNumber += "0";
                break;
            case R.id.button_star:
                currentNumber += "*";
                break;
            case R.id.button_hash:
                currentNumber += "#";
                break;
            default:
                break;
        }

        phoneNumberEditText.setText(currentNumber);
        phoneNumberEditText.setSelection(currentNumber.length());
    }


    private void checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            setupPhoneStateListener();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupPhoneStateListener();
            } else {
                Toast.makeText(this, "需要电话状态权限才能获取来电号码", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setupPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    incomingCallTextView.setText("接收到的电话：" + phoneNumber);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
    // onResume() 函数在活动恢复时调用，检查当前应用是否是默认短信应用，如果不是，则尝试将其设置为默认短信应用
    @Override
    protected void onResume() {
        super.onResume();
        if (!isDefaultSmsApp() && !userDeniedDefaultSmsApp) {
            setDefaultSmsApp();
        }
    }

    // 检查当前应用是否是默认短信应用
    private boolean isDefaultSmsApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this);
            final String packageName = getPackageName();
            return packageName.equals(defaultSmsPackageName);
        }
        return false;
    }

    // 将当前应用设置为默认短信应用
    private void setDefaultSmsApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivityForResult(intent, SET_DEFAULT_SMS_APP_REQUEST);
        }
    }

    // 处理 startActivityForResult() 的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_DEFAULT_SMS_APP_REQUEST) {
            if (isDefaultSmsApp()) {
                // 成功设置为默认短信应用
                Toast.makeText(this, "成功设置为默认短信应用", Toast.LENGTH_SHORT).show();
            } else {
                // 用户拒绝将应用设置为默认短信应用
                Toast.makeText(this, "用户拒绝将应用设置为默认短信应用", Toast.LENGTH_SHORT).show();
                userDeniedDefaultSmsApp = true;
            }
        }
    }


    // ShakeListener 类实现了加速度传感器的监听器接口，用于检测是否摇晃手机并拨打电话
    class ShakeListener implements SensorEventListener {
        // 摇晃阈值
        private static final int SHAKE_THRESHOLD = 800;
        // 上次更新时间
        private long lastUpdate;
        // 上次 x、y、z 的值
        private float lastX, lastY, lastZ;

        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // 计算加速度变化速度
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                // 如果加速度变化速度超过阈值，就拨打电话
                if (speed > SHAKE_THRESHOLD) {
                    callPhoneNumber(etPhoneNumber.getText().toString());
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not needed for this example
        }
    }

    // 拨打电话
    private void callPhoneNumber(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    // 发送短信
    private void sendSms(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_LONG).show();
        }
    }

    // 注销广播接收器
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(incomingCallReceiver);
        unregisterReceiver(incomingSmsReceiver);
    }

    // 更新接收到的短信的 TextView
    private void updateReceivedSmsTextView(String sender, String messageBody) {
        String newText = "來自: " + sender + "\n短信: " + messageBody + "\n\n";
        mReceivedSmsTextView.append(newText);
    }
}
