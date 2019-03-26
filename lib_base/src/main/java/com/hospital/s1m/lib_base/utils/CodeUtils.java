package com.hospital.s1m.lib_base.utils;

public class CodeUtils {
    public static String setCode(String code, String info) {
        switch (code) {
            case "0":
                return "网络错误请检查网络";
            //通用
            case "2000000":
                return "操作成功";
            case "1111111":
                return "系统异常,请联系客服400-666-9196";
            case "2222222":
                return "必填项为空,请检查";
            case "3333333":
                return "数据格式有误,请检查";
            case "4444444":
                return "网络繁忙,当前用户较多,请重试。";
            //患者
            case "2040101":
                return "该患者已存在。";
            case "2040102":
                return "该患者不存在。";
            case "2040103":
                return "患者添加失败。";
            case "2040104":
                return "该患者信息修改失败。";
            case "2040105":
                return "该患者删除失败。";
            case "2040106":
                return "患者同步失败，请登录重试。";
            case "2040107":
                return "该患者下有处方或订单，不可被删除";
            //用户相关
            case "2010101":
                return "该账号已存在";
            case "2010102":
                return "该手机号已注册";
            case "2010103":
                return "该邮箱已存在";
            case "2010104":
                return "该用户ID不存在";
            case "2010105":
                return "该账号不存在";
            case "2010106":
                return "该手机号不存在";
            case "2010107":
                return "该邮箱不存在";
            case "2010108":
                return "用户名或密码错误";
             case "2010109":
                 return "用户token无效";
            case "2010110":
                return "用户状态无效,联系客服400-666-9196";
            case "2010111":
                return "验证码失效或错误";
            case "2010112":
                return "该账号仅支持加密锁登录";
            case "2010113":
                return "原密码错误";
            case "2010114":
                return "账号详情不存在";
            case "2010117":
                return "该账号权限被修改,请重新登录";
            case "2010115":
                return "验证码获取失败";
            case "2020119":
                return "该医生已停诊，暂停挂号";
            case "2010301":
                return "诊所不存在";
            case "2010302":
                return "诊所雇员不存在！";
            case "2010303":
                return "诊所雇员初始化排班信息不存在！";
            case "2010304":
                return "用户与诊所不匹配！";
            case "2010305":
                return "诊所状态无效！";
            case "2010306":
                return "诊所信息没有完善需要在一体机或者PC端进行完善！";
            case "2010307":
                return "诊所特色已存在！";
            case "2010308":
                return "诊所特色添加受限！";
            case "2010309":
                return "该医生存在预约或挂号的患者，不可取消看病开方权限";
            case "2040108":
                return "该医生存在预约或挂号的患者，不可取消看病开方权限";
            default:
                return info;
        }
    }
}
