package uem.dam.meraki.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uem.dam.meraki.R;
import uem.dam.meraki.model.Tienda;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> implements View.OnClickListener{

    private ArrayList<Tienda> favoritosList;
    View.OnClickListener listener;

    public FavoritosAdapter(ArrayList<Tienda> favoritosList) {
        this.favoritosList = favoritosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favoritos_item, viewGroup, false);
        view.setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Tienda tienda = favoritosList.get(position);

        viewHolder.tvNombreT.setText(tienda.getNombre());
        viewHolder.tvTipoT.setText("Tienda de " + tienda.getTienda());
        viewHolder.tvTelefonoT.setText(tienda.getTelefono());
        viewHolder.tvDireccionT.setText(tienda.getDireccion());

    }

    @Override
    public int getItemCount() {
        return favoritosList.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public class  ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNombreT;
        private TextView tvTipoT;
        private TextView tvTelefonoT;
        private TextView tvDireccionT;

        public ViewHolder(View view) {
            super(view);

            this.tvNombreT = view.findViewById(R.id.tvNombreT);
            this.tvTipoT = view.findViewById(R.id.tvTipoT);
            this.tvTelefonoT = view.findViewById(R.id.tvTelefonoT);
            this.tvDireccionT = view.findViewById(R.id.tvDireccionT);
        }

    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

}
