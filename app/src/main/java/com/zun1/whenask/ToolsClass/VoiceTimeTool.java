package com.zun1.whenask.ToolsClass;

/**
 * Created by zun1user7 on 2016/8/16.
 */
public class VoiceTimeTool {
    public static String voiceTimeFromvoicelength(String voicelength){
        String voiceTime = null;
        if (Integer.parseInt(voicelength) < 59){
             voiceTime = voicelength + "''";
        }else {
            voiceTime = String.valueOf(Integer.parseInt(voicelength)/60) +"'"+ String.valueOf(Integer.parseInt(voicelength)%60)+"''";
        }
        return voiceTime;
    }
}
