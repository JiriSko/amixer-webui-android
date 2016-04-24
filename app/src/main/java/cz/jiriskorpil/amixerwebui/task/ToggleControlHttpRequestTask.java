package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for sending request to turn on/off specific control.
 */
public class ToggleControlHttpRequestTask extends AsyncHttpRequestTask
{
	public ToggleControlHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	/**
	 * {@inheritDoc}
	 * Sends request to turn on/off.
	 *
	 * @param params first parameter is control id,
	 *               second parameter is value (0 to turn off, 1 to turn on)
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/control/" + params[0] + "/" + params[1] + "/", "PUT");
	}
}
