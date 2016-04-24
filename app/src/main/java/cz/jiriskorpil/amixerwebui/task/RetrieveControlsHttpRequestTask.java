package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for retrieving controls from server while user is waiting on progress dialog.
 */
public class RetrieveControlsHttpRequestTask extends AsyncHttpRequestTask
{
	public RetrieveControlsHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	public RetrieveControlsHttpRequestTask(Context context, String url, OnFinishListener listener)
	{
		this(context, url);
		this.mOnFinishListener = listener;
	}

	/**
	 * {@inheritDoc}
	 * Retrieves controls from server.
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/controls/");
	}
}
