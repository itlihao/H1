package com.zhiyihealth.registration;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.inputmethod.pinyin.SoftKey;
import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhiyihealth.registration.adapter.DoctorListAdapter;
import com.zhiyihealth.registration.lib_base.base.BaseActivity;
import com.zhiyihealth.registration.lib_base.constants.Components;
import com.zhiyihealth.registration.lib_base.constants.Formatter;
import com.zhiyihealth.registration.lib_base.contract.MainContract;
import com.zhiyihealth.registration.lib_base.data.CacheDataSource;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfo;
import com.zhiyihealth.registration.lib_base.entity.DoctorInfoCheck;
import com.zhiyihealth.registration.lib_base.entity.LinearItemDecoration;
import com.zhiyihealth.registration.lib_base.entity.QuickRegistr;
import com.zhiyihealth.registration.lib_base.model.RegistrationModel;
import com.zhiyihealth.registration.lib_base.presenter.RegistrationPresenter;
import com.zhiyihealth.registration.lib_base.utils.LiveDataBus;
import com.zhiyihealth.registration.lib_base.utils.ToastUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import registration.zhiyihealth.com.lib_ime.listener.PinYinConnector;
import registration.zhiyihealth.com.lib_ime.listener.SoftKeyListener;
import registration.zhiyihealth.com.lib_ime.manager.PinYinManager;
import registration.zhiyihealth.com.lib_ime.view.SoftKeyContainer;

import static registration.zhiyihealth.com.lib_ime.view.SoftKeyContainer.KEYCODE_SWITCH;

/**
 * @author Lihao
 */
public class NRegistrationActivity extends BaseActivity implements View.OnClickListener, PinYinConnector, MainContract.RegistrationView {
    private FrameLayout mDwMenu;
    private DrawerLayout mDrawerLayout;

    private EditText mEtName;
    private EditText mEtYear;
    private EditText mEtMonth;
    private EditText mEtDay;
    private EditText mEtPhone;

    private Button mRegistration;

    private RecyclerView doctorView;

    private ArrayList<DoctorInfoCheck> mDoctorList;

    private SoftKeyContainer softKeyContainer;

    private PinYinManager manager;

    private MHandler mhandler = new MHandler();

    private boolean mShowComposingView;

    private PinYinManager.DecodingInfo mDecode;

    private PinYinManager.ImeState mImeState;

    private RegistrationPresenter mPresenter;

    private String sysUserId;

    private DoctorListAdapter doctorAdapter;

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    softKeyContainer.showCandidateWindow(mShowComposingView, mDecode, mImeState);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        mPresenter = new RegistrationPresenter(NRegistrationActivity.this, new RegistrationModel(), this);
        if (manager == null) {
            manager = PinYinManager.getInstance();
        }
        manager.addObserver(this);
        mDoctorList = new ArrayList<>();
        LiveDataBus.get().with("key_test", DoctorInfo.class)
                .observe(this, new Observer<DoctorInfo>() {
                    @Override
                    public void onChanged(@Nullable DoctorInfo info) {
                    }
                });

        onListener();
        initData();
        initAdapter();
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_nregistration;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        ImageView mIvMenu = findViewById(R.id.iv_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDwMenu = findViewById(R.id.dw_fragment);
        doctorView = findViewById(R.id.rv_doctor_item);

        mEtName = findViewById(R.id.et_patient_name);
        mEtYear = findViewById(R.id.et_year);
        mEtMonth = findViewById(R.id.et_month);
        mEtDay = findViewById(R.id.et_day);
        mEtPhone = findViewById(R.id.et_patient_phone);

        mRegistration = findViewById(R.id.btn_registration);
        mRegistration.setOnClickListener(this);

        softKeyContainer = findViewById(R.id.skContainer);
        SoftKeyNotifier softKeyNotifier = new SoftKeyNotifier();
        softKeyContainer.initialize(this, softKeyNotifier);
        mEtName.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                manager.setCurrentEdit(mEtName, 16);
                softKeyContainer.setKeyBoard(1);
                softKeyContainer.showKeyboard();
            }
            return false;
        });

        mEtYear.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                manager.setCurrentEdit(mEtYear, 4);
                softKeyContainer.setKeyBoard(2);
                softKeyContainer.showKeyboard();
            }
            return false;
        });

        mEtMonth.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                manager.setCurrentEdit(mEtMonth, 2);
                softKeyContainer.setKeyBoard(2);
                softKeyContainer.showKeyboard();
            }
            return false;
        });

        mEtDay.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                manager.setCurrentEdit(mEtDay, 2);
                softKeyContainer.setKeyBoard(2);
                softKeyContainer.showKeyboard();
            }
            return false;
        });

        mEtPhone.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                manager.setCurrentEdit(mEtPhone, 11);
                softKeyContainer.setKeyBoard(2);
                softKeyContainer.showKeyboard();
            }
            return false;
        });

        mIvMenu.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        doctorView.setLayoutManager(linearLayoutManager);

        doctorView.addItemDecoration(new LinearItemDecoration(0, 2, 0, 0));
    }

    private void initAdapter() {
        doctorAdapter = new DoctorListAdapter(this, mDoctorList);
        doctorAdapter.setOnItemClickListener((adapter, view, position) -> {
            DoctorInfoCheck infoCheck = (DoctorInfoCheck) adapter.getItem(position);
            assert infoCheck != null;
            sysUserId = infoCheck.getDoctorId();
            if (infoCheck.isFulled()) {
                return;
            }
            setCheck(adapter, position);
        });
        doctorView.setAdapter(doctorAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        disableShowSoftInput();

        if (mPresenter != null) {
            mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        }
    }

    /**
     * 禁止Edittext弹出软件盘,光标依然正常显示。
     */
    public void disableShowSoftInput() {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(mEtName, false);
            method.invoke(mEtYear, false);
            method.invoke(mEtMonth, false);
            method.invoke(mEtDay, false);
            method.invoke(mEtPhone, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onListener() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //动态添加fragment进来
                CC.obtainBuilder(Components.COMPONENT_USER)
                        .setActionName(Components.COMPONENT_USER_MENU)
                        .build()
                        .callAsyncCallbackOnMainThread(fragmentdrawer);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void setCheck(BaseQuickAdapter adapter, int position) {
        if (adapter instanceof DoctorListAdapter) {
            DoctorInfoCheck item = (DoctorInfoCheck) adapter.getItem(position);
            DoctorListAdapter adapter1 = (DoctorListAdapter) adapter;
            int preCheck = adapter1.getPreCheck();
            if (preCheck >= 0) {
                DoctorInfoCheck item1 = adapter1.getItem(preCheck);
                Objects.requireNonNull(item1).setCheck(false);
            }
            Objects.requireNonNull(item).setCheck(true);
            adapter1.setPreCheck(position);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                //cc调用lib_user侧滑界面
                mDrawerLayout.openDrawer(mDwMenu);
                CC.obtainBuilder(Components.COMPONENT_USER)
                        .setActionName(Components.COMPONENT_USER_MENU)
                        .build()
                        .callAsyncCallbackOnMainThread(fragmentdrawer);
                break;
            case R.id.btn_registration:
                // TODO 患者信息判断
                mPresenter.quickRegistration(CacheDataSource.getClinicId(), sysUserId);
                break;
            default:
                break;
        }
    }

    IComponentCallback fragmentdrawer = (cc, result) -> {
        if (result.isSuccess()) {
            Fragment fragment = result.getDataItem("Fragment_Jurisdiction");
            if (fragment != null) {
                showdwFragment(fragment);
            }
        }
    };

    private void showdwFragment(Fragment mDwFragment) {
        if (mDwFragment != null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.dw_fragment, mDwFragment);
            trans.commit();
        }
    }

    @Override
    public void onShowCandiateView(PinYinManager.DecodingInfo info, PinYinManager.ImeState imeState, boolean showComposingView) {
        Log.w("DemoActivity", "搜索结果回传: " + info + " ," + imeState + " ," + showComposingView);
        mShowComposingView = showComposingView;
        mDecode = info;
        mImeState = imeState;

        mhandler.sendEmptyMessage(1);
    }

    @Override
    public void onResetState(final PinYinManager.DecodingInfo info, final PinYinManager.ImeState imeState, final boolean showComposingView) {
        softKeyContainer.resetState(showComposingView, info, imeState);
    }

    @Override
    public void onHiddenCompose() {
        softKeyContainer.hiddenComposeView();
    }

    @SuppressLint("HandlerLeak")
    public class SoftKeyNotifier extends Handler implements SoftKeyListener {

        SoftKeyNotifier() {

        }

        @Override
        public void onKeyClick(SoftKey softKey, int msgType, boolean isCN) {
            Log.w("DemoActivity", "softKey: " + softKey.getKeyCode() + ", " + softKey.getKeyLabel());
            int primaryCode = softKey.getKeyCode();
            if (primaryCode == KEYCODE_SWITCH) {
                manager.switchLanguage(isCN);
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                manager.switchUpper(isCN);
            } else if (primaryCode == KeyEvent.KEYCODE_DEL) {
                manager.responseSoftKeyClick(softKey);
            } else {
                if (manager.isCanInput()) {
                    manager.responseSoftKeyClick(softKey);
                }
            }
        }
    }

    @Override
    public void onQuickRegistration(QuickRegistr quickRegistr) {
        showLoading("挂号成功，正在为您打印挂号单");
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        sysUserId = "";
        CC.obtainBuilder(Components.ComponentPrint)
                .setActionName(Components.ComponentPrintNumber)
                .addParam("number", quickRegistr.getRegistrationNo())
                .addParam("doctorName", quickRegistr.getDoctorName())
                .addParam("registerDate", Formatter.DATE_FORMAT4.format(new Date()))
                .addParam("registrationId", quickRegistr.getRegistrationId())
                .addParam("sysUserId", quickRegistr.getSysUserId())
                .addParam("periodType", quickRegistr.getPeriodType())
                .build()
                .callAsyncCallbackOnMainThread((cc, result) -> {
                    if (result.isSuccess()) {
                        ToastUtils.showToast(NRegistrationActivity.this, "打印完毕");
                        hideLoading();
                    }
                });
    }

    @Override
    public void onDoctorResult(ArrayList<DoctorInfo> result) {
        mDoctorList.clear();
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        doctorAdapter.setNewData(mDoctorList);
        doctorAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /*
             * 点击空白位置 隐藏软键盘
             */
            softKeyContainer.hideKeyboard();
        }
        return super.onTouchEvent(event);
    }
}
