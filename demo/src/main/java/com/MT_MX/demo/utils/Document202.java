package com.MT_MX.demo.utils;

import com.MT_MX.demo.ast.*;
import com.MT_MX.demo.iso20022.pacs_009_001_12.*;
import com.MT_MX.demo.semantic.*;
import com.MT_MX.demo.semantic.OrderingInstitutionIdentifier;
import com.MT_MX.demo.semantic.parser.*;

import javax.xml.datatype.*;
import java.time.*;
import java.util.*;

public class Document202 {
private Document202(){}
    public static Document buildMX(SwiftAst ast) throws Exception {
        Document document = new Document();
        FinancialInstitutionCreditTransferV12 fiToFi =
                new FinancialInstitutionCreditTransferV12();

        BlockNode b1 = ast.getBlock(1);
        BlockNode b2 = ast.getBlock(2);
        BlockNode b3 = ast.getBlock(3);
        BlockNode b4 = ast.getBlock(4);

        // ======================================================
        // 1️⃣ GROUP HEADER
        // ======================================================
        GroupHeader131 grpHdr = new GroupHeader131();
        /// 108 if present
        String mur = b3.getFieldValue("108");
        if (mur != null) {
            grpHdr.setMsgId(mur);
        } else {
            grpHdr.setMsgId(b4.getFieldValue("20"));
        }
        GregorianCalendar cal = GregorianCalendar.from(OffsetDateTime.now().toZonedDateTime());

        XMLGregorianCalendar xmlCal =
                DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(cal);
        grpHdr.setCreDtTm(xmlCal);
        grpHdr.setNbOfTxs("1");
        // Settlement Info
        SettlementInstruction15 sttlmInf = new SettlementInstruction15();
        sttlmInf.setSttlmMtd(SettlementMethod1Code.INDA);
        grpHdr.setSttlmInf(sttlmInf);
        // Instructing Agent (BIC from Block 1)
        if (b1 != null) {
            BranchAndFinancialInstitutionIdentification8 instgAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(SwiftBicUtil.extractBic(b1));
            instgAgt.setFinInstnId(finInst);
            grpHdr.setInstgAgt(instgAgt);
        }
        /// InstdAgt
        if (b2 != null) {
            BranchAndFinancialInstitutionIdentification8 instdAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInstTo =
                    new FinancialInstitutionIdentification23();
            finInstTo.setBICFI(SwiftBicUtil.extractBic2(b2));

            instdAgt.setFinInstnId(finInstTo);

            grpHdr.setInstdAgt(instdAgt);
        }
        fiToFi.setGrpHdr(grpHdr);
        if (b4 == null) {
            return document;
        }
        // ======================================================
        // 2️⃣ TRANSACTION
        // ======================================================
        CreditTransferTransaction67 tx =
                new CreditTransferTransaction67();

        // --- Payment Identification ---
        PaymentIdentification13 pmtId = new PaymentIdentification13();
        pmtId.setInstrId(b4.getFieldValue("20"));
        pmtId.setEndToEndId(b4.getFieldValue("21"));

        if (b3 == null) {
//                throw new ValidationException("Block 3 missing in MT103");
        }
        if(b3.getFieldValue("121")!=null){
            String uetr = b3.getFieldValue("121");

            if (uetr == null || uetr.isBlank()) {
//              /  throw new ValidationException("Missing mandatory UETR (121)");
            }
            pmtId.setUETR(uetr.trim());
        }
        if(b3.getFieldValue("111")!=null) {
            PaymentTypeInformation28 pTI=new PaymentTypeInformation28();
            List<ServiceLevel8Choice> svcLvl = new ArrayList<>();
            ServiceLevel8Choice slc = new ServiceLevel8Choice();
            slc.setCd(b3.getFieldValue("111"));
            pTI.getSvcLvl().add(slc);
            tx.setPmtTpInf(pTI);
        }
        if(b3.getFieldValue("403")!=null) {
            InstructionForNextAgent1 instr = new InstructionForNextAgent1();
            instr.setInstrInf(b3.getFieldValue("403"));
            tx.getInstrForNxtAgt().add(instr);
        }
        tx.setPmtId(pmtId);

        /// 32A

        // ======================================================
        // 32A → Amount & Settlement Date
        // ======================================================
        FieldNode f32A = b4.getField("32A");
        if (f32A != null) {
            Field32AValue val = Field32AParser.parse(f32A);
            if (val != null) {
                ActiveCurrencyAndAmount amt1 =
                        new ActiveCurrencyAndAmount();
                amt1.setCcy(val.getCurrency());
                amt1.setValue(val.getAmount());
                tx.setIntrBkSttlmAmt(amt1);
                tx.setIntrBkSttlmDt(toXmlDate(val.getValueDate()));
            }
        }
        ///  52A
        // ======================================================
        // 52A
        // ======================================================
        if(b4.getFieldValue("52A") != null){
            String bic52A = Field52AParser.parse(b4.getField("52A"));
            tx.setDbtr(getBranchAndFinancialInstitutionIdentification(bic52A));
        }
        if(b4.getFieldValue("52B")!=null){
            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field52BParser.parse(b4.getField("52B"));
            tx.setDbtr(getBranchAndFinancialInst(orderingInstitutionIdentifier.getBic(),orderingInstitutionIdentifier.getLocation()));
            tx.setDbtrAcct(getCashAccount(orderingInstitutionIdentifier.getAccount()));
        }
        if(b4.getFieldValue("52D")!=null){
            OrderingInstitution orderingInstitution=Field52DParser.parse(b4.getField("52D"));
            tx.setDbtr(getBranchAndFinancialInst(orderingInstitution.getName(),orderingInstitution.getAddress()));
            tx.setDbtrAcct(getCashAccount(orderingInstitution.getAccount()));
        }
        // ======================================================
        // 53A
        // ======================================================
        if(b4.getFieldValue("53A") != null){
            String bic53A = Field53AParser.parse(b4.getField("53A"));
            tx.setDbtrAgt(getBranchAndFinancialInstitutionIdentification(bic53A));
        }
        if(b4.getFieldValue("53B")!=null){
            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field53BParser.parse(b4.getField("53B"));
            tx.setDbtrAgt(getBranchAndFinancialInst(orderingInstitutionIdentifier.getBic(),orderingInstitutionIdentifier.getLocation()));
            tx.setDbtrAgtAcct(getCashAccount(orderingInstitutionIdentifier.getAccount()));
        }
        if(b4.getFieldValue("53D")!=null){
            SenderCorrespondent senderCorrespondent=Field53DParser.parse(b4.getField("53D"));
            tx.setDbtrAgt(getBranchAndFinancialInst(senderCorrespondent.getName(),senderCorrespondent.getAddress()));
            tx.setDbtrAgtAcct(getCashAccount(senderCorrespondent.getAccount()));
        }
        //      ======================================================
        // 56A
        // ======================================================
        if(b4.getFieldValue("56A")!=null) {
            String bic56A = Field56AParser.parse(b4.getField("56A"));
            tx.setIntrmyAgt1(getBranchAndFinancialInstitutionIdentification(bic56A));
            //  cTT.setIntrmyAgt2(dbtrAgt);
        }
        if(b4.getFieldValue("56B")!=null){
            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field56BParser.parse(b4.getField("56B"));
            tx.setIntrmyAgt1(getBranchAndFinancialInst(orderingInstitutionIdentifier.getBic(),orderingInstitutionIdentifier.getLocation()));
            tx.setIntrmyAgt1Acct(getCashAccount(orderingInstitutionIdentifier.getAccount()));
        }
        if(b4.getFieldValue("56D")!=null){
            IntermediaryInstitution intermediaryInstitution=Field56DParser.parse(b4.getField("56D"));
            tx.setIntrmyAgt1(getBranchAndFinancialInst(intermediaryInstitution.getName(),intermediaryInstitution.getAddress()));
            tx.setIntrmyAgt1Acct(getCashAccount(intermediaryInstitution.getAccount()));

        }
        // 57A → Creditor Agent
        if ( b4.getFieldValue("57A")!=null) {
            String bic1 = Field57AParser.parse(b4.getField("54"));
            tx.setCdtrAgt(getBranchAndFinancialInstitutionIdentification(bic1));
        }
        if(b4.getFieldValue("57B")!=null){
            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field57BParser.parse(b4.getField("57B"));
            tx.setCdtrAgt(getBranchAndFinancialInst(orderingInstitutionIdentifier.getBic(),orderingInstitutionIdentifier.getLocation()));
            tx.setCdtrAgtAcct(getCashAccount(orderingInstitutionIdentifier.getAccount()));
        }
        if(b4.getFieldValue("57D")!=null){
            IntermediaryInstitution intermediaryInstitution=Field56DParser.parse(b4.getField("57D"));
            tx.setCdtrAgt(getBranchAndFinancialInst(intermediaryInstitution.getName(),intermediaryInstitution.getAddress()));
            tx.setCdtrAgtAcct(getCashAccount(intermediaryInstitution.getAccount()));
        }
        // ======================================================
        // 58A
        // ======================================================
        if(b4.getFieldValue("58A")!=null){
            String bic58A = b4.getFieldValue("58A");
            tx.setCdtr(getBranchAndFinancialInstitutionIdentification(bic58A));
        }
        /// need to work
        List<Field72Instruction> instructions =
                Field72Parser.parse(b4.getField("72"));
        String code=null;
        StringBuffer text =new StringBuffer();
        if (instructions != null && !instructions.isEmpty()) {
            for (Field72Instruction inst : instructions) {
                if(code==null) {
                    code = inst.getCode();
                }
                text.append(inst.getInstruction());
//                if (text.toString() == null || text.toString().isBlank()) {
//                    text = code;
//                }
            }
            switch (code.toUpperCase().trim()) {
                case "ACC":
                case "BNF":

                    InstructionForCreditorAgent3 cdtrInstr =
                            new InstructionForCreditorAgent3();
                    cdtrInstr.setInstrInf("/"+code+"/"+text);
                    tx.getInstrForCdtrAgt().add(cdtrInstr);
                    break;

                case "INS":
                case "INT":
                case "REC":
                case "PCAS":
                case "PRIORITY":
                case "FND":
                    InstructionForNextAgent1 nxtInstr =
                            new InstructionForNextAgent1();
                    nxtInstr.setInstrInf("/"+code+"/"+text);
                    tx.getInstrForNxtAgt().add(nxtInstr);
                    break;
                case "PREV1":
                case "PREV2":
                case "PREV3":
                    InstructionForNextAgent1 prevInstr = new InstructionForNextAgent1();
                    prevInstr.setInstrInf("/"+code+"/"+text);
                    tx.getInstrForNxtAgt().add(prevInstr);
                    break;
                default:
                    InstructionForNextAgent1 genInstr = new InstructionForNextAgent1();
                    genInstr.setInstrInf("/"+code+"/"+text);
                    tx.getInstrForNxtAgt().add(genInstr);
                    break;
            }
        }
        fiToFi.getCdtTrfTxInf().add(tx);
        document.setFICdtTrf(fiToFi);
        return document;
    }
    private static BranchAndFinancialInstitutionIdentification8 getBranchAndFinancialInstitutionIdentification(String bic){
        BranchAndFinancialInstitutionIdentification8 BranchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification8();

        FinancialInstitutionIdentification23 finInst =
                new FinancialInstitutionIdentification23();

        finInst.setBICFI(bic);

        BranchAndFinancialInstitutionIdentification.setFinInstnId(finInst);
        return BranchAndFinancialInstitutionIdentification;
    }
    private static BranchAndFinancialInstitutionIdentification8 getBranchAndFinancialInst(String name,List<String> address){
        FinancialInstitutionIdentification23 finInst =
                new FinancialInstitutionIdentification23();
        finInst.setNm(name);
        PostalAddress27 adr = new PostalAddress27();
        adr.getAdrLine().addAll(address);
        finInst.setPstlAdr(adr);
        BranchAndFinancialInstitutionIdentification8 branchAndFinancialInstitutionIdentification =
                new BranchAndFinancialInstitutionIdentification8();
        branchAndFinancialInstitutionIdentification.setFinInstnId(finInst);
        return branchAndFinancialInstitutionIdentification;
    }
    private static BranchAndFinancialInstitutionIdentification8 getBranchAndFinancialInst(String bic,String location){
        FinancialInstitutionIdentification23 finInst =
                new FinancialInstitutionIdentification23();
        finInst.setBICFI(bic);
        PostalAddress27 adr = new PostalAddress27();
        adr.setTwnNm(location);
        finInst.setPstlAdr(adr);
        BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                new BranchAndFinancialInstitutionIdentification8();
        dbtrAgt.setFinInstnId(finInst);
        return dbtrAgt;
    }
    private static CashAccount40 getCashAccount(String account) {
        CashAccount40 cashAccount = new CashAccount40();
        AccountIdentification4Choice accountIdentification4Choice = new AccountIdentification4Choice();
        GenericAccountIdentification1 genericAccountIdentification1 = new GenericAccountIdentification1();
        genericAccountIdentification1.setId(account);
        accountIdentification4Choice.setOthr(genericAccountIdentification1);
        accountIdentification4Choice.setOthr(genericAccountIdentification1);
        cashAccount.setId(accountIdentification4Choice);
        return cashAccount;
    }
    private static XMLGregorianCalendar toXmlDate(LocalDate date) {
        try {
            return DatatypeFactory.newInstance()
                    .newXMLGregorianCalendarDate(
                            date.getYear(),
                            date.getMonthValue(),
                            date.getDayOfMonth(),
                            DatatypeConstants.FIELD_UNDEFINED
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

//package com.MT_MX.demo.utils;
//
//import com.MT_MX.demo.ast.*;
//import com.MT_MX.demo.iso20022.pacs_009_001_12.*;
//import com.MT_MX.demo.semantic.*;
//import com.MT_MX.demo.semantic.OrderingInstitutionIdentifier;
//import com.MT_MX.demo.semantic.parser.*;
//
//import javax.xml.datatype.*;
//import java.time.*;
//import java.util.*;
//
//public class Document202 {
//
//    public static Document buildMX(SwiftAst ast) throws Exception {
//
//        Document document = new Document();
//        FinancialInstitutionCreditTransferV12 fiToFi =
//                new FinancialInstitutionCreditTransferV12();
//
//        BlockNode b1 = ast.getBlock(1);
//        BlockNode b2 = ast.getBlock(2);
//        BlockNode b3 = ast.getBlock(3);
//        BlockNode b4 = ast.getBlock(4);
//
//        // ======================================================
//        // 1️⃣ GROUP HEADER
//        // ======================================================
//       GroupHeader131 grpHdr = new GroupHeader131();
//        /// 108 if present
//        String mur = b3.getFieldValue("108");
//        if (mur != null) {
//            grpHdr.setMsgId(mur);
//        } else {
//            grpHdr.setMsgId(b4.getFieldValue("20"));
//        }
//        GregorianCalendar cal = GregorianCalendar.from(OffsetDateTime.now().toZonedDateTime());
//
//        XMLGregorianCalendar xmlCal =
//                DatatypeFactory.newInstance()
//                        .newXMLGregorianCalendar(cal);
//        grpHdr.setCreDtTm(xmlCal);
//        grpHdr.setNbOfTxs("1");
//
//        // Settlement Info
//        SettlementInstruction15 sttlmInf = new SettlementInstruction15();
//        sttlmInf.setSttlmMtd(SettlementMethod1Code.INDA);
//        grpHdr.setSttlmInf(sttlmInf);
//
//        // Instructing Agent (BIC from Block 1)
//        if (b1 != null) {
//            BranchAndFinancialInstitutionIdentification8 instgAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//
//            finInst.setBICFI(SwiftBicUtil.extractBic(b1));
//            instgAgt.setFinInstnId(finInst);
//            grpHdr.setInstgAgt(instgAgt);
//        }
//
//        /// InstdAgt
//        if (b2 != null) {
//            BranchAndFinancialInstitutionIdentification8 instdAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInstTo =
//                    new FinancialInstitutionIdentification23();
//            finInstTo.setBICFI(SwiftBicUtil.extractBic2(b2));
//
//            instdAgt.setFinInstnId(finInstTo);
//
//            grpHdr.setInstdAgt(instdAgt);
//        }
//        fiToFi.setGrpHdr(grpHdr);
//
//        if (b4 == null) {
//            return document;
//        }
//
//        // ======================================================
//        // 2️⃣ TRANSACTION
//        // ======================================================
//        CreditTransferTransaction67 tx =
//                new CreditTransferTransaction67();
//
//        // --- Payment Identification ---
//        PaymentIdentification13 pmtId = new PaymentIdentification13();
//        pmtId.setInstrId(b4.getFieldValue("20"));
//        pmtId.setEndToEndId(b4.getFieldValue("21"));
//
//        if (b3 == null) {
////                throw new ValidationException("Block 3 missing in MT103");
//        }
//        if(b3.getFieldValue("121")!=null){
//            String uetr = b3.getFieldValue("121");
//
//            if (uetr == null || uetr.isBlank()) {
////              /  throw new ValidationException("Missing mandatory UETR (121)");
//            }
//
//            pmtId.setUETR(uetr.trim());
//        }
//        if(b3.getFieldValue("111")!=null) {
//         PaymentTypeInformation28 pTI=new PaymentTypeInformation28();
//            List<ServiceLevel8Choice> svcLvl = new ArrayList<>();
//           ServiceLevel8Choice slc = new ServiceLevel8Choice();
//            slc.setCd(b3.getFieldValue("111"));
//            pTI.getSvcLvl().add(slc);
//            tx.setPmtTpInf(pTI);
//        }
//        if(b3.getFieldValue("403")!=null) {
//           InstructionForNextAgent1 instr = new InstructionForNextAgent1();
//            instr.setInstrInf(b3.getFieldValue("403"));
//            tx.getInstrForNxtAgt().add(instr);
//        }
//        tx.setPmtId(pmtId);
//
//        /// 32A
//
//        // ======================================================
//        // 32A → Amount & Settlement Date
//        // ======================================================
//        FieldNode f32A = b4.getField("32A");
//
//        if (f32A != null) {
//
//            Field32AValue val = Field32AParser.parse(f32A);
//            if (val != null) {
//                ActiveCurrencyAndAmount amt1 =
//                        new ActiveCurrencyAndAmount();
//
//                amt1.setCcy(val.getCurrency());
//                amt1.setValue(val.getAmount());
//
//                tx.setIntrBkSttlmAmt(amt1);
//
//                tx.setIntrBkSttlmDt(toXmlDate(val.getValueDate()));
//            }
//        }
//        ///  52A
//        // ======================================================
//        // 52A
//        // ======================================================
//        String bic = Field52AParser.parse(b4.getField("52A"));
//
//        if(bic != null){
//
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//           FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//
//            finInst.setBICFI(bic);
//
//            dbtrAgt.setFinInstnId(finInst);
//
//            tx.setDbtr(dbtrAgt);
//        }
//        if(b4.getFieldValue("52B")!=null){
//            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field52BParser.parse(b4.getField("52B"));
//            tx.setDbtr(getBranchAndFinancialInst(orderingInstitutionIdentifier));
//            tx.setDbtrAcct(getCashAccount(orderingInstitutionIdentifier));
//        }
//        if(b4.getFieldValue("52D")!=null){
//            OrderingInstitution orderingInstitution=Field52DParser.parse(b4.getField("52D"));
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//            finInst.setNm(orderingInstitution.getName());
//            PostalAddress27 adr = new PostalAddress27();
//            adr.getAdrLine().addAll(orderingInstitution.getAddress());
//            finInst.setPstlAdr(adr);
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//            dbtrAgt.setFinInstnId(finInst);
//            tx.setDbtr(dbtrAgt);
//            CashAccount40 cashAccount=new CashAccount40();
//            AccountIdentification4Choice accountIdentification4Choice=new AccountIdentification4Choice();
//            GenericAccountIdentification1 genericAccountIdentification1=new GenericAccountIdentification1();
//            genericAccountIdentification1.setId(orderingInstitution.getAccount());
//            accountIdentification4Choice.setOthr(genericAccountIdentification1);
//            accountIdentification4Choice.setOthr(genericAccountIdentification1);
//            cashAccount.setId(accountIdentification4Choice);
//            tx.setDbtrAcct(cashAccount);
//
//        }
//
//        //        // ======================================================
////        // 53A
////        // ======================================================
//        String bic53A = Field53AParser.parse(b4.getField("53A"));
//
//        if(bic53A != null){
//
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//
//            finInst.setBICFI(bic53A);
//
//            dbtrAgt.setFinInstnId(finInst);
//
//            tx.setDbtrAgt(dbtrAgt);
//        }
//        if(b4.getFieldValue("53B")!=null){
//            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field53BParser.parse(b4.getField("53B"));
//            tx.setDbtrAgt(getBranchAndFinancialInst(orderingInstitutionIdentifier));
//            tx.setDbtrAgtAcct(getCashAccount(orderingInstitutionIdentifier));
//        }
//        if(b4.getFieldValue("53D")!=null){
//            SenderCorrespondent senderCorrespondent=Field53DParser.parse(b4.getField("53D"));
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//            finInst.setNm(senderCorrespondent.getName());
//            PostalAddress27 adr = new PostalAddress27();
//            adr.getAdrLine().addAll(senderCorrespondent.getAddress());
//            finInst.setPstlAdr(adr);
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//            dbtrAgt.setFinInstnId(finInst);
//            tx.setDbtrAgt(dbtrAgt);
//            CashAccount40 cashAccount=new CashAccount40();
//            AccountIdentification4Choice accountIdentification4Choice=new AccountIdentification4Choice();
//            GenericAccountIdentification1 genericAccountIdentification1=new GenericAccountIdentification1();
//            genericAccountIdentification1.setId(senderCorrespondent.getAccount());
//            accountIdentification4Choice.setOthr(genericAccountIdentification1);
//            accountIdentification4Choice.setOthr(genericAccountIdentification1);
//            cashAccount.setId(accountIdentification4Choice);
//            tx.setDbtrAgtAcct(cashAccount);
//
//        }
//
//        //      ======================================================
//        // 56A
//        // ======================================================
//        if(b4.getField("56A")!=null) {
//            String bic56A = Field56AParser.parse(b4.getField("56A"));
//
//            if (bic56A != null) {
//
//                BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                        new BranchAndFinancialInstitutionIdentification8();
//
//                FinancialInstitutionIdentification23 finInst =
//                        new FinancialInstitutionIdentification23();
//
//                finInst.setBICFI(bic56A);
//
//                dbtrAgt.setFinInstnId(finInst);
//
//                tx.setIntrmyAgt1(dbtrAgt);
//              //  cTT.setIntrmyAgt2(dbtrAgt);
//
//            }
//        }
//
//        if(b4.getFieldValue("56B")!=null){
//            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field56BParser.parse(b4.getField("56B"));
//
//            tx.setIntrmyAgt1(getBranchAndFinancialInst(orderingInstitutionIdentifier));
//            tx.setIntrmyAgt1Acct(getCashAccount(orderingInstitutionIdentifier));
//
//        }
//                    if(b4.getFieldValue("56D")!=null){
//                        IntermediaryInstitution intermediaryInstitution=Field56DParser.parse(b4.getField("56D"));
//                        FinancialInstitutionIdentification23 finInst =
//                                new FinancialInstitutionIdentification23();
//                        finInst.setNm(intermediaryInstitution.getName());
//                        PostalAddress27 adr = new PostalAddress27();
//                        adr.getAdrLine().addAll(intermediaryInstitution.getAddress());
//                        finInst.setPstlAdr(adr);
//                        BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                                new BranchAndFinancialInstitutionIdentification8();
//                        dbtrAgt.setFinInstnId(finInst);
//                        tx.setIntrmyAgt1(dbtrAgt);
//                        CashAccount40 cashAccount=new CashAccount40();
//                        AccountIdentification4Choice accountIdentification4Choice=new AccountIdentification4Choice();
//                        GenericAccountIdentification1 genericAccountIdentification1=new GenericAccountIdentification1();
//                        genericAccountIdentification1.setId(intermediaryInstitution.getAccount());
//                        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//                        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//                        cashAccount.setId(accountIdentification4Choice);
//                        tx.setIntrmyAgt1Acct(cashAccount);
//
//                    }
//
//        // 57A → Creditor Agent
//        FieldNode f57A = b4.getField("57A");
//        if (f57A != null && f57A.getValue() != null && !f57A.getValue().isBlank()) {
//            String bic1 = SwiftBicUtil.normalizeBic(f57A.getValue());
//
//            BranchAndFinancialInstitutionIdentification8 cdtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//          FinancialInstitutionIdentification23 finInst = new FinancialInstitutionIdentification23();
//            finInst.setBICFI(bic1);
//            cdtrAgt.setFinInstnId(finInst);
//            tx.setCdtrAgt(cdtrAgt);
//        }
//        if(b4.getFieldValue("57B")!=null){
//            OrderingInstitutionIdentifier orderingInstitutionIdentifier=Field57BParser.parse(b4.getField("57B"));
//            tx.setCdtrAgt(getBranchAndFinancialInst(orderingInstitutionIdentifier));
//            tx.setCdtrAgtAcct(getCashAccount(orderingInstitutionIdentifier));
//
//        }
//        if(b4.getFieldValue("57D")!=null){
//            IntermediaryInstitution intermediaryInstitution=Field56DParser.parse(b4.getField("57D"));
//            tx.setCdtrAgt(getBranchAndFinancialInst(intermediaryInstitution.getName(),intermediaryInstitution.getAddress()));
//            tx.setCdtrAgtAcct(getCashAccount(intermediaryInstitution.getAccount()));
//
//        }
//
//        // ======================================================
//        // 58A
//        // ======================================================
//        String bic58A = b4.getFieldValue("58A");
//        System.out.println("bic58A"+bic58A);
//        if(bic58A != null){
//
//            BranchAndFinancialInstitutionIdentification8 cdtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//            finInst.setBICFI(bic58A);
//
//            cdtrAgt.setFinInstnId(finInst);
//
//            tx.setCdtr(cdtrAgt);
//        }
//        /// need to work
//        List<Field72Instruction> instructions =
//                Field72Parser.parse(b4.getField("72"));
//int c=0;
//      String code=null;
//        StringBuffer text =new StringBuffer();
//        if (instructions != null && !instructions.isEmpty()) {
//
//            for (Field72Instruction inst : instructions) {
//                if(code==null) {
//                    code = inst.getCode();
//                }
//                 text.append(inst.getInstruction());
////                if (text.toString() == null || text.toString().isBlank()) {
////                    text = code;
////                }
//            }
//
//
//                switch (code.toUpperCase().trim()) {
//                    case "ACC":
//                    case "BNF":
//
//                        InstructionForCreditorAgent3 cdtrInstr =
//                                new InstructionForCreditorAgent3();
//                        cdtrInstr.setInstrInf("/"+code+"/"+text);
//                        tx.getInstrForCdtrAgt().add(cdtrInstr);
//                        break;
//
//                    case "INS":
//                    case "INT":
//                    case "REC":
//                    case "PCAS":
//                    case "PRIORITY":
//                    case "FND":
//                        InstructionForNextAgent1 nxtInstr =
//                                new InstructionForNextAgent1();
//                        nxtInstr.setInstrInf("/"+code+"/"+text);
//                        tx.getInstrForNxtAgt().add(nxtInstr);
//                        break;
//                    case "PREV1":
//                    case "PREV2":
//                    case "PREV3":
//                        InstructionForNextAgent1 prevInstr = new InstructionForNextAgent1();
//                        prevInstr.setInstrInf("/"+code+"/"+text);
//                        tx.getInstrForNxtAgt().add(prevInstr);
//                        break;
//                    default:
//                        InstructionForNextAgent1 genInstr = new InstructionForNextAgent1();
//                        genInstr.setInstrInf("/"+code+"/"+text);
//                        tx.getInstrForNxtAgt().add(genInstr);
//                        break;
//
//                }
//
//        }
//        fiToFi.getCdtTrfTxInf().add(tx);
//        document.setFICdtTrf(fiToFi);
//           return document;
//    }
//    private static BranchAndFinancialInstitutionIdentification8 getBranchAndFinancialInst(String name,List<String> address){
//        FinancialInstitutionIdentification23 finInst =
//                new FinancialInstitutionIdentification23();
//        finInst.setNm(name);
//        PostalAddress27 adr = new PostalAddress27();
//        adr.getAdrLine().addAll(address);
//        finInst.setPstlAdr(adr);
//        BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                new BranchAndFinancialInstitutionIdentification8();
//        dbtrAgt.setFinInstnId(finInst);
//        return dbtrAgt;
//    }
//    private static CashAccount40 getCashAccount(OrderingInstitution orderingInstitution) {
//        CashAccount40 cashAccount = new CashAccount40();
//        AccountIdentification4Choice accountIdentification4Choice = new AccountIdentification4Choice();
//        GenericAccountIdentification1 genericAccountIdentification1 = new GenericAccountIdentification1();
//        genericAccountIdentification1.setId(orderingInstitution.getAccount());
//        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//        cashAccount.setId(accountIdentification4Choice);
//        return cashAccount;
//    }
//    private static BranchAndFinancialInstitutionIdentification8 getBranchAndFinancialInst(OrderingInstitutionIdentifier orderingInstitutionIdentifier){
//        FinancialInstitutionIdentification23 finInst =
//                new FinancialInstitutionIdentification23();
//        finInst.setBICFI(orderingInstitutionIdentifier.getBic());
//        PostalAddress27 adr = new PostalAddress27();
//        adr.setTwnNm(orderingInstitutionIdentifier.getLocation());
//        finInst.setPstlAdr(adr);
//        BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                new BranchAndFinancialInstitutionIdentification8();
//        dbtrAgt.setFinInstnId(finInst);
//        return dbtrAgt;
//    }
//    private static CashAccount40 getCashAccount(String account) {
//        CashAccount40 cashAccount = new CashAccount40();
//        AccountIdentification4Choice accountIdentification4Choice = new AccountIdentification4Choice();
//        GenericAccountIdentification1 genericAccountIdentification1 = new GenericAccountIdentification1();
//        genericAccountIdentification1.setId(account);
//        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//        cashAccount.setId(accountIdentification4Choice);
//        return cashAccount;
//    }
//    private static CashAccount40 getCashAccount(OrderingInstitutionIdentifier orderingInstitutionIdentifier) {
//        CashAccount40 cashAccount = new CashAccount40();
//        AccountIdentification4Choice accountIdentification4Choice = new AccountIdentification4Choice();
//        GenericAccountIdentification1 genericAccountIdentification1 = new GenericAccountIdentification1();
//        genericAccountIdentification1.setId(orderingInstitutionIdentifier.getAccount());
//        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//        accountIdentification4Choice.setOthr(genericAccountIdentification1);
//        cashAccount.setId(accountIdentification4Choice);
//    return cashAccount;
//    }
//    private static XMLGregorianCalendar toXmlDate(LocalDate date) {
//        try {
//            return DatatypeFactory.newInstance()
//                    .newXMLGregorianCalendarDate(
//                            date.getYear(),
//                            date.getMonthValue(),
//                            date.getDayOfMonth(),
//                            DatatypeConstants.FIELD_UNDEFINED
//                    );
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}

//package com.MT_MX.demo.utils;
//
//import com.MT_MX.demo.ast.*;
//import com.MT_MX.demo.iso20022.pacs_009_001_12.*;
//import com.MT_MX.demo.semantic.BeneficiaryCustomer;
//import com.MT_MX.demo.semantic.Field32AValue;
//import com.MT_MX.demo.semantic.parser.*;
//
//import javax.xml.datatype.*;
//import java.math.BigDecimal;
//import java.time.*;
//import java.util.*;
//
//public class Document202 {
//
//    public static Document buildMX(SwiftAst ast) throws Exception {
//
//        Document document = new Document();
//        FinancialInstitutionCreditTransferV12 fiToFi =
//                new FinancialInstitutionCreditTransferV12();
//
//        BlockNode b1 = ast.getBlock(1);
//        BlockNode b2 = ast.getBlock(2);
//        BlockNode b3 = ast.getBlock(3);
//        BlockNode b4 = ast.getBlock(4);
//
//        // ======================================================
//        // GROUP HEADER
//        // ======================================================
//        GroupHeader131 grpHdr = new GroupHeader131();
//
//        grpHdr.setMsgId(
//                b3.getFieldValue("108") != null
//                        ? b3.getFieldValue("108")
//                        : b4.getFieldValue("20")
//        );
//
//        GregorianCalendar cal =
//                GregorianCalendar.from(OffsetDateTime.now().toZonedDateTime());
//
//        XMLGregorianCalendar xmlCal =
//                DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
//
//        grpHdr.setCreDtTm(xmlCal);
//        grpHdr.setNbOfTxs("1");
//
//        SettlementInstruction15 stlm = new SettlementInstruction15();
//        stlm.setSttlmMtd(SettlementMethod1Code.INDA);
//        grpHdr.setSttlmInf(stlm);
//
//        // Instg Agent
//        if (b1 != null) grpHdr.setInstgAgt(toAgent(SwiftBicUtil.extractBic(b1)));
//
//        // Instd Agent
//        if (b2 != null) grpHdr.setInstdAgt(toAgent(SwiftBicUtil.extractBic(b2)));
//
//        // ======================================================
//        // TRANSACTION
//        // ======================================================
//        if (b4 == null) {
//            fiToFi.setGrpHdr(grpHdr);
//            document.setFICdtTrf(fiToFi);
//            return document;
//        }
//
//        CreditTransferTransaction67 ctt = new CreditTransferTransaction67();
//
//        // ======================================================
//        // PAYMENT ID
//        // ======================================================
//        PaymentIdentification13 pmtId = new PaymentIdentification13();
//        pmtId.setInstrId(b4.getFieldValue("20"));
//        pmtId.setEndToEndId(b4.getFieldValue("21"));
//
//        String uetr = b3.getFieldValue("121");
//        if (uetr == null) throw new RuntimeException("UETR missing");
//
//        pmtId.setUETR(uetr.trim());
//        ctt.setPmtId(pmtId);
//
//        // ======================================================
//        // 32A
//        // ======================================================
//        FieldNode f32A = b4.getField("32A");
//        if (f32A != null) {
//            Field32AValue val = Field32AParser.parse(f32A);
//
//            ActiveCurrencyAndAmount amt = new ActiveCurrencyAndAmount();
//            amt.setCcy(val.getCurrency());
//            amt.setValue(val.getAmount());
//
//            ctt.setIntrBkSttlmAmt(amt);
//            ctt.setIntrBkSttlmDt(toXmlDate(val.getValueDate()));
//        }
//
//        // ======================================================
//        // 33B
//        // ======================================================
//        String value33B = b4.getFieldValue("33B");
//        if (value33B != null) {
//
//            String ccy = value33B.substring(0, 3);
//            BigDecimal amt = new BigDecimal(
//                    value33B.substring(3).replace(",", ".")
//            );
//
//            CreditTransferTransaction68 u = new CreditTransferTransaction68();
//            ActiveOrHistoricCurrencyAndAmount instdAmt =
//                    new ActiveOrHistoricCurrencyAndAmount();
//
//            instdAmt.setCcy(ccy);
//            instdAmt.setValue(amt);
//
//            u.setInstdAmt(instdAmt);
//            ctt.setUndrlygCstmrCdtTrf(u);
//        }
//
//        // ======================================================
//        // DEBTOR (50)
//        // ======================================================
//        if (b4.getFieldValue("50") != null) {
//            String ben=Field50AParser.parse(b4.getField("50A"));
//            ctt.setCdtr(toAgent(ben));
//        }
//
//        // ======================================================
//        // DEBTOR AGENT (52A)
//        // ======================================================
//        String bic52A = Field52AParser.parse(b4.getField("52A"));
//        if (bic52A != null) {
//            ctt.setDbtrAgt(toAgent(bic52A));
//        }
//
//        // ======================================================
//        // INTERMEDIARY AGENT 1 (53A)
//        // ======================================================
//        String bic53A = Field53AParser.parse(b4.getField("53A"));
//        if (bic53A != null) {
//            ctt.setIntrmyAgt1(toAgent(bic53A));
//        }
//
//        // 53B ACCOUNT
//        if (b4.getFieldValue("53B") != null) {
//            ctt.setIntrmyAgt1Acct(parseAccount(b4.getFieldValue("53B")));
//        }
//
//        // ======================================================
//        // INTERMEDIARY AGENT 2 (56A)
//        // ======================================================
//        if(b4.getField("56A")!=null) {
//            String bic56A = Field56AParser.parse(b4.getField("56A"));
//            if (bic56A != null) {
//                ctt.setIntrmyAgt2(toAgent(bic56A));
//            }
//        }
//
//        // ======================================================
//        // CREDITOR AGENT (57A)
//        // ======================================================
//        String bic57A = Field57AParser.parse(b4.getField("57A"));
//        if (bic57A != null) {
//            ctt.setCdtrAgt(toAgent(bic57A));
//        }
//
//        // ======================================================
//        // CREDITOR (59)
//        // ======================================================
//        BeneficiaryCustomer ben =
//                Field59Parser.parse(b4.getField("59"));
//
//        if (ben != null) {
//            ctt.setCdtr(toAgent(ben.getName()));
//
//            ctt.setCdtrAcct(parseAccountFromBeneficiary(ben));
//        }
//
//        // ======================================================
//        // 72 Instructions
//        // ======================================================
//        List<Field72Instruction> instructions =
//                Field72Parser.parse(b4.getField("72"));
//
//        if (instructions != null) {
//            for (Field72Instruction inst : instructions) {
//
//                String code = inst.getCode() != null
//                        ? inst.getCode().toUpperCase()
//                        : "";
//
//                String text = inst.getInstruction();
//
//                if (text == null) text = code;
//
//                switch (code) {
//
//                    case "ACC":
//                    case "BNF":
//                        InstructionForCreditorAgent3 cd =
//                                new InstructionForCreditorAgent3();
//                        cd.setInstrInf(text);
//                        ctt.getInstrForCdtrAgt().add(cd);
//                        break;
//
//                    case "INS":
//                    case "INT":
//                    case "REC":
//                    case "PRIORITY":
//                    case "FND":
//                        InstructionForNextAgent1 nx =
//                                new InstructionForNextAgent1();
//                        nx.setInstrInf(text);
//                        ctt.getInstrForNxtAgt().add(nx);
//                        break;
//
//                    default:
//                        SupplementaryData1 sup = new SupplementaryData1();
//                        sup.setPlcAndNm("Field72");
//                        ctt.getSplmtryData().add(sup);
//                }
//            }
//        }
//
//        // ======================================================
//        // FINAL ASSEMBLY
//        // ======================================================
//        fiToFi.setGrpHdr(grpHdr);
//        fiToFi.getCdtTrfTxInf().add(ctt);
//        document.setFICdtTrf(fiToFi);
//
//        return document;
//    }
//
//    // ======================================================
//    // HELPERS (CORRECT ISO 20022 TYPES)
//    // ======================================================
//
//    private static BranchAndFinancialInstitutionIdentification8 toAgent(String bic) {
//        if (bic == null) return null;
//
//        BranchAndFinancialInstitutionIdentification8 agt =
//                new BranchAndFinancialInstitutionIdentification8();
//
//        FinancialInstitutionIdentification23 fin =
//                new FinancialInstitutionIdentification23();
//
//        fin.setBICFI(bic);
//        agt.setFinInstnId(fin);
//
//        return agt;
//    }
//
//    private static PartyIdentification272 parseParty(String name) {
//        PartyIdentification272 p = new PartyIdentification272();
//        p.setNm(name);
//        return p;
//    }
//
//    private static PartyIdentification272 parsePartyFromBeneficiary(BeneficiaryCustomer ben) {
//        PartyIdentification272 p = new PartyIdentification272();
//        p.setNm(ben.getName());
//
//        if (ben.getAddress() != null) {
//            PostalAddress27 adr = new PostalAddress27();
//            adr.getAdrLine().addAll(ben.getAddress());
//            p.setPstlAdr(adr);
//        }
//
//        return p;
//    }
//
//    private static CashAccount40 parseAccount(String acc) {
//        CashAccount40 ca = new CashAccount40();
//
//        AccountIdentification4Choice id = new AccountIdentification4Choice();
//        GenericAccountIdentification1 othr = new GenericAccountIdentification1();
//
//        othr.setId(acc);
//        id.setOthr(othr);
//
//        ca.setId(id);
//        return ca;
//    }
//
//    private static CashAccount40 parseAccountFromBeneficiary(BeneficiaryCustomer ben) {
//        if (ben.getAccount() == null) return null;
//        return parseAccount(ben.getAccount());
//    }
//
//    private static XMLGregorianCalendar toXmlDate(LocalDate date) {
//        try {
//            return DatatypeFactory.newInstance()
//                    .newXMLGregorianCalendarDate(
//                            date.getYear(),
//                            date.getMonthValue(),
//                            date.getDayOfMonth(),
//                            DatatypeConstants.FIELD_UNDEFINED
//                    );
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//package com.MT_MX.demo.utils;
//
//import com.MT_MX.demo.ast.BlockNode;
//import com.MT_MX.demo.ast.FieldNode;
//import com.MT_MX.demo.ast.SwiftAst;
//import com.MT_MX.demo.iso20022.pacs_009_001_12.*;
//import com.MT_MX.demo.semantic.*;
//import com.MT_MX.demo.semantic.parser.*;
//
//import javax.xml.datatype.DatatypeConstants;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.datatype.XMLGregorianCalendar;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.OffsetDateTime;
//import java.util.GregorianCalendar;
//import java.util.List;
//
//public class Document202 {
//
//    public static Document buildMX(SwiftAst ast) throws Exception {
//        Document document=new Document();
//
//        FinancialInstitutionCreditTransferV12 fiToFi =new FinancialInstitutionCreditTransferV12();
//        BlockNode b1=ast.getBlock(1);
//        BlockNode b2=ast.getBlock(2);
//        BlockNode b3=ast.getBlock(3);
//        BlockNode b4=ast.getBlock(4);
//
//       GroupHeader131 grpHdr=new GroupHeader131();
//        String mur = b3.getFieldValue("108");
//        if (mur != null) {
//            grpHdr.setMsgId(mur);
//        } else {
//            grpHdr.setMsgId(b4.getFieldValue("20"));
//        }
//        System.out.println("108"+b3.getFieldValue("108"));
//
//        GregorianCalendar cal = GregorianCalendar.from(OffsetDateTime.now().toZonedDateTime());
//
//        XMLGregorianCalendar xmlCal =
//                DatatypeFactory.newInstance()
//                        .newXMLGregorianCalendar(cal);
//
//        grpHdr.setCreDtTm(xmlCal);
//        grpHdr.setNbOfTxs("1");
//
//        SettlementInstruction15 stlmInf=new SettlementInstruction15();
//         stlmInf.setSttlmMtd(SettlementMethod1Code.INDA);
//         grpHdr.setSttlmInf(stlmInf);
//     if(b1!=null){
//         BranchAndFinancialInstitutionIdentification8 instgAgt=new BranchAndFinancialInstitutionIdentification8();
//         FinancialInstitutionIdentification23 finInst=new FinancialInstitutionIdentification23();
//         finInst.setBICFI(SwiftBicUtil.extractBic(b1));
//         instgAgt.setFinInstnId(finInst);
//         grpHdr.setInstdAgt(instgAgt);
//     }
//
//     if (b2 != null) {
//            BranchAndFinancialInstitutionIdentification8 instdAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//           FinancialInstitutionIdentification23 finInstTo =
//                    new FinancialInstitutionIdentification23();
//
//            finInstTo.setBICFI(SwiftBicUtil.extractBic(b2));
//
//            instdAgt.setFinInstnId(finInstTo);
//
//            grpHdr.setInstdAgt(instdAgt);
//     }
//      if (b4 == null) {
//            document.setFICdtTrf(fiToFi);
//            return document;
//      }
//     /// 20
//        CreditTransferTransaction67 cTT=new CreditTransferTransaction67();
//        PaymentIdentification13 pI=new PaymentIdentification13();
//        System.out.println("kjjdhdbc"+b4.getFieldValue("20"));
//        pI.setInstrId(b4.getFieldValue("20"));
//        pI.setEndToEndId(b4.getFieldValue("21"));
//
//        if(b3.getFieldValue("121")==null){
//            throw new Exception("UTER is missing");
//        }
//        String uter=b3.getFieldValue("121");
//        pI.setUETR(uter.trim());
//        cTT.setPmtId(pI);
//        if(b3.getFieldValue("111")!=null) {
//            PaymentTypeInformation28 pTI=new PaymentTypeInformation28();
//            //List<ServiceLevel8Choice> svcLvl = new ArrayList<>();
//            ServiceLevel8Choice slc = new ServiceLevel8Choice();
//            slc.setCd(b3.getFieldValue("111"));
//          //  svcLvl.add(slc);
//            pTI.getSvcLvl().add(slc);
//            cTT.setPmtTpInf(pTI);
//        }
//        if(b3.getFieldValue("403")!=null) {
//            InstructionForNextAgent1 instr = new InstructionForNextAgent1();
//            instr.setInstrInf(b3.getFieldValue("403"));
//            cTT.getInstrForNxtAgt().add(instr);
//        }
//        cTT.setPmtId(pI);
//        // ======================================================
//        // 32A → Amount & Settlement Date
//        // ======================================================
//        FieldNode f32A = b4.getField("32A");
//
//        if (f32A != null) {
//
//            Field32AValue val = Field32AParser.parse(f32A);
//            if (val != null) {
//                ActiveCurrencyAndAmount amt1 =
//                        new ActiveCurrencyAndAmount();
//
//                amt1.setCcy(val.getCurrency());
//                amt1.setValue(val.getAmount());
//
//                cTT.setIntrBkSttlmAmt(amt1);
//
//                cTT.setIntrBkSttlmDt(toXmlDate(val.getValueDate()));
//            }
//        }
//        String value33B = b4.getFieldValue("33B");
//        if(value33B!=null) {
//            CreditTransferTransaction68 creditTransferTransaction68 = new CreditTransferTransaction68();
//            String currency = value33B.substring(0, 3);
//            BigDecimal amount = new BigDecimal(value33B.substring(3).replace(",", "."));
//
//            ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
//            instdAmt.setCcy(currency);
//            instdAmt.setValue(amount);
//            creditTransferTransaction68.setInstdAmt(instdAmt);
//            cTT.setUndrlygCstmrCdtTrf(creditTransferTransaction68);
//        }
//        // ======================================================
//        // 52A
//        // ======================================================
//        String bic52A = Field52AParser.parse(b4.getField("52A"));
//
//        if(bic52A != null){
//
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//
//            finInst.setBICFI(bic52A);
//
//            dbtrAgt.setFinInstnId(finInst);
//
//           // cTT.setDbtr(dbtrAgt);
//            cTT.setDbtrAgt(dbtrAgt);
//        }
//
//        // ======================================================
//        // 53A
//        // ======================================================
//        String bic53A = Field53AParser.parse(b4.getField("53A"));
//
//        if(bic53A != null){
//
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//
//            finInst.setBICFI(bic53A);
//
//            dbtrAgt.setFinInstnId(finInst);
//
//            cTT.setIntrmyAgt1(dbtrAgt);
//        }
//        if(b4.getFieldValue("53B")!=null){
//            String accountNum=Field53BParser.parse(b4.getField("53B"));
//            CashAccount40 intrmyAcct = new CashAccount40();
//            AccountIdentification4Choice id = new AccountIdentification4Choice();
//            GenericAccountIdentification1 gen = new GenericAccountIdentification1();
//            gen.setId(accountNum);
//            id.setOthr(gen);
//            intrmyAcct.setId(id);
//            cTT.setIntrmyAgt1Acct(intrmyAcct);
//        }
//       /// 53D Sender’s Correspondent
//
////        if(b4.getFieldValue("53D")!=null){
////            SenderCorrespondent senderCorrespondent53D=Field53DParser.parse(b4.getField("53D"));
////            FinancialInstitutionIdentification23 finInst =
////                    new FinancialInstitutionIdentification23();
////            finInst.setNm(senderCorrespondent53D.getName());
////           PostalAddress27 adr = new PostalAddress27();
////            adr.getAdrLine().addAll(senderCorrespondent53D.getAddress());
////            finInst.setPstlAdr(adr);
////            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
////                    new BranchAndFinancialInstitutionIdentification8();
////            dbtrAgt.setFinInstnId(finInst);
////            cTT.setDbtrAgt(dbtrAgt);
////        }
//
//
//
//
//
//        // ======================================================
//        // 56A
//        // ======================================================
//        if(b4.getField("56A")!=null) {
//            String bic56A = Field56AParser.parse(b4.getField("56A"));
//
//            if (bic56A != null) {
//
//                BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                        new BranchAndFinancialInstitutionIdentification8();
//
//                FinancialInstitutionIdentification23 finInst =
//                        new FinancialInstitutionIdentification23();
//
//                finInst.setBICFI(bic56A);
//
//                dbtrAgt.setFinInstnId(finInst);
//
////                cTT.setIntrmyAgt1(dbtrAgt);
//                cTT.setIntrmyAgt2(dbtrAgt);
//
//            }
//        }
////        if(b4.getFieldValue("56D")!=null){
////            IntermediaryInstitution intermediaryInstitution=Field56DParser.parse(b4.getField("56D"));
////            FinancialInstitutionIdentification23 finInst =
////                    new FinancialInstitutionIdentification23();
////            finInst.setNm(intermediaryInstitution.getName());
////            PostalAddress27 adr = new PostalAddress27();
////            adr.getAdrLine().addAll(intermediaryInstitution.getAddress());
////            finInst.setPstlAdr(adr);
////            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
////                    new BranchAndFinancialInstitutionIdentification8();
////            dbtrAgt.setFinInstnId(finInst);
////            cTT.setIntrmyAgt1(dbtrAgt);
////        }
//        // ======================================================
//        // 57A
//        // ======================================================
//        String bic57A = Field57AParser.parse(b4.getField("57A"));
//
//        if(bic57A != null){
//
//            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
//                    new BranchAndFinancialInstitutionIdentification8();
//
//            FinancialInstitutionIdentification23 finInst =
//                    new FinancialInstitutionIdentification23();
//
//            finInst.setBICFI(bic57A);
//
//            dbtrAgt.setFinInstnId(finInst);
//
//            cTT.setCdtrAgt(dbtrAgt);
//        }
////        if(b4.getFieldValue("57D")!=null){
////            AccountInstitution accountInstitution57D=Field57DParser.parse(b4.getField("57D"));
////            FinancialInstitutionIdentification23 finInst =
////                    new FinancialInstitutionIdentification23();
////            finInst.setNm(accountInstitution57D.getName());
////            PostalAddress27 adr = new PostalAddress27();
////            adr.getAdrLine().addAll(accountInstitution57D.getAddress());
////            finInst.setPstlAdr(adr);
////            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
////                    new BranchAndFinancialInstitutionIdentification8();
////            dbtrAgt.setFinInstnId(finInst);
////            cTT.setCdtrAgt(dbtrAgt);
////        }
////        // ======================================================
////        // 58A
////        // ======================================================
////        String bic58A = Field58AParser.parse(b4.getField("58A"));
////
////        if(bic58A != null){
////
////            BranchAndFinancialInstitutionIdentification8 cdtrAgt =
////                    new BranchAndFinancialInstitutionIdentification8();
////
////            FinancialInstitutionIdentification23 finInst =
////                    new FinancialInstitutionIdentification23();
////
////            finInst.setBICFI(bic58A);
////
////            cdtrAgt.setFinInstnId(finInst);
////
////            cTT.setCdtr(cdtrAgt);
////        }
////        if(b4.getFieldValue("58D")!=null){
////            BeneficiaryInstitution beneficiaryInstitution58D=Field58DParser.parse(b4.getField("58D"));
////            FinancialInstitutionIdentification23 finInst =
////                    new FinancialInstitutionIdentification23();
////            finInst.setNm(beneficiaryInstitution58D.getName());
////            PostalAddress27 adr = new PostalAddress27();
////            adr.getAdrLine().addAll(beneficiaryInstitution58D.getAddress());
////            finInst.setPstlAdr(adr);
////            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
////                    new BranchAndFinancialInstitutionIdentification8();
////            dbtrAgt.setFinInstnId(finInst);
////            cTT.setCdtr(dbtrAgt);
////        }
//
//        // ======================================================
//        // 59 → Creditor
//        // ======================================================
//        BeneficiaryCustomer ben =
//                Field59Parser.parse(b4.getField("59"));
//
//        if (ben != null) {
//            BranchAndFinancialInstitutionIdentification8 branchAndFinancialInstitutionIdentification8=new BranchAndFinancialInstitutionIdentification8();
//            FinancialInstitutionIdentification23 financialInstitutionIdentification23=new FinancialInstitutionIdentification23();
//            financialInstitutionIdentification23.setNm(ben.getName());
//
//            if (ben.getAddress() != null) {
//                PostalAddress27 adr = new PostalAddress27();
//                adr.getAdrLine().addAll(ben.getAddress());
//                financialInstitutionIdentification23.setPstlAdr(adr);
//            }
//          branchAndFinancialInstitutionIdentification8.setFinInstnId(financialInstitutionIdentification23);
//           cTT.setCdtr(branchAndFinancialInstitutionIdentification8);
//
//            CashAccount40 cdtrAcct = new CashAccount40();
//            AccountIdentification4Choice acctId =
//                    new AccountIdentification4Choice();
//
//            GenericAccountIdentification1 othr =
//                    new GenericAccountIdentification1();
//            othr.setId(ben.getAccount());
//
//            acctId.setOthr(othr);
//            cdtrAcct.setId(acctId);
//
//            cTT.setCdtrAcct(cdtrAcct);
//        }
//        List<Field72Instruction> instructions =
//                Field72Parser.parse(b4.getField("72"));
//
//        if (instructions != null && !instructions.isEmpty()) {
//            for (Field72Instruction inst : instructions) {
//
//                String code = inst.getCode();
//                String text = inst.getInstruction();
//
//                if (text == null || text.isBlank()) {
//                    text = code;
//                }
//                System.out.println(code.toUpperCase());
//
//                switch (code.toUpperCase().trim()) {
//                    case "ACC":
//                    case "BNF":
//                        InstructionForCreditorAgent3 cdtrInstr =
//                                new InstructionForCreditorAgent3();
//                        cdtrInstr.setInstrInf(text);
//                        cTT.getInstrForCdtrAgt().add(cdtrInstr);
//                        break;
//
//                    case "INS":
//                    case "INT":
//                    case "REC":
//                    case "PCAS":
//                    case "PRIORITY":
//                    case "FND":
//                        InstructionForNextAgent1 nxtInstr =
//                                new InstructionForNextAgent1();
//                        nxtInstr.setInstrInf(text);
//                        cTT.getInstrForNxtAgt().add(nxtInstr);
//                        break;
//
//                    default:
//                        // Unknown → SupplementaryData
//                        SupplementaryData1 sup = new SupplementaryData1();
//                        sup.setPlcAndNm("Field72");
//                        cTT.getSplmtryData().add(sup);
//                }
//            }
//        }
//        fiToFi.setGrpHdr(grpHdr);
//
//        fiToFi.getCdtTrfTxInf().add(cTT);
//        document.setFICdtTrf(fiToFi);
//        return document;
//    }
//    private static XMLGregorianCalendar toXmlDate(LocalDate date) {
//        try {
//            return DatatypeFactory.newInstance()
//                    .newXMLGregorianCalendarDate(
//                            date.getYear(),
//                            date.getMonthValue(),
//                            date.getDayOfMonth(),
//                            DatatypeConstants.FIELD_UNDEFINED
//                    );
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
