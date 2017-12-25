package nbit.com.networkreauest.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import nbit.com.networkreauest.util.IResponseListener;

/**
 * Created by dcpl-android on 18/5/17.
 */

public class RequestTask extends AsyncTask<Map<String, String>, String, String> {
    private static final String TAG = RequestTask.class.getSimpleName();
    private String mReqUrl;
    private String mDialogMsg;
    private Context mContext;
    private UrlConnectionRequest mUrlConnectionRequest;
    private IResponseListener mResponseListener;
    private CustomProgressDialog mProgressDialog;
    private boolean mSslCertificate;

    public RequestTask(String reqUrl, String dialogMsg, Context context, IResponseListener responseListener, boolean sslCertificate) {
        this.mReqUrl = reqUrl;
        this.mContext = context;
        this.mDialogMsg = dialogMsg;
        this.mResponseListener = responseListener;
        this.mProgressDialog = new CustomProgressDialog(this.mContext);
        this.mSslCertificate = sslCertificate;
    }

    @Override
    protected String doInBackground(Map<String, String>... params) {
        try {
            this.mUrlConnectionRequest = new UrlConnectionRequest(this.mReqUrl, "UTF-8", mResponseListener, mSslCertificate);
            if (null != params && params.length > 0) {
                Map<String, String> param = params[0];
                for (String key : param.keySet()) {
                    mUrlConnectionRequest.addFormField(key, param.get(key));
                }

                if (params.length >= 1){
                    Map<String, String> imgParam = params[1];
                    Log.i(TAG,"Map::" + imgParam.toString());
                    for (String key : imgParam.keySet()) {
                        Log.i(TAG,"Key::" + key + " value::" + imgParam.get(key));
                        mUrlConnectionRequest.addFilePart(key, new File(imgParam.get(key)));
                    }
                }
            }
            return mUrlConnectionRequest.finish();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage(this.mDialogMsg);
        mProgressDialog.showDialog();
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        mProgressDialog.closeDialog();
        if (null != mResponseListener) {
            mResponseListener.networkResponse(response);
        } else {
            mResponseListener.networkResponse(null);
        }
    }
}
