package registration.zhiyihealth.com.lib_ime.hcicloud;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.hwr.HciCloudHwr;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.hwr.HwrAssociateWordsResult;
import com.sinovoice.hcicloudsdk.common.hwr.HwrConfig;
import com.sinovoice.hcicloudsdk.common.hwr.HwrInitParam;
import com.sinovoice.hcicloudsdk.common.hwr.HwrRecogResult;
import com.sinovoice.hcicloudsdk.common.hwr.HwrRecogResultItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import registration.zhiyihealth.com.lib_ime.manager.PinYinManager;

public class HciCloudFuncHelper extends HciCloudHelper {
    private static final String TAG = HciCloudFuncHelper.class.getSimpleName();

    //实时每次识别传入笔迹的个数，以-1,0，-1，-1结尾
    static int g_nStrokeLen[] = {28, 60, 94, 148, 198, 250};

    /**
     * 显示结果集合
     *
     * @param recogResult
     */
    private static void showRecogResultResult(HwrRecogResult recogResult) {
        String strResult = "";
        List<String> resultList = null;
        if (recogResult != null) {
            ArrayList<HwrRecogResultItem> recogItemList = recogResult.getResultItemList();
            resultList = new ArrayList<>();
            for (int index = 0; index < recogItemList.size(); index++) {
                String strTmp = recogItemList.get(index).getResult();
                strResult = strResult.concat(strTmp).concat(";");
                resultList.add(strTmp);
            }
        }
        ShowMessage(strResult);

        PinYinManager.getInstance().setCandidatesList(resultList);
    }

    private static void showAssociateResultResult(HwrAssociateWordsResult recogResult) {
        String strResult = "";
        if (recogResult != null) {
            ArrayList<String> recogItemList = recogResult
                    .getResultList();
            for (int index = 0; index < recogItemList.size(); index++) {
                //String strTmp = recogItemList.get(index).getResult();
                strResult = strResult.concat(recogItemList.get(index)).concat(";");
            }
        }
        ShowMessage(strResult);
    }

    /**
     * 开始识别，此方法为非实时识别
     *
     * @param strokes
     */
    private static void recog(String capkey, HwrConfig recogConfig, short[] strokes) {
        int errCode = -1;
        HwrConfig sessionConfig = new HwrConfig();
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
        //sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_RES_PREFIX, "en_");
        Session session = new Session();
        ShowMessage("HciCloudHwr hciHwrSessionStart config " + sessionConfig.getStringConfig());
        // 开始会话
        errCode = HciCloudHwr.hciHwrSessionStart(sessionConfig.getStringConfig(), session);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            ShowMessage("hciHwrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        }
        ShowMessage("hciHwrSessionStart Success");

        Log.i(TAG, "HciCloudHwr HwrConfig: " + recogConfig.getStringConfig());
        // 开始识别
        int bRet = capkey.indexOf("hwr.local.associateword");
        if (bRet != -1) {
            //开始联想
            String associateWords = "中国";
            HwrAssociateWordsResult associateResult = new HwrAssociateWordsResult();
            errCode = HciCloudHwr.hciHwrAssociateWords(session, recogConfig.getStringConfig(), associateWords,
                    associateResult);
            if (HciErrorCode.HCI_ERR_NONE == errCode) {
                ShowMessage("hciHwrAssociateWords Success");
                showAssociateResultResult(associateResult);
            } else {
                ShowMessage("hciHwrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));
            }

            //联想词动态调整
            /*String adjustWords = "人民日报社";
            errCode = HciCloudHwr.hciHwrAssociateWordsAdjust(session, recogConfig.getStringConfig(), 
            		adjustWords);
            
            if (HciErrorCode.HCI_ERR_NONE == errCode) {
            	ShowMessage("hciHwrAssociateWordsAdjust Success");
            }
            else{
            	ShowMessage("hciHwrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));	
            }*/

            //再联想，查看调整结果
            /*HwrAssociateWordsResult associateResult2 = new HwrAssociateWordsResult();
            errCode = HciCloudHwr.hciHwrAssociateWords(session, recogConfig.getStringConfig(), associateWords,
            		associateResult2);
            if (HciErrorCode.HCI_ERR_NONE == errCode) {
            	ShowMessage("hciHwrAssociateWords Success");
            	showAssociateResultResult(associateResult2);
            }
            else{
            	ShowMessage("hciHwrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));	
            }*/

        } else {
            HwrRecogResult recogResult = new HwrRecogResult();
            errCode = HciCloudHwr.hciHwrRecog(session, strokes, recogConfig.getStringConfig(),
                    recogResult);
            if (HciErrorCode.HCI_ERR_NONE == errCode) {
                ShowMessage("hciHwrRecog Success");
                showRecogResultResult(recogResult);
            } else {
                ShowMessage("hciHwrRecog error:" + HciCloudSys.hciGetErrorInfo(errCode));
            }
        }


        // 停止会话
        HciCloudHwr.hciHwrSessionStop(session);
        ShowMessage(")hciHwrSessionStop");
    }

    // 本地实时识别
    public static void realtimeRecog(String capkey, HwrConfig recogConfig, short[] strokes) {

        HwrConfig sessionConfig = new HwrConfig();
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_REALTIME, "yes");
        ShowMessage("HciCloudHwr hciHwrSessionStart config " + sessionConfig.getStringConfig());

        Session nSessionId = new Session();
        int errCode = -1;
        // 开始会话
        errCode = HciCloudHwr.hciHwrSessionStart(sessionConfig.getStringConfig(), nSessionId);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            System.out.println("HciCloudHwr hciHwrSessionStart return " + errCode);
            return;
        }


        for (int nIndex = 0; nIndex < g_nStrokeLen.length; nIndex++) {
            int nCount = g_nStrokeLen[nIndex];
            short sTempShortData[] = new short[nCount];
            System.arraycopy(strokes, 0, sTempShortData, 0, nCount);
            sTempShortData[nCount - 2] = -1;
            sTempShortData[nCount - 1] = -1;

            HwrRecogResult recogResult = new HwrRecogResult();
            // 开始识别
            errCode = HciCloudHwr.hciHwrRecog(nSessionId, sTempShortData,
                    recogConfig.getStringConfig(), recogResult);
            if (HciErrorCode.HCI_ERR_NONE != errCode) {
                System.out.println("HciCloudHwr hciHwrRecog return " + errCode);
                return;
            }
            System.out.println("HciCloudHwr hciHwrRecog Success");
            // 结果输出
            showRecogResultResult(recogResult);
        }

        // 停止会话
        errCode = HciCloudHwr.hciHwrSessionStop(nSessionId);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            System.out.println("HciCloudHwr hciHwrSessionStop return " + errCode);
            return;
        }
        System.out.println("HciCloudHwr hciHwrSessionStop Success");
    }

    public static void Func(Context context, String capkey, TextView view) {
        setContext(context);
        setTextView(view);

        // HWR 初始化
        HwrInitParam hwrInitParam = new HwrInitParam();

        String dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sinovoice"
                + File.separator + context.getPackageName() + File.separator + "data";
        HciCloudHelper.copyAssetsFiles(context, dataPath);
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_FILE_FLAG, HwrInitParam.VALUE_OF_PARAM_FILE_FLAG_NONE);
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_INIT_CAP_KEYS, AccountInfo
                .getInstance().getCapKey());
        int errCode = HciCloudHwr.hciHwrInit(hwrInitParam.getStringConfig());
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            ShowMessage("hciHwrInit error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        } else {
            ShowMessage("hciHwrInit Success");
        }

        short sShortData[] = {103, 283, 105, 283, 107, 283, 113, 283,
                120, 283, 129, 283, 138, 283, 146, 283, 156, 283, 162, 283, 165,
                283, 166, 283, -1, 0, 282, 245, 277, 247, 270, 251, 266, 255, 263,
                257, 259, 261, 254, 266, 250, 273, 246, 281, 243, 286, 240, 292,
                240, 294, 239, 296, 238, 297, 238, 298, -1, 0, 262, 271, 264, 272,
                266, 272, 268, 272, 270, 273, 272, 274, 275, 274, 278, 276, 280,
                278, 283, 279, 286, 281, 289, 282, 289, 283, 291, 284, 292, 285,
                292, 286, -1, 0, 268, 281, 268, 282, 268, 284, 270, 287, 270, 290,
                270, 294, 270, 297, 270, 299, 270, 301, 270, 303, 270, 304, 270,
                306, 270, 308, 269, 309, 269, 310, 269, 311, 269, 312, 269, 314,
                269, 316, 269, 318, 269, 319, 269, 321, 269, 322, 269, 323, 269,
                324, 268, 324, -1, 0, 382, 255, 382, 256, 382, 260, 382, 263, 381,
                267, 378, 274, 375, 278, 373, 282, 372, 287, 371, 291, 369, 294,
                368, 297, 367, 300, 367, 301, 366, 302, 365, 304, 364, 305, 364,
                306, 363, 308, 362, 308, 362, 309, 361, 310, 361, 311, 360, 311,
                -1, 0, 376, 289, 377, 290, 378, 290, 380, 291, 381, 292, 382, 293,
                384, 294, 385, 294, 387, 297, 388, 298, 390, 299, 393, 300, 394,
                301, 396, 302, 398, 303, 400, 305, 401, 306, 403, 307, 404, 309,
                405, 309, 407, 311, 408, 312, 409, 314, 410, 314, 411, 314, -1, 0,
                -1, -1};


        HwrConfig recogConfig = new HwrConfig();
        //非实时识别
        recog(capkey, recogConfig, sShortData);

        //实时识别
        //realtimeRecog(capkey,recogConfig,sShortData);

        //HWR反初始化
        HciCloudHwr.hciHwrRelease();
        ShowMessage("hciHwrRelease");
    }

    public static void Funcs(Context context, short sShortData[]) {
        setContext(context);

        // HWR 初始化
        HwrInitParam hwrInitParam = new HwrInitParam();
        String capkey = AccountInfo.getInstance().getCapKey();

        String dataPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sinovoice"
                + File.separator + context.getPackageName() + File.separator + "data";
        HciCloudHelper.copyAssetsFiles(context, dataPath);
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_FILE_FLAG, HwrInitParam.VALUE_OF_PARAM_FILE_FLAG_NONE);
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_INIT_CAP_KEYS, capkey);
        int errCode = HciCloudHwr.hciHwrInit(hwrInitParam.getStringConfig());
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            ShowMessage("hciHwrInit error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        } else {
            ShowMessage("hciHwrInit Success");
        }


        HwrConfig recogConfig = new HwrConfig();
        //非实时识别

        recog(capkey, recogConfig, sShortData);

        //实时识别
        //realtimeRecog(capkey,recogConfig,sShortData);

        //HWR反初始化
        HciCloudHwr.hciHwrRelease();
        ShowMessage("hciHwrRelease");
    }
}
