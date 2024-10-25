package commer.mmr.m2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contatos.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_CONTATOS = "contatos";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_PHONE_TYPE = "phone_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTATOS_TABLE = "CREATE TABLE " + TABLE_CONTATOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
                + COLUMN_PHONE_TYPE + " TEXT" + ")";
        db.execSQL(CREATE_CONTATOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Apaga a tabela antiga e recria com a nova estrutura
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTATOS);
        onCreate(db);
    }

    // Método para adicionar um novo contato
    public void addContato(Contato contato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contato.getName());

        // Pegando o primeiro telefone da lista (ajuste se necessário)
        if (contato.getTelefone() != null && !contato.getTelefone().isEmpty()) {
            values.put(COLUMN_PHONE_NUMBER, contato.getTelefone().get(0).getNumber());
            values.put(COLUMN_PHONE_TYPE, contato.getTelefone().get(0).getType());
        }

        db.insert(TABLE_CONTATOS, null, values);
        db.close();
    }

    // Método para buscar um contato pelo ID
    public Contato getContato(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTATOS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_PHONE_NUMBER, COLUMN_PHONE_TYPE},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Contato contato = new Contato();
        contato.setId(cursor.getString(0));
        contato.setName(cursor.getString(1));

        List<Telefone> telefones = new ArrayList<>();
        telefones.add(new Telefone(cursor.getString(2), cursor.getString(3)));
        contato.setTelefone(telefones);

        cursor.close();
        db.close();

        return contato;
    }

    // Método para obter todos os contatos
    public List<Contato> getAllContatos() {
        List<Contato> listaContatos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTATOS + " ORDER BY " + COLUMN_NAME + " ASC"; // Adicionando ORDER BY

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Contato contato = new Contato();
                contato.setId(cursor.getString(0));
                contato.setName(cursor.getString(1));

                List<Telefone> telefones = new ArrayList<>();
                telefones.add(new Telefone(cursor.getString(2), cursor.getString(3)));
                contato.setTelefone(telefones);

                listaContatos.add(contato);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaContatos;
    }

    // Método para atualizar um contato existente
    public int updateContato(Contato contato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contato.getName());

        if (contato.getTelefone() != null && !contato.getTelefone().isEmpty()) {
            values.put(COLUMN_PHONE_NUMBER, contato.getTelefone().get(0).getNumber());
            values.put(COLUMN_PHONE_TYPE, contato.getTelefone().get(0).getType());
        }

        return db.update(TABLE_CONTATOS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(contato.getId())});
    }
}
