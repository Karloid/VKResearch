package com.krld.vkresearch.activitys;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import com.krld.vkresearch.R;
import com.krld.vkresearch.model.User;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.json.*;
import android.content.*;

public class MainActivity extends Activity
{
	//https://api.vk.com/method/friends.get?user_id=5548723&fields=name

	private ListView userListView;

	private EditText userIdTextView;

	private Button printButton;

	private List<User> users;

	private Button showLastResponseButton;

	private TextView lastResponseTextView;
	
	private String lastResponse;

	private LinearLayout layout;

	private ScrollView scrollView;

	private LinearLayout scrollLayout;

	private Button toGraphButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    layout = (LinearLayout) findViewById(R.id.mainlayout);
		userIdTextView = new EditText(this);
		layout.addView(userIdTextView);
		userIdTextView.setText("5378315");
		printButton = new Button(this);
		layout.addView(printButton);
		printButton.setText("print");
		
		toGraphButton = new Button(this);
		layout.addView(toGraphButton);
		toGraphButton.setText("to graph activity");
		
		showLastResponseButton = new Button(this);
		showLastResponseButton.setText("show last response");
		
		
		
	//	layout.addView(showLastResponseButton);
		
		scrollView = new ScrollView(this);
		layout.addView(scrollView);
		scrollLayout = new LinearLayout(this);
		scrollLayout.setOrientation(LinearLayout.VERTICAL);
		scrollLayout.addView(showLastResponseButton);
		scrollView.addView(scrollLayout);
		
		lastResponseTextView = new TextView(this);
		//layout.addView(lastResponseTextView);
		

		userListView = new ListView(this);

		layout.addView(userListView);
		
		userListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                userIdTextView.setText(users.get(p3).userId + "");
            }


        });
			
		toGraphButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					try {
					Intent intent = new Intent(p1.getContext(), GraphActivity.class);
				    startActivityForResult(intent,0);
					} catch (Exception e){
						lastResponse = e.getLocalizedMessage();
					}}
			});	
		printButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					showMsg("print");
					fillListView(new ArrayList());
					new AsyncRequestTask().execute();
					//fillListViewByUsers();
				}
			});
			
		showLastResponseButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
				    lastResponseTextView.setText(lastResponse);
					
					if (scrollLayout.indexOfChild(lastResponseTextView) > -1){
						scrollLayout.removeView(lastResponseTextView);
					}else{
						scrollLayout.addView(lastResponseTextView);
					}
				}
			});
		//   fillListView(strings);
	}

	private void showMsg(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public static String httpPost(String url)
	{

		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpPost httppost = new HttpPost(url);
// Depends on your web service
		httppost.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		try
		{
			HttpResponse response = httpclient.execute(httppost);           
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			result = sb.toString();
		}
		catch (Exception e)
		{ 
			// showMsg(e.getMessage());
		}
		finally
		{
			try
			{if (inputStream != null)inputStream.close();}
			catch (Exception squish)
			{}
		}
		return result;
	}

	private void fillListView(List<String> strings)
	{
		userListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings));
	}

	public class AsyncRequestTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected String doInBackground(Void[] p1)
		{
			Long userId = Long.valueOf(userIdTextView.getText().toString());
			return httpPost("https://api.vk.com/method/friends.get?user_id=" +userId +"&fields=name");
		}
		
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
		{
			lastResponse =result;
			users = new ArrayList<User>();

            try
			{
				JSONObject jObj = new JSONObject(result);
				JSONArray jusers = jObj.getJSONArray("response");
				for (int i=0; i < jusers.length(); i++)
				{
					JSONObject juser = jusers.getJSONObject(i);
					users.add(new User(juser.getLong("uid"), juser.getString("first_name"), juser.getString("last_name")));
				}
			}
			catch (JSONException e)
			{}
			fillListViewByUsers();
		}

	}

	private void fillListViewByUsers()
	{
		ArrayList strings = new ArrayList<String>();
		for (User user : users)
		{
			strings.add(user.userId + " " + user.firstName + " " + user.lastName);
		}
		fillListView(strings);
	}
}
