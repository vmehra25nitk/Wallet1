package com.example.vedantmehra.wallet1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SendMoney extends AppCompatActivity {

    private DatabaseReference databaseReferenceStudent, databaseReferenceInvestor;
    String id;

    private ListView listView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private Button confirm;
    private EditText userN;
    Integer val;
    String user;
    String amountToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null)
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            return;
        amountToSend = getIntent().getExtras().getString("moneyToSend");
        databaseReferenceInvestor = FirebaseDatabase.getInstance().getReference("Investor");
        databaseReferenceStudent = FirebaseDatabase.getInstance().getReference("Student");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        confirm = (Button)findViewById(R.id.confirm);
        userN = (EditText)findViewById(R.id.userName);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = userN.getText().toString();
                userN.setText("");
                if(!TextUtils.isEmpty(user)){
                    databaseReferenceInvestor.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean flag = true;
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                if(dataSnapshot1.child("name").getValue().toString() == user){
                                    flag = false;
                                    int value = dataSnapshot1.child("money").getValue(Integer.class);
                                    value += val;
                                    String k = dataSnapshot1.getKey();
                                    // increment other account
                                    databaseReferenceInvestor.child(k).child("money").setValue(value);

                                    // decrement this account
                                    value = dataSnapshot.child(id).child("money").getValue(Integer.class);
                                    databaseReferenceInvestor.child(id).child("money").setValue(value - val);
                                    Toast.makeText(SendMoney.this, "Transaction successful", Toast.LENGTH_SHORT);
                                }
                            }
                            if(flag){
                                Toast.makeText(SendMoney.this, "No user found", Toast.LENGTH_SHORT);
                            }
                            Intent intent = new Intent(SendMoney.this, WalletPage.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

    }

    /*void readData(){
        databaseReferenceInvestor.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
