package com.tencent.qcloud.cosxml.sample.BucketSample;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.GetBucketCORSRequest;
import com.tencent.cos.xml.model.bucket.GetBucketCORSResult;
import com.tencent.qcloud.cosxml.sample.ResultActivity;
import com.tencent.qcloud.cosxml.sample.ResultHelper;
import com.tencent.qcloud.cosxml.sample.common.QServiceCfg;
import com.tencent.qcloud.network.exception.QCloudException;

/**
 * Created by bradyxiao on 2017/6/1.
 * author bradyxiao
 *
 * Get Bucket CORS 接口实现 Bucket 持有者在 Bucket 上进行跨域资源共享的信息配置。
 * （CORS 是一个 W3C 标准，全称是"跨域资源共享"（Cross-origin resource sharing））。
 * 默认情况下，Bucket 的持有者直接有权限使用该 API 接口，Bucket 持有者也可以将权限授予其他用户。
 *
 */
public class GetBucketCORSSample {
    GetBucketCORSRequest getBucketCORSRequest;
    QServiceCfg qServiceCfg;

    public GetBucketCORSSample(QServiceCfg qServiceCfg){
        this.qServiceCfg = qServiceCfg;
    }

    public ResultHelper start(){
        ResultHelper resultHelper = new ResultHelper();
        getBucketCORSRequest = new GetBucketCORSRequest();
        getBucketCORSRequest.setBucket(qServiceCfg.getUserBucket());
        getBucketCORSRequest.setSign(600,null,null);
        try {
            GetBucketCORSResult getBucketCORSResult =
                    qServiceCfg.cosXmlService.getBucketCORS(getBucketCORSRequest);
            Log.w("XIAO",getBucketCORSResult.printHeaders());
            if(getBucketCORSResult.getHttpCode() >= 300){
                Log.w("XIAO",getBucketCORSResult.printError());
            }
            resultHelper.cosXmlResult = getBucketCORSResult;
            return resultHelper;
        } catch (QCloudException e) {
            Log.w("XIAO","exception =" + e.getExceptionType() + "; " + e.getDetailMessage());
            resultHelper.exception = e;
            return resultHelper;
        }
    }
    /**
     *
     * 采用异步回调操作
     *
     */
    public void startAsync(final Activity activity){
        getBucketCORSRequest = new GetBucketCORSRequest();
        getBucketCORSRequest.setBucket(qServiceCfg.getUserBucket());
        getBucketCORSRequest.setSign(600,null,null);
        qServiceCfg.cosXmlService.getBucketCORSAsync(getBucketCORSRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(cosXmlResult.printHeaders())
                        .append(cosXmlResult.printBody());
                Log.w("XIAO", "success = " + stringBuilder.toString());
                show(activity, stringBuilder.toString());
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(cosXmlResult.printHeaders())
                        .append(cosXmlResult.printError());
                Log.w("XIAO", "failed = " + stringBuilder.toString());
                show(activity, stringBuilder.toString());
            }
        });
    }

    private void show(Activity activity, String message){
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra("RESULT", message);
        activity.startActivity(intent);
    }

}
