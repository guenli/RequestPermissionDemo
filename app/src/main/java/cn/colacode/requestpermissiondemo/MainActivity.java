package cn.colacode.requestpermissiondemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int PERMISSION_CAMERA = 3301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isGrant = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                if (isGrant) {
                    takePhotoByPath(MainActivity.this, initImageFile().getAbsolutePath(), PERMISSION_CAMERA);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoByPath(MainActivity.this, initImageFile().getAbsolutePath(), PERMISSION_CAMERA);
            } else {
                Toast.makeText(MainActivity.this, permissions[0] + "被拒", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 拍照,返回拍照文件的绝对路径
     */
    private static String takePhotoByPath(Activity context, String filePath, int requestCode) {
        File file = new File(filePath);

        if (file.exists())
            file.delete();

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        context.startActivityForResult(intent, requestCode);
        return file.getPath();
    }

    public File initImageFile() {
        File fImageFile = null;

        if (hasSDCard()) {
            //构造存储图片的文件的路径,文件名为当前时间
            String strFilePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/"
                    + System.currentTimeMillis()
                    + ".png";
            fImageFile = new File(strFilePath);
            if (!fImageFile.exists()) {
                try {
                    fImageFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return fImageFile;
    }

    public boolean hasSDCard() {
        //获取外部存储的状态
        String strState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(strState)) {
            return true;
        }
        return false;
    }
}
