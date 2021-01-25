package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract class for asynchronous HTTP request to the server
 */
abstract public class AsyncHttpRequestTask extends android.os.AsyncTask<String, String, String>
{
	protected Context context;
	protected OnFinishListener mOnFinishListener;
	protected String url;

	/**
	 * Creates new instance of asynchronous task
	 *
	 * @param context The context to use. Usually {@link android.app.Activity} object.
	 * @param url     base url directing to server
	 */
	public AsyncHttpRequestTask(Context context, String url)
	{
		super();
		this.context = context;
		this.url = url;
	}

	/**
	 * Creates new instance of asynchronous task with callback after HTTP response arrives
	 *
	 * @param context  The context to use. Usually {@link android.app.Activity} object.
	 * @param url      base url directing to server
	 * @param listener listener which is called after HTTP response arrives
	 */
	public AsyncHttpRequestTask(Context context, String url, OnFinishListener listener)
	{
		this(context, url);
		this.mOnFinishListener = listener;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	abstract protected String doInBackground(String... params);

	/**
	 * {@inheritDoc}
	 *
	 * @param result
	 */
	@Override
	protected void onPostExecute(String result)
	{
		if (mOnFinishListener != null) {
			mOnFinishListener.onFinish(result);
		}
	}

	/**
	 * Creates HTTP request to specific URL path with GET method and returns downloaded data.
	 *
	 * @param urlString path
	 * @return downloaded data
	 */
	protected String downloadFromURL(String urlString)
	{
		return downloadFromURL(urlString, "GET");
	}

	/**
	 * Creates HTTP request to specific URL path and returns downloaded data.
	 *
	 * @param urlString path
	 * @param method    used method
	 * @return downloaded data
	 */
	@SuppressWarnings({"PMD.EmptyCatchBlock", "PMD.CloseResource"})
	protected String downloadFromURL(String urlString, String method)
	{
		//System.out.println("[" + method + "] Accessing " + this.url + urlString);

		String result = "";
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(this.url + urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod(method);

			InputStream in = new BufferedInputStream(urlConnection.getInputStream());

			int data = in.read();
			while (data != -1)
			{
				result += (char) data;
				data = in.read();
			}
			in.close();
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return result;
	}


	public interface OnFinishListener
	{
		/**
		 * Called when downloading finished.
		 *
		 * @param result downloaded data
		 */
		void onFinish(String result);
	}
}
