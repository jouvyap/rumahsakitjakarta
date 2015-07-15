/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package bravostudio.rumahsakitjakarta;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static ArrayList<Entity> mEntities;

    private static Context mContext;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textNoView;
        private final TextView textNamaView;
        private final TextView textJenisView;
        private final TextView textKotaView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetilActivity.class);
                    intent.putExtra("entities", mEntities);
                    intent.putExtra("position", getPosition());
                    v.getContext().startActivity(intent);
                }
            });

            textNoView = (TextView) v.findViewById(R.id.noTeks);
            textNamaView = (TextView) v.findViewById(R.id.namaTeks);
            textJenisView = (TextView) v.findViewById(R.id.jenisTeks);
            textKotaView = (TextView) v.findViewById(R.id.kotaTeks);
        }

        public TextView getTextNoView() {
            return textNoView;
        }
        public TextView getTextNamaView() {
            return textNamaView;
        }
        public TextView getTextJenisView() {
            return textJenisView;
        }
        public TextView getTextKotaView() {
            return textKotaView;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param entities List<Entity> containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(ArrayList<Entity> entities, Context context) {
        mEntities = entities;
        mContext = context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.hasil_view, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextNoView().setText(position + 1 + "");
        viewHolder.getTextNamaView().setText(mEntities.get(position).nama);
        viewHolder.getTextJenisView().setText(mEntities.get(position).jenis);
        viewHolder.getTextKotaView().setText(mEntities.get(position).kota);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEntities.size();
    }
}
