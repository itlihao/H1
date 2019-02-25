package com.hospital.s1m.lib_user.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_base.entity.EmnuLeft;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_user.R;
import com.hospital.s1m.lib_user.bean.MenuItem;

import java.util.ArrayList;

/**
 * @author zyjk654764
 * @date 2018/9/4
 */

public class DrawAdapter extends BaseMultiItemQuickAdapter<EmnuLeft, BaseViewHolder> implements CompoundButton.OnCheckedChangeListener {

    private Context context;

    private static String needPringList = "needPrint";
    public static String MODEL_QUICK = "quickRegistration";

    private OnCheckedChangedListener changedListener;

    public DrawAdapter(Context context, ArrayList list) {
        super(list);
        this.context = context;
        addItemType(EmnuLeft.ITEM_TITLE, R.layout.user_draw_list);
        addItemType(EmnuLeft.ITEM_IMAGE, R.layout.user_draw_last);
    }

    @Override
    protected void convert(BaseViewHolder helper, EmnuLeft item) {
        switch (helper.getItemViewType()) {
            case EmnuLeft.ITEM_TITLE:
                helper.setText(R.id.draw_text, item.getName());
                break;
            case EmnuLeft.ITEM_IMAGE:
                helper.setText(R.id.last_text, item.getName());
                switch (item.getKey()) {
                    case MenuItem.PRINT_CHECK:
                        boolean autoPrint = (boolean) SPDataSource.get(mContext, needPringList, true);
                        helper.setChecked(R.id.on_off, autoPrint);
                        helper.setOnCheckedChangeListener(R.id.on_off, (buttonView, isChecked) -> SPDataSource.put(mContext, needPringList, isChecked));
                        break;
                    case MenuItem.FAST_REGISTER:
                        boolean quickModel = (boolean) SPDataSource.get(mContext, MODEL_QUICK, true);
                        helper.setChecked(R.id.on_off, quickModel);
                        helper.setOnCheckedChangeListener(R.id.on_off, this);
                        break;
                    default:
                        helper.setOnCheckedChangeListener(R.id.on_off, (buttonView, isChecked) -> ToastUtils.showToast(context, "敬请期待"));
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (changedListener != null) {
            changedListener.onCheckedChanged(buttonView, isChecked);
        }
    }

    public interface OnCheckedChangedListener {
        void onCheckedChanged(View view, boolean isChecked);
    }

    public void setCheckedChangedListener(OnCheckedChangedListener clickListener) {
        this.changedListener = clickListener;
    }
}
