package cz.jiriskorpil.amixerwebui.control;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Equalizer implements Parcelable
{
	private List<ControlContainer> controls;
	private String rawData;

	public Equalizer(String rawData)
	{
		controls = new ArrayList<>();
		this.rawData = rawData;
	}

	public List<ControlContainer> getControls()
	{
		if (controls.isEmpty())
		{
			try {
				controls = ControlParser.parse(new JSONArray(rawData));
			} catch (JSONException e) {
				Log.e("JSONException", "Error: " + e.toString());
			}
		}
		return controls;
	}


	public Equalizer(Parcel in)
	{
		controls = new ArrayList<>();
		rawData = in.readString();
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i)
	{
		parcel.writeString(rawData);
	}

	public static final Parcelable.Creator<Equalizer> CREATOR= new Parcelable.Creator<Equalizer>()
	{
		@Override
		public Equalizer createFromParcel(Parcel parcel)
		{
			return new Equalizer(parcel);
		}

		@Override
		public Equalizer[] newArray(int i)
		{
			return new Equalizer[i];
		}
	};
}
