package com.chengm.app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengm.cslayout.CSLayout;
import com.chengm.cslayout.ShareAnimKt;
import com.chengm.cslayout.keyparms.KeyParm;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * Target
 * 2019-11-09
 */
public class TargetActivity extends Activity {

    private CSLayout csLayout;
    private ImageView image;
    private LinearLayout linearContent;
    private TextView text_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        // 初始化
        initView();
    }

    private void initView() {
        csLayout = findViewById(R.id.cslayout);
        image = findViewById(R.id.image);
        linearContent = findViewById(R.id.view_content);
        text_title = findViewById(R.id.text_title);

        // 设置图片宽高
        KeyParm parm = getIntent().getParcelableExtra("image");
        ViewGroup.LayoutParams lp = image.getLayoutParams();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        lp.height = (int) (point.x * (parm.getRect().height() * 1f / parm.getRect().width()));
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        Log.d("XXX", "width = " + parm.getRect().width() + ", height = " + parm.getRect().height());
        Log.d("XXX", "lp.height = " + lp.height + ", lp.width = " + lp.width);

        image.setLayoutParams(lp);

        // 数据
        Banner banner = null;
        Intent intent = getIntent();
        if (intent != null) {
            banner = (Banner) intent.getSerializableExtra("data");
        }

        if (banner != null) {
            image.setImageResource(banner.getResId());
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

    private Animator anim;
    private boolean isFirst = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst && hasFocus) {
            ValueAnimator animator = (ValueAnimator) ShareAnimKt.createAnimator(true, getIntent(), "image", image);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    linearContent.setTranslationY(image.getTranslationY() * 0.72f);
                    linearContent.setTranslationX(image.getTranslationX());
                }
            });

            Animator[] animators = new Animator[]{animator,
                    ShareAnimKt.createAnimator(true, getIntent(), "title", text_title)};
            anim = ShareAnimKt.startShareAnim(TargetActivity.this, csLayout, animators, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    return null;
                }
            });
        }
        isFirst = false;
    }

    @Override
    public void onBackPressed() {
        if (anim != null && anim.isRunning()) {
            return;
        }
        ValueAnimator animator = (ValueAnimator) ShareAnimKt.createAnimator(false, getIntent(), "image", image);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                linearContent.setTranslationY(image.getTranslationY() * 0.72f);
                linearContent.setTranslationX(image.getTranslationX());
            }
        });

        Animator[] animators = new Animator[]{animator,
                ShareAnimKt.createAnimator(false, getIntent(), "title", text_title)};
        ShareAnimKt.finishShareAnim(TargetActivity.this, csLayout, animators, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finish();
                overridePendingTransition(0, R.anim.exit_fade);
                return null;
            }
        });
    }
}
