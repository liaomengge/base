package cn.ly.base_common.utils.excel;

import cn.ly.base_common.utils.io.LyIOUtil;
import cn.ly.base_common.utils.log4j2.LyLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LyExcelUtil {

    private Logger log = LyLogger.getInstance(LyExcelUtil.class);

    private final int MAX_ROW = 65534;

    public void export(Map<String, String> titleMap, List<?> list, ServletOutputStream out) {
        int currPage = 0;
        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(out);
            try {
                int pages = list.size() % MAX_ROW == 0 ? (list.size() / MAX_ROW) : (list.size() / MAX_ROW + 1);
                WritableSheet[] wsArr = new WritableSheet[pages + 1];
                for (int i = 0; i < pages + 1; i++) {
                    wsArr[i] = workbook.createSheet("Sheet" + (i + 1), i);
                    wsArr[i].getSettings().setDefaultColumnWidth(13);
                }
                WritableFont[] wf = new WritableFont[2];
                WritableCellFormat[] wcfFC = new WritableCellFormat[4];
                wf[0] = new WritableFont(WritableFont.createFont("宋体"), 14, WritableFont.BOLD, false);
                wcfFC[0] = new jxl.write.WritableCellFormat(wf[0]);
                wcfFC[0].setAlignment(Alignment.CENTRE);

                wf[1] = new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD, false);
                wcfFC[1] = new jxl.write.WritableCellFormat(wf[1]);
                wcfFC[1].setAlignment(Alignment.CENTRE);

                int columnNum;
                int rowNum = 0;
                for (int i = 0; i < list.size(); i++) {
                    columnNum = 0;
                    if (rowNum % MAX_ROW == 0) {
                        currPage++;
                        for (Map.Entry<String, String> entry : titleMap.entrySet()) {
                            wsArr[currPage - 1].addCell(new Label(columnNum, 0, entry.getValue(), wcfFC[0]));
                            columnNum++;
                        }
                        columnNum = 0;
                        rowNum = 1;
                    }
                    for (Map.Entry<String, String> entry : titleMap.entrySet()) {
                        String value = BeanUtils.getProperty(list.get(i), entry.getKey());
                        wsArr[currPage - 1].addCell(new Label(columnNum, rowNum, value, wcfFC[1]));
                        columnNum++;
                    }
                    rowNum++;
                }
                for (int i = currPage; i < wsArr.length; i++) {
                    workbook.removeSheet(i);
                }
                workbook.write();
            } catch (Exception e) {
                log.error("Export Excel Fail", e);
            } finally {
                if (workbook != null) {
                    try {
                        workbook.close();
                    } catch (WriteException e) {
                        log.error("Close WorkBook Fail", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Create WorkBook Fail", e);
        } finally {
            if (out != null) {
                LyIOUtil.closeQuietly(out);
            }
        }

    }
}
