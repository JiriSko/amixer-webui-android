package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for sending request to change equalizer values of specific control.
 */
public class ChangeEqualizerHttpRequestTask extends AsyncHttpRequestTask
{
	public ChangeEqualizerHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	/**
	 * {@inheritDoc}
	 * Sends request to change equalizer values.
	 *
	 * @param params first parameters is control id,
	 *               second parameter is new set of volumes [in form: <value>(/<value>)*, e.g. 50/40/60 ]
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/equalizer/" + params[0] + "/" + params[1] + "/", "PUT");
	}
}
