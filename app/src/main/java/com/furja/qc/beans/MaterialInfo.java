package com.furja.qc.beans;


import com.alibaba.fastjson.JSON;
import com.furja.qc.jsonbeans.MaterialJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 物料信息用于保存物料代码,物料内码、名称及规格等信息
 */

public class MaterialInfo {

    private String materialId;
    private String materialName;
    private String norm;
    private String materialISN;//物料内码
    public MaterialInfo()
    {

    }

    /**
     * 将取得的json格式化为该类
     * @param json
     */
    public void formatJson(String json) throws Exception {
        MaterialJson materialJson=JSON.parseObject(json,MaterialJson.class);
        MaterialJson.ErrDataBean dataBean=materialJson.getErrData();
        if(dataBean==null)
            throw new NullPointerException();
        else
        {
            setMaterialId(dataBean.getFShortNumber());
            setMaterialName(dataBean.getFName());
            setNorm(dataBean.getFModel());
            setMaterialISN(dataBean.getFItemID());
        }
    }



    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }


    public String getMaterialISN() {
        return materialISN;
    }

    public void setMaterialISN(String materialISN) {
        this.materialISN = materialISN;
    }
}

