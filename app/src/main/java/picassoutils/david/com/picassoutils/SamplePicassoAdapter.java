package picassoutils.david.com.picassoutils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import picassoutils.david.com.picassoutils.utils.PicassoUtils;

/**
 * @author WeiDeng
 * @date 16/2/23
 * @description
 */
public class SamplePicassoAdapter extends BaseAdapter {

    private Context mContext;
    private List<String>  mDatas;
    private LayoutInflater mLayoutinflater;

    public SamplePicassoAdapter(Context context, List<String> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.mLayoutinflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutinflater.inflate(R.layout.item_image_layout,parent,false);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.content_item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PicassoUtils.into(mContext, mDatas.get(position), viewHolder.img,R.mipmap.default_unit_big);

        return convertView;
    }

    static class ViewHolder {
        ImageView img;
    }
}
