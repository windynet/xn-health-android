package com.chengdai.ehealthproject.model.tabsurrounding.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.chengdai.ehealthproject.R;
import com.chengdai.ehealthproject.base.AbsBaseActivity;
import com.chengdai.ehealthproject.databinding.ActivityHotelDetailsBinding;
import com.chengdai.ehealthproject.databinding.ActivityHotelSelectBinding;
import com.chengdai.ehealthproject.databinding.ActivityStoreDetailsBinding;
import com.chengdai.ehealthproject.model.tabsurrounding.model.DZUpdateModel;
import com.chengdai.ehealthproject.model.tabsurrounding.model.StoreDetailsModel;
import com.chengdai.ehealthproject.uitls.ImgUtils;
import com.chengdai.ehealthproject.uitls.StringUtils;
import com.chengdai.ehealthproject.uitls.nets.RetrofitUtils;
import com.chengdai.ehealthproject.uitls.nets.RxTransformerHelper;
import com.chengdai.ehealthproject.weigit.GlideImageLoader;
import com.chengdai.ehealthproject.weigit.appmanager.SPUtilHelpr;
import com.zzhoujay.richtext.RichText;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**商户详情
 * Created by 李先俊 on 2017/6/12.
 */

public class HoteldetailsActivity extends AbsBaseActivity {

    private ActivityHotelDetailsBinding mBinding;

    private String storeCode;

    private StoreDetailsModel mStoreDetailsModel;

    private List<String> imgs;

    /**
     * 打开当前页面
     * @param context
     * @param storeCode 商店编号
     */
    public static void open(Context context,String storeCode){
        if(context==null){
            return;
        }
        Intent intent=new Intent(context,HoteldetailsActivity.class);

        intent.putExtra("storeCode",storeCode);

        context.startActivity(intent);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding= DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_hotel_details, null, false);

        addMainView(mBinding.getRoot());

        setTopTitle(getString(R.string.store_details));

        if(getIntent() != null) storeCode=getIntent().getStringExtra("storeCode");

        setSubLeftImgState(true);

        getStoreDetailsRequest(storeCode);

        initViews();


        mBinding.imgDz.setOnClickListener(v -> {

            if(!SPUtilHelpr.isLogin(this)){

                return;
            }

            Map map=new HashMap();
            map.put("storeCode",storeCode);
            map.put("type","1");
            map.put("userId", SPUtilHelpr.getUserId());

            //点赞和取消点赞
            RetrofitUtils.getLoaderServer().DZRequest("808240", StringUtils.getJsonToString(map))
                    .compose(RxTransformerHelper.applySchedulerResult(this))
                    .filter(isSuccessModes -> isSuccessModes!=null)
                    .subscribe(baseResponseModel -> {
                        if(mStoreDetailsModel!=null && baseResponseModel.isSuccess()){
                            if(mStoreDetailsModel.isDZ()){
                                ImgUtils.loadImgId(this,R.mipmap.good_hand_un,mBinding.imgDz);
                                mStoreDetailsModel.setTotalDzNum(mStoreDetailsModel.getTotalDzNum()-1);
                            }else if(! mStoreDetailsModel.isDZ()){
                                ImgUtils.loadImgId(this,R.mipmap.good_hand_,mBinding.imgDz);
                                mStoreDetailsModel.setTotalDzNum(mStoreDetailsModel.getTotalDzNum()+1);
                            }
                            mBinding.tvDzsum.setText(mStoreDetailsModel.getTotalDzNum()+"");
                            mStoreDetailsModel.setDZ(!mStoreDetailsModel.isDZ());


                            DZUpdateModel dz=new DZUpdateModel();

                            dz.setCode(mStoreDetailsModel.getCode());
                            dz.setDz(mStoreDetailsModel.isDZ());
                            dz.setDzSum(mStoreDetailsModel.getTotalDzNum());

                            EventBus.getDefault().post(dz,"dzUpdate"); //SurroundingFragment 点赞效果刷新
                        }

                    },Throwable::printStackTrace);


        });

    }

    private void initViews() {
        mBinding.btnPay.setOnClickListener(v -> {
            if(mStoreDetailsModel!=null)
                 PayActivity.open(this,mStoreDetailsModel.getRate1(),mStoreDetailsModel.getCode());
        });

        mBinding.btnBookHotel.setOnClickListener(v -> {
            HotelSelectActivity.open(this,mStoreDetailsModel.getAddress(),mStoreDetailsModel.getPic(),mStoreDetailsModel.getName());
        });

    }


    /**
     * 获取商户详情
     */
    public void getStoreDetailsRequest(String code){
        Map map=new HashMap();

        map.put("userId", SPUtilHelpr.getUserId());
        map.put("code",code);

       mSubscription.add( RetrofitUtils.getLoaderServer().GetStoreDetails("808218", StringUtils.getJsonToString(map))

                .compose(RxTransformerHelper.applySchedulerResult(this))

               .filter(storeDetailsModel -> storeDetailsModel!=null)

                .subscribe(storeListModel -> {

                    mStoreDetailsModel=storeListModel;

                    setShowData(storeListModel);

                },Throwable::printStackTrace));
    }

    private void setShowData(StoreDetailsModel storeListModel) {
        imgs= StringUtils.splitAsList(storeListModel.getPic(),"\\|\\|");

        //设置图片集合
        mBinding.bannerStoreDetail.setImages(imgs);
        mBinding.bannerStoreDetail.setImageLoader(new GlideImageLoader());
        //banner设置方法全部调用完毕时最后调用
        mBinding.bannerStoreDetail.start();
        mBinding.bannerStoreDetail.startAutoPlay();


        mBinding.tvStoreName.setText(storeListModel.getName());
        mBinding.tvDescription.setText(storeListModel.getSlogan());

        mBinding.tvAddress.setText(storeListModel.getAddress());

        mBinding.tvPhoneNumber.setText(storeListModel.getBookMobile());

        mBinding.tvDzsum.setText(storeListModel.getTotalDzNum()+"");

        RichText.from(storeListModel.getDescription()).into(mBinding.tvTxtdescription);

        if(storeListModel.isDZ()){
            ImgUtils.loadImgId(this, R.mipmap.good_hand_,mBinding.imgDz);
        }else{
            ImgUtils.loadImgId(this,R.mipmap.good_hand_un,mBinding.imgDz);
        }
    }


    @Override
    protected void onDestroy() {
        mBinding.bannerStoreDetail.stopAutoPlay();
        super.onDestroy();
    }
}