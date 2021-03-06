package com.meditationtracker.preferences;

import com.meditationtracker.R;
import com.meditationtracker.R.id;
import com.meditationtracker.R.layout;
import com.meditationtracker.R.styleable;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SliderPreference extends Preference {
	public interface IScrollReactor {
		void onScroll(int val);
		void onStopTracking();
		void onStartTracking(Context context);
	}
	
	
	private static final int DEFAULT = 3;
	private final int max;
	private int min;
	private int cur;
	private final String actorClass;
	private IScrollReactor reactor = dummyReactor;
	
	public SliderPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = getContext().obtainStyledAttributes(attrs, styleable.Attributes);
		max = a.getInteger(styleable.Attributes_max, 100);
		actorClass = a.getString(styleable.Attributes_actor);
		
		
		if (actorClass!=null) {
			Class<?> actor;
			try {
				actor = Class.forName(actorClass);
				reactor = (IScrollReactor) actor.getConstructor((Class[])null).newInstance((Object[])null);
			} catch (Exception e) {
				Log.e("MTRK", "Failed creating reactor class", e);
			}
		} else Log.w("MTRK", "No actor for SliderPreference");
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		cur = restorePersistedValue ? getPersistedInt(DEFAULT) : Integer.getInteger(defaultValue.toString(), DEFAULT);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		setLayoutResource(layout.slider_pref);
		
		View result = super.onCreateView(parent);
		SeekBar sb = (SeekBar) result.findViewById(id.prefSeekBar);
		sb.setMax(max - min);
		sb.setProgress(cur);
		sb.setOnSeekBarChangeListener(seekChanged);

		return result;
	}

	private final OnSeekBarChangeListener seekChanged = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			reactor.onStopTracking();
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			reactor.onStartTracking(getContext());
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			reactor.onScroll(progress+min);
			persistInt(progress+min);
		}
	};
	
	private static final IScrollReactor dummyReactor = new IScrollReactor() {
		
		@Override
		public void onScroll(int val) {
		}
		
		@Override
		public void onStopTracking() {
		}

		@Override
		public void onStartTracking(Context ctx) {
			
		}
	};
}
