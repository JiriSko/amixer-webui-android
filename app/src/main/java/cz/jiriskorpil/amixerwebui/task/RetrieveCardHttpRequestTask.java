package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for retrieving active sound card from server.
 */
public class RetrieveCardHttpRequestTask extends AsyncHttpRequestTask
{
	public RetrieveCardHttpRequestTask(Context context, String url, OnFinishListener listener)
	{
		super(context, url);
		this.mOnFinishListener = listener;
	}

	/**
	 * {@inheritDoc}
	 * Retrieves active sound card from server.
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/card/");
	}
}
