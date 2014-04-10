package com.krld.vkresearch.activitys;

import android.app.*;
import android.os.*;
import com.krld.vkresearch.views.GraphView;

public class GraphActivity extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(new GraphView(this));
		}
}
