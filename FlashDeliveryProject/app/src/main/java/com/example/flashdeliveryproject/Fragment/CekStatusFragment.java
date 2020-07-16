package com.example.flashdeliveryproject.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flashdeliveryproject.Dialog.CekStatusDialog;
import com.example.flashdeliveryproject.MainActivity;
import com.example.flashdeliveryproject.Model.DataPengiriman;
import com.example.flashdeliveryproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CekStatusFragment extends Fragment {

    private TextInputEditText txtKodePengiriman;
    private Button btnCekStatus;

    private FirebaseDatabase database;
    private DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cekstatus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference();

        txtKodePengiriman = view.findViewById(R.id.txtKodePengiriman);
        btnCekStatus = view.findViewById(R.id.btnCekStatus);

        btnCekStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String kode = txtKodePengiriman.getText().toString().toUpperCase();

                    if(kode.isEmpty() || kode.equals("")) {

                        txtKodePengiriman.setError("Kode pengiriman tidak boleh kosong");
                    }

                    else {

                        getDataFromFirebase(kode);
                    }


                    // Untuk hide keyboard setelah button di
                    // harus dalam try catch karena bisa saja button di klik pada saat keyboard tidak muncul
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

    }

    private void getDataFromFirebase(final String kode) {

        dbRef.child("data_pengiriman").child(kode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataPengiriman data = dataSnapshot.getValue(DataPengiriman.class);

                if(data == null) {

                    openDialog(kode, "Data Tidak Ditemukan");
                }

                else {

                    String status = data.getStatus_pengiriman();

                    openDialog(kode, status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Data Gagal Dimuat", Toast.LENGTH_LONG).show();
                Log.e("MyListActivity", databaseError.getDetails() + " " + databaseError.getMessage());
            }
        });
    }

    private void openDialog(String kode, String message) {
        CekStatusDialog dialog = new CekStatusDialog().newInstance(kode, message);

        dialog.show(getFragmentManager(), "Cek Status Dialog");

    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity())
                .setActionBarTitle("Cek Status Pengiriman");
    }
}
