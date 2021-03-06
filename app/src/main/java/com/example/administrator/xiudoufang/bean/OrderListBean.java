package com.example.administrator.xiudoufang.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2018/8/30
 */

public class OrderListBean {


    /**
     * status : 1
     * message : null
     * fx_ordermainlists : [{"iid":"34431","dianid":"45","c_id":"3155","customername":"义乌总店","sno":"XS180829-0002","xiadanriqi":"2018/8/29 9:47:43","yingshou_amt":"0.1500","queren_status":"0","shipstatus":"0","peihuo_status":"0"}]
     */

    private String status;
    private Object message;
    private List<OrderBean> fx_ordermainlists;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public List<OrderBean> getFx_ordermainlists() {
        return fx_ordermainlists;
    }

    public void setFx_ordermainlists(List<OrderBean> fx_ordermainlists) {
        this.fx_ordermainlists = fx_ordermainlists;
    }

    public static class OrderBean implements Parcelable {
        /**
         * iid : 34431
         * dianid : 45
         * c_id : 3155
         * customername : 义乌总店
         * sno : XS180829-0002
         * xiadanriqi : 2018/8/29 9:47:43
         * yingshou_amt : 0.1500
         * queren_status : 0
         * shipstatus : 0
         * peihuo_status : 0
         */

        private String iid;
        private String dianid;
        private String c_id;
        private String customername;
        private String sno;
        private String xiadanriqi;
        private String yingshou_amt;
        private String queren_status;
        private String shipstatus;
        private String peihuo_status;

        public String getIid() {
            return iid;
        }

        public void setIid(String iid) {
            this.iid = iid;
        }

        public String getDianid() {
            return dianid;
        }

        public void setDianid(String dianid) {
            this.dianid = dianid;
        }

        public String getC_id() {
            return c_id;
        }

        public void setC_id(String c_id) {
            this.c_id = c_id;
        }

        public String getCustomername() {
            return customername;
        }

        public void setCustomername(String customername) {
            this.customername = customername;
        }

        public String getSno() {
            return sno;
        }

        public void setSno(String sno) {
            this.sno = sno;
        }

        public String getXiadanriqi() {
            return xiadanriqi;
        }

        public void setXiadanriqi(String xiadanriqi) {
            this.xiadanriqi = xiadanriqi;
        }

        public String getYingshou_amt() {
            return yingshou_amt;
        }

        public void setYingshou_amt(String yingshou_amt) {
            this.yingshou_amt = yingshou_amt;
        }

        public String getQueren_status() {
            return queren_status;
        }

        public void setQueren_status(String queren_status) {
            this.queren_status = queren_status;
        }

        public String getShipstatus() {
            return shipstatus;
        }

        public void setShipstatus(String shipstatus) {
            this.shipstatus = shipstatus;
        }

        public String getPeihuo_status() {
            return peihuo_status;
        }

        public void setPeihuo_status(String peihuo_status) {
            this.peihuo_status = peihuo_status;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.iid);
            dest.writeString(this.dianid);
            dest.writeString(this.c_id);
            dest.writeString(this.customername);
            dest.writeString(this.sno);
            dest.writeString(this.xiadanriqi);
            dest.writeString(this.yingshou_amt);
            dest.writeString(this.queren_status);
            dest.writeString(this.shipstatus);
            dest.writeString(this.peihuo_status);
        }

        public OrderBean() {
        }

        protected OrderBean(Parcel in) {
            this.iid = in.readString();
            this.dianid = in.readString();
            this.c_id = in.readString();
            this.customername = in.readString();
            this.sno = in.readString();
            this.xiadanriqi = in.readString();
            this.yingshou_amt = in.readString();
            this.queren_status = in.readString();
            this.shipstatus = in.readString();
            this.peihuo_status = in.readString();
        }

        public static final Creator<OrderBean> CREATOR = new Creator<OrderBean>() {
            @Override
            public OrderBean createFromParcel(Parcel source) {
                return new OrderBean(source);
            }

            @Override
            public OrderBean[] newArray(int size) {
                return new OrderBean[size];
            }
        };
    }
}
