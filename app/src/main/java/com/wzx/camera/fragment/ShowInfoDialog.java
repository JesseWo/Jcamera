package com.wzx.camera.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.wzx.camera.R;
import com.wzx.camera.rx.Rx;

import rx.functions.Action1;

/**
 * Created by wangzhx on 16/7/14.
 */

public class ShowInfoDialog extends FixDialogFragment implements DialogInterface.OnKeyListener {

    public static final String TAG = ShowInfoDialog.class.getSimpleName();

    public static final String TITLE = "title";
    public static final String INFO = "info";
    public static final String HTML = "html";
    public static final String OK_BUTTON_TEXT = "ok_text";
    public static final String CANCEL_BUTTON_TEXT = "cancel_text";
    public static final String SHOW_TITLE = "show_title";
    public static final String SHOW_CANCEL_BUTTON = "show_cancel_button";
    public static final String CANCELABLE = "cancelable";
    public static final String INFO_FORMAT_HTML = "info_html";
    public static final String TEXT_CENTER = "text_center";

    private WebView webView;

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    public interface OnClickListener {
        void onClickOk();
        void onClickCancel();
    }

    public static ShowInfoDialog newInstance(OnClickListener listener, Bundle info) {
        ShowInfoDialog dialog = new ShowInfoDialog();
        dialog.setOnClickListener(listener);
        if (info == null) info = new Bundle();
        dialog.setArguments(info);
        return dialog;
    }

    public static ShowInfoDialog newInstance(Action1 okAction, Bundle info) {
        return newInstance(new OnClickListener() {
            @Override
            public void onClickOk() {
                if (okAction != null) {
                    okAction.call(null);
                }
            }

            @Override
            public void onClickCancel() {

            }
        }, info);
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    private OnClickListener onClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            theme = android.R.style.Theme_DeviceDefault_Dialog_MinWidth;
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.setCanceledOnTouchOutside(false);
        if (!getArguments().getBoolean(CANCELABLE, true)) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(this);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String info = bundle.getString(HTML);
        int layout = R.layout.fragment_info_dialog;
        View main;
        if (info != null) {
            layout = R.layout.fragment_html_info_dialog;
            main = inflater.inflate(layout, container, false);
            webView = (WebView) main.findViewById(R.id.info);
        } else {
            main = inflater.inflate(layout, container, false);
            boolean show_title = bundle.getBoolean(SHOW_TITLE, false);
            if (show_title) {
                TextView textView = (TextView) main.findViewById(R.id.info);
                boolean center = bundle.getBoolean(TEXT_CENTER, false);
                if (center) textView.setGravity(Gravity.CENTER);
                info = bundle.getString(INFO);
                if (info != null) {
                    boolean html = bundle.getBoolean(INFO_FORMAT_HTML, false);
                    if (html) {
                        textView.setText(Html.fromHtml(info));
                    } else {
                        textView.setText(info);
                    }
                }
                textView = (TextView) main.findViewById(R.id.title);
                textView.setVisibility(View.VISIBLE);
                info = bundle.getString(TITLE);
                if (info != null) {
                    textView.setText(info);
                }
            } else {
                TextView textView = (TextView) main.findViewById(R.id.info);
                boolean center = bundle.getBoolean(TEXT_CENTER, true);
                if (center) textView.setGravity(Gravity.CENTER);
                info = bundle.getString(INFO);
                if (info != null) {
                    boolean html = bundle.getBoolean(INFO_FORMAT_HTML, false);
                    if (html) {
                        textView.setText(Html.fromHtml(info));
                    } else {
                        textView.setText(info);
                    }
                }
            }
        }

        final Button button_yes = (Button) main.findViewById(R.id.btn_yes);
        String btn_text = bundle.getString(OK_BUTTON_TEXT);
        if (btn_text != null) {
            button_yes.setText(btn_text);
        }
        Rx.clicks(button_yes, v->{
            dismiss();
            if (onClickListener != null) onClickListener.onClickOk();
        });

        final Button button_cancel = (Button) main.findViewById(R.id.btn_cancel);
        btn_text = bundle.getString(CANCEL_BUTTON_TEXT);
        if (btn_text != null) {
            button_cancel.setText(btn_text);
        }
        boolean showCancelButton = bundle.getBoolean(SHOW_CANCEL_BUTTON, true);
        if (showCancelButton) {
            button_cancel.setVisibility(View.VISIBLE);
        } else {
            button_cancel.setVisibility(View.GONE);
        }
        Rx.clicks(button_cancel,v->{
            dismiss();
            if (onClickListener != null) onClickListener.onClickCancel();
        });

        if (webView != null) {
            button_yes.setEnabled(false);
            button_cancel.setEnabled(false);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    button_yes.setEnabled(true);
                    button_cancel.setEnabled(true);
                }
            });
            webView.loadData(info, "text/html; charset=utf-8", "utf-8");
        }

        return main;
    }
}
