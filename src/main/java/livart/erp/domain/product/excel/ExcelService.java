package livart.erp.domain.product.excel;


import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.Option;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.repository.OptionRepository;
import livart.common.domain.product.repository.ProductRepository;
import livart.common.dto.enums.product.ProductExcelField;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final GlobalService globalService;
    private final OptionRepository optionRepository;
    public List<ExcelFieldResponse> getFieldList(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<ExcelFieldResponse> fields = Arrays.stream(ProductExcelField.values())
                .map(field -> new ExcelFieldResponse(field.getField(), field.getLabel()))
                .collect(Collectors.toList());

        return fields;
    }

    public Workbook generateProductExcel(CustomUserDetails customUserDetails, ExcelDownloadRequest request) {

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

