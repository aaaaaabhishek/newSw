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

        //  GROUP HEADER
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
        // 52A
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
        // 56A
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
