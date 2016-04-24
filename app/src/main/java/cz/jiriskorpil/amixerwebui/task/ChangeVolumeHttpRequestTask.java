package cz.jiriskorpil.amixerwebui.task;

import android.content.Context;

/**
 * Asynchronous task for sending request to change volumes of specific control.
 */
public class ChangeVolumeHttpRequestTask extends AsyncHttpRequestTask
{
	public ChangeVolumeHttpRequestTask(Context context, String url)
	{
		super(context, url);
	}

	/**
	 * {@inheritDoc}
	 * Sends request to change volumes.
	 *
	 * @param params first parameters is control id,
	 *               second parameter is new set of volumes [in form: <volume>(/<volume>)*, e.g. 50/40/60 ]
	 */
	@Override
	protected String doInBackground(String... params)
	{
		return downloadFromURL("/volume/" + params[0] + "/" + params[1] + "/", "PUT");
	}
}
