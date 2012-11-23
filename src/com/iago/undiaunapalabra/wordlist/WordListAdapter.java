package com.iago.undiaunapalabra.wordlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.iago.undiaunapalabra.R;

public class WordListAdapter extends ArrayAdapter<WordAdapter>{
	private Context context;
	List<WordAdapter> words;
	List<WordAdapter> original;
	List<WordAdapter> fitems;
	int textViewResourceId;
	private Filter filter;


	public WordListAdapter(Context context, int textViewResourceId, List<WordAdapter> words) {
		super(context, textViewResourceId, words);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.words = words;
		this.original = new ArrayList<WordAdapter>(words);
		this.fitems = new ArrayList<WordAdapter>(words);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		WordHolder holder = null;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(textViewResourceId, parent, false);

			holder = new WordHolder();
			holder.ratingBar = (RatingBar)row.findViewById(R.id.ratingBar);
			holder.word = (TextView)row.findViewById(R.id.textWord);
			holder.date = (TextView)row.findViewById(R.id.textDate);
			holder.comments = (TextView)row.findViewById(R.id.textComments);
			row.setTag(holder);
		}
		else
		{
			holder = (WordHolder)row.getTag();
		}

		WordAdapter word = words.get(position);
		holder.ratingBar.setRating(word.getRating());
		holder.word.setText(word.getWord());
		holder.date.setText(word.getDate());
		holder.comments.setText(String.valueOf(word.getComments()));
		return row;
	}

	static class WordHolder
	{
		TextView word;
		TextView date;
		RatingBar ratingBar;
		TextView comments;
	}

	@Override
	public Filter getFilter()
	{
		if (filter == null)
			filter = new WordFilter();

		return filter;
	}

	private class WordFilter extends Filter
	{
		@Override
		protected FilterResults performFiltering(CharSequence constraint)
		{   
			FilterResults results = new FilterResults();
			String prefix = constraint.toString().toLowerCase();

			if (prefix == null || prefix.length() == 0)
			{
				List<WordAdapter> list = new ArrayList<WordAdapter>(original);
				results.values = list;
				results.count = list.size();
			}
			else
			{
				final List<WordAdapter> list = new ArrayList<WordAdapter>(original);
				final List<WordAdapter> nlist = new ArrayList<WordAdapter>();
				int count = list.size();

				for (int i=0; i<count; i++)
				{
					final WordAdapter item = list.get(i);
					final String value = item.getWord().toLowerCase();

					if (value.startsWith(prefix))
					{
						nlist.add(item);
					}
				}
				results.values = nlist;
				results.count = nlist.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			fitems = (ArrayList<WordAdapter>)results.values;

			clear();
			int count = fitems.size();
			for (int i=0; i<count; i++)
			{
				WordAdapter item = (WordAdapter)fitems.get(i);
				add(item);
			}
		}

	}

}
