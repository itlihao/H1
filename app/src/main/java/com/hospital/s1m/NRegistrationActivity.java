package com.hospital.s1m;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.android.inputmethod.pinyin.SoftKey;
import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponentCallback;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hospital.s1m.adapter.DoctorListAdapter;
import com.hospital.s1m.adapter.PatientItemListAdapter;
import com.hospital.s1m.lib_base.base.BaseActivity;
import com.hospital.s1m.lib_base.constants.Components;
import com.hospital.s1m.lib_base.constants.Formatter;
import com.hospital.s1m.lib_base.contract.MainContract;
import com.hospital.s1m.lib_base.dao.PatientDao;
import com.hospital.s1m.lib_base.data.CacheDataSource;
import com.hospital.s1m.lib_base.data.SPDataSource;
import com.hospital.s1m.lib_base.entity.DoctorInfo;
import com.hospital.s1m.lib_base.entity.DoctorInfoCheck;
import com.hospital.s1m.lib_base.entity.LinearItemDecoration;
import com.hospital.s1m.lib_base.entity.Patient;
import com.hospital.s1m.lib_base.entity.PatientAndRegistrationParmar;
import com.hospital.s1m.lib_base.entity.RegistrCall;
import com.hospital.s1m.lib_base.model.RegistrationModel;
import com.hospital.s1m.lib_base.presenter.RegistrationPresenter;
import com.hospital.s1m.lib_base.utils.LiveDataBus;
import com.hospital.s1m.lib_base.utils.Logger;
import com.hospital.s1m.lib_base.utils.PhoneNumUtils;
import com.hospital.s1m.lib_base.utils.PinyinUtils;
import com.hospital.s1m.lib_base.utils.ToastUtils;
import com.hospital.s1m.lib_base.utils.UUID;
import com.hospital.s1m.lib_base.utils.Utils;
import com.hospital.s1m.lib_base.view.CustomPopWindow;
import com.hospital.s1m.lib_base.view.MDDialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

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
    private RadioGroup new_rgroup;

    private Button mRegistration;

    private Button mCancel;

    private RecyclerView doctorView;

    private SwipeRefreshLayout mSwiperefreshlayout;

    private ArrayList<DoctorInfoCheck> mDoctorList;

    private SoftKeyContainer softKeyContainer;

    private PinYinManager manager;

    private MHandler mhandler = new MHandler();

    private boolean mShowComposingView;

    private PinYinManager.DecodingInfo mDecode;

    private PinYinManager.ImeState mImeState;

    private RegistrationPresenter mPresenter;

    private String sysUserId;
    private String doctorName = "";

    private DoctorListAdapter doctorAdapter;
    private MDDialog mDialog;

    private PatientDao myDao;
    private CustomPopWindow mCustomPopWindow;
    private View contentView;
    private boolean isChoose = false;
    private boolean isRegistration = false;
    private PatientItemListAdapter adapter;
    private RecyclerView patientList;

    @SuppressLint("HandlerLeak")
    private class MHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    softKeyContainer.showCandidateWindow(mShowComposingView, mDecode, mImeState);
                    break;
                case 2:
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
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
        myDao = new PatientDao(this);
        mPresenter = new RegistrationPresenter(NRegistrationActivity.this, new RegistrationModel(), this);
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 0);
        String ver = myDao.getMaxVersion(CacheDataSource.getClinicId());
        if (TextUtils.isEmpty(ver)) {
            ver = "1111111111111";
        }
        mPresenter.getPatient(ver);

        if (manager == null) {
            manager = PinYinManager.getInstance();
        }
        manager.addObserver(this);
        mDoctorList = new ArrayList<>();

        onListener();
        initData();
        initAdapter();

        LiveDataBus.get().with("jpushMessage", String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String info) {
                        Logger.d("BaseActivity", "[JPushReceiver] 接收到推送下来的: " + info);
                        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
                    }
                });
        new ThreadGetPatient().start();
    }


    @Override
    protected int setContentView() {
        return R.layout.activity_nregistration;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        new_rgroup = findViewById(R.id.new_rgroup);

        mRegistration = findViewById(R.id.btn_registration);
        mRegistration.setOnClickListener(this);

        mCancel = findViewById(R.id.btn_cancel);
        mCancel.setOnClickListener(this);

        mSwiperefreshlayout = findViewById(R.id.swiperefreshlayout);
        mSwiperefreshlayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwiperefreshlayout.setOnRefreshListener(this::refresh);

        softKeyContainer = findViewById(R.id.skContainer);
        SoftKeyNotifier softKeyNotifier = new SoftKeyNotifier();
        softKeyContainer.initialize(this, softKeyNotifier);

        contentView = LayoutInflater.from(this).inflate(R.layout.queue_dialog_patientt, null);
        patientList = contentView.findViewById(R.id.rv_patient_list);
        patientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mEtName.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                manager.setCurrentEdit(mEtName, 16);
                softKeyContainer.setKeyBoard(1);
                softKeyContainer.showKeyboard();
            }
            return false;
        });

        mEtName.addTextChangedListener(new TextWatcher() {
            //记录输入的字数
            private CharSequence wordNum;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //实时记录输入的字数
                wordNum = s;
                mEtName.setSelection(wordNum.length());
                if (CacheDataSource.getAllPatient() != null && CacheDataSource.getAllPatient().size() < 1) {
                    new ThreadGetPatient().start();
                }

                if (wordNum.length() < 1) {
                    if (mCustomPopWindow != null) {
                        mCustomPopWindow.dissmiss();
                        mCustomPopWindow = null;
                        isChoose = false;
                    }
                    new_rgroup.clearCheck();
//                    new_rgtime.setText("");
//                    new_rgtel.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = mEtName.getSelectionStart();
                selectionEnd = mEtName.getSelectionEnd();
                mEtName.setSelection(wordNum.length());
                if (wordNum.length() > 8) {
                    //删除多余输入的字（不会显示出来）
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;

                    mEtName.setText(s);
                    //设置光标在最后
                    mEtName.setSelection(tempSelection);
                }

                if (wordNum.length() > 0 && !isChoose) {
                    ArrayList<Patient> fileterList = (ArrayList<Patient>) search(wordNum);
                    if (fileterList.size() > 0) {
                        //处理popWindow 显示内容
                        handleLogic(NRegistrationActivity.this, fileterList);
                        //创建并显示popWindow
                        if (mCustomPopWindow == null) {
                            mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(NRegistrationActivity.this)
                                    .setView(contentView)
                                    .create()
                                    .showAsDropDown(mEtName);
                        } else {
                            mCustomPopWindow.showAsDropDown(mEtName);
                        }
                        softKeyContainer.bringToFront();
                    } else {
                        if (mCustomPopWindow != null) {
                            mCustomPopWindow.dissmiss();
                            mCustomPopWindow = null;
                            isChoose = false;
                        }
                    }
                }
            }
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
        mDoctorList = DoctorInfoCheck.transformation(CacheDataSource.getClinicDoctorInfo());
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
            doctorName = infoCheck.getRealName();
            setCheck(adapter, position);
        });
        doctorView.setAdapter(doctorAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        disableShowSoftInput();

        /*if (mPresenter != null) {
            mPresenter.getDoctorInfo(CacheDataSource.getClinicId());
        }*/
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

    private class ThreadGetPatient extends Thread {
        @Override
        public void run() {
            CacheDataSource.setAllPatient(myDao.getDeviceByClinicId(CacheDataSource.getClinicId()));
        }
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     */
    private void handleLogic(final Context context, ArrayList<Patient> list) {
        @SuppressLint("DefaultLocale") BaseQuickAdapter.OnItemClickListener listener = (adapter, view, position) -> {

            isChoose = true;
            Patient patient = (Patient) adapter.getItem(position);
            mEtName.setText(patient.getUserName());
            if (patient.getSex() == 1) {
                new_rgroup.check(R.id.new_rgboy);
            } else {
                new_rgroup.check(R.id.new_rggirl);
            }
            String birthday = patient.getBirthday().replaceAll("–", "-");
            String strs[] = birthday.trim().split("-");
            int selectYear = Integer.parseInt(strs[0]);
            int selectMonth = Integer.parseInt(strs[1]);
            int selectDay = Integer.parseInt(strs[2]);
            mEtYear.setText(String.format("%d", selectYear));
            mEtMonth.setText(String.format("%02d", selectMonth));
            mEtDay.setText(String.format("%02d", selectDay));
            mEtPhone.setText(patient.getPhone());

            if (mCustomPopWindow != null) {
                mCustomPopWindow.dissmiss();
                mCustomPopWindow = null;
                isChoose = false;
            }
        };

        if (adapter == null) {
            adapter = new PatientItemListAdapter(context, list);
            patientList.setAdapter(adapter);
            adapter.setOnItemClickListener(listener);
        } else {
            adapter.setNewData(list);
        }
    }

    /**
     * 模糊查询
     */
    private List<Patient> search(CharSequence word) {
        String str = word.toString();
        List<Patient> filterList = new ArrayList<>(); // 过滤后的list
        if (CacheDataSource.getAllPatient() != null) {
            // 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            if (str.matches("^([0-9]|[/+]).*")) {
                String simpleStr = str.replaceAll("\\-|\\s", "");

                for (Patient patient : CacheDataSource.getAllPatient()) {
                    if (patient.getPhone() != null && patient.getUserName() != null) {
                        if (patient.simpleNumber.contains(simpleStr) || patient.getUserName().contains(str)) {
                            if (!filterList.contains(patient)) {
                                filterList.add(patient);
                            }
                        }
                    }
                }
            } else {
                for (Patient patient : CacheDataSource.getAllPatient()) {
                    if (patient.getUserName() != null) {
                        //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                        if (patient.getUserName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                                || patient.conPinyin.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                                || patient.conPinyinHdr.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))) {
                            if (!filterList.contains(patient)) {
                                filterList.add(patient);
                            }
                        }
                    }
                }
            }
        }

        return filterList;
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
            if (preCheck == position) {
                adapter1.setPreCheck(-1);
                adapter.notifyDataSetChanged();
                sysUserId = "";
                doctorName = "";
                return;
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
                PatientAndRegistrationParmar info = new PatientAndRegistrationParmar();

                Editable text = mEtName.getText();
                info.setUserName(text.toString());
                info.setUserShortName(PinyinUtils.getPinyinjp(text.toString()));
                int checkedRadioButtonId = new_rgroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.new_rgboy) {
                    info.setSex(1);
                } else if (checkedRadioButtonId == R.id.new_rggirl) {
                    info.setSex(2);
                } else {
                    info.setSex(0);
                }
                Editable year = mEtYear.getText();
                Editable month = mEtMonth.getText();
                Editable day = mEtDay.getText();
                info.setYear(year.toString());
                info.setMonth(month.toString());
                info.setDay(day.toString());


                Editable phone = mEtPhone.getText();
                info.setPhone(phone.toString());
                info.setHomeAddress("");
                info.setId(UUID.getUUID());
                info.setIdCardNo("");
                info.setDoctorName(doctorName);
                if (!checkValues(info)) {
                    return;
                }
                info.setSysUserIdRegi(sysUserId);

                if (!Utils.isFastClick() || isRegistration) {
                    isRegistration = true;
                    mPresenter.saveAndRegistration(info);
                }
                break;
            case R.id.btn_cancel:
                clearText();
                break;
            default:
                break;
        }
    }

    /**
     * 点击完成 判断患者信息是否合格
     */
    @SuppressLint("DefaultLocale")
    private boolean checkValues(PatientAndRegistrationParmar info) {
        if (!PinyinUtils.isPatientName(info.getUserName())) {
            ToastUtils.showToast(this, "请输入正确的患者姓名");
            return false;
        }
        if (info.getSex() == 0) {
            ToastUtils.showToast(this, "请选择性别");
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        if (TextUtils.isEmpty(info.getYear())) {
            ToastUtils.showToast(this, "年份不能为空");
            return false;
        }

        if (TextUtils.isEmpty(info.getMonth())) {
            ToastUtils.showToast(this, "月份不能为空");
            return false;
        }

        if (TextUtils.isEmpty(info.getDay())) {
            ToastUtils.showToast(this, "日期不能为空");
            return false;
        }

        if (!pattern.matcher(info.getYear()).matches() || Integer.parseInt(info.getYear()) > year) {
            ToastUtils.showToast(this, "请输入正确的年份");
            return false;
        }

        int month = Integer.parseInt(info.getMonth());
        if (!pattern.matcher(info.getMonth()).matches() || month > 12) {
            ToastUtils.showToast(this, "请输入正确的月份");
            return false;
        }

        int day = Integer.parseInt(info.getDay());
        if (!pattern.matcher(info.getDay()).matches() || day > 31) {
            ToastUtils.showToast(this, "请输入正确的日期");
            return false;
        }

        info.setBirthday(info.getYear() + "-" + String.format("%02d", month) + "-" + String.format("%02d", day));

        if (!info.getPhone().isEmpty()) {
            if (info.getPhone().length() != 0 && (info.getPhone().length() != 11 || !PhoneNumUtils.isPhoneNumber(info.getPhone()))) {
                ToastUtils.showToast(this, "请输入11位正确的手机号码");
                return false;
            }
        }

        if (TextUtils.isEmpty(info.getDoctorName())) {
            ToastUtils.showToast(this, "请选择挂号医生");
            return false;
        }

        return true;
    }

    private void clearText() {
        mEtName.setText("");
        mEtYear.setText("");
        mEtMonth.setText("");
        mEtDay.setText("");
        mEtPhone.setText("");
        new_rgroup.clearCheck();
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

    public void refresh() {
        mDoctorList.clear();
        doctorAdapter.getData().clear();
        doctorAdapter.notifyDataSetChanged();
        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onQuickRegistration(RegistrCall quickRegistr) {
        clearText();

        mPresenter.getDoctorInfo(CacheDataSource.getClinicId(), 1);
        sysUserId = "";
        isRegistration = false;

        boolean print = (boolean) SPDataSource.get(this, "needPrint", true);
        if (print) {
            showLoading("挂号成功，正在为您打印挂号单");
            List<RegistrCall.TimeTableBean> timeTable = quickRegistr.getTimeTable();
            String s1 = "", s2 = "", s3 = "";
            for (int i = 0; i < timeTable.size(); i++) {
                if (timeTable.get(i).getPeriodType() == 1) {
                    s1 = "上午 " + timeTable.get(i).getStartTime() + "-" + timeTable.get(i).getEndTime();
                } else if (timeTable.get(i).getPeriodType() == 2) {
                    s2 = "下午 " + timeTable.get(i).getStartTime() + "-" + timeTable.get(i).getEndTime();
                } else if (timeTable.get(i).getPeriodType() == 3) {
                    s3 = "晚上 " + timeTable.get(i).getStartTime() + "-" + timeTable.get(i).getEndTime();
                }
            }
            CC.obtainBuilder(Components.ComponentPrint)
                    .setActionName(Components.ComponentPrintNumber)
                    .addParam("number", quickRegistr.getRegistrationNo())
                    .addParam("doctorName", doctorName)
                    .addParam("registerDate", Formatter.DATE_FORMAT4.format(new Date()))
                    .addParam("registrationId", quickRegistr.getRegistrationId())
                    .addParam("sysUserId", quickRegistr.getSysUserId())
                    .addParam("periodType", quickRegistr.getPeriodType())
                    .addParam("registerType", 2)
                    .addParam("timeS1", s1)
                    .addParam("timeS2", s2)
                    .addParam("timeS3", s3)
                    .build()
                    .callAsyncCallbackOnMainThread((cc, result) -> {
                        if (result.isSuccess()) {
                            hideLoading();
                        }
                    });
        } else {
            mDialog = new MDDialog.Builder(this)
                    .setShowTitle(false)
                    .setShowMessage(true)
                    .setMessages(Objects.requireNonNull(this).getString(com.hospital.s1m.lib_user.R.string.sr_registration_suc))
                    .setShowAvi(false)
                    .setshowRNo(true)
                    .setRNo(String.format("%03d", quickRegistr.getRegistrationNo()) + "号")
                    .setWidthMaxDp(440)
                    .setShowNegativeButton(false)
                    .setShowPositiveButton(false)
                    .setCancelable(false)
                    .create();
            mDialog.show();
            mhandler.sendEmptyMessageDelayed(2, 1500);
        }
        doctorName = "";
    }

    @Override
    public void onDoctorResult(ArrayList<DoctorInfo> result) {
        mSwiperefreshlayout.setRefreshing(false);
        hideLoading();
        mDoctorList = DoctorInfoCheck.transformationNoCheck(CacheDataSource.getClinicDoctorInfo());
        doctorAdapter.setNewData(mDoctorList);
        doctorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPatientResult(List<Patient> result) {
        for (int i = 0; i < result.size(); i++) {

            myDao.addPatient(result.get(i).getUserName(), result.get(i).getBirthday(), result.get(i).getSex(), result.get(i).getPhone(),
                    result.get(i).getBaseVersion(), CacheDataSource.getClinicId(), result.get(i).getId());

            if (i == result.size() && result.size() <= 10000) {
                mPresenter.getPatient(myDao.getMaxVersion(CacheDataSource.getClinicId()));
            }
        }
    }

    @Override
    public void onPeriodFullResult(String result, String type) {

    }

    @Override
    public void onFailed() {
        sysUserId = "";
        isRegistration = false;
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
