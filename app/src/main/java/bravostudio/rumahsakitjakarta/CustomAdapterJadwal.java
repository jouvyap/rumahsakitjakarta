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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapterJadwal extends RecyclerView.Adapter<CustomAdapterJadwal.ViewHolder> {
    private static ArrayList<JadwalEntity> mEntities;

    private static Context mContext;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textNama;
        private final TextView textSenin;
        private final TextView textSelasa;
        private final TextView textRabu;
        private final TextView textKamis;
        private final TextView textJumat;
        private final TextView textSabtu;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Posisi", "" + getPosition());
                }
            });

            textNama = (TextView) v.findViewById(R.id.namaDokter);
            textSenin = (TextView) v.findViewById(R.id.jadwalSenin);
            textSelasa = (TextView) v.findViewById(R.id.jadwalSelasa);
            textRabu = (TextView) v.findViewById(R.id.jadwalRabu);
            textKamis = (TextView) v.findViewById(R.id.jadwalKamis);
            textJumat = (TextView) v.findViewById(R.id.jadwalJumat);
            textSabtu = (TextView) v.findViewById(R.id.jadwalSabtu);
        }

        public TextView getTextNama() {
            return textNama;
        }
        public TextView getTextSenin() {
            return textSenin;
        }
        public TextView getTextSelasa() {
            return textSelasa;
        }
        public TextView getTextRabu() {
            return textRabu;
        }
        public TextView getTextKamis() {
            return textKamis;
        }
        public TextView getTextJumat() {
            return textJumat;
        }
        public TextView getTextSabtu() {
            return textSabtu;
        }
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param entities List<Entity> containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapterJadwal(ArrayList<JadwalEntity> entities, Context context) {
        mEntities = entities;
        mContext = context;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.jadwal_view, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextNama().setText(mEntities.get(position).nama);
        viewHolder.getTextSenin().setText("Senin: " + mEntities.get(position).senin);
        viewHolder.getTextSelasa().setText("Selasa: " + mEntities.get(position).selasa);
        viewHolder.getTextRabu().setText("Rabu: " + mEntities.get(position).rabu);
        viewHolder.getTextKamis().setText("Kamis: " + mEntities.get(position).kamis);
        viewHolder.getTextJumat().setText("Jumat: " + mEntities.get(position).jumat);
        viewHolder.getTextSabtu().setText("Sabtu: " + mEntities.get(position).sabtu);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEntities.size();
    }
}
