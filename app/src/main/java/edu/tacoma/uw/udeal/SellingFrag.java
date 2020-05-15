package edu.tacoma.uw.udeal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.ItemDisplaySellingFrag;


/**
 * A simple {@link Fragment} subclass.
 */
public class SellingFrag extends Fragment {

    private List<ItemDisplaySellingFrag> mItemList;

    private RecyclerView mRecyclerView;

    public SellingFrag.SimpleItemRecyclerViewAdapter mAdapter;

    private static int LOAD_LIMIT = 3;

    private static int INITIAL_LOAD = 3;

    private int loadCount;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selling, container, false);

        loadCount = 0;

        mItemList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerView);
        assert mRecyclerView != null;
        initialRecyclerView((RecyclerView) mRecyclerView);
        SharedPreferences settings = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        int theID = settings.getInt(getString(R.string.member_id), 0);
        new SellingFrag.DisplayItemsAsyncTask().execute(getString(R.string.load_limited_myitems) + "?limit=" + INITIAL_LOAD + "&offset=" + 0  + "&theSellerID=" + theID);

        return view;
    }

    private void initialRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SellingFrag.SimpleItemRecyclerViewAdapter((CartActivity) getActivity(), mItemList);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("hello", "Last Item Wow !");
                            SharedPreferences settings = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                            int theID = settings.getInt(getString(R.string.member_id), 0);
                            new SellingFrag.DisplayItemsAsyncTask()
                                    .execute(getString(R.string.load_limited_myitems) + "?limit=" + LOAD_LIMIT + "&offset=" + totalItemCount + "&theSellerID=" + theID);
                            //  loading = true;
                        }
                    }
                }
            }
        });
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SellingFrag.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final CartActivity mParentActivity;
        private final List<ItemDisplaySellingFrag> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemDisplaySellingFrag item = (ItemDisplaySellingFrag) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDisplaySellingDetailActivity.class);
                intent.putExtra(ItemDisplaySellingDetailActivity.ARG_ITEM_ID, item);

                context.startActivity(intent);
            }
        };

        SimpleItemRecyclerViewAdapter(CartActivity parent,
                                      List<ItemDisplaySellingFrag> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public SellingFrag.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_item, parent, false);
            return new SellingFrag.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SellingFrag.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getMyTitle());
            //holder.mContentView.setText(mValues.get(position).getMyPrice() + "");
            holder.mImageView.setImageBitmap(mValues.get(position).getMyBitmap());
            if(mValues.get(position).getMyBitmap() != null) {
                holder.mImageView.setVisibility(holder.mImageView.VISIBLE);
                holder.mPBar.setVisibility(holder.mPBar.GONE);
            } else {
                holder.mImageView.setVisibility(holder.mImageView.GONE);
                holder.mPBar.setVisibility(holder.mPBar.VISIBLE);
            }
            final ItemDisplaySellingFrag temp = mValues.get(position);
            holder.mLikeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    temp.setMyLiked(!temp.getmyLiked());
                }
            });
            if(mValues.get(position).getmyLiked()) {
                holder.mLikeImage.setImageResource(R.drawable.heart_icon_pressed);

            } else {
                holder.mLikeImage.setImageResource(R.drawable.heart_icon);
            }
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            //final TextView mContentView;
            final ImageView mImageView;
            final ImageView mLikeImage;
            final ProgressBar mPBar;


            ViewHolder(View view) {
                super(view);
                mPBar = (ProgressBar) view.findViewById(R.id.pBar);
                mIdView = (TextView) view.findViewById(R.id.textViewTitle);
                //mContentView = (TextView) view.findViewById(R.id.textViewPrice);
                mImageView = (ImageView) view.findViewById(R.id.imageViewImage);
                mLikeImage = (ImageView) view.findViewById(R.id.heartImage);
            }
        }
    }

    private class DisplayItemsAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to get item information, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.startsWith("Unable to")) {
                Toast.makeText(getActivity(), "Unable to get items information: " + s, Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                if (jsonObject.getBoolean("success")) {
                    JSONArray myJSONArray = jsonObject.getJSONArray("names");
                    for(int i = 0; i < myJSONArray.length(); i++) {
                        int temp = mItemList.size();
                        if(getActivity() != null) {
                            Log.d("myLog", "onPostExecute: Success");
                            SharedPreferences settings = getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
                            int theID = settings.getInt(getString(R.string.member_id), 0);
                            mItemList.add(ItemDisplaySellingFrag.parseItemJson(myJSONArray.getJSONObject(i), temp, mAdapter, theID));
                            mAdapter.notifyItemInserted(mItemList.size() - 1);
                            loading = true;
                        } else {
                            Log.d("myLog", "onPostExecute: Get Activity is NULL");
                        }
                    }
                }
            } catch (JSONException e) {
                Log.d("myTag", "FAILURE");
                Toast.makeText(getActivity(), "JSON Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
