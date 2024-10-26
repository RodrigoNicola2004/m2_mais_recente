package commer.mmr.m2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddOuEditContatos extends AppCompatActivity {

    private EditText editTextName;
    private LinearLayout phoneContainer;
    private Button buttonSaveContact, buttonAddPhoneNumber, buttonDeleteContact;
    private DatabaseHelper databaseHelper;
    private String id;
    private Contato contatoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ou_edit_contatos);

        // Inicialização dos componentes
        editTextName = findViewById(R.id.editTextName);
        phoneContainer = findViewById(R.id.phoneContainer);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);
        buttonAddPhoneNumber = findViewById(R.id.buttonAddPhoneNumber);
        buttonDeleteContact = findViewById(R.id.buttonDeleteContact);
        databaseHelper = new DatabaseHelper(this);

        // Verifica se foi passado um ID para editar um contato
        id = getIntent().getStringExtra("id");

        if (id != null) {
            carregarContato();
            buttonDeleteContact.setVisibility(View.VISIBLE); // Exibe o botão de exclusão se estiver editando
            buttonDeleteContact.setOnClickListener(v -> excluirContato()); // Configura o clique no botão de exclusão
        } else {
            buttonDeleteContact.setVisibility(View.GONE); // Oculta o botão se for um novo contato
        }

        buttonSaveContact.setOnClickListener(v -> salvarContato());

        // Botão para adicionar novos campos de telefone
        buttonAddPhoneNumber.setOnClickListener(v -> adicionarCampoTelefone(null, null));
    }

    // Método para add campos de telefone
    private void adicionarCampoTelefone(String numero, String tipo) {
        View phoneView = LayoutInflater.from(this).inflate(R.layout.item_telefone, phoneContainer, false);

        EditText editTextPhoneNumber = phoneView.findViewById(R.id.editTextPhoneNumber);
        Spinner spinnerPhoneType = phoneView.findViewById(R.id.spinnerPhoneType);

        if (numero != null) {
            editTextPhoneNumber.setText(numero);
        }

        if (tipo != null) {
            int spinnerPosition = ((ArrayAdapter<String>) spinnerPhoneType.getAdapter()).getPosition(tipo);
            spinnerPhoneType.setSelection(spinnerPosition);
        }

        phoneContainer.addView(phoneView);
    }

    private void carregarContato() {
        // Converte string para int e carrega o contato do banco
        contatoAtual = databaseHelper.getContato(Integer.parseInt(id));

        if (contatoAtual != null) {
            editTextName.setText(contatoAtual.getName());

            if (contatoAtual.getTelefone() != null && !contatoAtual.getTelefone().isEmpty()) {
                // Para cada telefone do contato, adiciona um campo de telefone
                for (Telefone telefone : contatoAtual.getTelefone()) {
                    adicionarCampoTelefone(telefone.getNumber(), telefone.getType());
                }
            }
        }
    }

    private void salvarContato() {
        String name = editTextName.getText().toString();

        List<Telefone> telefones = new ArrayList<>();

        // Itera por todos os campos de telefone adicionados
        for (int i = 0; i < phoneContainer.getChildCount(); i++) {
            View phoneView = phoneContainer.getChildAt(i);

            EditText editTextPhoneNumber = phoneView.findViewById(R.id.editTextPhoneNumber);
            Spinner spinnerPhoneType = phoneView.findViewById(R.id.spinnerPhoneType);

            String phoneNumber = editTextPhoneNumber.getText().toString();
            String phoneType = spinnerPhoneType.getSelectedItem().toString();

            if (!phoneNumber.isEmpty()) {
                telefones.add(new Telefone(phoneNumber, phoneType));
            }
        }

        // Verifica se ao menos um telefone foi adicionado
        if (!telefones.isEmpty() && !name.isEmpty()) {
            if (contatoAtual != null) {
                // Atualiza o contato existente
                contatoAtual.setName(name);
                contatoAtual.setTelefone(telefones);
                databaseHelper.updateContato(contatoAtual);
                Toast.makeText(this, "Contato atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                // Cria um novo contato
                Contato contato = new Contato();
                contato.setName(name);
                contato.setTelefone(telefones);

                // Armazena no banco de dados
                databaseHelper.addContato(contato);
                Toast.makeText(this, "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }
            finish(); // Volta para a lista de contatos
        } else {
            Toast.makeText(this, "Por favor, preencha o nome e pelo menos um telefone.", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirContato() {
        if (contatoAtual != null) { // Verifica se existe um contato para excluir
            databaseHelper .deleteContato(Integer.parseInt(contatoAtual.getId())); // Chama o método para excluir no banco
            Toast.makeText(this, "Contato excluído com sucesso!", Toast.LENGTH_SHORT).show();
            finish(); // Fecha a tela atual e volta para a lista de contatos
        } else {
            Toast.makeText(this, "Erro ao excluir o contato.", Toast.LENGTH_SHORT).show();
        }
    }
}
