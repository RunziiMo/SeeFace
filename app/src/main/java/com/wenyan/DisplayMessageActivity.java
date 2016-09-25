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
			Toast.makeText(this, "û�м�⵽����", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			textFaceScore = (TextView) findViewById(R.id.textFaceScore);
			textSex = (TextView) findViewById(R.id.textSex);
			textAge = (TextView) findViewById(R.id.textAge);
			mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_display);
			mResultView = (TextView) findViewById(R.id.mResultView);
			String sex;
			if (message.get(1) < 10) {
				sex = "Ů";
			} else if (message.get(1) < 50) {
				sex = "Ů����";
			} else if (message.get(1) < 90) {
				sex = "����";
			} else {
				sex = "��";
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
					return "�����߻����������㣬��Щ���ﶼ���������������ò����ֻ��˵������ϵ�ң�";
				}else if(score>80){
					return "�·���������֮���£�Ʈ�u��������֮��ѩ��";
				}else if(score>70){
					return "����������֮�ң�����������֮��.";
				}else if(score>60){
					return "��֮һ����̫������֮һ����̫��.������Ǹ�Сƻ����";
				}else if(score>50){
					return "��ֽ���޸���������ָ�������ˣ�����һ��Ȱ���ú�ѧϰ�ɣ�";
				}else if(score>40){
					return "���ú����֤��������һ����";
				}else if(score>30){
					return "����ܽ�أ������";
				}else if(score>20){
					return "����ҹ·�����������ʲô����������";
				}else if(score>10){
					return "ֻ����Ϩ�ƺ����ġ�";
				}else{
					return "��ҹû�ˣ����ǻ���";
				}
			}else{
				if(score == 100){
					return "��˧�����Һϲ�£��";
				}else if(score>90){
					return "��ѽ���͸�������ģ��������������ԭ���㡣";
				}else if(score>80){
					return "�������Ŀ�����ǣ���������������Ϳ֬�������������������Ρ�";
				}else if(score>70){
					return "��Ͷ̥��ɶ�ؾ����ǲ��ǻ�¸�����ˣ�";
				}else if(score>60){
					return "С���⵽�㲻�ϣ�������������ɡ�";
				}else if(score>50){
					return "���ǣ��ú�Ŭ���ɣ�����С�����";
				}else if(score>40){
					return "�ѹֶ���á�";
				}else if(score>30){
					return "����ܽ�أ������";
				}else if(score>20){
					return "������廭���Ҳ��Ǻ�ϲ��";
				}else if(score>10){
					return "�Ҳ���Ӧ����1�㵽3������ġ�";
				}else{
					return "������ɶ���������°ɡ�";
				}
			}
		}
		return null;
	}

}
