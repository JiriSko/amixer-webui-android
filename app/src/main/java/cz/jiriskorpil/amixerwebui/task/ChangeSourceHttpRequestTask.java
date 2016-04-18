package cz.jiriskorpil.amixerwebui.task;

import android.app.Activity;

/**
 * Asynchronous task for sending request to change source of specific control.
 */
public class ChangeSourceHttpRequestTask extends AsyncHttpRequestTask
{
	public ChangeSourceHttpRequestTask(Activity activity, String url)
	{
		super(activity, url);
	}

	/**
	 * {@inheritDoc}
	 * Sends request to change source.
	 *
	 * @param params first parameter is control id,
	 *               second parameter is new source
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/source/" + params[0] + "/" + params[1] + "/", "PUT");
	}
}
