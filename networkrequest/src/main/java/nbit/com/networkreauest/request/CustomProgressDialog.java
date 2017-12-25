package nbit.com.networkreauest.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by bhoskar1 on 29/1/17.
 */

public class CustomProgressDialog {

    private ProgressDialog mPDialog;
    private static final String TAG = CustomProgressDialog.class.getSimpleName();

    public CustomProgressDialog(Context context) {
        mPDialog = new ProgressDialog(context);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPDialog.setIndeterminate(true);
    }

    public void setMessage(String message) {
        if (null == message) {
            Log.i(TAG, "Null message");
            return;
        }
        mPDialog.setMessage(message);
    }

    public void showDialog(){
        if (null == mPDialog){
            Log.i(TAG, "Dialog not initialized");
            return;
        }
        mPDialog.show();
    }

    public void closeDialog(){
        if (null == mPDialog){
            Log.i(TAG, "Dialog not initialized");
            return;
        }
        mPDialog.dismiss();
    }
}
