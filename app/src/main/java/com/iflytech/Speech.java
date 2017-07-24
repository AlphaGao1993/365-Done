package com.iflytech;

import android.content.Context;

import com.alphagao.done365.ui.view.DataListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/**
 * Created by Alpha on 2017/4/24.
 */

public class Speech {
    private DataListener<String> listener;
    private Context context;

    public Speech(Context context, DataListener<String> listener) {
        this.listener = listener;
        this.context = context;
    }

    public void startSpeech(boolean hasSymbol) {
        SpeechRecognizer recognizer = SpeechRecognizer.createRecognizer(context, null);
        recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        recognizer.setParameter(SpeechConstant.ACCENT, "zh_cn");
        //设置听写引擎
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //返回结果格式
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //开始前几秒不说话超时
        recognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
        //停顿几秒后表示结束
        recognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
        //是否加标点符号，0 表示不加，1 表示加
        recognizer.setParameter(SpeechConstant.ASR_PTT, hasSymbol ? "1" : "0");
        recognizer.startListening(null);

        //听写交互动画
        RecognizerDialog dialog = new RecognizerDialog(context, null);
        dialog.setListener(dialogListener);
        dialog.show();
    }

    private RecognizerDialogListener dialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            if (!b) {//只需要第一次识别出的字符串，后续会输出空白
                String content = SpeechResultParser.parseIatResult(
                        recognizerResult.getResultString());
                if (listener != null && content.length() > 0) {
                    listener.onComplete(content);
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };
}
