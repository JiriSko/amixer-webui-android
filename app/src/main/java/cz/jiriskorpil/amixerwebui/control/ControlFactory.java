package cz.jiriskorpil.amixerwebui.control;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cz.jiriskorpil.amixerwebui.control.mixer.MixerControlFactory;

/**
 * Factory for create new control from JSON object
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class ControlFactory
{
	/**
	 * Creates control instance.
	 *
	 * @param jsonObject JSON object representing control retrieved from server
	 * @return new control instance
	 */
	public static Control createControl(JSONObject jsonObject)
	{
		try
		{
			switch (InterfaceType.fromString(jsonObject.getString("iface")))
			{
				case MIXER:
					return MixerControlFactory.createMixerControl(jsonObject);
				default:
					return new Control(jsonObject);
			}
		} catch (JSONException e) {
			Log.e("JSONException", "Error: " + e.toString());
		}
		return null;
	}
}
