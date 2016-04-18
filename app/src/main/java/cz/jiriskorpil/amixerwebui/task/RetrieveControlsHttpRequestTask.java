package cz.jiriskorpil.amixerwebui.task;

import android.app.Activity;

/**
 * Asynchronous task for retrieving controls from server while user is waiting on progress dialog.
 */
public class RetrieveControlsHttpRequestTask extends AsyncHttpRequestTask
{
	public RetrieveControlsHttpRequestTask(Activity activity, String url)
	{
		super(activity, url);
	}

	public RetrieveControlsHttpRequestTask(Activity activity, String url, AsyncCallback callback)
	{
		this(activity, url);
		this.callback = callback;
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
