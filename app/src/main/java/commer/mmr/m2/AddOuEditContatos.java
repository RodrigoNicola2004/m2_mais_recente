package commer.mmr.m2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AddOuEditContatos extends AppCompatActivity {

    private EditText editTextName, editTextPhoneNumber;
    private Spinner spinnerPhoneType;
    private Button buttonSaveContact;
    private DatabaseHelper databaseHelper;
    private String id;
    private Contato contatoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ou_edit_contatos);

        // Inicialização dos componentes
        editTextName = findViewById(R.id.editTextName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        spinnerPhoneType = findViewById(R.id.spinnerPhoneType);
        buttonSaveContact = findViewById(R.id.buttonSaveContact);
        databaseHelper = new DatabaseHelper(this);

        // Verifica se foi passado um ID para editar um contato
        id = getIntent().getStringExtra("id");

        if (id != null) {
            carregarContato();
        }
        buttonSaveContact.setOnClickListener(v -> salvarContato());
    }

    private void carregarContato() {
        // converter string p/ int
        contatoAtual = databaseHelper.getContato(Integer.parseInt(id));

        if (contatoAtual != null) {
            editTextName.setText(contatoAtual.getName());

            if (contatoAtual.getTelefone() != null && !contatoAtual.getTelefone().isEmpty()) {
                editTextPhoneNumber.setText(contatoAtual.getTelefone().get(0).getNumber());
            }
        }
    }

    private void salvarContato() {
        String name = editTextName.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();
        String phoneType = null;

        // Verifica se o Spinner está selecionado e não nulo
        if (spinnerPhoneType.getSelectedItem() != null) {
            phoneType = spinnerPhoneType.getSelectedItem().toString();
        }

        // Verifica se todos os campos foram preenchidos
        if (phoneType != null && !name.isEmpty() && !phoneNumber.isEmpty()) {
            // Criar uma lista de telefones
            List<Telefone> telefones = new ArrayList<>();
            telefones.add(new Telefone(phoneNumber, phoneType));

            // Se o ctt existe
            if (contatoAtual != null) {
                contatoAtual.setName(name);
                contatoAtual.setTelefone(telefones);
                databaseHelper.updateContato(contatoAtual);
                Toast.makeText(this, "Contato atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                // Criar um novo contato
                Contato contato = new Contato();
                contato.setName(name);
                contato.setTelefone(telefones);

                // Armazenar no SQLite
                databaseHelper.addContato(contato);
                Toast.makeText(this, "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }
            finish(); // Voltar para a lista de contatos
        } else {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
        }
    }
}
