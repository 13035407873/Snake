package cn.xuemcu.snake;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener,OnClickListener
    ,Runnable{
    // the true snake object
    private Snake snakeinstance = null;

    private boolean showAnimation = true;
    private boolean autoAdapt = true;
    private int maxIteration = 300;
    private double alpha = 0.2;
    private double beta = 0.2;
    private double gamma = 0.4;
    private double delta = 0.1;
    private int everyXIterations = 10;
    private int minSegmentLength = 8;
    private int maxSegmentLength = 16;
    private int[][] chanel_gradient = null;
    private int[][] chanel_flow = null;

    private Bitmap yBitmap= null;
    private Bitmap hBitmap= null;
    private Bitmap zBitmap= null;

    private SeekBar seekBar = null;
    private Button btnLoadImage = null;
    private Button btnRunSnake  = null;
    private Button btnConfigure = null;
    private ImageView imageView = null;

    private int disPhotoFlag = 0;
    private String path = "";

    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btnLoadImage = (Button) findViewById(R.id.loadImage);
        btnRunSnake  = (Button) findViewById(R.id.runSnake);
        btnConfigure = (Button) findViewById(R.id.configure);
        imageView = (ImageView) findViewById(R.id.imageView);

        seekBar.setOnSeekBarChangeListener(this);
        btnLoadImage.setOnClickListener(this);
        btnRunSnake.setOnClickListener(this);
        btnConfigure.setOnClickListener(this);
        imageView.setOnClickListener(this);

        this.seekBar.setProgress(8);
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    imageView.setImageBitmap(hBitmap);
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "Error:" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    disPhotoFlag = 0;
                    imageView.setImageBitmap(zBitmap);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //该方法拖动进度条进度改变的时候调用
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //该方法拖动进度条开始拖动的时候调用
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //该方法拖动进度条停止拖动的时候调用
        //Toast.makeText(this, "SeekBar Count:" + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "In transformation,Please wait!", Toast.LENGTH_SHORT).show();
        new Thread(this).start();
    }

    public void displayImage() {
        if(disPhotoFlag == 0)
            imageView.setImageBitmap(zBitmap);
        else
            imageView.setImageBitmap(hBitmap);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loadImage:
                //Toast.makeText(this, "loadImage Click", Toast.LENGTH_SHORT).show();

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    this.checkPermission();
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");//选择图片
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.runSnake:
                //Toast.makeText(this, "runSnake Click", Toast.LENGTH_SHORT).show();
                if(yBitmap != null)
                    new Thread(snakerunner).start();
                break;
            case R.id.configure:
                //Toast.makeText(this, "configure Click", Toast.LENGTH_SHORT).show();
                displayDialog();
                break;
            case R.id.imageView:
                if(hBitmap != null && zBitmap != null) {
                    disPhotoFlag = 1 - disPhotoFlag;
                    displayImage();
                }
                break;
        }
    }

    // error (exception) display
    private void error(String text, Exception ex) {
        if (ex != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String s = sw.toString();
            s = s.substring(0, Math.min(512, s.length()));
            text = text + "\n\n" + s + " (...)";
        }
        Message message = new Message();
        message.what = 1;
        message.obj = text;
        this.myHandler.sendMessage(message);
    }

    // Snake Runnable
    final Runnable snakerunner = new Runnable() {
        public void run() {
            try {
                startsnake();
            } catch (Exception ex) {
                error(ex.getMessage(), ex);
            }
        }
    };

    // ---------------------------------------------------------------------
    //                         START SNAKE SEGMENTATION
    // ---------------------------------------------------------------------

    private void startsnake() {
        int W = yBitmap.getWidth();
        int H = yBitmap.getHeight();
        int MAXLEN = maxSegmentLength; /* max segment length */

        // initial points
        double radius = (W/2 + H/2) / 2;
        double perimeter = 6.28 * radius;
        int nmb = (int) (0.5*perimeter / MAXLEN);
        Point[] circle = new Point[nmb];
        for (int i = 0; i < circle.length; i++) {
            double x = (W / 2 + 0) + (W / 2 - 2)* Math.cos((6.28 * i) / circle.length);
            double y = (H / 2 + 0) + (H / 2 - 2)* Math.sin((6.28 * i) / circle.length);
            circle[i] = new Point((int) x, (int) y);
        }

        // create snake instance
        snakeinstance = new Snake(W, H, chanel_gradient, chanel_flow, circle);
        // snake base parameters
        snakeinstance.alpha = alpha;
        snakeinstance.beta = beta;
        snakeinstance.gamma = gamma;
        snakeinstance.delta = delta;
        // snake extra parameters
        snakeinstance.SNAKEGUI = this;
        snakeinstance.SHOWANIMATION = showAnimation;
        snakeinstance.AUTOADAPT = autoAdapt ;
        snakeinstance.AUTOADAPT_LOOP = everyXIterations;
        snakeinstance.AUTOADAPT_MINLEN = minSegmentLength;
        snakeinstance.AUTOADAPT_MAXLEN = maxSegmentLength;
        snakeinstance.MAXITERATION = maxIteration;

        // animate snake
        //System.out.println("initial snake points:" + snakeinstance.snake.size());
        int nmbloop = snakeinstance.loop();
        //System.out.println("final snake points:" + snakeinstance.snake.size());
        //System.out.println("iterations: " + nmbloop);

        // display final result
        display();

        //System.out.println("END");
    }

    public void display() { /* callback from snakeinstance */
        //zBitmap = Bitmap.createBitmap(yBitmap.getWidth(), yBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        //zBitmap = yBitmap;
        zBitmap = yBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(zBitmap);

        Paint p = new Paint();
        p.setColor(Color.RED);// 设置红色
        p.setStrokeWidth(2.0f);
        List<Point> snakepoints = snakeinstance.snake;
        for (int i = 0; i < snakepoints.size(); i++) {
            int j = (i + 1) % snakepoints.size();
            Point p1 = snakepoints.get(i);
            Point p2 = snakepoints.get(j);
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, p);
        }

        // draw snake points
        p.setColor(0xFFFFC800);
        for (int i = 0; i < snakepoints.size(); i++) {
            Point p1 = snakepoints.get(i);
            canvas.drawRect(p1.x-2, p1.y-2, p1.x+2, p1.y+2, p);
        }

        // swing display
        //Toast.makeText(this, "In transformation,Please wait!", Toast.LENGTH_SHORT).show();
        this.myHandler.sendEmptyMessage(2);
    }

    public void displayDialog() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle("Parameter Setting");
        builder.setShowAnimation(showAnimation);
        builder.setAutoAdapt(autoAdapt);
        builder.setMaxIteration(Integer.toString(maxIteration));
        builder.setAlpha(Double.toString(alpha));
        builder.setBeta(Double.toString(beta));
        builder.setGamma(Double.toString(gamma));
        builder.setDelta(Double.toString(delta));
        builder.setEveryXIterations(Integer.toString(everyXIterations));
        builder.setMinSegmentLength(Integer.toString(minSegmentLength));
        builder.setMaxSegmentLength(Integer.toString(maxSegmentLength));

        builder.setPositiveButton("Config", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //设置你的操作事项
                showAnimation = builder.getShowAnimation();
                autoAdapt =  builder.getAutoAdapt();
                maxIteration = Integer.parseInt(builder.getMaxIteration());
                alpha = Double.parseDouble(builder.getAlpha());
                beta = Double.parseDouble(builder.getBeta());
                gamma = Double.parseDouble(builder.getGamma());
                delta = Double.parseDouble(builder.getDelta());
                everyXIterations = Integer.parseInt(builder.getEveryXIterations());
                minSegmentLength = Integer.parseInt(builder.getMinSegmentLength());
                maxSegmentLength = Integer.parseInt(builder.getMaxSegmentLength());
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //设置你的操作事项
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                path = uri.getPath();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
            }
            //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            if(!path.equals("")) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(path);
                    yBitmap = BitmapFactory.decodeStream(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if(yBitmap == null)
                    Toast.makeText(this, "Load Image Error!", Toast.LENGTH_SHORT).show();
                else {
                    this.imageView.setImageBitmap(yBitmap);
                    Toast.makeText(this, "In transformation,Please wait!", Toast.LENGTH_SHORT).show();
                    new Thread(this).start();
                }
            } else {
                Toast.makeText(this, "Load Image Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

     public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
            }
        return res;
     }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
        return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }
        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private void computegflow() {
        int W = yBitmap.getWidth();
        int H = yBitmap.getHeight();

        int THRESHOLD = seekBar.getProgress();
        if(THRESHOLD == 0) THRESHOLD = 1;
        //THRESHOLD fixed

        // GrayLevelScale (Luminance)
        int[][] clum = new int[W][H];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++) {
                int rgb=yBitmap.getPixel(x,y);
                int r = Color.red(rgb);
                int g = Color.green(rgb);
                int b = Color.blue(rgb);
                clum[x][y] = (int)(0.299*r + 0.587*g + 0.114*b);
            }

        // Gradient  sobel
        this.chanel_gradient = new int[W][H];
        int maxgradient=0;
        for (int y = 0; y < H-2; y++)
            for (int x = 0; x < W-2; x++) {
                int p00 = clum[x+0][y+0]; int p10 = clum[x+1][y+0]; int p20 = clum[x+2][y+0];
                int p01 = clum[x+0][y+1]; /*-------------------- */ int p21 = clum[x+2][y+1];
                int p02 = clum[x+0][y+2]; int p12 = clum[x+1][y+2]; int p22 = clum[x+2][y+2];
                int sx = (p20+2*p21+p22)-(p00+2*p01+p02);
                int sy = (p02+2*p12+p22)-(p00+2*p10+p20);
                int snorm = (int)Math.sqrt(sx*sx+sy*sy);
                chanel_gradient[x+1][y+1]=snorm;
                maxgradient=Math.max(maxgradient, snorm);
            }

        // thresholding
        boolean[][] binarygradient = new boolean[W][H];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                if (chanel_gradient[x][y] > THRESHOLD*maxgradient/100) {
                    binarygradient[x][y]=true;
                } else {
                    chanel_gradient[x][y]=0;
                }

        // distance map to binarized gradient
        chanel_flow = new int[W][H];
        double[][] cdist = new ChamferDistance(ChamferDistance.chamfer5).compute(binarygradient, W,H);
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                chanel_flow[x][y]=(int)(5*cdist[x][y]);

        // show flow + gradient
        int[] rgb = new int[3];

        Bitmap tempBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
        //BufferedImage imgflow = new BufferedImage(W, H, ColorSpace.TYPE_RGB);
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int vflow = chanel_flow[x][y];
                int vgrad = binarygradient[x][y]?255:0;

                if (vgrad > 0) {
                    rgb[0] = 0;
                    rgb[1] = vgrad;
                    rgb[2] = 0;
                } else {
                    rgb[0] = Math.max(0, 255 - vflow);
                    rgb[1] = 0;
                    rgb[2] = 0;
                }
                int irgb = (0xFF<<24)+(rgb[0]<<16)+(rgb[1]<<8)+rgb[2];
                //imgflow.setRGB(x, y, irgb);
                tempBitmap.setPixel(x, y, irgb);
            }
        }
        hBitmap = tempBitmap;
        // swing display
        disPhotoFlag = 1;
        this.myHandler.sendEmptyMessage(0);
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    private boolean checkPermission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );
        // 如果这2个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            //有权限
            return true;
        } else {
            //无权限
            /**
             * 第 2 步: 请求权限
             */
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    },
                    MY_PERMISSION_REQUEST_CODE
            );
            return false;
        }
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");//选择图片
                //intent.setType(“audio/*”); //选择音频
                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
                //intent.setType(“video/*;image/*”);//同时选择视频和图片
                //intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                Toast.makeText(MainActivity.this, "The application requires permissions to read the file! ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void run() {
        this.computegflow();
    }
}
