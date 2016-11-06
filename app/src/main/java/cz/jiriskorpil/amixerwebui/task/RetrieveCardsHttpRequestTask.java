package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for retrieving available sound cards from server.
 */
public class RetrieveCardsHttpRequestTask extends AsyncHttpRequestTask
{
	public RetrieveCardsHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	public RetrieveCardsHttpRequestTask(Context context, String url, OnFinishListener listener)
	{
		this(context, url);
		this.mOnFinishListener = listener;
	}

	/**
	 * {@inheritDoc}
	 * Retrieves available sound cards from server.
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/cards/");
	}
}
