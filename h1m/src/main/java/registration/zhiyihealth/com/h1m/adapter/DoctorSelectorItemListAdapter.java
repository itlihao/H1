package registration.zhiyihealth.com.h1m.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfoCheck;

import java.util.ArrayList;

import registration.zhiyihealth.com.h1m.R;

public class DoctorSelectorItemListAdapter extends BaseQuickAdapter<DoctorInfoCheck, BaseViewHolder> {

    private int preCheck = -1;

    public int getPreCheck() {
        return preCheck;
    }

    public void setPreCheck(int preCheck) {
        this.preCheck = preCheck;
    }

    public DoctorSelectorItemListAdapter(Context context, ArrayList<DoctorInfoCheck> data) {
        super(R.layout.queue_doctor_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorInfoCheck item) {
        if ("1".equals(item.getSex())) {
            helper.setImageResource(R.id.iv_doctor, R.drawable.iv_doctor_man);
        } else {
            helper.setImageResource(R.id.iv_doctor, R.drawable.iv_doctor_women);
        }

        helper.setChecked(R.id.new_rgboy, item.isCheck());

        helper.setText(R.id.tv_doctor_name, item.getRealName());

        helper.setText(R.id.tv_number, item.getWaitNum() + "人排队中...");
    }

}
