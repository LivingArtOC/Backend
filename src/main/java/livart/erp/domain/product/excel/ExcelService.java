package livart.erp.domain.product.excel;


import livart.common.domain.product.entity.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExcelService {

    public Workbook generateProductExcel(List<Product> products) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "상품 ID");
        createHeaderCell(headerRow, 1, "상품 카테고리");
        createHeaderCell(headerRow, 2, "상품명");
        createHeaderCell(headerRow, 3, "상품 코드");
        createHeaderCell(headerRow, 4, "상품 상태");
        createHeaderCell(headerRow, 5, "상품 품절 상태");
        createHeaderCell(headerRow, 6, "브랜드");
        createHeaderCell(headerRow, 7, "정가");
        createHeaderCell(headerRow, 8, "판매가");
        createHeaderCell(headerRow, 9, "할인가");
        createHeaderCell(headerRow, 10, "할인율");
        createHeaderCell(headerRow, 11, "가격 대체 문구");
        createHeaderCell(headerRow, 12, "등록일");
        createHeaderCell(headerRow, 13, "최근 수정일");

        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getCategoryId());
            row.createCell(2).setCellValue(product.getProductName());
            row.createCell(3).setCellValue(product.getProductCode());
            row.createCell(4).setCellValue(product.getProductStatus().name());
            row.createCell(4).setCellValue(product.getStatus().name());
        }

        // 📌 열 너비 자동 조정
        for (int i = 0; i <= 4; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }

    private void createHeaderCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);

        // (선택) 헤더 스타일 추가
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        Font font = row.getSheet().getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
    }
}

