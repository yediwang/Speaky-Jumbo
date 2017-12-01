package com.example.ywang.speaky_jumbo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class homepage extends AppCompatActivity {

    LinearLayout LL;
    ImageButton b_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        LL = (LinearLayout)findViewById(R.id.lnlo);;

        init();
    }

    void init() {
        LL.addView(createCard("mary"));
    }

    View createCard(String username) {
        ImageView card = new ImageView(this);
        ImageView avatar = new ImageView(this);
        TextView lang = new TextView(this);
        RelativeLayout rl = new RelativeLayout(this);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, calculateDpToPx(155));
        rl.setLayoutParams(lp);

        RelativeLayout.LayoutParams card_lp = new RelativeLayout.LayoutParams(
                calculateDpToPx(380), ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card_lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        card.setImageResource(R.drawable.card);
        card.setLayoutParams(card_lp);
        card.setId(View.generateViewId());
        rl.addView(card);

        RelativeLayout.LayoutParams avatar_lp = new RelativeLayout.LayoutParams(
                calculateDpToPx(100), ViewGroup.LayoutParams.WRAP_CONTENT
        );
        avatar_lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        avatar_lp.addRule(RelativeLayout.ALIGN_START, card.getId());
        avatar_lp.setMarginStart(calculateDpToPx(19));
        avatar.setImageResource(R.drawable.avatar);
        avatar.setLayoutParams(avatar_lp);
        avatar.setId(View.generateViewId());
        rl.addView(avatar);

        RelativeLayout.LayoutParams lang_lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lang_lp.addRule(RelativeLayout.END_OF, avatar.getId());
        lang_lp.setMarginStart(calculateDpToPx(17));
        lang_lp.addRule(RelativeLayout.CENTER_VERTICAL);
        lang.setLayoutParams(lang_lp);
        lang.setText("Native Language : English\nLanguage of Study : Spanish");
        lang.setTextSize(16);
        lang.setLineSpacing(0,(float)1.8);
        rl.addView(lang);

        return rl;
    }

    private int calculateDpToPx(int padding_in_dp){
        final float scale = getResources().getDisplayMetrics().density;
        return  (int) (padding_in_dp * scale + 0.5f);
    }
}
