package cz.jiriskorpil.amixerwebui.control.mixer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cz.jiriskorpil.amixerwebui.control.ControlType;

/**
 * Factory for create new mixer control from JSON object
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class MixerControlFactory
{
	/**
	 * Creates mixer control instance.
	 *
	 * @param jsonObject JSON object representing mixer control retrieved from server
	 * @return new mixer control instance
	 */
	@SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault")
	public static MixerControl createMixerControl(JSONObject jsonObject)
	{
		try {
			switch (ControlType.fromString(jsonObject.getString("type")))
			{
				case ENUMERATED:
					return new EnumeratedControl(jsonObject);
				case BOOLEAN:
					return new BooleanControl(jsonObject);
				case INTEGER:
					return new IntegerControl(jsonObject);
			}
		} catch (JSONException e) {
			Log.e("JSONException", "Error: " + e.toString());
		}
		return null;
	}
}
