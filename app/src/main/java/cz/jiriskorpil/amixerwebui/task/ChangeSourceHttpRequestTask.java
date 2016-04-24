package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for sending request to change source of specific control.
 */
public class ChangeSourceHttpRequestTask extends AsyncHttpRequestTask
{
	public ChangeSourceHttpRequestTask(Context context, String url)
	{
		super(context, url);
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
