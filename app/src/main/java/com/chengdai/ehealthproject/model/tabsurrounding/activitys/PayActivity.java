package com.chengdai.ehealthproject.model.tabsurrounding.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

import com.chengdai.ehealthproject.R;
import com.chengdai.ehealthproject.base.AbsBaseActivity;
import com.chengdai.ehealthproject.databinding.ActivityPayBinding;
import com.chengdai.ehealthproject.uitls.ImgUtils;
import com.chengdai.ehealthproject.uitls.LogUtil;
import com.chengdai.ehealthproject.uitls.StringUtils;
import com.chengdai.ehealthproject.uitls.nets.RetrofitUtils;
import com.chengdai.ehealthproject.uitls.nets.RxTransformerHelper;
import com.chengdai.ehealthproject.weigit.appmanager.SPUtilHelpr;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.chengdai.ehealthproject.uitls.StringUtils.doubleFormatMoney;
import static com.chengdai.ehealthproject.uitls.StringUtils.doubleFormatMoney2;

/**
 * Created by 李先俊 on 2017/6/12.
 */

public class PayActivity extends AbsBaseActivity {

    private ActivityPayBinding mBinding;

    private float rate;//折扣

    private String  mStoreCode;

    private Double mDiscountMoney=0.0;

    private int mPayType=1;

    /**
     * 打开当前页面
     * @param context
     */
    public static void open(Context context,float rate,String storeCode){
        if(context==null){
            return;
        }
        Intent intent=new Intent(context,PayActivity.class);
        intent.putExtra("rate",rate);
        intent.putExtra("storeCode",storeCode);
        context.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding= DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_pay, null, false);

        addMainView(mBinding.getRoot());

        setTopTitle(getString(R.string.txt_pay));

        setSubLeftImgState(true);

        if(getIntent()!=null){
            mStoreCode=getIntent().getStringExtra("storeCode");
            rate= getIntent().getFloatExtra("rate",0);
        }

        initViews();

        initPayTypeSelectState();

    }

    /**
     * 初始化支付方式选择状态
     */
    private void initPayTypeSelectState() {

        ImgUtils.loadImgId(PayActivity.this,R.mipmap.pay_select,mBinding.imgBalace);
        ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgWeixin);
        ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgZhifubao);

        mBinding.linBalace.setOnClickListener(v -> {
            mPayType=1;
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.pay_select,mBinding.imgBalace);
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgWeixin);
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgZhifubao);
        });
     mBinding.linWeipay.setOnClickListener(v -> {
            mPayType=2;
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgBalace);
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.pay_select,mBinding.imgWeixin);
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgZhifubao);
        });

     mBinding.linZhifubao.setOnClickListener(v -> {
           mPayType=3;
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgBalace);
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.un_select,mBinding.imgWeixin);
            ImgUtils.loadImgId(PayActivity.this,R.mipmap.pay_select,mBinding.imgZhifubao);
        });


    }



    private void initViews() {

        mBinding.edtPrice.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});

        mBinding.tvRate.setText((int)(rate*10)+"折");

        mBinding.txtPay.setOnClickListener(v -> {
            if(SPUtilHelpr.isLogin(this)){

                if(mPayType != 1){
                    showToast("暂未开通此支付功能");
                    return;
                }

                if(TextUtils.isEmpty(mBinding.edtPrice.getText().toString())){
                    showToast("请输入消费金额");
                    return;
                }

                payRequest(mPayType);
            }
        });

        RxTextView.textChanges(mBinding.edtPrice).subscribe(charSequence -> {
            if(!TextUtils.isEmpty(charSequence.toString())){

                mDiscountMoney= (Double.valueOf(charSequence.toString())*rate);

                mBinding.txtDiscountMoney.setText(StringUtils.doubleFormatMoney(mDiscountMoney)+"");

            }else{
                mDiscountMoney=0.0;
                mBinding.txtDiscountMoney.setText("0");
            }

        });
    }

    private void payRequest(int PayType) {
/*// 用户编号（必填）
    private String userId;

    // 商家编号（必填）
    private String storeCode;

    // 消费金额（必填）
    private String amount;

    // 支付类型（必填） 1-余额支付  2-微信APP支付 3-支付宝APP支付
    private String payType;*/

        Map map=new HashMap();

        map.put("userId", SPUtilHelpr.getUserId());
        map.put("storeCode", mStoreCode);
        map.put("amount",doubleFormatMoney2(mDiscountMoney)*1000);
        map.put("payType",PayType);

        mSubscription.add(RetrofitUtils.getLoaderServer().Pay("808271", StringUtils.getJsonToString(map))
                .compose(RxTransformerHelper.applySchedulerResult(this))
                .subscribe(data -> {
                  if(TextUtils.isEmpty(data)){
                      showToast("支付失败");
                  }else{
                      showToast("支付成功");
                      finish();
                  }

                },Throwable::printStackTrace));

    }

}