package com.comingx.zanao.presentation;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.comingx.zanao.R;
import com.comingx.zanao.app.AppApplication;
import com.comingx.zanao.presentation.base.BaseActivity;
import com.comingx.zanao.presentation.eGuideActivity;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.sdk.TbsVideoCacheTask;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zaixiaoyuan.hybridge.type.WritableHBMap;
import com.zaixiaoyuan.hybridge.view.HBBaseWebView;
import defpackage.cd;
import defpackage.lq;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
/**
 * eGuideActivity 类继承自 BaseActivity，主要用于展示引导页面.
 * 
 * 成员变量:
 * - h: HBBaseWebView 对象,用于展示网页内容.
 * - i: cd 对象,未知用途.
 * - k: String 类型,存储 URL。
 * - l: String 类型,存储 URL。
 * - m, n, o, p, q, r: TextView 对象,用于展示文本内容.
 * - s: Button 对象,用于用户交互.
 * - t: RelativeLayout 对象,布局容器.
 * - u: WritableHBMap 对象,存储可写的键值对.
 * - j: boolean 类型,标识某种状态.
 * - v: String 数组,存储权限列表.
 * - w: List<String> 对象,存储字符串列表.
 * 
 * 内部类:
 * - a: 继承自 WebViewClient，用于处理 WebView 的各种事件.
 * - b: 继承自 WebChromeClient，用于处理 WebView 的 JavaScript 事件.
 * - c: 实现 AppApplication.e 接口,用于处理应用程序的某些事件.
 * - d, e, f, g, h: 实现 View.OnClickListener 接口,用于处理按钮点击事件.
 * 
 * 重写方法:
 * - finish(): 结束当前 Activity，并禁用过渡动画.
 * - l(): 返回布局资源 ID。
 * - onConfigurationChanged(): 处理配置更改事件,如夜间模式切换.
 * - onCreate(): 初始化 Activity，设置 WebView 和其他 UI 元素.
 * - onStart(): 在 Activity 启动时调用,加载引导页面或显示隐私政策确认对话框.
 * - q(): 启动新的 Activity，并根据参数决定是否使用过渡动画.
 * 
 * 私有方法:
 * - w(): 检查用户是否已确认隐私政策.
 */
/**
 * eGuideActivity 是一个继承自 BaseActivity 的活动类,主要用于展示引导页面.
 * 该类包含多个内部类和方法,用于处理 WebView 的加载/页面配置变化/按钮点击事件等.
 * 
 * 成员变量:
 * - h: HBBaseWebView 对象,用于加载和显示网页内容.
 * - i: cd 对象,用于处理 WebView 的相关操作.
 * - k: String 类型,存储网页 URL。
 * - l: String 类型,存储从 Intent 获取的 URL。
 * - m, n, o, p, q, r: TextView 对象,用于显示不同的文本内容.
 * - s: Button 对象,用于处理按钮点击事件.
 * - t: RelativeLayout 对象,用于布局管理.
 * - u: WritableHBMap 对象,用于存储和传递数据.
 * - j: boolean 类型,标识是否需要重新加载页面.
 * - v: String 数组,存储权限列表.
 * - w: List<String> 对象,用于存储权限列表.
 * 
 * 内部类:
 * - a: 继承自 WebViewClient，用于处理 WebView 的页面加载事件.
 * - b: 继承自 WebChromeClient，用于处理 WebView 的 JavaScript 提示框.
 * - c: 实现 AppApplication.e 接口,用于处理应用程序的回调事件.
 * - d, e, f, g, h: 实现 View.OnClickListener 接口,用于处理不同按钮的点击事件.
 * 
 * 方法:
 * - finish(): 重写自 Activity 类,用于结束当前活动.
 * - l(): 重写自 BaseActivity 类,返回布局资源 ID。
 * - onConfigurationChanged(Configuration configuration): 重写自 AppCompatActivity 类,用于处理配置变化.
 * - onCreate(Bundle bundle): 重写自 BaseActivity 类,用于初始化活动.
 * - onStart(): 重写自 BaseActivity 类,用于处理活动启动时的操作.
 * - q(Intent intent, Boolean bool): 重写自 BaseActivity 类,用于启动新的活动.
 * - w(): 检查 SharedPreferences 中是否已确认隐私政策.
 */
public class eGuideActivity extends BaseActivity {
    public HBBaseWebView h;
    public cd i;
    public String k;
    public String l;
    public TextView m;
    public TextView n;
    public TextView o;
    public TextView p;
    public TextView q;
    public TextView r;
    public Button s;
    public RelativeLayout t;
    public WritableHBMap u;
    public boolean j = false;
    public String[] v = {"android.permission.READ_PHONE_STATE"};
    public List<String> w = new ArrayList();

    /* loaded from: classes.dex */
    public class a extends WebViewClient {

        /* renamed from: com.comingx.zanao.presentation.eGuideActivity$a$a, reason: collision with other inner class name */
        /* loaded from: classes.dex */
        public class C0034a extends TimerTask {
            public final /* synthetic */ WebView a;

            /* renamed from: com.comingx.zanao.presentation.eGuideActivity$a$a$a, reason: collision with other inner class name */
            /* loaded from: classes.dex */
            public class RunnableC0035a implements Runnable {
                public RunnableC0035a() {
                }

                @Override // java.lang.Runnable
                public void run() {
                    StringBuilder sb = new StringBuilder();
                    sb.append(System.currentTimeMillis());
                    sb.append("");
                    C0034a.this.a.setVisibility(0);
                }
            }

            public C0034a(WebView webView) {
                this.a = webView;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                eGuideActivity.this.runOnUiThread(new RunnableC0035a());
            }
        }

        public a() {
        }

        public static /* synthetic */ void b() {
        }

        @Override // com.tencent.smtt.sdk.WebViewClient
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            if (eGuideActivity.this.j) {
                eGuideActivity.this.i.c(webView, new cd.a() { // from class: h40
                    @Override // cd.a
                    public final void onReady() {
                        eGuideActivity.a.b();
                    }
                });
                eGuideActivity.this.j = false;
                new Timer().schedule(new C0034a(webView), 1200L);
            }
        }

        @Override // com.tencent.smtt.sdk.WebViewClient
        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
            StringBuilder sb = new StringBuilder();
            sb.append(System.currentTimeMillis());
            sb.append("");
            eGuideActivity.this.j = true;
        }

        @Override // com.tencent.smtt.sdk.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            webView.loadUrl(str);
            return true;
        }
    }

    /* loaded from: classes.dex */
    public class b extends WebChromeClient {
        public b() {
        }

        @Override // com.tencent.smtt.sdk.WebChromeClient
        public boolean onJsPrompt(WebView webView, String str, String str2, String str3, JsPromptResult jsPromptResult) {
            if (eGuideActivity.this.i.a(webView, str2, jsPromptResult)) {
                return true;
            }
            return super.onJsPrompt(webView, str, str2, str3, jsPromptResult);
        }
    }

    /* loaded from: classes.dex */
    public class c implements AppApplication.e {
        public c() {
        }

        @Override // com.comingx.zanao.app.AppApplication.e
        public void a() {
            AppApplication.b().l = true;
        }
    }

    /* loaded from: classes.dex */
    public class d implements View.OnClickListener {
        public d() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(eGuideActivity.this, (Class<?>) LBYActivity.class);
            intent.putExtra(TbsVideoCacheTask.KEY_VIDEO_CACHE_PARAM_URL, "http://c.zanao.com/stp/terms.phtml");
            intent.putExtra("scroll_refresh", true);
            intent.putExtra("is_customRefreshBgImg", false);
            intent.putExtra("pull_able", true);
            intent.putExtra("slide_able", false);
            intent.putExtra("scroll_bar_enable", true);
            intent.putExtra("full_screen", true);
            String valueOf = String.valueOf(System.currentTimeMillis());
            lq.K(valueOf, eGuideActivity.this.u);
            intent.putExtra("key", valueOf);
            eGuideActivity.this.startActivity(intent);
        }
    }

    /* loaded from: classes.dex */
    public class e implements View.OnClickListener {
        public e() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(eGuideActivity.this, (Class<?>) LBYActivity.class);
            intent.putExtra(TbsVideoCacheTask.KEY_VIDEO_CACHE_PARAM_URL, "https://c.zanao.com/stp/app-privacy.phtml");
            intent.putExtra("scroll_refresh", true);
            intent.putExtra("is_customRefreshBgImg", false);
            intent.putExtra("pull_able", true);
            intent.putExtra("slide_able", false);
            intent.putExtra("scroll_bar_enable", true);
            intent.putExtra("full_screen", true);
            String valueOf = String.valueOf(System.currentTimeMillis());
            lq.K(valueOf, eGuideActivity.this.u);
            intent.putExtra("key", valueOf);
            eGuideActivity.this.startActivity(intent);
        }
    }

    /* loaded from: classes.dex */
    public class f implements View.OnClickListener {
        public f() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Intent intent = new Intent(eGuideActivity.this, (Class<?>) LBYActivity.class);
            intent.putExtra(TbsVideoCacheTask.KEY_VIDEO_CACHE_PARAM_URL, "https://c.zanao.com/stp/app-sdk.phtml");
            intent.putExtra("scroll_refresh", true);
            intent.putExtra("is_customRefreshBgImg", false);
            intent.putExtra("pull_able", true);
            intent.putExtra("slide_able", false);
            intent.putExtra("scroll_bar_enable", true);
            intent.putExtra("full_screen", true);
            String valueOf = String.valueOf(System.currentTimeMillis());
            lq.K(valueOf, eGuideActivity.this.u);
            intent.putExtra("key", valueOf);
            eGuideActivity.this.startActivity(intent);
        }
    }

    /* loaded from: classes.dex */
    public class g implements View.OnClickListener {
        public g() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            eGuideActivity.this.finish();
        }
    }

    /* loaded from: classes.dex */
    public class h implements View.OnClickListener {
        public final /* synthetic */ FrameLayout a;
        public final /* synthetic */ View b;

        /* loaded from: classes.dex */
        public class a implements AppApplication.e {
            public a() {
            }

            @Override // com.comingx.zanao.app.AppApplication.e
            public void a() {
                AppApplication.b().l = true;
            }
        }

        public h(FrameLayout frameLayout, View view) {
            this.a = frameLayout;
            this.b = view;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            SharedPreferences.Editor edit = eGuideActivity.this.getSharedPreferences("init_sp", 0).edit();
            edit.putBoolean("hasConfirm", true);
            edit.apply();
            if (AppApplication.b().l) {
                eGuideActivity.this.h.loadUrl(eGuideActivity.this.k, new HashMap());
                this.a.removeView(this.b);
            } else {
                AppApplication.b().d(new a());
                eGuideActivity.this.h.loadUrl(eGuideActivity.this.k, new HashMap());
                this.a.removeView(this.b);
            }
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // com.comingx.zanao.presentation.base.BaseActivity
    public int l() {
        return R.layout.activity_e_guide;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    @RequiresApi(api = 23)
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.uiMode & 48;
        if (i == 16) {
            getWindow().setStatusBarColor(Color.parseColor("#FEFEFE"));
            getWindow().setNavigationBarColor(Color.parseColor("#FEFEFE"));
            getWindow().getDecorView().setSystemUiVisibility(8192);
            if (AppApplication.b().j) {
                AppApplication.b().i = false;
                return;
            }
            return;
        }
        if (i != 32) {
            return;
        }
        getWindow().setStatusBarColor(Color.parseColor("#191919"));
        getWindow().setNavigationBarColor(Color.parseColor("#191919"));
        getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & (-8193));
        if (AppApplication.b().j) {
            AppApplication.b().i = true;
        }
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [boolean, int] */
    @Override // com.comingx.zanao.presentation.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    @RequiresApi(api = 23)
    @SuppressLint({"SetJavaScriptEnabled"})
    public void onCreate(Bundle bundle) {
        int i;
        this.l = getIntent().getStringExtra(TbsVideoCacheTask.KEY_VIDEO_CACHE_PARAM_URL);
        super.onCreate(bundle);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.guide_bg);
        if (AppApplication.b().i) {
            frameLayout.setBackgroundColor(Color.parseColor("#191919"));
        }
        HBBaseWebView hBBaseWebView = (HBBaseWebView) findViewById(R.id.guide_web);
        this.h = hBBaseWebView;
        if (hBBaseWebView.getX5WebViewExtension() != null) {
            this.h.getX5WebViewExtension().setVerticalTrackDrawable(null);
        }
        if (this.i == null) {
            this.i = cd.d(this.h);
        }
        WebSettings settings = this.h.getSettings();
        ?? r0 = AppApplication.b().j;
        if (AppApplication.b().i) {
            getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() & (-8193));
            getWindow().setStatusBarColor(Color.parseColor("#191919"));
            getWindow().setNavigationBarColor(Color.parseColor("#191919"));
            i = r0 + 2;
        } else {
            getWindow().getDecorView().setSystemUiVisibility(8192);
            getWindow().setStatusBarColor(Color.parseColor("#FEFEFE"));
            getWindow().setNavigationBarColor(Color.parseColor("#FEFEFE"));
            i = r0;
        }
        settings.setUserAgentString(this.h.getSettings().getUserAgentString() + " __HB__NightMode-" + i);
        settings.setJavaScriptEnabled(true);
        this.h.setVisibility(0);
        WritableHBMap.Create create = new WritableHBMap.Create();
        this.u = create;
        create.putInt("height", 200);
        this.u.putString("backgroundColor", "#F8F8F8");
        this.u.putString("leftIcon", "back");
        this.u.putString("leftText", "返回");
        this.u.putInt("textSize", 53);
        this.u.putString("leftTextColor", "#000000");
        this.u.putInt("iconSize", 53);
        this.u.putString("iconColor", "#000000");
        a aVar = new a();
        this.h.setWebChromeClient(new b());
        this.h.setWebViewClient(aVar);
        this.k = AppApplication.b().a + "/entry.html#/?from=start";
        if (getIntent().getBooleanExtra("isJump", false)) {
            this.k = AppApplication.b().a + getIntent().getStringExtra("entrypath");
        }
    }

    @Override // com.comingx.zanao.presentation.base.BaseBaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    @RequiresApi(api = 29)
    public void onStart() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("");
        super.onStart();
        if (AppApplication.b().i) {
            this.h.setBackgroundColor(Color.parseColor("#191919"));
        }
        if (this.h.getX5WebViewExtension() != null) {
            this.h.getX5WebViewExtension().setVerticalScrollBarEnabled(false);
        } else {
            try {
                this.h.getView().setVerticalScrollBarEnabled(false);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.guide_bg);
        if (w()) {
            if (AppApplication.b().l) {
                this.h.loadUrl(this.k, new HashMap());
                return;
            } else {
                AppApplication.b().d(new c());
                this.h.loadUrl(this.k, new HashMap());
                return;
            }
        }
        View inflate = View.inflate(this, R.layout.e_guide_item_confirm, null);
        this.m = (TextView) inflate.findViewById(R.id.privacy_neg);
        this.s = (Button) inflate.findViewById(R.id.privacy_pos);
        this.n = (TextView) inflate.findViewById(R.id.privacy_context);
        this.o = (TextView) inflate.findViewById(R.id.privacy_context_2);
        this.t = (RelativeLayout) inflate.findViewById(R.id.privacy_content);
        this.p = (TextView) inflate.findViewById(R.id.click1);
        this.q = (TextView) inflate.findViewById(R.id.click2);
        this.r = (TextView) inflate.findViewById(R.id.click3);
        this.n.setText("感谢您使用集市服务，我们非常重视您的个人信息和隐私保护，为了更好地保障您的个人权益，请您务必审慎阅读、充分理解《用户协议》与《隐私政策》各条款。如果您同意，请点击下面的“同意”按钮以接受我们的服务。");
        this.p.setText("  《用户协议》                    ");
        this.p.setMovementMethod(LinkMovementMethod.getInstance());
        this.q.setText("  《隐私政策》                    ");
        this.q.setMovementMethod(LinkMovementMethod.getInstance());
        this.r.setText("  《校园集市APP接入第三方SDK目录》  ");
        this.r.setMovementMethod(LinkMovementMethod.getInstance());
        this.o.setText(Html.fromHtml("<p style=\"text-align:justify\">1.在您使用软件及服务的过程中，我们向您提供的相关功能，将根据合法、正当、必要的原则，收集或使用必要的个人信息。部分第三方SDK可能会在应用启动时以特定频率多次收集手机状态信息，如IMEI、IMSI、设备MAC地址等。</p>\n<p style=\"text-align:justify\">2.我们会一直采取行业领先的数据安全措施来保护您的个人信息安全。</p>\n<p style=\"text-align:justify\">3.为保障软件的安全运行与账号安全，我们会申请收集您的网络连接、设备信息、日志信息、IP地址、MAC地址、蓝牙信息、应用安装列表等。</p>\n<p style=\"text-align:justify\">4.上传或拍摄图片、视频，需要使用您的存储、相机、麦克风权限。</p>\n<p style=\"text-align:justify\">5.我们可能会申请位置权限，用于帮助您在发布信息中展示位置信息。</p>\n<p style=\"text-align:justify\">6.为实现信息分享、参加相关活动等目的所必需，我们可能会调用您的剪切板并使用与功能相关的最小必要权限。</p>\n<p style=\"text-align:justify\">7.同时我们也会接入服务商提供的SDK，如腾讯TBS等，在启动时会获取应用安装列表等信息以提供更好的服务。接入第三方SDK详情请查看《校园集市APP接入第三方SDK目录》。</p>\n<p style=\"text-align:justify\">8.为了让用户及时获得其他用户的回复，我们使用了第三方推送SDK——MiPush，该第三方组件可能会在应用启动时以特定频率收集手机状态信息，如IMEI、IMSI、设备MAC地址等。</p>\n"));
        this.p.setOnClickListener(new d());
        this.q.setOnClickListener(new e());
        this.r.setOnClickListener(new f());
        this.m.setOnClickListener(new g());
        this.s.setOnClickListener(new h(frameLayout, inflate));
        frameLayout.addView(inflate);
    }

    @Override // com.comingx.zanao.presentation.base.BaseActivity
    @RequiresApi(api = 21)
    public void q(Intent intent, Boolean bool) {
        intent.putExtra("visible", true);
        super.startActivity(intent);
        if (bool.booleanValue()) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, new Pair[0]).toBundle());
        }
    }

    public final boolean w() {
        return getSharedPreferences("init_sp", 0).getBoolean("hasConfirm", false);
    }
}