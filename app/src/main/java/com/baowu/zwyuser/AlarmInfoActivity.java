package com.baowu.zwyuser;

import static com.baowu.zwyuser.Utils.BEARING_BUSH_RECORD;
import static com.baowu.zwyuser.Utils.BEARING_RECORD;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.baowu.zwyuser.bean.BearingAlarmInfo;
import com.baowu.zwyuser.bean.BearingBushAlarmInfo;
import com.baowu.zwyuser.databinding.ActivityQueryAlarmBinding;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AlarmInfoActivity extends AppCompatActivity {
    private ActivityQueryAlarmBinding binding;
    private Handler handler;
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";
    public static final String BEARING_BUSH_TYPE = "BEARING_BUSH_TYPE";
    public static final String BEARING_TYPE = "BEARING_TYPE";
    private static final int REQUEST_CODE_PERMISSION = 100;
    private ArrayList<BearingBushAlarmInfo> bearingBushRecord;
    private ArrayList<BearingAlarmInfo> bearingRecord;
    private String deviceType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent = getIntent();
        int deviceId = intent.getIntExtra(DEVICE_ID, 0);
        deviceType = intent.getStringExtra(DEVICE_TYPE);
        if (deviceType.equals(BEARING_BUSH_TYPE)) {
            Utils.queryBearingBushAlarm(handler, deviceId);
        } else {
            Utils.queryBearingAlarm(handler, deviceId);
        }
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.download.setOnClickListener(v -> download());
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_query_alarm);
        Utils.configureTable(binding.alarmTable);
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case Utils.ALARM_BEARING_BUSH:
                        bearingBushRecord = msg.getData().getParcelableArrayList(BEARING_BUSH_RECORD);
                        binding.alarmTable.setData(bearingBushRecord);
                        break;
                    case Utils.ALARM_BEARING:
                        bearingRecord = msg.getData().getParcelableArrayList(BEARING_RECORD);
                        binding.alarmTable.setData(bearingRecord);
                }
            }
        };
    }

    private void download() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        }
        exportExcelAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                exportExcelAsync();
            } else {
                Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void exportExcelAsync() {
        // ????????????????????????Excel??????
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret = false;

                try {
                    if (deviceType.equals(BEARING_BUSH_TYPE)) {
                        ret = exportBearingBushExcel();
                    } else {
                        ret = exportBearingExcel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isFinishing()) {
                    return;
                }

                boolean finalRet = ret;
                runOnUiThread(() -> Toast.makeText(AlarmInfoActivity.this, finalRet ? "????????????" : "????????????", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private boolean exportBearingBushExcel() {
        // ????????????????????????????????????
        int colNum = 9;
        int rowNum = bearingBushRecord.size() + 1;

        // ??????excel xlsx??????
        Workbook wb = new SXSSFWorkbook();
        // ???????????????
        Sheet sheet = wb.createSheet();

        // ???????????????????????????
        for (int i = 0; i < colNum; i++) {
            if ( i == colNum - 1) {
                sheet.setColumnWidth(i, 30 * 256);
            } else {
                sheet.setColumnWidth(i, 20 * 256);  // ??????20??????????????????
            }
        }

        // ?????????????????????:????????????
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CreationHelper creationHelper = wb.getCreationHelper();

        for (int i = 0; i < rowNum; i++) {
            // ???????????????
            Row row = sheet.createRow(i);
            // ???????????????????????????
            row.setHeightInPoints(20f);

            for (int j = 0; j < colNum; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);

                switch (j) {
                    case 0:
                        if (i == 0) {
                            cell.setCellValue("??????A");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getData1());
                        }
                        break;
                    case 1:
                        if (i == 0) {
                            cell.setCellValue("??????B");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getData2());
                        }
                        break;
                    case 2:
                        if (i == 0) {
                            cell.setCellValue("??????C");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getData3());
                        }
                        break;
                    case 3:
                        if (i == 0) {
                            cell.setCellValue("??????D");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getData4());
                        }
                        break;
                    case 4:
                        if (i == 0) {
                            cell.setCellValue("??????????????????");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getData5());
                        }
                        break;
                    case 5:
                        if (i == 0) {
                            cell.setCellValue("?????????????????????");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getData6());
                        }
                        break;
                    case 6:
                        if (i == 0) {
                            cell.setCellValue("??????");
                        } else {
                            cell.setCellValue(bearingBushRecord.get(i - 1).getAlarmDate());
                        }
                        break;
                    case 7:
                        break;
                }
            }
        }

        // ??????excel??????
        createExcel(wb);
        return true;
    }

    private boolean exportBearingExcel() {
        // ????????????????????????????????????
        int colNum = 5;
        int rowNum = bearingRecord.size() + 1;

        // ??????excel xlsx??????
        Workbook wb = new SXSSFWorkbook();
        // ???????????????
        Sheet sheet = wb.createSheet();

        // ???????????????????????????
        for (int i = 0; i < colNum; i++) {
            if (i == colNum - 1) {
                sheet.setColumnWidth(i, 30 * 256);
            } else {
                sheet.setColumnWidth(i, 20 * 256);  // ??????20??????????????????
            }
        }

        // ?????????????????????:????????????
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CreationHelper creationHelper = wb.getCreationHelper();

        for (int i = 0; i < rowNum; i++) {
            // ???????????????
            Row row = sheet.createRow(i);
            // ???????????????????????????
            row.setHeightInPoints(20f);

            for (int j = 0; j < colNum; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);

                switch (j) {
                    case 0:
                        if (i == 0) {
                            cell.setCellValue("??????A");
                        } else {
                            cell.setCellValue(bearingRecord.get(i - 1).getData1());
                        }
                        break;
                    case 1:
                        if (i == 0) {
                            cell.setCellValue("??????B");
                        } else {
                            cell.setCellValue(bearingRecord.get(i - 1).getData2());
                        }
                        break;
                    case 2:
                        if (i == 0) {
                            cell.setCellValue("??????");
                        } else {
                            cell.setCellValue(bearingRecord.get(i - 1).getAlarmDate());
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        // ??????excel??????
        createExcel(wb);
        return true;
    }

    private void createExcel(Workbook wb) {
        FileOutputStream fos = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/"+
                    System.currentTimeMillis() + "alarm.xls");
            if (!file.exists()) {
                file.createNewFile();
            }
            Log.d("ALARMINFO", file.getPath());
            fos = new FileOutputStream(file);
            wb.write(fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
