package com.jamub.payaccess.api.services;

//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
import com.jamub.payaccess.api.dao.TicketDao;
import com.jamub.payaccess.api.dao.TransactionDao;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.TransactionTicket;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.AssignTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CloseTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CreateTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.TransactionFilterRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class ReportService {


    @Autowired
    private TransactionDao transactionDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${path.report.folder}")
    private String reportFolderPath;




    @Autowired
    public ReportService(TransactionDao transactionDao){
        this.transactionDao = transactionDao;
    }


//    public ResponseEntity createTransactionReport(TransactionFilterRequest transactionFilterRequest, User authenticatedUser,
//                                                  Long actorId, String ipAddress, String description,
//                                                  ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference) throws IOException {
//        Map allTransactionResp = transactionDao.getAll(transactionFilterRequest, null, null);
//        List<Transaction> allTransactions = (List<Transaction>)allTransactionResp.get("list");
//
//
//        String inputHTML = "<html>\n" +
//                "    <head>\n" +
//                "        <style>\n" +
//                "            .center_div {\n" +
//                "                border: 1px solid gray;\n" +
//                "                margin-left: auto;\n" +
//                "                margin-right: auto;\n" +
//                "                width: 90%;\n" +
//                "                background-color: #d0f0f6;\n" +
//                "                text-align: left;\n" +
//                "                padding: 8px;\n" +
//                "            }\n" +
//                "        </style>\n" +
//                "    </head>\n" +
//                "    <body>\n" +
//                "        <div class=\"center_div\">\n" +
//                "            <h1>Hello Baeldung!</h1>\n" +
//                "            <img src=\"Java_logo.png\">\n" +
//                "            <div class=\"myclass\">\n" +
//                "                <p>This is the tutorial to convert html to pdf.</p>\n" +
//                "            </div>\n" +
//                "        </div>\n" +
//                "    </body>\n" +
//                "</html>";
////
////        InputStream is = new ByteArrayInputStream(inputHTML.getBytes());
////        HtmlLoadOptions htmloptions = new HtmlLoadOptions();
////        Document pdfDocument = new Document(is, htmloptions);\
////        pdfDocument.save("HTML-to-PDF.pdf");
////        Document document = Jsoup.parse(inputHTML, "UTF-8");
////        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
////
////
////        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm");
////        String fileName = "Transaction Report - ".concat(df.format(new Date()));
////
////        OutputStream outputStream = new FileOutputStream(reportFolderPath.concat(File.separator).concat(fileName).concat(".pdf"));
////        ITextRenderer renderer = new ITextRenderer();
////        SharedContext sharedContext = renderer.getSharedContext();
////        sharedContext.setPrint(true);
////        sharedContext.setInteractive(false);
////        renderer.setDocumentFromString(inputHTML);
////        renderer.layout();
////        renderer.createPDF(outputStream);
//
//
//        File file = new File(reportFolderPath.concat(File.separator).concat(fileName).concat(".pdf"));
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName+".pdf");
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//
//    }
//

}
