package com.tryrosberry.ololostfilm.debug;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.tryrosberry.ololostfilm.LostFilmApp;
import com.tryrosberry.ololostfilm.logic.storage.ConstantStorage;

public class DebugHandler implements Thread.UncaughtExceptionHandler {
	private static String MAIL_SENDER = ConstantStorage.DEBUG_EMAIL;
	private static String MAIL_PASSWORD = ConstantStorage.DEBUG_PASS;

    private Context mContext;
	private String mMessageTo;
    private Thread.UncaughtExceptionHandler DefUEH;

    public DebugHandler(Context contextOrApp, String messageTo){
        mContext = contextOrApp;
        mMessageTo = messageTo;
        DefUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if(throwable != null){
            String bug = "BUGREPORT: \n \n";

            if(throwable.getMessage() != null){
                bug = bug + "Message: " + throwable.getMessage() + "\n \n";
            }
            bug = getCause(throwable, bug);
            if(throwable.toString() != null){
                bug = bug + "Exception: " + throwable.toString() + "\n \n";
            }
            if(throwable.getStackTrace() != null && throwable.getStackTrace().length > 0){
                StackTraceElement[] stack = throwable.getStackTrace();
                bug = bug + "StackTrace: \n";
                for(StackTraceElement element : stack){
                    bug = bug + element.toString() + "\n";
                }
                bug = bug + "\n \n";
            }

            LostFilmApp.getInstance().getSettings().saveCrash(bug);

            //try sending report
            sendReport(mContext,bug,mMessageTo, true);

        }

        DefUEH.uncaughtException(thread,throwable);

    }

    /**
     * Make Report Bug Mail (background sending)
     **/
    public static void sendReport(final Context context, final String bug, final String messageTo, boolean byMail){
        if(byMail){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mail m = new Mail(MAIL_SENDER, MAIL_PASSWORD);
                    try {
                        m.sendMail(context.getPackageName() + " bug report from " + Build.MODEL, bug, MAIL_SENDER, messageTo);
                    } catch (Exception e) {
                        Toast.makeText(context,"Can\'t send debug by mail, looks like kind of error =( sorry",Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            //Make Report Bug Mail (by intent)
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, bug);
            emailIntent.setData(Uri.parse("mailto:" + messageTo));
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(emailIntent);
        }
    }

	private String getCause(Throwable throwable, String bug) {
		Throwable cause = throwable.getCause();
		if(cause != null){
			bug = bug + "Cause: " + cause + "\n \n";
			if(cause.getStackTrace().length > 0) {
				StackTraceElement[] stack = cause.getStackTrace();
                bug = bug + "StackTrace: \n";
                for(StackTraceElement element : stack){
                    bug = bug + element.toString() + "\n";
                }
                bug = bug + "\n \n";
			}
        }
		if(cause != null && cause.getCause() != null) {
			bug = getCause(cause, bug);
		}
		return bug;
	}

}
