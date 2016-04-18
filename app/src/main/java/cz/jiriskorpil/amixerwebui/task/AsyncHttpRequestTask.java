package cz.jiriskorpil.amixerwebui.task;

import android.app.Activity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Abstract class for asynchronous HTTP request to the server
 */
abstract public class AsyncHttpRequestTask extends android.os.AsyncTask<String, String, String>
{
	protected Activity activity;
	protected AsyncCallback callback;
	protected String url;

	/**
	 * Creates new instance of asynchronous task
	 *
	 * @param activity parent activity
	 * @param url      base url directing to server
	 */
	public AsyncHttpRequestTask(Activity activity, String url)
	{
		super();
		this.activity = activity;
		this.url = url;
	}

	/**
	 * Creates new instance of asynchronous task with callback after HTTP response arrives
	 *
	 * @param activity parent activity
	 * @param url      base url directing to server
	 * @param callback callback which is called after HTTP response arrives
	 */
	public AsyncHttpRequestTask(Activity activity, String url, AsyncCallback callback)
	{
		this(activity, url);
		this.callback = callback;
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
		if (callback != null) {
			callback.execute(result);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		return result;
	}
}
