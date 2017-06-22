package com.chengdai.ehealthproject.model.healthmanager.model;

import java.util.List;

/**
 * Created by 李先俊 on 2017/6/22.
 */

public class WeatherModel {

    /**
     * status : 1
     * count : 1
     * info : OK
     * infocode : 10000
     * forecasts : [{"city":"杭州市","adcode":"330100","province":"浙江","reporttime":"2017-06-22 18:00:00","casts":[{"date":"2017-06-22","week":"4","dayweather":"阵雨","nightweather":"多云","daytemp":"29","nighttemp":"23","daywind":"东南","nightwind":"东南","daypower":"≤3","nightpower":"≤3"},{"date":"2017-06-23","week":"5","dayweather":"中雨","nightweather":"中雨","daytemp":"28","nighttemp":"24","daywind":"东南","nightwind":"东南","daypower":"≤3","nightpower":"≤3"},{"date":"2017-06-24","week":"6","dayweather":"中雨","nightweather":"阵雨","daytemp":"29","nighttemp":"23","daywind":"南","nightwind":"西南","daypower":"≤3","nightpower":"≤3"},{"date":"2017-06-25","week":"7","dayweather":"中雨","nightweather":"中雨","daytemp":"26","nighttemp":"22","daywind":"西南","nightwind":"西南","daypower":"≤3","nightpower":"≤3"}]}]
     */

    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<ForecastsBean> forecasts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public List<ForecastsBean> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<ForecastsBean> forecasts) {
        this.forecasts = forecasts;
    }

    public static class ForecastsBean {
        /**
         * city : 杭州市
         * adcode : 330100
         * province : 浙江
         * reporttime : 2017-06-22 18:00:00
         * casts : [{"date":"2017-06-22","week":"4","dayweather":"阵雨","nightweather":"多云","daytemp":"29","nighttemp":"23","daywind":"东南","nightwind":"东南","daypower":"≤3","nightpower":"≤3"},{"date":"2017-06-23","week":"5","dayweather":"中雨","nightweather":"中雨","daytemp":"28","nighttemp":"24","daywind":"东南","nightwind":"东南","daypower":"≤3","nightpower":"≤3"},{"date":"2017-06-24","week":"6","dayweather":"中雨","nightweather":"阵雨","daytemp":"29","nighttemp":"23","daywind":"南","nightwind":"西南","daypower":"≤3","nightpower":"≤3"},{"date":"2017-06-25","week":"7","dayweather":"中雨","nightweather":"中雨","daytemp":"26","nighttemp":"22","daywind":"西南","nightwind":"西南","daypower":"≤3","nightpower":"≤3"}]
         */

        private String city;
        private String adcode;
        private String province;
        private String reporttime;
        private List<CastsBean> casts;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAdcode() {
            return adcode;
        }

        public void setAdcode(String adcode) {
            this.adcode = adcode;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getReporttime() {
            return reporttime;
        }

        public void setReporttime(String reporttime) {
            this.reporttime = reporttime;
        }

        public List<CastsBean> getCasts() {
            return casts;
        }

        public void setCasts(List<CastsBean> casts) {
            this.casts = casts;
        }

        public static class CastsBean {
            /**
             * date : 2017-06-22
             * week : 4
             * dayweather : 阵雨
             * nightweather : 多云
             * daytemp : 29
             * nighttemp : 23
             * daywind : 东南
             * nightwind : 东南
             * daypower : ≤3
             * nightpower : ≤3
             */

            private String date;
            private String week;
            private String dayweather;
            private String nightweather;
            private String daytemp;
            private String nighttemp;
            private String daywind;
            private String nightwind;
            private String daypower;
            private String nightpower;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getWeek() {
                return week;
            }

            public void setWeek(String week) {
                this.week = week;
            }

            public String getDayweather() {
                return dayweather;
            }

            public void setDayweather(String dayweather) {
                this.dayweather = dayweather;
            }

            public String getNightweather() {
                return nightweather;
            }

            public void setNightweather(String nightweather) {
                this.nightweather = nightweather;
            }

            public String getDaytemp() {
                return daytemp;
            }

            public void setDaytemp(String daytemp) {
                this.daytemp = daytemp;
            }

            public String getNighttemp() {
                return nighttemp;
            }

            public void setNighttemp(String nighttemp) {
                this.nighttemp = nighttemp;
            }

            public String getDaywind() {
                return daywind;
            }

            public void setDaywind(String daywind) {
                this.daywind = daywind;
            }

            public String getNightwind() {
                return nightwind;
            }

            public void setNightwind(String nightwind) {
                this.nightwind = nightwind;
            }

            public String getDaypower() {
                return daypower;
            }

            public void setDaypower(String daypower) {
                this.daypower = daypower;
            }

            public String getNightpower() {
                return nightpower;
            }

            public void setNightpower(String nightpower) {
                this.nightpower = nightpower;
            }
        }
    }
}
