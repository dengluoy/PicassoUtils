package picassoutils.david.com.picassoutils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import picassoutils.david.com.picassoutils.utils.Data;
import picassoutils.david.com.picassoutils.utils.PicassoUtils;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.content_listview);

        List<String> datas = Arrays.asList(Data.URLS);
        SamplePicassoAdapter adapter = new SamplePicassoAdapter(this,datas);
        mListView.setAdapter(adapter);

        PicassoUtils.setPauseOnScrollListener(this, mListView,true,true);
    }

}
