/**<ul>
 * <li>TextToSpeechToBasile</li>
 * <li>com.android2ee.child.app.texttospeech.tobasile.adapter</li>
 * <li>20 avr. 2014</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.child.app.texttospeech.tobasile.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android2ee.child.app.texttospeech.tobasile.R;
import com.android2ee.child.app.texttospeech.tobasile.SpeakerIntf;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to:
 *        <ul>
 *        <li></li>
 *        </ul>
 */
public class TextArrayAdapter extends ArrayAdapter<String> {
	/******************************************************************************************/
	/** Attributes **************************************************************************/
	/******************************************************************************************/

	/**
	 * The inflater
	 */
	LayoutInflater inflater;
	/**
	 * The picture for odd elements
	 */
	Drawable playIcon;
	/**
	 * To know if the device has a postJellyBean os version
	 */
	boolean postJellyBean;
	/**
	 * The speaker instanciate by the activity
	 */
	SpeakerIntf speaker;
	/******************************************************************************************/
	/** Constructors **************************************************************************/
	/******************************************************************************************/

	/**
	 * @param context
	 * @param resource
	 */
	public TextArrayAdapter(Context context, ArrayList<String> data) {
		super(context, R.layout.item, data);
		inflater = LayoutInflater.from(context);
		playIcon = context.getResources().getDrawable(R.drawable.ic_play_selector);
		postJellyBean=context.getResources().getBoolean(R.bool.postJellyBean);
		speaker=(SpeakerIntf)context;
	} 

	/******************************************************************************************/
	/** GetView **************************************************************************/
	/******************************************************************************************/
	/**
	 * 
	 */
	private String messageTemp;
	/**
	 * 
	 */
	private View rowView;
	/**
	 * 
	 */
	private ViewHolder viewHolder;

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("TextArrayAdapter", "getView called on "+position+" with "+getItem(position));
		messageTemp = getItem(position);
		rowView = convertView;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.item, parent, false);
			viewHolder = new ViewHolder(rowView);
			rowView.setTag(viewHolder);
		}
		viewHolder=(ViewHolder) rowView.getTag();
		Log.e("TextArrayAdapter", "getView called on "+position+" with "+messageTemp);
		viewHolder.getTxvMessage().setText(messageTemp);

		//then add a clicklistener
		final int pos=position;
		viewHolder.getImvPicture().setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				speaker.speak(pos);
			}
		});
		viewHolder.getImvDelete().setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				speaker.deleteItem(pos);
			}
		});
		viewHolder.getTxvMessage().setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				speaker.copyItem(pos);
			}
		});
		return rowView;
	}

	/******************************************************************************************/
	/** ViewHolder **************************************************************************/
	/******************************************************************************************/
	private class ViewHolder {
		View rowView;
		TextView txvMessage;
		ImageView imvPlay;
		ImageView imvDelete;

		

		/**
		 * @param rowView
		 */
		public ViewHolder(View rowView) {
			super();
			this.rowView = rowView;
		}

		/**
		 * @return the txvName
		 */
		public final TextView getTxvMessage() {
			if (null == txvMessage) {
				txvMessage = ((TextView) rowView.findViewById(R.id.txv_message));
			}
			return txvMessage;
		}

		/**
		 * @return the imvPicture
		 */
		public final ImageView getImvPicture() {
			if (null == imvPlay) {
				imvPlay = ((ImageView) rowView.findViewById(R.id.imv_Image));
			}
			return imvPlay;
		}
		/**
		 * @return
		 */
		public final ImageView getImvDelete() {
			if (null == imvDelete) {
				imvDelete = ((ImageView) rowView.findViewById(R.id.imv_delete));
			}
			return imvDelete;
		}
	}
}
