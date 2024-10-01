package com.PokeMeng.OldManGO;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.PokeMeng.OldManGO.Firstlogin.FacebookGoogle;

import java.util.ArrayList;
import java.util.List;

public class exchange_history extends AppCompatActivity {

    private ListView exchangeHistoryListView;
    private List<String> exchangeHistoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_history);

        exchangeHistoryListView = findViewById(R.id.exchangeHistoryListView);

        // 读取保存的兑换记录
        SharedPreferences sharedPreferences = getSharedPreferences("ExchangeHistory", MODE_PRIVATE);
        String history = sharedPreferences.getString("history", "");

        exchangeHistoryList = new ArrayList<>();
        if (!history.isEmpty()) {
            String[] records = history.split("\n");
            for (String record : records) {
                exchangeHistoryList.add(record);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, exchangeHistoryList);
        exchangeHistoryListView.setAdapter(adapter);
    }

    public void gotoprize (View v){
        Intent it=new Intent(this, prize.class);
        startActivity(it);
    }
}
