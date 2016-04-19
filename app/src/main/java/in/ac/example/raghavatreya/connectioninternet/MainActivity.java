package in.ac.example.raghavatreya.connectioninternet;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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


public class MainActivity extends AppCompatActivity {

    //http://10.0.2.2/testrest/src/vendor/slim/slim/example/api/
    private String stringurl;
    Button btnhit;
    public TextView tvData;
    ArrayList<Document> doc;
    ListView showData;
    EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // doc = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        stringurl = new String("http://10.0.2.2/testrest/src/vendor/slim/slim/example/api/search/");
        btnhit = (Button) findViewById(R.id.btnhit);
        // tvData = (TextView) findViewById(R.id.tvJSONItem);

        showData = (ListView) findViewById(R.id.showData);
        keyword = (EditText) findViewById(R.id.keyword);
        btnhit.setOnClickListener(new View.OnClickListener() {


                                      @Override
                                      public void onClick(View v) {

                                          Log.i("tag", keyword.getText().toString());
                                          stringurl = stringurl.concat(keyword.getText().toString());
                                          new JSONTask().execute(stringurl);
                                      }
                                  }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            // new JSonTask.Url ... of thing  we rename the button with refresh
            // for refreshing the code
            keyword = (EditText) findViewById(R.id.keyword);
           String stringurl1 = new String("http://10.0.2.2/testrest/src/vendor/slim/slim/example/api/search/");
                                     Log.i("tag keywords ", keyword.getText().toString());
                                              stringurl1 = stringurl1.concat(keyword.getText().toString());
                                              new JSONTask().execute(stringurl1);
            // till here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class JSONTask extends AsyncTask<String, String, ArrayList<Document>> {

        @Override
        protected ArrayList<Document> doInBackground(String... params) {

            HttpURLConnection conection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;
            try {
                URL url = new URL(params[0]);
                conection = (HttpURLConnection) url.openConnection();
                conection.connect();

                InputStream stream = conection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
//                    Log.d("WTF", line);
                }
                Log.d("WTF", buffer.toString());

                doc = new ArrayList<>();
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("Document");

                String Download = new String("http://10.0.2.2/testrest/src/vendor/slim/slim/example/api/download/");
                StringBuffer finalBufferData = new StringBuffer();
                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);

                    String Name = finalObject.getString("name");
                    String DownLoadurl = Download.concat(finalObject.getString("name"));
                    Log.i("tag", Name);
                    //  String DownLoadurl = finalObject.getString("URL");
                    // finalBufferData.append(Name+" "+DownLoadurl+"\n");
                    doc.add(new Document(Name, DownLoadurl));

                    // for (int k=0;k<finalObject.getJSONArray("Cast").length();i++)
                    // {
                    // }
                }

                //Name + " " + DownLoadurl;

//                Toast.makeText(MainActivity.this, "yff"+buffer.toString(), Toast.LENGTH_SHORT).show();
                //return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(ArrayList<Document> result) {
            super.onPostExecute(result);
//            Toast.makeText(MainActivity.this, ""+result, Toast.LENGTH_SHORT).show();
            //tvData.setText(result);
            DocumentAdapter adapter = new DocumentAdapter(getApplicationContext(), R.layout.row, result);
            showData.setAdapter(adapter);
            // Adapter
        }
    }

    public class Document {
        private String DownloadUrl;
        private String Name;

        public Document(String N, String D) {
            Name = N;
            DownloadUrl = D;
        }

        public String getDownloadUrl() {
            return DownloadUrl;
        }

        public String getName() {
            return Name;
        }

        public void setDownloadUrl(String downloadUrl) {
            DownloadUrl = downloadUrl;
        }

        public void setName(String name) {
            Name = name;
        }
    }

    public class DocumentAdapter extends ArrayAdapter {

        private List<Document> DisplayDocumentList;
        private int resource;
        private LayoutInflater inflater;

        public DocumentAdapter(Context context, int resource, List<Document> objects) {
            super(context, resource, objects);
            DisplayDocumentList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return DisplayDocumentList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // iterator length to number of iteam in list
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);

            }
            TextView Downloadurl = (TextView) convertView.findViewById(R.id.Downloadurl);
            TextView DocumentName = (TextView) convertView.findViewById(R.id.DocumentName);
            TextView Author = (TextView) convertView.findViewById(R.id.Author);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

            DocumentName.setText(DisplayDocumentList.get(position).getName());
            // Author.setText(DisplayDocumentList.get(position).getAuthor);
            Downloadurl.setText(DisplayDocumentList.get(position).getName());

            return convertView;
        }
    }
}






