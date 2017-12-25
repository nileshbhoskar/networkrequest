package nbit.com.networkreauest.request;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import nbit.com.networkreauest.util.INetworkConstants;
import nbit.com.networkreauest.util.IResponseListener;

/**
 * Created by dcpl-android on 25/5/17.
 */

public class UrlConnectionRequest {

    private static final String TAG = UrlConnectionRequest.class.getSimpleName();
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private IResponseListener mResponseListener;

    /**
     * Constructor to establish class, all parameters are compulsory to instantiate the class. Also for low memory
     * usage instantiate class using {@link java.lang.ref.WeakReference}.
     *
     * @param requestURL       Server request url used to establish connection in server and application
     * @param responseListener a listener to receive response after for given request.
     * @throws IOException Exception thrown while instantiating class if connection not established or problem occurred while
     *                     establish connection
     */
    public UrlConnectionRequest(String requestURL, IResponseListener responseListener)
            throws IOException {

        this(requestURL, INetworkConstants.CHAR_SET_UTF_8, responseListener);
    }

    /**
     * Constructor to establish class, all parameters are compulsory to instantiate the class. Also for low memory
     * usage instantiate class using {@link java.lang.ref.WeakReference}.
     *
     * @param requestURL       Server request url used to establish connection in server and application
     * @param charset          Character set to transmit data.
     * @param responseListener a listener to receive response after for given request.
     * @throws IOException Exception thrown while instantiating class if connection not established or problem occurred while
     *                     establish connection
     */
    public UrlConnectionRequest(String requestURL, String charset, IResponseListener responseListener)
            throws IOException {

        this(INetworkConstants.METHOD_TYPE_GET, requestURL, charset, responseListener, false);
    }

    /**
     * Constructor to establish class, all parameters are compulsory to instantiate the class. Also for low memory
     * usage instantiate class using {@link java.lang.ref.WeakReference}.
     *
     * @param requestURL       Server request url used to establish connection in server and application
     * @param charset          Character set to transmit data.
     * @param responseListener a listener to receive response after for given request.
     * @param sslCertificate   If request is for HTTPS server then set it to true otherwise false
     * @throws IOException Exception thrown while instantiating class if connection not established or problem occurred while
     *                     establish connection
     */
    public UrlConnectionRequest(String requestURL, String charset, IResponseListener responseListener, boolean sslCertificate)
            throws IOException {
        this(INetworkConstants.METHOD_TYPE_GET, requestURL, charset, responseListener, sslCertificate);

    }

    /**
     * Constructor to establish class, all parameters are compulsory to instantiate the class. Also for low memory
     * usage instantiate class using {@link java.lang.ref.WeakReference}.
     *
     * @param requestURL       Server request url used to establish connection in server and application
     * @param charset          Character set to transmit data.
     * @param responseListener a listener to receive response after for given request.
     * @throws IOException Exception thrown while instantiating class if connection not established or problem occurred while
     *                     establish connection
     */
    public UrlConnectionRequest(String methodType, String requestURL, String charset, IResponseListener responseListener)
            throws IOException {
        this(methodType, requestURL, charset, responseListener, false);
    }

    /**
     * Constructor to establish class, all parameters are compulsory to instantiate the class. Also for low memory
     * usage instantiate class using {@link java.lang.ref.WeakReference}.
     *
     * @param requestURL       Server request url used to establish connection in server and application
     * @param charset          Character set to transmit data.
     * @param responseListener a listener to receive response after for given request.
     * @param sslCertificate   If request is for HTTPS server then set it to true otherwise false
     * @throws IOException Exception thrown while instantiating class if connection not established or problem occurred while
     *                     establish connection
     */
    public UrlConnectionRequest(String methodType, String requestURL, String charset, IResponseListener responseListener, boolean sslCertificate)
            throws IOException {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "===";
        URL url = new URL(requestURL);
        if (sslCertificate) {
            httpConn = (HttpsURLConnection) url.openConnection();
        } else {
            httpConn = (HttpURLConnection) url.openConnection();
        }

        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestMethod(methodType);
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("ENCTYPE", "multipart/form-data");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        this.mResponseListener = responseListener;
    }


    public static UrlConnectionRequest getInstance(String requestUrl, IResponseListener responseListener) throws IOException {

        return new UrlConnectionRequest(requestUrl, responseListener);
    }

    public static UrlConnectionRequest getInstance(String requestUrl, IResponseListener responseListener, boolean sslCertificate) throws IOException {

        return new UrlConnectionRequest(INetworkConstants.METHOD_TYPE_GET, requestUrl, INetworkConstants.CHAR_SET_UTF_8, responseListener, sslCertificate);
    }

    public static UrlConnectionRequest getInstance(String requestUrl, String charset, IResponseListener responseListener) throws IOException {

        return new UrlConnectionRequest(requestUrl, charset, responseListener);
    }

    public static UrlConnectionRequest getSSLInstance(String requestUrl, IResponseListener responseListener) throws IOException {

        return new UrlConnectionRequest(INetworkConstants.METHOD_TYPE_POET, requestUrl, INetworkConstants.CHAR_SET_UTF_8, responseListener, true);
    }

    public static UrlConnectionRequest getPostInstance(String requestUrl, IResponseListener responseListener) throws IOException {

        return new UrlConnectionRequest(INetworkConstants.METHOD_TYPE_POET, requestUrl, INetworkConstants.CHAR_SET_UTF_8, responseListener, false);
    }

    /**
     * Static method to get instance of this class to make request to HTTPS Server.
     * @param requestUrl Server request url used to establish connection in server and application
     * @param responseListener a listener to receive response after for given request.
     * @return instance of {@link #UrlConnectionRequest} class.
     * @throws IOException
     */
    public static UrlConnectionRequest getSSLPostInstanceWith(String requestUrl, IResponseListener responseListener) throws IOException {

        return new UrlConnectionRequest(INetworkConstants.METHOD_TYPE_POET, requestUrl, INetworkConstants.CHAR_SET_UTF_8, responseListener, true);
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        Log.i(TAG, "name::" + name + " value::" + value);
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a upload file section to the request
     *
     * @param fieldName  name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {

        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName
                + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() throws IOException {
        StringBuilder response = new StringBuilder();

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                Log.i("FileUpload", "Received response::" + line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        if (null != mResponseListener) {
            mResponseListener.networkResponse(response);
        }
        return response.toString();
    }
}
