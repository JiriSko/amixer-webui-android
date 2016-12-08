package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for retrieving equalizer from server.
 */
public class RetrieveEqualizerHttpRequestTask extends AsyncHttpRequestTask
{
	public RetrieveEqualizerHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	public RetrieveEqualizerHttpRequestTask(Context context, String url, OnFinishListener listener)
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
		return downloadFromURL("/equalizer/");
	}
}
