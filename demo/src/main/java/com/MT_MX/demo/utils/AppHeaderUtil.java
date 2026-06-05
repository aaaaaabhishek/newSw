package com.MT_MX.demo.utils;

import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.iso20022.head_001_001_02.*;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.OffsetDateTime;
import java.util.GregorianCalendar;

public class AppHeaderUtil {
    @Value("${swift.cbpr.bizSvc}")
    private static String bizSvc;
    @Value("${swift.cbpr.msgDefIdr.pacs008}")
    private static String msgDefIdr008;
    @Value("${swift.cbpr.msgDefIdr.pacs009}")
    private static String msgDefIdr009;
private AppHeaderUtil(){}
    public static BusinessApplicationHeaderV02 buildAppHdr(SwiftAst ast,boolean isDuplicate)
            throws DatatypeConfigurationException {

        BusinessApplicationHeaderV02 appHdr =
                new BusinessApplicationHeaderV02();

        BlockNode b1 = ast.getBlock(1);
        BlockNode b2 = ast.getBlock(2);

        BlockNode b3 = ast.getBlock(3);
        BlockNode b4 = ast.getBlock(4);

        // ===== FROM (Sender)

//        String senderBic = SwiftBicUtil.normalizeBic(SwiftBicUtil.extractBic(b1));
        String senderBicRaw = SwiftBicUtil.extractBic(b1);
        String receiverBicRaw = SwiftBicUtil.extractBic2(b2);

        String senderBic = SwiftBicUtil.normalizeBic(senderBicRaw);
        String receiverBic = SwiftBicUtil.normalizeBic(receiverBicRaw);

        if (senderBic == null || senderBic.isBlank()) {
            throw new IllegalArgumentException("Sender BIC is missing/invalid");
        }

        if (receiverBic == null || receiverBic.isBlank()) {
            throw new IllegalArgumentException("Receiver BIC is missing/invalid");
        }
        Party44Choice fr = new Party44Choice();
        BranchAndFinancialInstitutionIdentification6 frFin =
                new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 frId =
                new FinancialInstitutionIdentification18();

        frId.setBICFI(senderBic);
        frFin.setFinInstnId(frId);
        fr.setFIId(frFin);

        appHdr.setFr(fr);
        System.out.println("===========================================");
        System.out.println(SwiftBicUtil.extractBic2(b2));
        System.out.println(SwiftBicUtil.normalizeBic(SwiftBicUtil.extractBic2(b2)));
        System.out.println("===========================================");


        // ===== TO (Receiver)
//        String receiverBic = SwiftBicUtil.normalizeBic(SwiftBicUtil.extractBic2(b2));

        Party44Choice to = new Party44Choice();
        BranchAndFinancialInstitutionIdentification6 toFin =
                new BranchAndFinancialInstitutionIdentification6();
        FinancialInstitutionIdentification18 toId =
                new FinancialInstitutionIdentification18();

        toId.setBICFI(receiverBic);
        toFin.setFinInstnId(toId);
        to.setFIId(toFin);

        appHdr.setTo(to);

        // ===== Business Message Id
        appHdr.setBizMsgIdr(b4.getFieldValue("20"));
        System.out.println(b3.getField("121"));
        System.out.println(b3.getFieldValue("108"));

        if (!isDuplicate) {
            appHdr.setCpyDplct(CopyDuplicate1Code.DUPL);
        }
        // ===== Message Definition
        String mtType = SwiftMessageUtils.detectMtType(ast);
        if ("103".equals(mtType)){
            appHdr.setMsgDefIdr(msgDefIdr008);
         }else if("202".equals(mtType) || "202COV".equals(mtType)){
            appHdr.setMsgDefIdr(msgDefIdr009);
        }
        if(bizSvc!=null){
            appHdr.setBizSvc(bizSvc);
        }
        // ===== Creation Date
        GregorianCalendar cal =
                GregorianCalendar.from(OffsetDateTime.now().toZonedDateTime());

        XMLGregorianCalendar xmlCal =
                DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(cal);

        appHdr.setCreDt(xmlCal);

        return appHdr;
    }

}
