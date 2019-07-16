package controller;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.os.SystemClock;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import constant.Constant;
import java.io.File;

public class FileController {
    private static Constant constant = new Constant();
     //+ constant.DATABASE_NAME
    private static String appDBPath = "";
    private static String rootLocalStorage = "";
    private Context context;

    public FileController(Context context){
        this.context = context;
        this.appDBPath = "/data/" + this.context.getApplicationContext().getPackageName() + "/databases/";
        this.rootLocalStorage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getApplicationName(this.context);
    }


    public void exportDBToLocalStorage(){
        /*
        Export database file to local storage
         */
        String src_path = appDBPath  + constant.DATABASE_NAME;
        File srcFile = new File(Environment.getDataDirectory(), src_path);
        File destFile = new File(new File(rootLocalStorage + "/db"), constant.DATABASE_NAME);
        createFolder(rootLocalStorage + "/db");
        try{
            FileChannel srcChannel = new FileInputStream(srcFile).getChannel();
            FileChannel destChannel = new FileOutputStream(destFile).getChannel();
            destChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            destChannel.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void importDBFromLocalStorage() throws FileNotFoundException {
        /*
        Import database file from local storage
         */
        File srcFile = new File(rootLocalStorage + "/db", constant.DATABASE_NAME);
        File destFile = new File(Environment.getDataDirectory() + appDBPath, constant.DATABASE_NAME);
        if (srcFile.exists()) {
            try {
                createFolder(Environment.getDataDirectory() + appDBPath);
                destFile.createNewFile();
                FileChannel srcChanel = new FileInputStream(srcFile).getChannel();
                FileChannel destChannel = new FileOutputStream(destFile).getChannel();
                srcChanel.transferTo(0, srcChanel.size(), destChannel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new FileNotFoundException("Cannot found file at '" + rootLocalStorage + "/db/" + constant.DATABASE_NAME + "'");
        }
    }

    /*  *******************************************************
                        PRIVATE FUNCTIONS
     ******************************************************* */
    private void createFolder (String folderPath){
        /*
        Create folder if it is not existed
         */
        File folder = new File (folderPath);
        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}