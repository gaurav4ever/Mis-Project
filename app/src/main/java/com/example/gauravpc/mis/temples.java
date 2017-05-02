package com.example.gauravpc.mis;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class temples extends AppCompatActivity {
    
    ListView listView;
    Typeface typeface;
    RelativeLayout r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temples);

        Context context = getApplicationContext();
        AssetManager am = context.getApplicationContext().getAssets();

        typeface = Typeface.createFromAsset(am,
                String.format(Locale.US, "fonts/%s", "lato.ttf"));

        TextView t=(TextView)findViewById(R.id.headerText);
        t.setTypeface(typeface);

        ImageView backImage=(ImageView)findViewById(R.id.back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("back", "pressed");
                onBackPressed();
            }
        });

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        
        listView=(ListView)findViewById(R.id.templesList);
        listView.setVisibility(View.GONE);
        r=(RelativeLayout)findViewById(R.id.loadingLayout);
        r.setVisibility(View.VISIBLE);

        boolean check=isNetworkAvailable();
        if(check==true) {
            new JSONTask().execute("https://gauravpersonal.herokuapp.com/api/mis/all");
        }
        else{
            Toast.makeText(getApplicationContext(), "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public class JSONTask extends AsyncTask<String, String, List<TempleModel>> {


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected List<TempleModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("all");

                List<TempleModel> templeModelList = new ArrayList<>();
                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    TempleModel templeModel = new TempleModel();
                    templeModel.setName(finalObject.getString("temple_name"));
                    templeModel.setImg(finalObject.getString("img"));
                    templeModel.setDesc(finalObject.getString("desc"));

                    templeModelList.add(templeModel);
                }

                return templeModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<TempleModel> TempleModels) {
            super.onPostExecute(TempleModels);

            r.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);


            TemplesAdapter templesAdapter = new TemplesAdapter(getApplicationContext(), R.layout.row_temple, TempleModels);
            listView.setAdapter(templesAdapter);
        }
    }

    public class TemplesAdapter extends ArrayAdapter {
        public List<TempleModel> TempleModelList;
        private int resource;
        private LayoutInflater inflater;

        public TemplesAdapter(Context context, int resource, List<TempleModel> objects) {
            super(context, resource,objects);
            TempleModelList=objects;
            this.resource=resource;
            inflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView=inflater.inflate(R.layout.row_temple,null);
            }

            TextView temple_nameText,descText;

            temple_nameText=(TextView)convertView.findViewById(R.id.temple_name);
            descText=(TextView)convertView.findViewById(R.id.desc);


            temple_nameText.setText(TempleModelList.get(position).getName());
            temple_nameText.setTypeface(typeface);
            descText.setText(TempleModelList.get(position).getDesc());
            descText.setTypeface(typeface);

            ImageView img=(ImageView)convertView.findViewById(R.id.img);
            ImageLoader.getInstance().displayImage(TempleModelList.get(position).getImg(),img);


            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



