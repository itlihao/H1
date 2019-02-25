package com.hospital.s1m.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hospital.s1m.R;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.utils.Utils;

import java.util.ArrayList;

public class PatientItemListAdapter extends BaseQuickAdapter<Patient,BaseViewHolder> {

    public PatientItemListAdapter(Context context, ArrayList<Patient> data) {
        super(R.layout.queue_patient_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Patient item) {
        helper.setText(R.id.tv_patient_name, item.getUserName());
        helper.setText(R.id.tv_patient_sex, item.getSex() == 1 ? "男" : "女");
        helper.setText(R.id.tv_patient_phone, item.getPhone());
        helper.setText(R.id.tv_patient_age, Utils.getAgeFromBirthTime(item.getBirthday()) + "岁");
    }

}
