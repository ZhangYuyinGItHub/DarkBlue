package com.zhangyy.bluetooth.le;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupListAdapter extends ArrayAdapter<String> {

	private List<String> listTag = null;
	private List<String> mProperties = null;

	public GroupListAdapter(Context context, List<String> objects,
			List<String> tags, List<String> property) {
		super(context, 0, objects);
		this.listTag = tags;
		this.mProperties = property;
	}

	@Override
	public boolean isEnabled(int position) {
		if (listTag.contains(getItem(position))) {
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = convertView;
		int state = 0x0;

		if (listTag.contains(getItem(position))) {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.group_list_item_tag, null);

			state = 0x04;

		} else {
			view = LayoutInflater.from(getContext()).inflate(
					R.layout.group_list_item, null);

			TextView properties = (TextView) view.findViewById(R.id.property);
			properties.setText(mProperties.get(position));
			
			/*set characteristic launcher*/
			if ((mProperties.get(position).length() > 13)
					&& (mProperties.get(position).substring(7, 12)
							.equals("voice"))) {
				ImageView img = (ImageView) view
						.findViewById(R.id.characteritic_launcher);
				img.setImageResource(R.drawable.ic_launcher_voice);
			} else {
				ImageView img = (ImageView) view
						.findViewById(R.id.characteritic_launcher);
				img.setImageResource(R.drawable.ic_launcher);
			}
		}

		if ((position == 0) || (listTag.contains(getItem(position - 1)))) {
			state = state | 0x01;
		}
		if ((position == getCount() - 1)
				|| (listTag.contains(getItem(position + 1)))) {
			state = state | 0x02;
		}

		switch (state) {
		case 0x0:
			view.setBackgroundResource(R.drawable.no_round);
			break;
		case 0x01:
			view.setBackgroundResource(R.drawable.top_round);
			break;
		case 0x02:
			view.setBackgroundResource(R.drawable.bottom_round);
			break;
		case 0x03:
			view.setBackgroundResource(R.drawable.bottom_and_top_round);
			break;

		default:

			break;
		}

		TextView textView = (TextView) view
				.findViewById(R.id.group_list_item_text);
		textView.setText(getItem(position));

		// textView.setBackgroundResource(R.drawable.bottom_and_top_round);

		return view;
	}
}
