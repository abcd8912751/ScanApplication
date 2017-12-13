package com.furja.qc.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.furja.qc.R;

public class MainActivity extends AppCompatActivity {
    private TextView resultView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView=(TextView)findViewById(R.id.resultView);
        resultView.setText("点我扫码");
        resultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent=new Intent(MainActivity.this,BadLogActivity.class);
                startActivity(intent);
            }
        });


        toLogBad();
//        toLogin();
    }

    private void toLogin() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳至物料异常收集界面
     */
    private void toLogBad() {
        Intent intent=new Intent(MainActivity.this,BadLogActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 开始扫码
     */
    private void scanBarCode()
    {

//        QRCodeManager.getInstance()
//                .with(this)
//                .scanningQRCode(new OnQRCodeScanCallback() {
//                    @Override
//                    public void onCompleted(String result) {//扫描成功之后回调，result就是扫描的结果
//                        showResult(result);
//                    }
//
//                    @Override
//                    public void onError(Throwable errorMsg) {//扫描出错的时候回调
//                        showResult(errorMsg.toString());
//                    }
//
//                    @Override
//                    public void onCancel() {//取消扫描的时候回调
//                        Toast.makeText(MainActivity.this,"已取消扫码行动",Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

//    private void showResult(String result)
//    {
//        if(resultView!=null)
//            resultView.setText(result);
//        if(result.contains("http"))
//        {
//            Intent intent=new Intent(this,SurfActivity.class);
//            intent.setData(Uri.parse(result));
//            startActivity(intent);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        QRCodeManager.getInstance().with(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
