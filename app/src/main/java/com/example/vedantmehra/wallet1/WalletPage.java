package com.example.vedantmehra.wallet1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletPage extends AppCompatActivity {


    // Amount entered and database initialised.
    private Button addMoney, sendMoney, currentBalanceButton;
    private EditText amount, userN;
    private DatabaseReference databaseInvestor, databaseStudent;
    private TextView currentBalanceText;
    // user id of current user
    String id, userName, studentId;
    Task<Integer> tsk;
    // value entered by the user
    Integer val;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(FirebaseAuth.getInstance() != null && FirebaseAuth.getInstance().getCurrentUser() != null)
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        else
            return;


        addMoney = (Button)findViewById(R.id.add_money_btn);
        sendMoney = (Button)findViewById(R.id.send_money_btn);
        amount = (EditText)findViewById(R.id.amt);
        userN = (EditText)findViewById(R.id.userName);
        databaseInvestor = FirebaseDatabase.getInstance().getReference("Investor");
        databaseStudent = FirebaseDatabase.getInstance().getReference("Student");
        currentBalanceButton = (Button)findViewById(R.id.currentBalBtn);
        currentBalanceText = (TextView)findViewById(R.id.currentBalText);
        flag = true;
        currentBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseInvestor.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("money").exists()){
                            String s = dataSnapshot.child("money").getValue().toString();
                            currentBalanceText.setText("Balance " + s);
                        }else{
                            currentBalanceText.setText("Balance 0");
                        }
                        for (int i = 0 ; i < 10000000 ; i++){
                            ;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountToAdd = (amount.getText().toString());
                String emailEntered = userN.getText().toString();
                if(!TextUtils.isEmpty(emailEntered)){
                    Toast.makeText(WalletPage.this, "Only enter the money field", Toast.LENGTH_LONG).show();
                    return;
                }
                userN.setText("");
                amount.setText("");
                if(!TextUtils.isEmpty(amountToAdd)){
                    Toast.makeText(WalletPage.this, "Money added", Toast.LENGTH_LONG).show();
                    val = Integer.parseInt(amountToAdd);
                    databaseInvestor.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("money").exists())
                            {
                                Integer value = dataSnapshot.child("money").getValue(Integer.class);
                                val += value;
                                databaseInvestor.child(id).child("money").setValue(val);
                            }else {
                                databaseInvestor.child(id).child("money").setValue(val);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                userName = userN.getText().toString();
                userN.setText("");
                String amountToSend = amount.getText().toString();
                amount.setText("");
                if(!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(amountToSend)){
                    val = Integer.parseInt(amountToSend);
                    databaseInvestor.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.child("money").exists()){
                                Toast.makeText(WalletPage.this, "Insufficient Balance", Toast.LENGTH_LONG).show();
                                return;
                            }
                            Integer value = dataSnapshot.child("money").getValue(Integer.class);
                            if(val > value){
                                Toast.makeText(WalletPage.this, "Insufficient Balance", Toast.LENGTH_LONG).show();
                                return;
                            }

                            int cnt = (int) dataSnapshot.child("relations").getChildrenCount();
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.child("relations").getChildren()){
                                studentId = dataSnapshot1.getValue().toString();
                                Log.d("WalletPage", "here1 " + studentId);
                                updateStudentBalance();
                                cnt--;
                            }

                            Log.d("WalletPage", "flag after all iterations " + flag);
                            /*if(flag){
                                Log.d("WalletPage", "value of flag " + flag);
                                Toast.makeText(WalletPage.this, "No user found", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(WalletPage.this, "Transaction successful", Toast.LENGTH_LONG).show();
                                Log.d("WalletPage", "value of flag " + flag);
                                int currentInvestorBalance = Integer.parseInt(dataSnapshot.child("money").getValue().toString());
                                databaseInvestor.child(id).child("money").setValue(currentInvestorBalance - val);
                            }*/

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(WalletPage.this, "Enter amount and username", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    void updateSenderBalance(){
        databaseInvestor.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("WalletPage", "here in update Sender balance " + id);
                Toast.makeText(WalletPage.this, "Transaction successful", Toast.LENGTH_LONG).show();
                Log.d("WalletPage", "value of flag " + flag);
                int currentInvestorBalance = Integer.parseInt(dataSnapshot.child("money").getValue().toString());
                databaseInvestor.child(id).child("money").setValue(currentInvestorBalance - val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void updateStudentBalance(){
        Log.d("WalletPage", "here2 " + studentId);
        databaseStudent.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("WalletPage", "user name " + userName);
                Log.d("WalletPage", "child name " + dataSnapshot.child("email").getValue().toString());
                String v = dataSnapshot.child("email").getValue().toString();
                if(new String(v).equals(userName)) {
                    Log.d("WalletPage", "here3 " + studentId);
                    flag = false;
                }else{
                    return;
                }
                updateSenderBalance();
                if(!dataSnapshot.child("money").exists()){
                    databaseStudent.child(studentId).child("money").setValue(val);
                    //Toast.makeText(WalletPage.this, "Transaction successful", Toast.LENGTH_LONG).show();
                }else{
                    int currentStudentBalance = Integer.parseInt(dataSnapshot.child("money").getValue().toString());
                    databaseStudent.child(studentId).child("money").setValue(currentStudentBalance + val);
                    //Toast.makeText(WalletPage.this, "Transaction successful", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance() != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (FirebaseAuth.getInstance() == null || user == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
