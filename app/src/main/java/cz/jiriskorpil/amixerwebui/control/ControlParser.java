package cz.jiriskorpil.amixerwebui.control;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cz.jiriskorpil.amixerwebui.control.mixer.MixerControl;

public final class ControlParser
{
	private static List<ControlContainer> controls;

	private ControlParser() {}

	/**
	 * Parses controls out of JSON array.
	 *
	 * @param jsonArray JSON array representing controls retrieved from server
	 * @return list of control containers
	 */
	public static List<ControlContainer> parse(JSONArray jsonArray)
	{
		Control control;
		controls = new ArrayList<>();

		try {
			for (int i = 0; i < jsonArray.length(); i++)
			{
				control = ControlFactory.createControl(jsonArray.getJSONObject(i));
				if (control instanceof MixerControl) {
					getControlContainer(control.getName()).addControl(control);
				}/* else {
					// not supported (yet?)
				}*/
			}
		} catch (JSONException e) {
			Log.e("JSONException", "Error: " + e.toString());
		}
		return controls;
	}

	private static ControlContainer getControlContainer(String controlName)
	{
		int index = findControlContainer(controlName);

		if (index == -1) // if doesn't exists then create new container
		{
			controls.add(new ControlContainer(controlName));
			index = controls.size() - 1;
		}

		return controls.get(index);
	}

	private static int findControlContainer(String controlName)
	{
		for (int i = 0; i < controls.size(); i++) {
			if (controls.get(i).getName().equals(ControlContainerType.getContainerName(controlName))) {
				return i;
			}
		}
		return -1;
	}
}
