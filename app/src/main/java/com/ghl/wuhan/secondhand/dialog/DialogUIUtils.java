package com.ghl.wuhan.secondhand.dialog;

import android.app.Dialog;
import android.content.Context;

/**
 * 项目名称：GLOWallet
 * 类描叙：
 * 创建人：chenmin
 * 创建时间： 2017/11/9 14:13
 * 修改人：chenmin
 * 修改时间： 2017/11/9 14:13
 * 修改备注：
 * 版本：
 */
public class DialogUIUtils {


    /**
     * Show loading dialog dialog.
     *
     * @param context the context
     * @param showMes the show mes
     * @return the dialog
     */
    public static Dialog showLoadingDialog(Context context, String showMes){
        Dialog progressDialog;
        progressDialog = CustomProgressTransDialog.createLoadingDialog(context, showMes);
        return progressDialog;
    }

    /**
     * Dismiss.
     *
     * @param dialog the dialog
     */
    public static void dismiss(Dialog dialog){

        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
    }

}
