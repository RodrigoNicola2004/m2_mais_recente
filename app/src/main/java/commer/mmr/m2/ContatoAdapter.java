package commer.mmr.m2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ContatoViewHolder> {

    private List<Contato> listaContatos;
    private OnContatoClickListener listener;

    // Interface para cliques nos itens
    public interface OnContatoClickListener {
        void onContatoClick(Contato contato);
    }

    public ContatoAdapter(List<Contato> listaContatos, OnContatoClickListener listener) {
        this.listaContatos = listaContatos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContatoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contato, parent, false);
        return new ContatoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContatoViewHolder holder, int position) {
        Contato contato = listaContatos.get(position);
        holder.textViewName.setText(contato.getName());
        holder.textViewPhoneNumber.setText(contato.getTelefone().get(0).getNumber());

        // Configura o clique no item
        holder.itemView.setOnClickListener(v -> listener.onContatoClick(contato));
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public static class ContatoViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public TextView textViewPhoneNumber;

        public ContatoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName); // ID corrigido
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber); // ID corrigido
        }
    }
}
