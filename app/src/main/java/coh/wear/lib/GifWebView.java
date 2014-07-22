package coh.wear.lib;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by Christopher on 22/07/2014.
 */
public class GifWebView extends WebView {

    public GifWebView(Context context, String path) {
        super(context);
        if(!isInEditMode()) {
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null);
        }

        loadUrl(path);
    }
}
