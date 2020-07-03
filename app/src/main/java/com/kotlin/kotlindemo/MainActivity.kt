package com.kotlin.kotlindemo

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.itheima.updatelib.PatchUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * 增量更新
 * @author wwn
 */
class MainActivity : AppCompatActivity() {

    private val mDialog: ProgressDialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        bt_update.setOnClickListener {
            update()
        }
    }
    private fun checkPermission() { //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) { //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show()
            }
            //申请权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),0)
        } else {
            toast("授权成功！")
        }
    }


    /**
     * 测试使用方便 增量更新
     *
     * 可以下载不同环境下不同的patch包 进行合并安装
     *
     * 测试环境 patch
     * 正式环境 patch
     *
     * 将patch上传后台  编写一个下载patch 的代码存储到本地
     */
    fun update() {
        var pm: PackageManager = packageManager
        val appInfo = pm.getApplicationInfo("upzy.oil.strongunion.com.oil_app", 0)
        val oldPath: String = appInfo.sourceDir
        //指定保存路径 bsdiff 原包  更新包  patch名
        val patchFile = File(Environment.getExternalStorageDirectory(), "newPatch.patch")
        //设置新版本apk保留路径
        var newApkFile = File(Environment.getExternalStorageDirectory(), "ip_new.apk")


        mDialog.show()
        //合并生成新版本 手动生成增量更新so库  jni
        doAsync {
            //合并
            val result = PatchUtil.patch(oldPath,newApkFile.absolutePath,patchFile.absolutePath)
            runOnUiThread {
                mDialog.dismiss()
//                /storage/emulated/0/StrongUnion/Temp/20200702-174041_crop.png
//                readTxtFile(Environment.getExternalStorageDirectory().absolutePath, "google.txt")

                val apkUri =
                    FileProvider.getUriForFile(applicationContext, "com.kotlin.kotlindemo.fileprovider", newApkFile)

                val intent = Intent()
                //编写安装代码 意图过滤
                intent.setAction(Intent.ACTION_VIEW)
//                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if(Build.VERSION.SDK_INT>=24) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri,"application/vnd.android.package-archive")
                }else{
                    intent.setDataAndType(Uri.fromFile(newApkFile),"application/vnd.android.package-archive")
                }
                startActivity(intent)
            }
        }

    }


    @Throws(Exception::class)
    fun readTxtFile(filePath: String, fileName: String): String? {
        var result: String? = ""
        val fileName = File("$filePath/$fileName")
        var fileReader: FileReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            fileReader = FileReader(fileName)
            bufferedReader = BufferedReader(fileReader)
            try {
                var read: String? = null
                while ({ read = bufferedReader.readLine();read }() != null) {
                    result = result + read + "\r\n"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close()
            }
            if (fileReader != null) {
                fileReader.close()
            }
        }
        println("读取出来的文件内容是：\r\n$result")
        return result
    }
}
