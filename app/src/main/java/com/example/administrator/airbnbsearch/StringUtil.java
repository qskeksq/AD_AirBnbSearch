package com.example.administrator.airbnbsearch;

import android.os.Build;
import android.text.Html;
import android.widget.TextView;


public class StringUtil {

    public static void setHtmlText(TextView target, String text){
        target.setAllCaps(false);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            target.setText(Html.fromHtml(text));
        } else {
            target.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        }
    }

}
