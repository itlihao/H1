package com.hospital.s1m.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hospital.s1m.R;
import com.hospital.s1m.lib_base.entity.DoctorInfoCheck;
import com.hospital.s1m.lib_base.listener.CallInterface;

import java.util.ArrayList;

/**
 * @author Lihao
 * @date 2019-1-11
 * Email heaolihao@163.com
 */
public class DoctorItemAdapter extends BaseQuickAdapter<DoctorInfoCheck, BaseViewHolder> {

    private Context mContext;

    private CallInterface callInterface;

    public void setCallInterface(CallInterface callInterface) {
        this.callInterface = callInterface;
    }

    public DoctorItemAdapter(Context context, int layoutResId, ArrayList<DoctorInfoCheck> data) {
        super(layoutResId, data);
        mContext = context;
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
            helper.setVisible(R.id.iv_label, true);
            helper.setAlpha(R.id.btn_getNum, (float) 0.5);
        } else {
            helper.setAlpha(R.id.btn_getNum, (float) 1.0);
        }

        helper.setOnClickListener(R.id.btn_getNum, v -> callInterface.getNum(item.getDoctorId(), item.isFulled()));

        String name = item.getRealName();
        if (name.length() > 4) {
            name = item.getRealName().substring(0, 4) + "...";
        }
        helper.setText(R.id.tv_doctor_name, name);
        if ("1".equals(item.getSex())) {
            helper.setImageResource(R.id.iv_doctor, R.drawable.ic_head_man);

        } else {
            helper.setImageResource(R.id.iv_doctor, R.drawable.ic_head_women);
        }

        helper.setText(R.id.tv_list_num, item.getWaitNum() + "人排队中...");
    }
}
