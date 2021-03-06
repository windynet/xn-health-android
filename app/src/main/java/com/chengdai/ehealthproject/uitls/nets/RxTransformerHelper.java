package com.chengdai.ehealthproject.uitls.nets;

import android.content.Context;
import android.util.Log;

import com.chengdai.ehealthproject.base.BaseActivity;
import com.chengdai.ehealthproject.model.api.BaseResponseModel;
import com.chengdai.ehealthproject.uitls.LogUtil;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;


/**
 * Retrofit帮助类
 */
public class RxTransformerHelper {

    /*0=成功；1=权限错误；2=参数错误；3=业务错误；9=未知错误*/

    public static final String REQUESTOK = "0";   //请求后台成功

    public static final String REQUESTFECODE3= "3";

    public static final String REQUESTFECODE4 = "4";//重新登录

    public static final String REQUESTFECODE9 = "9";

    public static final String NET_ERROR = "-1";


    /**
     * 简单的请求业务错误过滤器（弹出toast）
     */
    private static Predicate<BaseResponseModel> verifySimpleBusiness(Context context) {
        return verifyBusiness(new SimpleErrorVerify(context));
    }

    /**
     * 业务错误过滤器（自定义）
     */
    private static <T> Predicate<T> verifyBusiness(ErrorVerify errorVerify) {
        return response -> {
            if (response instanceof BaseResponseModel) {
                BaseResponseModel baseResponse = (BaseResponseModel) response;
                String responseCode = baseResponse.getErrorCode();
                boolean isSuccess = REQUESTOK.equals(responseCode);
                if (!isSuccess) {
                    if (errorVerify != null) {
                        errorVerify.call(baseResponse.getErrorCode(), baseResponse.getErrorInfo());
                    }
                }
                LogUtil.E("网络请求"+isSuccess+responseCode);
                return isSuccess;
            } else {
                return false;
            }
        };
    }

    /**
     * 非空过滤器（自定义）
     */
    private static <T> Predicate<T> verifyNotEmpty() {
        return response -> response != null;
    }

    /**
     * 错误提醒处理
     * @param context
     * @param errorVerify
     * @param <T>
     * @return
     */
    private static  <T> Function<Throwable,T> doError(Context context,ErrorVerify errorVerify) {
        return throwable -> {
            throwable.printStackTrace();
            errorVerify.call("0",throwable.toString());
            if (errorVerify != null) {
                if(!NetUtils.isNetworkConnected())
                {
                    errorVerify.call(NET_ERROR, "暂无网络");

                }else {
                    if(LogUtil.isLog){
                        LogUtil.E("请求错误"+throwable.toString());
                        errorVerify.call("0",throwable.toString());
                    }else{
                        errorVerify.call("0", "未知错误");
                    }
                }
            }
            return null;
        };
    }

    /**
     * sessionId 过期的过滤器
     */
    private static <T> Predicate<T> verifyResultCode(Context context) {
        return response -> {
            if (response instanceof BaseResponseModel) {
                BaseResponseModel baseResponse = (BaseResponseModel) response;
                String state = baseResponse.getErrorCode();
                if (REQUESTFECODE4.equals(state)) {
                    OnOkFailure.StartDoFailure(context, baseResponse.getErrorInfo());  //请求成功 但是服务器状态错误  如 被迫下线
                    return false;
                }
                return true;
            } else {
                return false;
            }
        };
    }

    /**
     * 优先使用这个，可以继续使用操作符
     */
    private static <T> ObservableTransformer<T, T> applySchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 聚合了session过滤器,业务过滤器及合并操作 自定义错误回调
     */
    public static <T> ObservableTransformer<T, T>
    applySchedulersAndAllFilter(Context context, ErrorVerify errorVerify) {
        return observable -> observable
                .compose(applySchedulers())
                .doOnSubscribe(disposable -> {
                    if (context instanceof BaseActivity)
                        ((BaseActivity) context).showLoadingDialog();
                })
                .subscribeOn(AndroidSchedulers.mainThread()) //指定 doOnSubscribe工作线程
                .filter(verifyNotEmpty())
                .filter(verifyResultCode(context))
                .filter(verifyBusiness(errorVerify))
                .onErrorReturn(doError(context,errorVerify))
                .doFinally(() -> {
                    if (context instanceof BaseActivity)
                        ((BaseActivity) context).disMissLoading();
                });
    }

    /**
     * 聚合了session过滤器,简单业务过滤器及合并操作及自定义的错误返回
     */
    public static <T> ObservableTransformer<BaseResponseModel<T>, T>
    applySchedulersResult(Context context, ErrorVerify errorVerify) {
        return observable -> observable
                .compose(applySchedulersAndAllFilter(context, errorVerify))
                .map(t -> t.getData());
    }

    /**
     * 聚合了session过滤器,简单业务过滤器及合并操作
     * 结果直接返回包含BaseModelNew<T>里定义的 T 对象
     */
    public static <T> ObservableTransformer<BaseResponseModel<T>, T> applySchedulerResult(Context context) {
        return applySchedulersResult(context, new SimpleErrorVerify(context));
    }

    /**
     * 聚合了session过滤器,简单业务过滤器及合并操作
     * 结果返回包含BaseModelNew的对象
     */
    public static <T> ObservableTransformer<T, T> applySchedulerAndAllFilter(Context context) {

        return applySchedulersAndAllFilter(context, new SimpleErrorVerify(context));
    }
}

