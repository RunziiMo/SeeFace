package com.wenyan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wenyan.R;

public class DisplayMessageActivity extends Activity {
	TextView textFaceScore, textSex, textAge, mResultView = null;
	RelativeLayout mRelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_display_message);

		Intent intent = getIntent();
		ArrayList<Integer> message = intent
				.getIntegerArrayListExtra(MainActivity.JSON);
		if (message.size() == 0) {
			Toast.makeText(this, "没有检测到人脸", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			textFaceScore = (TextView) findViewById(R.id.textFaceScore);
			textSex = (TextView) findViewById(R.id.textSex);
			textAge = (TextView) findViewById(R.id.textAge);
			mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_display);
			mResultView = (TextView) findViewById(R.id.mResultView);
			String sex;
			if (message.get(1) < 10) {
				sex = "女";
			} else if (message.get(1) < 50) {
				sex = "女汉子";
			} else if (message.get(1) < 90) {
				sex = "娘炮";
			} else {
				sex = "男";
			}
			
			mResultView.setText(getResultComment(message.get(0), message.get(1)));

			textFaceScore.setText("" + message.get(0));
			textSex.setText(sex);
			textAge.setText("" + message.get(2) / 100);
			mRelativeLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					DisplayMessageActivity.this.finish();
				}
			});
		}

	}
	
	private String getResultComment(final int score,final int sex){
		if(mResultView !=null){
			if(sex<0.5){
				if(score>90){
					return "闭月羞花。沉鱼落雁，这些词语都不足以形容你的美貌，我只想说，请联系我！";
				}else if(score>80){
					return "仿佛兮若轻云之蔽月，飘飖兮若流风之回雪。";
				}else if(score>70){
					return "俏丽若三春之桃，清素若九秋之菊.";
				}else if(score>60){
					return "增之一分则太长，减之一分则太短.你就是那个小苹果！";
				}else if(score>50){
					return "妹纸，嫁富二代咱是指望不上了，听哥一句劝，好好学习吧！";
				}else if(score>40){
					return "长得和身份证里跳出来一样的";
				}else if(score>30){
					return "赛过芙蓉，美过宇春";
				}else if(score>20){
					return "你走夜路把脸照清楚就什么都不用怕了";
				}else if(score>10){
					return "只敢在熄灯后自拍。";
				}else{
					return "初夜没了，初吻还在";
				}
			}else{
				if(score == 100){
					return "你帅的让我合不拢腿";
				}else if(score>90){
					return "哎呀，就哥们你这模样，吸毒都有人原谅你。";
				}else if(score>80){
					return "面如冠玉，目如朗星，鼻若悬胆，唇若涂脂，长身玉立，风流倜傥。";
				}else if(score>70){
					return "你投胎有啥秘诀吗？是不是贿赂阎王了？";
				}else if(score>60){
					return "小鲜肉到算不上，算你是冷鲜肉吧。";
				}else if(score>50){
					return "哥们，好好努力吧，不比小白脸差。";
				}else if(score>40){
					return "难怪读书好。";
				}else if(score>30){
					return "赛过芙蓉，美过宇春";
				}else if(score>20){
					return "你的整体画风我不是很喜欢";
				}else if(score>10){
					return "我猜你应该是1点到3点出生的。";
				}else{
					return "还形容啥啊，当面吐吧。";
				}
			}
		}
		return null;
	}

}
