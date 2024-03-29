package com.hospital.s1m.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hospital.s1m.R;
import com.hospital.s1m.lib_base.entity.DoctorInfoCheck;

import java.util.ArrayList;

/**
 * @author Lihao
 * @date 2019-1-11
 * Email heaolihao@163.com
 */
public class DoctorListAdapter extends BaseQuickAdapter<DoctorInfoCheck, BaseViewHolder> {

    private int preCheck = 0;

    private Context mContext;

    public int getPreCheck() {
        return preCheck;
    }

    public void setPreCheck(int preCheck) {
        this.preCheck = preCheck;
    }

    public DoctorListAdapter(Context context, ArrayList<DoctorInfoCheck> data) {
        super(R.layout.item_doctor, data);
//        data.get(preCheck).setCheck(true);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorInfoCheck item) {
        helper.setVisible(R.id.iv_label, false);
        helper.setText(R.id.tv_doctor_sex, "1".equals(item.getSex()) ? "男" : "女");

        if (item.isCheck()) {
            helper.setBackgroundRes(R.id.rl_solid, R.drawable.item_choose_shape);
            helper.setTextColor(R.id.tv_doctor_name, ContextCompat.getColor(mContext, R.color.color_white));
            helper.setTextColor(R.id.tv_doctor_sex, ContextCompat.getColor(mContext, R.color.color_white));
            helper.setTextColor(R.id.tv_list_num, ContextCompat.getColor(mContext, R.color.color_white));
        } else {
            helper.setBackgroundRes(R.id.rl_solid, R.drawable.item_unchoose_shape);
            helper.setTextColor(R.id.tv_doctor_name, ContextCompat.getColor(mContext, R.color.color_33));
            helper.setTextColor(R.id.tv_doctor_sex, ContextCompat.getColor(mContext, R.color.color_7B));
            helper.setTextColor(R.id.tv_list_num, ContextCompat.getColor(mContext, R.color.color_66));
        }

        helper.setText(R.id.tv_doctor_name, item.getRealName());
        if ("1".equals(item.getSex())) {
            helper.setImageResource(R.id.iv_doctor, R.drawable.ic_head_man);

        } else {
            helper.setImageResource(R.id.iv_doctor, R.drawable.ic_head_women);
        }

        helper.setText(R.id.tv_list_num, item.getWaitNum() + "人排队中...");
    }
}
