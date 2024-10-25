package commer.mmr.m2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListaContatos extends AppCompatActivity {

    private RecyclerView recyclerViewContatos;
    private DatabaseHelper databaseHelper;
    private List<Contato> listaContato;
    private Button buttonAddContact;
    private ContatoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contatos);

        recyclerViewContatos = findViewById(R.id.recyclerViewContatos);
        buttonAddContact = findViewById(R.id.buttonAddContact);
        databaseHelper = new DatabaseHelper(this);
        listaContato = new ArrayList<>();

        // Definir LayoutManager no RecyclerView
        recyclerViewContatos.setLayoutManager(new LinearLayoutManager(this));

        buttonAddContact.setOnClickListener(v -> {
            Intent intent = new Intent(ListaContatos.this, AddOuEditContatos.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarContato(); // Atualiza a lista de contatos sempre que a tela é exibida
    }

    private void carregarContato() {
        listaContato = databaseHelper.getAllContatos();

        adapter = new ContatoAdapter(listaContato, contato -> {
            Intent intent = new Intent(ListaContatos.this, AddOuEditContatos.class);
            intent.putExtra("id", contato.getId());
            startActivity(intent);
        });

        recyclerViewContatos.setAdapter(adapter);

        if (listaContato.isEmpty()) {
            Toast.makeText(this, "Nenhum contato encontrado.", Toast.LENGTH_SHORT).show();
        }
    }
}
