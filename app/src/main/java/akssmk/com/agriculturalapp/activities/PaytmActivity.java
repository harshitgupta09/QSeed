package akssmk.com.agriculturalapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.Log;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;

import akssmk.com.agriculturalapp.R;
import akssmk.com.agriculturalapp.modals.Api;
import akssmk.com.agriculturalapp.modals.Checksum;
import akssmk.com.agriculturalapp.modals.Constants;
import akssmk.com.agriculturalapp.modals.Paytm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaytmActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    //the textview in the interface where we have the price
    TextView textViewPrice;
    Button buy1, buy2, buy3, buy4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm);

        //getting the textview
        textViewPrice = findViewById(R.id.textViewPrice);
        buy1 = findViewById(R.id.buttonBuy);
        buy2 = findViewById(R.id.buttonBuy1);
        buy3 = findViewById(R.id.buttonBuy2);
        buy4 = findViewById(R.id.buttonBuy3);


        //attaching a click listener to the button buy
        findViewById(R.id.buttonBuy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //calling the method generateCheckSum() which will generate the paytm checksum for payment
                generateCheckSum();
            }
        });
        buy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPaytm();
            }
        });
        buy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPaytm();
            }
        });
        buy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPaytm();
            }
        });
        buy4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPaytm();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void openPaytm() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("net.one97.paytm");
        startActivity( launchIntent );
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void generateCheckSum() {

        //getting the tax amount first.
        String txnAmount = textViewPrice.getText().toString().trim();

        //creating a retrofit object.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the retrofit api service
        Api apiService = retrofit.create(Api.class);

        //creating paytm object
        //containing all the values required
        final Paytm paytm = new Paytm(
                Constants.M_ID,
                Constants.CHANNEL_ID,
                txnAmount,
                Constants.WEBSITE,
                Constants.INDUSTRY_TYPE_ID
        );
        Log.d("paytm_channelId", paytm.getChannelId());
        Log.d("paytm_custId", paytm.getCustId());
        Log.d("paytm_IndustryType", paytm.getIndustryTypeId());
        Log.d("paytm_MID", paytm.getmId());
        Log.d("paytm_OrderId", paytm.getOrderId());
        Log.d("paytm_TxnAmount", paytm.getTxnAmount());
        Log.d("paytm_website", paytm.getWebsite());

        //creating a call object from the apiService
        Call<Checksum> call = apiService.getChecksum(
                paytm.getmId(),
                paytm.getOrderId(),
                paytm.getCustId(),
                paytm.getIndustryTypeId(),
                paytm.getChannelId(),
                paytm.getTxnAmount(),
                paytm.getWebsite()

        );

        //making the call to generate checksum
        call.enqueue(new Callback<Checksum>() {
            @Override
            public void onResponse(Call<Checksum> call, Response<Checksum> response) {
                //once we get the checksum we will initiailize the payment.
                //the method is taking the checksum we got and the paytm object as the parameter

                Log.d("generated_hash", response.body().getChecksumHash());
//                String checksum = getCorrectHash(response.body().getChecksumHash());
                initializePaytmPayment(response.body().getChecksumHash(), paytm);
//                Log.d("correct_hash", checksum);
                // Why checksum Hash mismatch always?
                /*
                * the hash generator server is generating hash with letters, numbers as well as /, + and =.
                * / and + are creating the problem as when they are put in the params of the post request / is taken
                * different meaning and is converted to some %3C. This might be the reason of hash mismatch.
                *
CHECKSUMHASH = kJldWvM4ACM13d6bL08sfZD5\/0tgN\/Ssdkicj3jUuurqlESnITCVg23PXpDzL6EzvwfidvrQiwfrkdstHkUSSKua9\/JHwUPYhWGIMTnAARg=
URL encoded String is INDUSTRY_TYPE_ID=Retail&CHANNEL_ID=WAP&CHECKSUMHASH=kJldWvM4ACM13d6bL08sfZD5%5C%2F0tgN-->%5C<--(see here)-->%2F<--Ssdkicj3jUuurqlESnITCVg23PXpDzL6EzvwfidvrQiwfrkdstHkUSSKua9%5C%2FJHwUPYhWGIMTnAARg%3D&MOBILE_NO=7777777777&TXN_AMOUNT=10.00&MID=IEEEDT67188777807734&EMAIL=username%40emailprovider.com&CALLBACK_URL=https%3A%2F%2Fsecuregw.paytm.in%2Ftheia%2FpaytmCallback%3FORDER_ID%3Dea385b1a21874ca1ac6941545e858a86&CUST_ID=099c88d76a3b4d86a168a3c58cbef897&WEBSITE=APPSTAGING&ORDER_ID=ea385b1a21874ca1ac6941545e858a86
Merchant Response is {"ORDERID":"ea385b1a21874ca1ac6941545e858a86", "MID":"IEEEDT67188777807734", "TXNAMOUNT":"10.00", "CURRENCY":"INR", "STATUS":"TXN_FAILURE", "RESPCODE":"330", "RESPMSG":"Paytm checksum mismatch.", "BANKTXNID":"", "CHECKSUMHASH":"gpcvxl9WjmRYKp5Quvap3VfBHedzkfp69TX3y14qJz/iypvVEGXQwj1aSNw0XaOu2fRE/WVZ2warHx5ClJNG0ND8pAyUbd7nlDehc/HaxSs="}
RESPMSG = Paytm checksum mismatch.
CHECKSUMHASH = gpcvxl9WjmRYKp5Quvap3VfBHedzkfp69TX3y14qJz/iypvVEGXQwj1aSNw0XaOu2fRE/WVZ2warHx5ClJNG0ND8pAyUbd7nlDehc/HaxSs=

                * */
            }

            @Override
            public void onFailure(Call<Checksum> call, Throwable t) {

            }
        });
    }

    //    private static String getCorrectHash(String checksumHash) {
//        String hash = "";
//        for (int i = 0; i < checksumHash.length(); i++) {
//            if (checksumHash.charAt(i) == '/')
//                hash = hash + "\\";
//
//
//            hash = hash + (String.valueOf(checksumHash.charAt(i)));
//        }
//
//        return hash;
//    }
    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        //getting paytm service
        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", "IEEEDT67188777807734");
        paramMap.put("ORDER_ID", paytm.getOrderId());
        paramMap.put("CUST_ID", paytm.getCustId());
        paramMap.put("MOBILE_NO", "7777777777");
        paramMap.put("EMAIL", "username@emailprovider.com");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", paytm.getTxnAmount());
        paramMap.put("WEBSITE", paytm.getWebsite());
        paramMap.put("CALLBACK_URL", "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + paytm.getOrderId());
        paramMap.put("CHECKSUMHASH", checksumHash);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");

        Log.d("param_hash", String.valueOf(paramMap));
        //creating a paytm order object using the hashmap
        PaytmOrder order = new PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);
// EV8nHTdFYpgGadeynxXCi7eaao66srxmmf/Eow43P94Ll0vfm+Q8FCoudFtDMLGNIggk/3kB+aHsrQWiQljxeOzmx5vC/mhW44ydDYg365Q=
        //finally starting the payment transaction
        Service.startPaymentTransaction(this, true, true, this);

    }

    //all these overriden method is to detect the payment result accordingly
    @Override
    public void onTransactionResponse(Bundle bundle) {
        Toast.makeText(this, bundle.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();

    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        Log.d("_error_", s);
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Toast.makeText(this, s + bundle.toString(), Toast.LENGTH_LONG).show();
        Log.d("_error_2", s);
    }
}