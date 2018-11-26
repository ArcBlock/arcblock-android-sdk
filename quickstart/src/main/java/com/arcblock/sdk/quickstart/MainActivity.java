package com.arcblock.sdk.quickstart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arcblock.corekit.ABCoreKitClient;
import com.arcblock.corekit.CoreKitQuery;
import com.arcblock.corekit.CoreKitResultListener;
import com.arcblock.corekit.config.CoreKitConfig;
import com.blankj.utilcode.util.ToastUtils;

public class MainActivity extends AppCompatActivity {

    private TextView tryTv;
    private EditText addressEt;
    private Button queryBtn;
    private TextView addressTv;
    private TextView balanceTv;
    private TextView totalAmountSentTv;
    private TextView totalAmountReceivedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tryTv = findViewById(R.id.try_tv);
        tryTv.getPaint().setUnderlineText(true);

        addressEt = findViewById(R.id.address_et);
        queryBtn = findViewById(R.id.query_btn);
        addressTv = findViewById(R.id.address_tv);
        balanceTv = findViewById(R.id.balance_tv);
        totalAmountSentTv = findViewById(R.id.total_amount_sent_tv);
        totalAmountReceivedTv = findViewById(R.id.total_amount_received_tv);

        tryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressEt.setText("3D2oetdNuZUqQHPJmcMDDHYoqkyNVsFk9r");
                addressEt.setSelection(addressEt.getText().toString().length());
                query();
            }
        });

        queryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });
    }

    private void query() {
        if (TextUtils.isEmpty(addressEt.getText().toString())) {
            ToastUtils.showShort("Please input a btc account address");
            return;
        }

        CoreKitQuery coreKitQuery = new CoreKitQuery(this, ABCoreKitClient.defaultInstance(this, CoreKitConfig.ApiType.API_TYPE_BTC));
        coreKitQuery.query(AccountByAddressQuery.builder().address(addressEt.getText().toString()).build(), new CoreKitResultListener<AccountByAddressQuery.Data>() {
            @Override
            public void onSuccess(AccountByAddressQuery.Data data) {
                addressTv.setText(data.getAccountByAddress().getAddress());
                balanceTv.setText(BtcValueUtils.formatBtcValue(data.getAccountByAddress().getBalance()));
                totalAmountSentTv.setText(BtcValueUtils.formatBtcValue(data.getAccountByAddress().getTotalAmountSent()));
                totalAmountReceivedTv.setText(BtcValueUtils.formatBtcValue(data.getAccountByAddress().getTotalAmountReceived()));
            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort("error=>"+e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
