package com.zun1.whenask;

import android.net.wifi.WifiManager;

import java.security.PublicKey;

import retrofit2.http.PUT;

/**
 * Created by dell on 2016/6/30.
 */
public class Constant {
    public final static String USERNAME = "username";
    public final static String TRUSTNAME = "trustname";
    public final static String FIRSTNAME = "firstname";
    public final static String LASTNAME = "lastname";
    public final static String LANGUAGE_CODE = "languagecode";
    public final static String IDENTIFICATION = "identification";
    public final static String PASSWORD = "password";
    public final static String EMAIL  = "email";
    public final static String PHONE = "phone";
    public final static String ADDRESS = "address";
    public final static String AREA = "area";
    public final static String GRADE= "grade";
    public final static String BIRTHDAY = "birthday";
    public final static String SCHOOL = "school";
    public final static String GENDER = "gender";
    public final static String AVATER = "avatar";
    public final static String HOBBY = "hobby";
    public final static String NICKNAME = "nickname";
    public final static String LOGINKEYCHAIN = "loginkeychain";
    public final static String USERTYPE = "usertype";
    public final static String  USERID = "userid";
    public final static String fileName = "user.ini";
    public final static String ISCHECK = "ischeck";
    public final static String DEGREE = "degree";
    public final static String WORKLONG = "worklong";
    public final static String SUBJECTIDLIST = "subjectidlist";
    public final static String SUBJECTLIST ="subjectlist";
    public final static String POSTSUBJECTID = "subjectid";
    public final static String POSTGRADE = "postgrade";
    public final static String SUBJECT = "subject";
    public final static String INDEX = "index";
    public final static String CERTIFICATE_1 = "certificate_1";
    public final static String CERTIFICATE_2 = "certificate_2";
    public final static String QID = "qid";
    public final static String TEACHERINTT = "teacherIntroduce";
    public final static String DISPALYHOMEASUPENABLED = "displayhomeasupenabled";
    public final static String ISAGREE = "isagree";
    public final static String RANK = "rank";
    public final static String AUTHCODE = "authcode";
    public final static String DEVICEID = "deviceid";
    public final static String PUSHID = "qid";

    public final static String URL = "https://whenask.com/";
    public final static String REGISTERURL = URL+"mobileapi/student/signup/";
    public final static String POSTHEADIMGURL =URL + "/mobileapi/public/upavatar/";
    public final static String LOGINURL = URL+"mobileapi/public/signin/";
    public final static String AUTOLOGINURL = URL+"mobileapi/public/autosignin/";
    public final static String USERINFOURL_STUDENT = URL+"mobileapi/student/profile/";
    public final static String USERINFOURL_TEATHER = URL+"mobileapi/tutor/profile/";
    public final static String REGISTERURL_TEACHER = URL+"mobileapi/tutor/signup/";
    public final static String EXITURL = URL+"mobileapi/public/signout/";
    public final static String POSTQUESTIONURL =URL+"mobileapi/student/askquestion/";
    public final static String HISTORYQUESTIONWITHNOURL = URL+"mobileapi/student/unfinishedquestion/";
    public final static String HISTORYQUESTIONURL = URL+"mobileapi/student/accomplishquestion/";
    public final static String AUTHCODEURL = URL+"mobileapi/public/getcaptcha/";
    public final static String TEACHERPOSTCERTIFICATEURL = URL+"mobileapi/tutor/selectsubject/";
    public final static String STUDENTRESETUSERINFOURL =URL+"mobileapi/student/modifyprofile/";
    public final static String TEACHERRESETUSERINFOURL = URL+"mobileapi/tutor/modifyprofile/";
    public final static String TEACHERRANDOMGETQUESTIONURL = URL+"mobileapi/tutor/scanquestion/";
    public final static String TEACHERSOLVEQUESTIONURL = URL+"mobileapi/tutor/lockquestion/";
    public final static String TEACHERCOMMITANSWERURL = URL+"mobileapi/tutor/commitanswer/";
    public final static String TEACHERGIVEUPQUSTIONURL = URL+"mobileapi/tutor/unlockquestion/";
    public final static String QUESTIONDETAILEDURL = URL+"mobileapi/student/scananswer/";
    public final static String STUDENTCHOICEANSWERURL = URL + "/mobileapi/student/choiceanswer/";
    public final static String RESETPASSWORIDURL = URL + "/mobileapi/public/modifypass/";
    public final static String GETAUTHCODEFORMEMAILURL = URL + "/mobileapi/public/getauthcode/";
    public final static String VERIFYAUTHCODEURL = URL + "/mobileapi/public/verifyauthcode/";
    public final static String FORGOTPASSWORDURL = URL + "/mobileapi/public/forgotpass/";
    public final static String TEACHERANSWERHISTORYURL = URL + "/mobileapi/tutor/answerhistory/";
    public final static String DEVICEIDURL = URL + "mobileapi/device/android/";
    public final static String REGISTERGETAUTHCODEURL = URL + "/mobileapi/public/signupemailauthcode/";
    public final static String TUTOR_LOOKQUESKTION= URL+"mobileapi/tutor/lookquestion/";
    public final static String OPINOINURL = URL+"/mobileapi/public/submitopinion/";
    public final static String LOOKOPPOSITEURL = URL + "/mobileapi/chat/lookopposite/";

}
