package nbit.com.networkreauest.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import nbit.com.networkreauest.util.INetworkConstants;
import nbit.com.networkreauest.util.IResponseListener;

/**
 * Created by bhoskar1 on 28/1/17.
 */

/**
 *
 */
public class NetworkRequests {
    public static final String TAG = NetworkRequests.class.getSimpleName();
    public static final int HEAP_SIZE = 4096;
    public static final int DURATION = 30;

    /**
     * @param context
     * @param webRequest
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestGETString(Context context, String webRequest, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.GET, INetworkConstants.REQUEST_TYPE_STRING, webRequest, null, dialogMsg, showDialog, listener);
    }

    /**
     * @param context
     * @param requestType
     * @param webRequest
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestGET(Context context, int requestType, String webRequest, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.GET, requestType, webRequest, null, dialogMsg, showDialog, listener);
    }

    /**
     * @param context
     * @param requestType
     * @param webRequest
     * @param params
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestGET(Context context, int requestType, String webRequest, Map<String, String> params, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.GET, requestType, webRequest, params, dialogMsg, showDialog, listener);
    }

    public void webRequestGETString(Context context, String webRequest, Map params, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.GET, INetworkConstants.REQUEST_TYPE_STRING, webRequest, params, dialogMsg, showDialog, listener);
    }

    /**
     * @param context
     * @param webRequest
     * @param dialogMsg
     * @param listener
     */
    public void webRequestGETImage(Context context, String webRequest, String dialogMsg, IResponseListener listener) {
        webServiceRequest(context, Request.Method.GET, INetworkConstants.REQUEST_TYPE_BITMAP, webRequest, null, dialogMsg, false, listener);
    }

    /**
     * @param context
     * @param webRequest
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestPOSTString(Context context, String webRequest, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.POST, INetworkConstants.REQUEST_TYPE_STRING, webRequest, null, dialogMsg, showDialog, listener);
    }

    /**
     * @param context
     * @param webRequest
     * @param params
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestPOSTString(Context context, String webRequest, Map params, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.POST, INetworkConstants.REQUEST_TYPE_STRING, webRequest, params, dialogMsg, showDialog, listener);
    }


    /**
     * @param context
     * @param requestType
     * @param webRequest
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestPOST(Context context, int requestType, String webRequest, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.POST, requestType, webRequest, null, dialogMsg, showDialog, listener);
    }

    /**
     * @param context
     * @param requestType
     * @param webRequest
     * @param params
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webRequestPOST(Context context, int requestType, String webRequest, Map<String, String> params, String dialogMsg, boolean showDialog, IResponseListener listener) {
        webServiceRequest(context, Request.Method.POST, requestType, webRequest, params, dialogMsg, showDialog, listener);
    }

    /**
     * @param context
     * @param methodType
     * @param responseType
     * @param webRequest
     * @param params
     * @param dialogMsg
     * @param showDialog
     * @param listener
     */
    public void webServiceRequest(final Context context, int methodType, int responseType, String webRequest, final Map<String, String> params, String dialogMsg, final boolean showDialog, final IResponseListener listener) {
        Log.i(TAG, "NetworkRequests:: webServiceRequest::");
        if (null == webRequest) {
            Log.i(TAG, "NetworkRequests:: webServiceRequest:: Found null request url");
            return;
        }

        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "No Network connection", Toast.LENGTH_LONG).show();
            return;
        }
        final CustomProgressDialog dialog = new CustomProgressDialog(context);
        if (showDialog) {
            dialog.setMessage(dialogMsg);
            dialog.showDialog();
        }
        Log.i(TAG, "NetworkRequests:: webServiceRequest:: Making Requesting ");
        Cache cache = new DiskBasedCache(context.getCacheDir(), HEAP_SIZE * HEAP_SIZE);

        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);

        mRequestQueue.start();

        if (methodType == Request.Method.GET) {
            Log.i(TAG, "NetworkRequests:: webServiceRequest:: webRequest::GET");
            Log.i(TAG, "NetworkRequests:: webServiceRequest:: webRequest::" + webRequest);
            if (null != params) {
                webRequest = appendParam(webRequest, params);
            }
            Log.i(TAG, "NetworkRequests:: webServiceRequest:: UpdatedRequest::" + webRequest);
            if (INetworkConstants.REQUEST_TYPE_STRING == responseType) {
                StringRequest request = new StringRequest(methodType, webRequest, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(TAG, "NetworkRequests:: webServiceRequest:: WebRequest::response::" + s);
                        if (null != listener) {
                            listener.networkResponse(s);
                        }
                        if (showDialog) {
                            dialog.closeDialog();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (showDialog) {
                            dialog.closeDialog();
                        }

                        Log.e(TAG, "NetworkRequests:: webServiceRequest::volleyError::" + volleyError);
                        volleyError.printStackTrace();
                        if (null != listener) {
                            listener.networkResponse(null);
                        }
                    }
                });
                mRequestQueue.add(request);
            }

            if (INetworkConstants.REQUEST_TYPE_BITMAP == responseType) {
                ImageRequest request = new ImageRequest(webRequest, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        if (showDialog) {
                            dialog.closeDialog();
                        }
                        Log.i(TAG, "NetworkRequests:: webServiceRequest::response::" + bitmap);
                        if (null != listener) {
                            listener.networkResponse(bitmap);
                        }

                    }
                }, 0, 0, null, null, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (showDialog) {
                            dialog.closeDialog();
                        }

                        Log.e(TAG, "NetworkRequests:: webServiceRequest::volleyError::" + volleyError);
                        volleyError.printStackTrace();
                        if (null != listener) {
                            listener.networkResponse(null);
                        }
                    }
                });
                cache.clear();
                mRequestQueue.add(request);
            }

        } else if (methodType == Request.Method.POST) {
            Log.i(TAG, "NetworkRequests:: webServiceRequest:: webRequest::POST");
            Log.i(TAG, "NetworkRequests:: webServiceRequest::" + webRequest);

            if (INetworkConstants.REQUEST_TYPE_STRING == responseType) {
                StringRequest request = new StringRequest(methodType, webRequest, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (showDialog) {
                            dialog.closeDialog();
                        }

                        Log.i(TAG, "NetworkRequests:: webServiceRequest::response::" + s);
                        if (null != listener) {
                            listener.networkResponse(s);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (showDialog) {
                            dialog.closeDialog();
                        }
                        Log.e(TAG, "NetworkRequests:: webServiceRequest::volleyError::" + volleyError);
                        volleyError.printStackTrace();
                        if (null != listener) {
                            listener.networkResponse(null);
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams");
                        if (null != params && params.size() > 0) {
                            Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::set custom params");
                            Log.i(TAG, "custom params::" + params.toString());
                            return params;
                        }
                        Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::set default params");
                        return super.getParams();
                    }
                };
                cache.clear();
                mRequestQueue.add(request);
            }
        } else if (methodType == Request.Method.PUT) {
            //TODO : Write put method call
        } else if (methodType == Request.Method.DELETE) {
            //TODO : write delete method call
        }
    }

    public void workerThreadStringRequest(final Context context, int methodType, String requestUrl, final Map<String, String> params, final IResponseListener listener) {
        workerThreadRequest(context, methodType, INetworkConstants.REQUEST_TYPE_STRING, requestUrl, params, listener);
    }

    public void workerThreadRequest(final Context context, int methodType, int responseType, String requestUrl, final Map<String, String> params, final IResponseListener listener) {

        Log.i(TAG, "NetworkRequests:: webServiceRequest::");
        if (null == requestUrl) {
            Log.i(TAG, "NetworkRequests:: webServiceRequest:: Found null request url");
            return;
        }

        if (!isNetworkAvailable(context)) {
            Toast.makeText(context, "No Network connection", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i(TAG, "NetworkRequests:: webServiceRequest:: Making Requesting ");
        Cache cache = new DiskBasedCache(context.getCacheDir(), HEAP_SIZE * HEAP_SIZE);

        Network network = new BasicNetwork(new HurlStack());
        RequestQueue mRequestQueue = new RequestQueue(cache, network);

        mRequestQueue.start();

        Log.i(TAG, "Request URL : requestUrl : " + requestUrl);
        mRequestQueue.start();
        if (methodType == Request.Method.GET && responseType == INetworkConstants.REQUEST_TYPE_STRING) {
            if (null != params) {
                requestUrl = appendParam(requestUrl, params);
            }

            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(methodType, requestUrl, future, future);
            mRequestQueue.add(request);
            String data = null;
            try {
                data = future.get(DURATION, TimeUnit.SECONDS);

                Log.i(TAG, "onHandleIntent::webRequestPOSTString::getting response");
                /*if (!TextUtils.isEmpty(data)) {
                     listener.networkResponse(data);
                }*/
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                //Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != listener) {
                    listener.networkResponse(data);
                }
            }
        } else if (methodType == Request.Method.POST && responseType == INetworkConstants.REQUEST_TYPE_STRING) {

            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(methodType, requestUrl, future, future) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::");
                    if (null != params && params.size() > 0) {
                        Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::set custom params");
                        return params;
                    }
                    Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::set default params");
                    return super.getParams();
                }
            };

            mRequestQueue.add(request);
            String data = null;
            try {

                data = future.get(DURATION, TimeUnit.SECONDS);
                Log.i(TAG, "onHandleIntent::webRequestPOSTString::getting response");
                /*if (!TextUtils.isEmpty(data)) {
                    listener.networkResponse(data);
                }*/
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            } finally {
                if (null != listener) {
                    listener.networkResponse(data);
                }
            }
        } else if (methodType == Request.Method.GET && responseType == INetworkConstants.REQUEST_TYPE_BITMAP) {
            if (null != params) {
                requestUrl = appendParam(requestUrl, params);
            }

            RequestFuture<Bitmap> future = RequestFuture.newFuture();
            ImageRequest request = new ImageRequest(requestUrl, future, 0, 0, null, null, future);
            mRequestQueue.add(request);
            try {
                Log.i(TAG, "onHandleIntent::webRequestPOSTString::getting response");
                if (null != listener) {
                    listener.networkResponse(future.get(DURATION, TimeUnit.SECONDS));
                }

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            }
        } else if (methodType == Request.Method.POST && responseType == INetworkConstants.REQUEST_TYPE_STRING) {
            RequestFuture<Bitmap> future = RequestFuture.newFuture();
            ImageRequest request = new ImageRequest(requestUrl, future, 0, 0, null, null, future) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::");
                    if (null != params && params.size() > 0) {
                        Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::set custom params");
                        return params;
                    }
                    Log.i(TAG, "NetworkRequests:: webServiceRequest::getParams::set default params");
                    return super.getParams();
                }
            };
            mRequestQueue.add(request);
            try {
                Log.i(TAG, "onHandleIntent::webRequestPOSTString::getting response");
                if (null != listener) {
                    listener.networkResponse(future.get(DURATION, TimeUnit.SECONDS));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.i(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * @param webRequest
     * @param params
     * @return
     */
    private String appendParam(String webRequest, Map<String, String> params) {
        if (TextUtils.isEmpty(webRequest)) {
            Log.e(TAG, "NetworkRequests::appendParam::Invalid URL");
            return null;
        }
        StringBuilder urlBuilder = new StringBuilder(webRequest);
        if (urlBuilder.lastIndexOf("?") == -1) {
            urlBuilder.append("?");
        }

        if (null == params || params.size() == 0) {
            Log.e(TAG, "NetworkRequests::appendParam::parameter list is empty ");
            return urlBuilder.toString();
        }

        Set<String> keySet = params.keySet();
        String value;
        String key;

        Iterator<String> iterator = keySet.iterator();

        while (iterator.hasNext()) {
            key = iterator.next();
            value = params.get(key);
            urlBuilder.append(key);
            urlBuilder.append("=");

            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }

            urlBuilder.append(value);
            if (iterator.hasNext()) {
                urlBuilder.append("&");
            }
        }
        Log.i(TAG, "NetworkRequests::appendParam::Builded URL : " + urlBuilder.toString());

        return urlBuilder.toString();
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
