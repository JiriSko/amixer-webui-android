package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for retrieving server's hostname.
 */
public class RetrieveHostnameHttpRequestTask extends AsyncHttpRequestTask
{
	public RetrieveHostnameHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	public RetrieveHostnameHttpRequestTask(Context context, String url, OnFinishListener listener)
	{
		this(context, url);
		this.mOnFinishListener = listener;
	}

	/**
	 * {@inheritDoc}
	 * Retrieves server's hostname.
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/hostname/");
	}
}
