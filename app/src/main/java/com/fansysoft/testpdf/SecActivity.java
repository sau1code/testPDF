package com.fansysoft.testpdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;

import com.fansysoft.testpdf.databinding.ActivitySecBinding;

public class SecActivity extends AppCompatActivity {

    private ActivitySecBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sec);

        binding.bar.tvPageName.setText("第二頁");

        binding.bar.ivBack.setOnClickListener(v->{
            finish();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.trans_in_from_left,R.anim.trans_out_to_right);
    }
}