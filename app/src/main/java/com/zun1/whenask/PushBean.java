package com.zun1.whenask;

/**
 * Created by dell on 2016/8/18.
 */
public class PushBean {
    private String ErrorCode;
    private Items Items;

    public static class Items {
        private String picture;
        private String fulltext;
        private String voicelength;
        private String grade;
        private String subjectid;
        private String voice;
        private String asktime;

        public String getPicture() {
            return picture;
        }

        public String getFulltext() {
            return fulltext;
        }

        public String getVoicelength() {
            return voicelength;
        }

        public String getGrade() {
            return grade;
        }

        public String getSubjectid() {
            return subjectid;
        }

        public String getVoice() {
            return voice;
        }

        public String getAsktime() {
            return asktime;
        }

        @Override
        public String toString() {
            return "Items{" +
                    "picture='" + picture + '\'' +
                    ", fulltext='" + fulltext + '\'' +
                    ", voicelength='" + voicelength + '\'' +
                    ", grade='" + grade + '\'' +
                    ", subjectid='" + subjectid + '\'' +
                    ", voice='" + voice + '\'' +
                    ", asktime='" + asktime + '\'' +
                    '}';
        }
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public PushBean.Items getItems() {
        return Items;
    }

    @Override
    public String toString() {
        return "PushBean{" +
                "ErrorCode='" + ErrorCode + '\'' +
                ", Items=" + Items +
                '}';
    }
}
