package com.example.photoshare.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.AException;

import org.apache.thrift.TException;


public class MainActivity extends ActionBarActivity {
    private static final String LOG_TAG = "MainActivity";
    private EditText mTextName;
    private Button mBtnHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextName = (EditText) findViewById(R.id.name);
        mBtnHello = (Button) findViewById(R.id.hello);
        mBtnHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelloTask(MainActivity.this, mTextName.getText().toString()).execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    abstract class BaseTask extends AsyncTask<Void, Void, Object> {
        private ProgressDialog mProgress;
        protected Context mContext;

        public BaseTask(Context context) {
            mContext = context;
            mProgress = new ProgressDialog(context);
            mProgress.setTitle("Loading");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!mProgress.isShowing()) {
                mProgress.show();
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (mProgress.isShowing()) {
                mProgress.dismiss();
            }
            if (o instanceof AException) {
                String msg = ((AException) o).getMessage();
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class HelloTask extends BaseTask {
        private String mName;

        public HelloTask(Context context, String name) {
            super(context);
            mName = name;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                return RPCHelper.getPhotoService().hello(mName);
            } catch (AException ae) {
                return ae;
            } catch (TException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o instanceof TException) {
                Toast.makeText(mContext, "Cannot connect to internet.", Toast.LENGTH_SHORT).show();
            }
            if (o instanceof String) {
                Toast.makeText(mContext, o.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
