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
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_CONTATOS = "contatos";
    private static final String TABLE_TELEFONES = "telefones";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    private static final String COLUMN_PHONE_ID = "phone_id";
    private static final String COLUMN_CONTACT_ID = "contact_id";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_PHONE_TYPE = "phone_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTATOS_TABLE = "CREATE TABLE " + TABLE_CONTATOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT" + ")";

        String CREATE_TELEFONES_TABLE = "CREATE TABLE " + TABLE_TELEFONES + "("
                + COLUMN_PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CONTACT_ID + " INTEGER,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
                + COLUMN_PHONE_TYPE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_CONTACT_ID + ") REFERENCES " + TABLE_CONTATOS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";

        db.execSQL(CREATE_CONTATOS_TABLE);
        db.execSQL(CREATE_TELEFONES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TELEFONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTATOS);
        onCreate(db);
    }

    // Método para adicionar um novo contato com múltiplos telefones
    public void addContato(Contato contato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contato.getName());
        long contactId = db.insert(TABLE_CONTATOS, null, values);

        if (contactId != -1) {
            for (Telefone telefone : contato.getTelefone()) {
                ContentValues phoneValues = new ContentValues();
                phoneValues.put(COLUMN_CONTACT_ID, contactId);
                phoneValues.put(COLUMN_PHONE_NUMBER, telefone.getNumber());
                phoneValues.put(COLUMN_PHONE_TYPE, telefone.getType());
                db.insert(TABLE_TELEFONES, null, phoneValues);
            }
        }
        db.close();
    }

    // Método para atualizar um contato existente
    public int updateContato(Contato contato) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contato.getName());
        int rowsUpdated = db.update(TABLE_CONTATOS, values, COLUMN_ID + " = ?", new String[]{contato.getId()});

        // Remove todos os telefones atuais do contato e insere os novos
        db.delete(TABLE_TELEFONES, COLUMN_CONTACT_ID + " = ?", new String[]{contato.getId()});
        for (Telefone telefone : contato.getTelefone()) {
            ContentValues phoneValues = new ContentValues();
            phoneValues.put(COLUMN_CONTACT_ID, contato.getId());
            phoneValues.put(COLUMN_PHONE_NUMBER, telefone.getNumber());
            phoneValues.put(COLUMN_PHONE_TYPE, telefone.getType());
            db.insert(TABLE_TELEFONES, null, phoneValues);
        }

        db.close();
        return rowsUpdated;
    }

    // Método para buscar um contato pelo ID, incluindo seus telefones
    public Contato getContato(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTATOS, new String[]{COLUMN_ID, COLUMN_NAME},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        Contato contato = null;
        if (cursor != null && cursor.moveToFirst()) {
            contato = new Contato();
            contato.setId(cursor.getString(0));
            contato.setName(cursor.getString(1));

            List<Telefone> telefones = new ArrayList<>();
            Cursor phoneCursor = db.query(TABLE_TELEFONES, new String[]{COLUMN_PHONE_NUMBER, COLUMN_PHONE_TYPE},
                    COLUMN_CONTACT_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

            if (phoneCursor.moveToFirst()) {
                do {
                    telefones.add(new Telefone(phoneCursor.getString(0), phoneCursor.getString(1)));
                } while (phoneCursor.moveToNext());
            }

            contato.setTelefone(telefones);
            phoneCursor.close();
        }

        cursor.close();
        db.close();
        return contato;
    }

    // Método para obter todos os contatos com todos os seus telefones
    public List<Contato> getAllContatos() {
        List<Contato> listaContatos = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CONTATOS + " ORDER BY " + COLUMN_NAME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Contato contato = new Contato();
                contato.setId(cursor.getString(0));
                contato.setName(cursor.getString(1));

                List<Telefone> telefones = new ArrayList<>();
                Cursor phoneCursor = db.query(TABLE_TELEFONES, new String[]{COLUMN_PHONE_NUMBER, COLUMN_PHONE_TYPE},
                        COLUMN_CONTACT_ID + "=?", new String[]{contato.getId()}, null, null, null);

                if (phoneCursor.moveToFirst()) {
                    do {
                        telefones.add(new Telefone(phoneCursor.getString(0), phoneCursor.getString(1)));
                    } while (phoneCursor.moveToNext());
                }

                contato.setTelefone(telefones);
                phoneCursor.close();
                listaContatos.add(contato);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaContatos;
    }
    // Método para excluir um contato pelo ID
    public void deleteContato(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTATOS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
