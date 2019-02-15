package com.zhiyihealth.registration.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiyihealth.registration.R;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfoCheck;

import java.util.ArrayList;

/**
 * @author Lihao
 * @date 2019-1-11
 * Email heaolihao@163.com
 */
public class DoctorItemAdapter extends BaseQuickAdapter<DoctorInfoCheck, BaseViewHolder> {

    private int preCheck = -1;

    private Context mContext;

    public int getPreCheck() {
        return preCheck;
    }

    public void setPreCheck(int preCheck) {
        this.preCheck = preCheck;
    }

    public DoctorItemAdapter(Context context, int layoutResId, ArrayList<DoctorInfoCheck> data) {
        super(layoutResId, data);
        mContext = context;
        if (data.size() == 1) {
            data.get(0).setCheck(true);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorInfoCheck item) {
        helper.setVisible(R.id.iv_label, false);
        helper.setText(R.id.tv_doctor_sex, "1".equals(item.getSex()) ? "男" : "女");

        helper.setTextColor(R.id.tv_doctor_name, ContextCompat.getColor(mContext, R.color.color_33));
        helper.setTextColor(R.id.tv_doctor_sex, ContextCompat.getColor(mContext, R.color.color_7B));
        helper.setTextColor(R.id.tv_list_num, ContextCompat.getColor(mContext, R.color.color_66));

        if (item.isFulled()) {
            helper.setTextColor(R.id.tv_doctor_name, ContextCompat.getColor(mContext, R.color.color_C5));
            helper.setTextColor(R.id.tv_doctor_sex, ContextCompat.getColor(mContext, R.color.color_C5));
            helper.setTextColor(R.id.tv_list_num, ContextCompat.getColor(mContext, R.color.color_C5));
            helper.setImageResource(R.id.item_radio, R.drawable.radio_fulled);
            helper.setVisible(R.id.iv_label, true);
        }

        if (item.isCheck()) {
            helper.setBackgroundRes(R.id.rl_solid, R.drawable.card_choose_shape);
            helper.setImageResource(R.id.item_radio, R.drawable.radio_checked);
        } else {
            helper.setBackgroundRes(R.id.rl_solid, R.drawable.card_unchoose_shape);
            if (!item.isFulled()) {
                helper.setImageResource(R.id.item_radio, R.drawable.radio_unchecked);
            }
        }

        helper.setText(R.id.tv_doctor_name, item.getRealName());
        if ("1".equals(item.getSex())) {
            helper.setImageResource(R.id.iv_doctor, R.drawable.ic_head_man);

        } else {
            helper.setImageResource(R.id.iv_doctor, R.drawable.ic_head_women);
        }
//        helper.setChecked(R.id.item_radio, item.isCheck());

        helper.setText(R.id.tv_list_num, item.getWaitNum() + "人排队中...");
    }
}
