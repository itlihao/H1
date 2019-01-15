package com.zhiyihealth.registration.lib_user.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiyihealth.registration.lib_base.data.SPDataSource;
import com.zhiyihealth.registration.lib_base.entity.EmnuLeft;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;
import com.zhiyihealth.registration.lib_user.R;
import com.zhiyihealth.registration.lib_user.bean.MenuItem;

import java.util.ArrayList;

/**
 *
 * @author zyjk654764
 * @date 2018/9/4
 */

public class DrawAdapter extends BaseMultiItemQuickAdapter<EmnuLeft, BaseViewHolder> {

    private Context context;

    private static String needPringList = "needPrint";
    private static String MODEL_QUICK = "quickRegistration";

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
                        helper.setOnCheckedChangeListener(R.id.on_off, (buttonView, isChecked) -> {
                            SPDataSource.put(mContext, MODEL_QUICK, isChecked);
                            if (isChecked) {
                                // 跳转到快速挂号页面
                            } else {
                                // 跳转到挂号页面
                            }
                        });
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


}
