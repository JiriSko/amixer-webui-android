package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for sending request to change sound card.
 */
public class ChangeCardHttpRequestTask extends AsyncHttpRequestTask
{
	public ChangeCardHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	public ChangeCardHttpRequestTask(Context context, String url, OnFinishListener listener)
	{
		this(context, url);
		this.mOnFinishListener = listener;
	}

	/**
	 * {@inheritDoc}
	 * Sends request to change card.
	 *
	 * @param params first parameter is card id,
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/card/" + params[0] + "/", "PUT");
	}
}
