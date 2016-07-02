package cz.jiriskorpil.amixerwebui.control.mixer;

import org.json.JSONException;
import org.json.JSONObject;

import cz.jiriskorpil.amixerwebui.control.Control;
import cz.jiriskorpil.amixerwebui.control.ControlType;

/**
 * Represents mixer control.
 */
public class MixerControl extends Control
{
	private ControlType type;

	/**
	 * Creates new instance of mixer control.
	 *
	 * @param jsonObject JSON object representing integer control retrieved from server
	 * @throws JSONException
	 */
	public MixerControl(JSONObject jsonObject) throws JSONException
	{
		super(jsonObject);

		type = ControlType.fromString(jsonObject.getString("type"));
	}

	/**
	 * Returns mixer control type.
	 *
	 * @return type of mixer control
	 */
	public ControlType getType()
	{
		return type;
	}
}
