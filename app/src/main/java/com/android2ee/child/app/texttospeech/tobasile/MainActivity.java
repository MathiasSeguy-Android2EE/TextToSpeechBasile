package com.android2ee.child.app.texttospeech.tobasile;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android2ee.child.app.texttospeech.tobasile.adapter.TextArrayAdapter;

public class MainActivity extends Activity implements SpeakerIntf {
	private final int TTS_DATA_CHECK = 4112008;
	private final String MY_PREFS = "WordsList";
	private final String MY_PREFS_WordsLenght = "WorsdListLenght";
	private final String MY_PREFS_WordNum = "WordNum";

	/******************************************************************************************/
	/** SpeakerIntf **************************************************************************/
	/******************************************************************************************/

	public void speak(int position) {
		say(arrayAdapter.getItem(position));
	}

	
	public void copyItem(int position) {
		itemSelected(position);
	}

	public void deleteItem(int position) {
		Log.e("TTS_MainActivity", "deleteItem called on " + position + " returns " + arrayAdapter.getItem(position));
		// arrayAdapter.remove(arrayAdapter.getItem(position));
		messages.remove(position);
		arrayAdapter.notifyDataSetChanged();
	}

	/******************************************************************************************/
	/** TextToSpeech Methods **************************************************************************/
	/******************************************************************************************/
	/**
	 * The text to speech engine
	 */
	private TextToSpeech tts;
	/**
	 * The boolean to know if text to speech is initialized
	 */
	private boolean ttsIsInit;
	/**
	 * The map to have a feed back when speaking
	 */
	private HashMap<String, String> TTS_Param_Utterance = new HashMap<String, String>();

	/**
	 * TextToSpeech engine intialization
	 */
	private void initTextToSpeech() {
		TTS_Param_Utterance.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "stringId");
		// create and launch the Intent that start the textToSpeach engine
		Intent intent = new Intent(Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(intent, TTS_DATA_CHECK);
	}

	/**
	 * The method to call to say the text contained in the edittext
	 */
	private void speak() {
		say(edt_text.getText().toString());

	}

	/**
	 * Called when tts is initialized
	 */
	private void speakInitialText() {
		say(getString(R.string.edt_text_hint));
	}

	/**
	 * The speak method
	 * 
	 * @param text
	 */
	private void say(String text) {
		if (tts != null && ttsIsInit) {
			tts.speak(text, TextToSpeech.QUEUE_ADD, TTS_Param_Utterance);
		} else {
			initTextToSpeech();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("TTS_MainActivity", "onActivityResult called");
		if (requestCode == TTS_DATA_CHECK && resultCode == Engine.CHECK_VOICE_DATA_PASS) {
			tts = new TextToSpeech(this, new OnInitListener() {
				public void onInit(int status) {
					if (status == TextToSpeech.SUCCESS) {
						ttsIsInit = true;
						// Initialize the UU
						initTTSFeedBack();
						// then speak
						speakInitialText();
					}
				}
			});
		} else {
			startActivity(new Intent(Engine.ACTION_INSTALL_TTS_DATA));
		}

	}

	/**
	 * Try to have a feed back with the text to speak engine
	 */
	private void initTTSFeedBack() {
		if (Build.VERSION.SDK_INT >= 15) {
			int listenerResult = tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
				@Override
				public void onDone(String utteranceId) {
					onDoneSpeaking();
				}

				@Override
				public void onError(String utteranceId) {
					onErrorSpeaking();
				}

				@Override
				public void onStart(String utteranceId) {
					onStartSpeaking();
				}
			});
		} else {
			int listenerResult = tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
				@Override
				public void onUtteranceCompleted(String utteranceId) {
					onDoneSpeaking();
				}
			});
		}
	}

	private void onDoneSpeaking() {
		// animate speaking
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				Toast.makeText(MainActivity.this, "onDoneSpeaking", Toast.LENGTH_SHORT).show();
				teacherAnimation.stop();
				teacherAnimation.selectDrawable(0);
			}
		});

	}

	private void onErrorSpeaking() {
		// animate speaking
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				teacherAnimation.stop();
				teacherAnimation.selectDrawable(0);
			}
		});
	}

	private void onStartSpeaking() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// animate speaking
				teacherAnimation.start();
			}
		});
	}

	/******************************************************************************************/
	/** Cuurent attributes **************************************************************************/
	/******************************************************************************************/
	/**
	 * The EditText to speack
	 */
	private EditText edt_text;
	/**
	 * The speack button
	 */
	private Button btn_speech;
	/**
	 * The drawabler that animate when the teacher speaks
	 */
	private AnimationDrawable teacherAnimation;
	/**
	 * The TextView Result
	 */
	private ListView lsvMessage;
	/**
	 * The arrayAdapter of the list view (in a way its model)
	 */
	private TextArrayAdapter arrayAdapter;
	/**
	 * The list of objects disaplyed by the listView
	 */
	private ArrayList<String> messages;

	/******************************************************************************************/
	/** Managing life cycle **************************************************************************/
	/******************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("TTS_MainActivity", "onCreate called");
		setContentView(R.layout.activity_main);
		edt_text = (EditText) findViewById(R.id.edt_text);
		btn_speech = (Button) findViewById(R.id.btn_speech);
		teacherAnimation = (AnimationDrawable) btn_speech.getBackground();
		btn_speech.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addItem();
				speak();
				clearEdt();
			}
		});
		messages = new ArrayList<String>();
		// reload the data
		SharedPreferences prefs = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
		int wordsdListLenght = prefs.getInt(this.MY_PREFS_WordsLenght, 0);
		for (int i = 0; i < wordsdListLenght; i++) {
			if (prefs.contains(MY_PREFS_WordNum + i)) {
				messages.add(prefs.getString(MY_PREFS_WordNum + i, "A problem occurs"));
			}
		}
		lsvMessage = (ListView) findViewById(R.id.lsvMessages);
		lsvMessage.setItemsCanFocus(true);
		arrayAdapter = new TextArrayAdapter(this, messages);
		lsvMessage.setAdapter(arrayAdapter);
		lsvMessage.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// do the same as when the string is selected
				speak(position);
			}
		});

	}

	/**
	 * The item at the position position has been clicked by the user
	 * 
	 * @param position
	 */
	private void itemSelected(int position) {
		// copy the value of the item in the editText
		edt_text.setText(arrayAdapter.getItem(position));
		edt_text.setSelection(edt_text.length());
		speak();
	}

	/**
	 * Add the item to the list
	 */
	private void addItem() {
		String messageToAdd = edt_text.getText().toString();
		if (!messages.contains(messageToAdd)) {
			arrayAdapter.add(messageToAdd);
		}
	}

	private void clearEdt() {
		edt_text.setText("");
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.e("TTS_MainActivity", "onStart called");
		super.onStart();
		initTextToSpeech();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.e("TTS_MainActivity", "onResume called");
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.e("TTS_MainActivity", "onPause called");
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.e("TTS_MainActivity", "onStop called");
		// reload the data
		SharedPreferences prefs = getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(this.MY_PREFS_WordsLenght, messages.size());
		for (int i = 0; i < messages.size(); i++) {
			edit.putString(MY_PREFS_WordNum + i, messages.get(i));
		}
		edit.commit();
		tts.shutdown();
		super.onStop();
	}

	/******************************************************************************************/
	/** Managing menu **************************************************************************/
	/******************************************************************************************/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
