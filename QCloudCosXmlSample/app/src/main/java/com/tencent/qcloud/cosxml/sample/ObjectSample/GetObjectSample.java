package com.tencent.qcloud.cosxml.sample.ObjectSample;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.qcloud.cosxml.sample.ProgressActivity;
import com.tencent.qcloud.cosxml.sample.ResultActivity;
import com.tencent.qcloud.cosxml.sample.ResultHelper;
import com.tencent.qcloud.cosxml.sample.common.QServiceCfg;
import com.tencent.qcloud.network.QCloudProgressListener;
import com.tencent.qcloud.network.exception.QCloudException;

/**
 * Created by bradyxiao on 2017/6/7.
 * author bradyxiao
 *
 * Get Object 接口请求可以在 COS 的 Bucket 中将一个文件（Object）下载至本地。该操作需要请求者对目标 Object 具有读权限或目标 Object 对所有人都开放了读权限（公有读）。
 *
 */
public class GetObjectSample {
    GetObjectRequest getObjectRequest;
    QServiceCfg qServiceCfg;
    String cosPath;
    Handler handler;
    public GetObjectSample(QServiceCfg qServiceCfg, String cosPath, Handler handler){
        this.qServiceCfg = qServiceCfg;
        this.cosPath = cosPath;
        this.handler = handler;
    }
    public ResultHelper start(){
        ResultHelper resultHelper = new ResultHelper();
        getObjectRequest = new GetObjectRequest(qServiceCfg.downloadDir);
        getObjectRequest.setBucket(qServiceCfg.bucket);
        getObjectRequest.setCosPath(cosPath);
        getObjectRequest.setSign(600,null,null);
        getObjectRequest.setRange(1);
        getObjectRequest.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                Log.w("XIAO","progress = "+progress+" max = "+max);
                handler.obtainMessage(0, (int) (100.0 * progress / max)).sendToTarget();
            }
        });
        try {
            GetObjectResult getObjectResult = qServiceCfg.cosXmlService.getObject(getObjectRequest);
            resultHelper.cosXmlResult = getObjectResult;
            Log.w("XIAO","headers :\n " + getObjectResult.printHeaders());
            if(getObjectResult.getHttpCode() >= 300){
                Log.w("XIAO","error :\n " +getObjectResult.printError());
            }
            handler.sendEmptyMessage(1);
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
        getObjectRequest = new GetObjectRequest(qServiceCfg.downloadDir);
        getObjectRequest.setBucket(qServiceCfg.bucket);
        getObjectRequest.setCosPath(cosPath);
        getObjectRequest.setSign(600,null,null);
        getObjectRequest.setRange(1);
        getObjectRequest.setProgressListener(new QCloudProgressListener() {
            @Override
            public void onProgress(long progress, long max) {
                Log.w("XIAO","progress = "+progress+" max = "+max);
                handler.obtainMessage(0, (int) ((100.00 * progress / max))).sendToTarget();
            }
        });
        qServiceCfg.cosXmlService.getObjectAsync(getObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(cosXmlResult.printHeaders())
                        .append(cosXmlResult.printBody());
                Log.w("XIAO", "success = " + stringBuilder.toString());
                handler.sendEmptyMessage(1);
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
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra("RESULT", message);
        activity.startActivity(intent);
        if (activity instanceof ProgressActivity) {
            activity.finish();
        }
    }
}