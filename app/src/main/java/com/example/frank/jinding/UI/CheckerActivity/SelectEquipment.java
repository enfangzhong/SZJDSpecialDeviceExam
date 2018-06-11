package com.example.frank.jinding.UI.CheckerActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.frank.jinding.Conf.CheckControl;
import com.example.frank.jinding.Conf.URLConfig;
import com.example.frank.jinding.R;
import com.example.frank.jinding.Service.ApiService;
import com.example.frank.jinding.Upload.FtpClientUpload;
import com.example.frank.jinding.Upload.FtpUpload;
import com.example.frank.jinding.Utils.CameraPermissionCompat;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectEquipment extends AppCompatActivity {

    private Button add_infomation,add_photo,upload,post,addopinion,lookresult;
    //private Spinner mySpinner;
    private ArrayAdapter<String> spadapter;
    private List<String> ls = new ArrayList<String>();
    private List<String> list = new ArrayList<String>();
    private EditText opinion;
    //private File file;

    private ImageButton back;
    private TextView title ,device;
    private ListView lv_tasksss;

    private String deviceinfo="",isMainChecker="",consignmentId="",orderId="",deviceId="",submission_id="";

    private static boolean dirurl=false;
    private MyAdapter mAdapter;
    FtpUpload ff=new FtpUpload();
    private  String path = Environment.getExternalStorageDirectory() + "/Luban/image/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_equipment);

       /* StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
*/
        Intent intent=getIntent();
        deviceinfo=intent.getStringExtra("device");
        isMainChecker=intent.getStringExtra("isMainChecker");
        orderId=intent.getStringExtra("orderId");
        deviceId=intent.getStringExtra("deviceId");
        submission_id=intent.getStringExtra("submission_id");
        consignmentId=intent.getStringExtra("consignmentId");

        init();
        device.setText("正在检测设备："+deviceinfo);
        //标题栏设置
        title.setText("设备检验");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //判断那一个人进行这一个设备的检验意见填写
        /*if (!isMainChecker.equals("true")){
            addopinion.setVisibility(addopinion.INVISIBLE);
            lookresult.setVisibility(lookresult.INVISIBLE);
        }*/


      /*  add_infomation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //打印Button的点击信息
                new  AlertDialog.Builder(SelectEquipment.this)
                        .setTitle("系统提示")
                        .setMessage("\n添加成功！")
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {
                                        opinion.setText("");
                                    }
                                }).show();
            }
        });*/


        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean permission=CameraPermissionCompat.checkCameraPermission(SelectEquipment.this, new CameraPermissionCompat.OnCameraPermissionListener() {
                    @Override
                    public void onGrantResult(boolean granted) {

                        Log.i("相机权限：",granted+"");
                    }
                });

                if (permission) {
                    Intent intent = new Intent(SelectEquipment.this, Equipment_Recorde.class);
                    //startActivity(intent);
                    startActivityForResult(intent, 5201);
                }
                else {
                    new AlertDialog.Builder(SelectEquipment.this).setTitle("系统提示").setMessage("您还没有给该应用赋予拍照的权限，请前往手机设置里面手动赋予该应用相机权限").show();
                }
            }
        });






        // /*为ListView添加点击事件*/


        lv_tasksss.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {


            }
        });



        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!dirurl){
                    Toast.makeText(SelectEquipment.this,"正在设置检验环境",Toast.LENGTH_SHORT).show();
                    dir_url();


                }
                    new AlertDialog.Builder(SelectEquipment.this)
                            .setTitle("系统提示")
                            .setMessage("\n您是否确定上传本次检验记录？\n\n如果上传完成，可以填写检验意见！")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        //文件测试

                                                        for (int i = 0; i < mAdapter.listItem.size(); i++) {

                                                            if (mAdapter.listItem.get(i).get("Tag").toString().equals("0")) {
                                                                //上传文件到服务器
                                                                String eqfilename = mAdapter.listItem.get(i).get("ItemImage").toString();
                                                                String datafilename = eqfilename.substring(eqfilename.lastIndexOf("/") + 1, eqfilename.length());
                                                                FtpClientUpload.UploadFile(eqfilename, orderId + "/" + consignmentId + "/" + deviceId + "/", SelectEquipment.this, datafilename);
                                                                //ff.upload(SelectEquipment.this,orderId+"/"+consignmentId+"/"+deviceId,mAdapter.listItem.get(i).get("ItemImage").toString());

                                                                //上传文字描述到服务器
                                                                String filename = mAdapter.listItem.get(i).get("ItemImage").toString();
                                                                String dd = orderId + "#" + consignmentId + "#" + deviceId + "#" + filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) + "#" + mAdapter.listItem.get(i).get("ItemText").toString();
                                                                Map<String, Object> paremetes = new HashMap<>();
                                                                paremetes.put("data", dd);
                                                                ApiService.GetString(SelectEquipment.this, "pictureInfomation", paremetes, new RxStringCallback() {
                                                                    boolean flag = false;

                                                                    @Override
                                                                    public void onNext(Object tag, String response) {

                                                                        if (response.trim().equals("上传成功！")) {
                                                                            Toast.makeText(SelectEquipment.this, "上传成功", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(Object tag, Throwable e) {
                                                                        Toast.makeText(SelectEquipment.this, "" + e, Toast.LENGTH_SHORT).show();
                                                                    }

                                                                    @Override
                                                                    public void onCancel(Object tag, Throwable e) {
                                                                        Toast.makeText(SelectEquipment.this, "" + e, Toast.LENGTH_SHORT).show();

                                                                    }


                                                                });

                                                            }

                                                        }


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        }
                                    }).show();


                
              /*  new  AlertDialog.Builder(SelectEquipment.this)
                        .setTitle("系统提示")
                        .setMessage("关于此型号设备是否检测完成？点击“确定”上传添加的所有文字信息和照片信息，并进行撰写报告。点击“取消”继续添加设备相关信息！")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {


                                        CheckInfo.listItem=null;
                                        Toast.makeText(SelectEquipment.this,"信息上传成功！",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(SelectEquipment.this,CheckOpinion.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();*/

            }
        });






        addopinion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                new  AlertDialog.Builder(SelectEquipment.this)
                        .setTitle("系统提示")
                        .setMessage("\n撰写报告前请点击“上传保存”，以免信息丢失！如果您确定检测已经完成，点击“确定”进行撰写检测意见，点击“取消”继续进行相关检测！")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {

                                        Intent intent=new Intent(SelectEquipment.this,CheckOpinion.class);
                                        intent.putExtra("submission_id", submission_id);
                                        intent.putExtra("orderId", orderId);
                                        intent.putExtra("deviceId",deviceId);
                                        intent.putExtra("consignmentId",consignmentId);

                                        startActivity(intent);
                                        //finish();


                                    }
                                }).show();


            }
        });


        lookresult.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intent=new Intent(SelectEquipment.this,ResultOpinion.class);
                intent.putExtra("submission_id", submission_id);
                intent.putExtra("orderId", orderId);
                intent.putExtra("deviceId",deviceId);
                startActivity(intent);

            }
        });

        /*refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getCheckDetails();

            }
        });*/


       /* //第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项

        list.add("请选择相应的设备：");
        list.add("桥塔设备a324");
        list.add("吊塔设备b3423");
        list.add("启动电机a234");
        list.add("挖掘机k23434");
        list.add("控制电机a543322");


        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        spadapter  =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,  list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        spadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mySpinner.setAdapter(spadapter);
        //mySpinner.setSelection(0,false);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中

        mySpinner.setOnItemSelectedListener(new  Spinner.OnItemSelectedListener(){
            public  void  onItemSelected(AdapterView<?> arg0, View  arg1, int  arg2, long  arg3)  {
                //  TODO  Auto-generated  method  stub
                *//*  将所选mySpinner  的值带入myTextView  中*//*
                if (device.getText().toString().trim().equals("请选择检验设备型号！")){
                    add_photo.setEnabled(false);
                }else {
                    add_photo.setEnabled(true);
                }

                if (mySpinner.getSelectedItemId()!=0){



                new  AlertDialog.Builder(SelectEquipment.this)
                        .setTitle("系统提示")
                        .setMessage("\n您确定对："+mySpinner.getSelectedItem().toString()+"  现在进行检测吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("确定",
                                new  DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public  void  onClick(DialogInterface dialog, int  which)
                                    {

                                        device.setText("正在检测设备："+mySpinner.getSelectedItem().toString().trim());

                                        HashMap<String, Object> map = new HashMap<String, Object>();
                                        map.put("ItemName", mySpinner.getSelectedItem().toString());
                                        mAdapter.listItem.add(map);
                                        mAdapter.notifyDataSetChanged();

                                        spadapter.remove(mySpinner.getSelectedItem().toString().trim());
                                        spadapter.notifyDataSetChanged();
                                        mySpinner.setSelection(0,true);

                                    }
                                }).show();
                }

            }
            public  void  onNothingSelected(AdapterView<?>  arg0)  {
                //  TODO  Auto-generated  method  stub
                //myTextView.setText("NONE");
            }
        });


        *//*下拉菜单弹出的内容选项触屏事件处理*//*
        mySpinner.setOnTouchListener(new  Spinner.OnTouchListener(){
            public  boolean  onTouch(View  v,  MotionEvent event)  {
                //  TODO  Auto-generated  method  stub
                *//**
                 *
                 *//*
                return  false;
            }
        });
                        *//*下拉菜单弹出的内容选项焦点改变事件处理*//*
        mySpinner.setOnFocusChangeListener(new  Spinner.OnFocusChangeListener(){
            public  void  onFocusChange(View  v,  boolean  hasFocus)  {
                //  TODO  Auto-generated  method  stub

            }
        });


*/



        /*final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                *//*HashMap<String, Object> map=new HashMap<>();
                map.put("ItemImage", da[1]);
                map.put("ItemText", da[2]);
                map.put("Tag",da[3]);*//*
                if (CheckInfo.listItem.size()>0) {
                    HashMap<String, Object> map=CheckInfo.listItem.get(0);
                    mAdapter.listItem.add(CheckInfo.listItem.get(0));
                    mAdapter.notifyDataSetChanged();
                    CheckInfo.listItem.clear();
                    //new AlertDialog.Builder(SelectEquipment.this).setMessage(map.get("ItemImage")+"=="+map.get("")+"=="+map.get("Tag")).show();

                }
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(runnable, 4000);*/





    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==5201&& CheckControl.isPhoto) {
            CheckControl.isPhoto=false;

           String dd=data.getStringExtra("dd");
            String da[]=dd.split("##");
            System.out.println("返回的数据："+dd);
            if (da[0].equals("true")){

                HashMap<String, Object> map=new HashMap<>();
                map.put("ItemImage", da[1]);
                map.put("ItemText", da[2]);
                map.put("Tag",da[3]);
                mAdapter.listItem.add(map);
                //Toast.makeText(SelectEquipment.this,map.get("ItemImage").toString(),Toast.LENGTH_SHORT).show();
               // mAdapter.notifyDataSetChanged();

                //new AlertDialog.Builder(SelectEquipment.this).setMessage(map.get("ItemImage")+"=="+map.get("ItemText")+"=="+map.get("Tag")).show();
                mAdapter.notifyDataSetChanged();

            }


        }
    }

    private void init(){

        add_photo=(Button)this.findViewById(R.id.add_photo);
        addopinion=(Button)this.findViewById(R.id.add_opinion);
        lookresult=(Button)this.findViewById(R.id.look_result);
        upload=(Button)this.findViewById(R.id.other_infomation);
        post=(Button)this.findViewById(R.id.work_end);
        opinion=(EditText)this.findViewById(R.id.editText);


        back=(ImageButton)this.findViewById(R.id.titleback);
        title=(TextView)this.findViewById(R.id.titleplain);

        device=(TextView)this.findViewById(R.id.textView49);

        lv_tasksss=(ListView)this.findViewById(R.id.lv_equipment_situation);
        mAdapter = new MyAdapter(this);//得到一个MyAdapter对象
        lv_tasksss.setAdapter(mAdapter);//为ListView绑定Adapter

        File filecname = new File(path);
        filecname.mkdirs();// 创建文件夹

        dir_url();
        //ff.download(SelectEquipment.this,orderId+"/"+deviceId+"/","20180103_040520"+".jpg");
        getCheckDetails();
    }


    private void getCheckDetails(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    String data=orderId+"#"+consignmentId+"#"+deviceId;
                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data",data);
                    ApiService.GetString(SelectEquipment.this, "checkDetails", paremetes, new RxStringCallback() {
                        boolean flag = false;

                        @Override
                        public void onNext(Object tag, String response) {


                            //refreshLayout.setRefreshing(false);
                            if (!response.trim().equals("获取失败！")&&response.trim().length()>20) {
                                //Toast.makeText(SelectEquipment.this, "获取到的数据：" + response, Toast.LENGTH_SHORT).show();
                                //String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
                                String data[]=response.split("##");
                                for (int i=0;i+1<data.length;i+=2){
                                    //ff.download(SelectEquipment.this,orderId+"/"+consignmentId+"/"+deviceId+"/",data[i]+".jpg");
                                    String imgurl=URLConfig.CompanyURL+orderId+"/"+consignmentId+"/"+deviceId+"/"+data[i]+".jpg";

                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    map.put("ItemImage", imgurl);
                                    //map.put("ItemImage", path+data[i]+".jpg");
                                    map.put("ItemText", data[i+1]);
                                    map.put("Tag","1");
                                    mAdapter.listItem.add(map);


                                }
                                mAdapter.notifyDataSetChanged();


                            }
                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                            //refreshLayout.setRefreshing(false);

                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "获取失败" + e, Toast.LENGTH_SHORT).show();
                            //refreshLayout.setRefreshing(false);
                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    private void dir_url(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Map<String, Object> paremetes = new HashMap<>();
                    paremetes.put("data", orderId+","+ URLConfig.CompanyURL+orderId+"/");
                    ApiService.GetString(SelectEquipment.this, "orderDetailsUrl", paremetes, new RxStringCallback() {
                        boolean flag = false;

                        @Override
                        public void onNext(Object tag, String response) {

                            if (response.trim().equals("提交成功！")) {

                                dirurl=true;
                                Toast.makeText(SelectEquipment.this,"检验环境设置成功",Toast.LENGTH_SHORT).show();

                            }else {
                                dirurl=false;
                              //  Toast.makeText(SelectEquipment.this, "检验环境设置失败！" , Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "提交失败" + e, Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onCancel(Object tag, Throwable e) {
                            Toast.makeText(SelectEquipment.this, "提交失败" + e, Toast.LENGTH_SHORT).show();

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }




    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        //ArrayList<HashMap<String, Object>> listItem = CheckInfo.listItem;
        private  ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String,     Object>>();
        /*构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public int getCount() {

            return listItem.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        /*书中详细解释该方法*/
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            //观察convertView随ListView滚动情况
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.inspection_report,null);
                holder = new ViewHolder();
                /*得到各个控件的对象*/

                holder.title = (TextView) convertView.findViewById(R.id.textView33);
                holder.delete=(Button)convertView.findViewById(R.id.button60);
                holder.modify=(Button)convertView.findViewById(R.id.button61);
                holder.pic = (ImageView) convertView.findViewById(R.id.imageView5);

                convertView.setTag(holder);//绑定ViewHolder对象
            }
            else{
                holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/


            /*holder.title.setText(getDate().get(position).get("ItemText").toString());
            holder.pic.setImageResource(Integer.parseInt(getDate().get(position).get("ItemImage").toString()));*/

            //holder.pic.setImageURI(Uri.parse(getDate().get(position).get("ItemImg").toString()));
            /*Bitmap bm = BitmapFactory.decodeFile(listItem.get(position).get("ItemImage").toString());
            holder.pic.setImageBitmap(bm);*/

            holder.title.setText(listItem.get(position).get("ItemText").toString());
            //图片处理

            /*Bitmap bm = BitmapFactory.decodeFile(listItem.get(position).get("ItemImage").toString());
            holder.pic.setImageBitmap(bm);*/


            if (listItem.get(position).get("Tag").toString().trim().equals("1")){
                Glide.with(SelectEquipment.this).load(listItem.get(position).get("ItemImage").toString()).into(holder.pic);

                /*Bitmap bm=GetThumbnail.getImageThumbnail(SelectEquipment.this,getContentResolver(),listItem.get(position).get("ItemImage").toString());
                holder.pic.setImageBitmap(bm);*/
            }else if (listItem.get(position).get("Tag").toString().trim().equals("0")){

                Bitmap bm = BitmapFactory.decodeFile(listItem.get(position).get("ItemImage").toString());
                holder.pic.setImageBitmap(bm);

            }


            holder.pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // final SelectEquipment.ViewHolder holder;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.dialog_report, (ViewGroup) findViewById(R.id.dalog_report));
                    final ImageView img = (ImageView) layout.findViewById(R.id.imageView6);
                    final TextView textv =(TextView) layout.findViewById(R.id.textView34);

                    //img.setImageResource(R.drawable.report);

                    //获取imageview中显示的图片
                    /*holder.pic.buildDrawingCache(true);
                    holder.pic.buildDrawingCache();
                    Bitmap bitmap = holder.pic.getDrawingCache();
                    img.setImageBitmap(bitmap);
                    holder.pic.setDrawingCacheEnabled(false);*/

                    String picname=mAdapter.listItem.get(position).get("ItemImage").toString();

                    /*final Bitmap bitmap = loadingImageBitmap(picname);
                    img.setImageBitmap(bitmap);*/

                    if (listItem.get(position).get("Tag").toString().trim().equals("1")){
                        Glide.with(SelectEquipment.this).load(picname).into(img);
                    }else if (listItem.get(position).get("Tag").toString().trim().equals("0")){

                        final Bitmap bitmap = loadingImageBitmap(picname);
                        img.setImageBitmap(bitmap);

                    }






                    textv.setText(holder.title.getText());
                    new AlertDialog.Builder(SelectEquipment.this).setTitle("详细信息").setView(layout)
                            .setPositiveButton("确定", new  DialogInterface.OnClickListener()
                            {
                                @Override
                                public  void  onClick(DialogInterface dialog, int  which)
                                {

                                }
                            })
                            .setNegativeButton("取消", new  DialogInterface.OnClickListener()
                            {
                                @Override
                                public  void  onClick(DialogInterface dialog, int  which)
                                {
                                }
                            }).show();
                }
            });

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new  AlertDialog.Builder(SelectEquipment.this)
                            .setTitle("系统提示")
                            .setMessage("\n您确定删除本条检测记录吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定",
                                    new  DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public  void  onClick(DialogInterface dialog, int  which)
                                        {


                                            if (mAdapter.listItem.get(position).get("Tag").equals("0")){
                                                mAdapter.listItem.remove(position);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(SelectEquipment.this,"删除成功",Toast.LENGTH_SHORT).show();

                                            }else if (mAdapter.listItem.get(position).get("Tag").equals("1")){
                                                try {

                                                    String picname=mAdapter.listItem.get(position).get("ItemImage").toString();
                                                    String datafilename = picname.substring(picname.lastIndexOf("/") + 1, picname.lastIndexOf("."));
                                                    Map<String, Object> paremetes = new HashMap<>();
                                                    paremetes.put("data", orderId+"#"+consignmentId+"#"+deviceId+"#"+datafilename);
                                                    ApiService.GetString(SelectEquipment.this, "deletePicture", paremetes, new RxStringCallback() {
                                                        boolean flag = false;

                                                        @Override
                                                        public void onNext(Object tag, String response) {

                                                            if (response.trim().equals("删除成功！")) {
                                                                String eqfilename = mAdapter.listItem.get(position).get("ItemImage").toString();
                                                                String datafile = eqfilename.substring(eqfilename.lastIndexOf("/") + 1, eqfilename.length());

                                                                FtpClientUpload.DeleteFile(orderId + "/" + consignmentId + "/" + deviceId + "/",datafile);

                                                                mAdapter.listItem.remove(position);
                                                                mAdapter.notifyDataSetChanged();
                                                                Toast.makeText(SelectEquipment.this,"删除成功",Toast.LENGTH_SHORT).show();

                                                            }
                                                        }

                                                        @Override
                                                        public void onError(Object tag, Throwable e) {
                                                            Toast.makeText(SelectEquipment.this, "删除失败" + e, Toast.LENGTH_SHORT).show();


                                                        }

                                                        @Override
                                                        public void onCancel(Object tag, Throwable e) {
                                                            Toast.makeText(SelectEquipment.this, "删除失败" + e, Toast.LENGTH_SHORT).show();

                                                        }
                                                    });


                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        }
                                    }).show();
                }
            });

            holder.modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final EditText et=new EditText(SelectEquipment.this);
                    et.setText(mAdapter.listItem.get(position).get("ItemText").toString());
                    new  AlertDialog.Builder(SelectEquipment.this)
                            .setTitle("系统提示")
                            .setMessage("\n请修改新的检验情况说明：")
                            .setView(et)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("确定",
                                    new  DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public  void  onClick(DialogInterface dialog, int  which)
                                        {

                                            String datatext=mAdapter.listItem.get(position).get("ItemText").toString();
                                            final String temimg = mAdapter.listItem.get(position).get("ItemImage").toString();
                                            final String tagtag = mAdapter.listItem.get(position).get("Tag").toString();

                                            if (mAdapter.listItem.get(position).get("Tag").toString().trim().equals("0")) {
                                                HashMap<String, Object> map = new HashMap<String, Object>();
                                                map.put("ItemImage", temimg);
                                                map.put("ItemText", et.getText());
                                                map.put("Tag", tagtag);
                                                mAdapter.listItem.remove(position);
                                                mAdapter.listItem.add(map);
                                                mAdapter.notifyDataSetChanged();
                                                Toast.makeText(SelectEquipment.this, "修改成功", Toast.LENGTH_SHORT).show();
                                            }else if (!et.getText().toString().equals(datatext)&&mAdapter.listItem.get(position).get("Tag").toString().trim().equals("1")){
                                                String filename = mAdapter.listItem.get(position).get("ItemImage").toString();
                                                String datafile = filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) ;

                                                Map<String, Object> paremetes = new HashMap<>();
                                                paremetes.put("data", orderId+"#"+consignmentId+"#"+deviceId+"#"+datafile+"#"+et.getText());
                                                ApiService.GetString(SelectEquipment.this, "modifyDescription", paremetes, new RxStringCallback() {
                                                    boolean flag = false;

                                                    @Override
                                                    public void onNext(Object tag, String response) {
                                                        if (response.trim().equals("修改成功！")) {
                                                            HashMap<String, Object> map = new HashMap<String, Object>();
                                                            map.put("ItemImage", temimg);
                                                            map.put("ItemText", et.getText());
                                                            map.put("Tag", tagtag);
                                                            mAdapter.listItem.remove(position);
                                                            mAdapter.listItem.add(map);
                                                            mAdapter.notifyDataSetChanged();
                                                            Toast.makeText(SelectEquipment.this,"修改成功",Toast.LENGTH_SHORT).show();

                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Object tag, Throwable e) {
                                                        Toast.makeText(SelectEquipment.this, "修改失败" + e, Toast.LENGTH_SHORT).show();


                                                    }

                                                    @Override
                                                    public void onCancel(Object tag, Throwable e) {
                                                        Toast.makeText(SelectEquipment.this, "修改失败" + e, Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }


                                        }
                                    }).show();

                }
            });


            return convertView;
        }

    }
    /*存放控件*/
    public final class ViewHolder{
        public TextView title;
        public Button delete,modify;
        public ImageView pic;

    }



    public Bitmap loadingImageBitmap(String imagePath) {
        /**
         * 获取屏幕的宽与高
         */
        final int width = getWindowManager().getDefaultDisplay().getWidth();
        final int height = getWindowManager().getDefaultDisplay().getHeight();
        /**
         * 通过设置optios来只加载大图的尺寸
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            /**
             * 计算手机宽高与显示大图的宽高，然后确定缩放有比例
             */
            int widthRaio = (int) Math.ceil(options.outWidth/(float)width);
            int heightRaio = (int) Math.ceil(options.outHeight/(float)height);
            if (widthRaio>1&&heightRaio>1){
                if (widthRaio>heightRaio){
                    options.inSampleSize = widthRaio;
                }else {
                    options.inSampleSize = heightRaio;
                }
            }
            /**
             * 设置加载缩放后的图片
             */
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


}