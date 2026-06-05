package com.MT_MX.demo.utils;

import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.iso20022.pacs_009_001_12.*;
import com.MT_MX.demo.semantic.*;
import com.MT_MX.demo.semantic.OrderingInstitutionIdentifier;
import com.MT_MX.demo.semantic.parser.*;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Document202Cov {
    public static Document buildMX(SwiftAst ast) throws Exception {
        Document document=new Document();

        FinancialInstitutionCreditTransferV12 fiToFi =new FinancialInstitutionCreditTransferV12();
        BlockNode b1=ast.getBlock(1);
        BlockNode b2=ast.getBlock(2);
        BlockNode b3=ast.getBlock(3);
        BlockNode b4=ast.getBlock(4);

        GroupHeader131 grpHdr=new GroupHeader131();

        grpHdr.setMsgId(b4.getFieldValue("20"));

        GregorianCalendar cal = GregorianCalendar.from(OffsetDateTime.now().toZonedDateTime());

        XMLGregorianCalendar xmlCal =
                DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(cal);

        grpHdr.setCreDtTm(xmlCal);
        grpHdr.setNbOfTxs("1");

        SettlementInstruction15 stlmInf=new SettlementInstruction15();
        stlmInf.setSttlmMtd(SettlementMethod1Code.INDA);
        grpHdr.setSttlmInf(stlmInf);
        if(b1!=null){
            BranchAndFinancialInstitutionIdentification8 instgAgt=new BranchAndFinancialInstitutionIdentification8();
            FinancialInstitutionIdentification23 finInst=new FinancialInstitutionIdentification23();
            finInst.setBICFI(SwiftBicUtil.extractBic(b1));
            instgAgt.setFinInstnId(finInst);
            grpHdr.setInstdAgt(instgAgt);
        }

        if (b2 != null) {
            BranchAndFinancialInstitutionIdentification8 instdAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInstTo =
                    new FinancialInstitutionIdentification23();

            finInstTo.setBICFI(SwiftBicUtil.extractBic(b2));

            instdAgt.setFinInstnId(finInstTo);

            grpHdr.setInstdAgt(instdAgt);
        }
        if (b4 == null) {
            document.setFICdtTrf(fiToFi);
            return document;
        }
        /// 20
//        CreditTransferTransaction67 cTTmain=new CreditTransferTransaction67();
//        CreditTransferTransaction68 cTT=new CreditTransferTransaction68();
        CreditTransferTransaction67 wrapper = new CreditTransferTransaction67();
        CreditTransferTransaction68 tx = new CreditTransferTransaction68();
        PaymentIdentification13 pI=new PaymentIdentification13();


        pI.setInstrId(b3.getFieldValue("20"));
        pI.setEndToEndId(b3.getFieldValue("21"));

        if(b3.getFieldValue("121")==null){
            throw new Exception("UTER is missing");
        }
        String uter=b3.getFieldValue("121");
        assert uter != null;
        pI.setUETR(uter.trim());
        tx.setPmtId(pI);
//        if(b3.getFieldValue("119")!=null){
//          PaymentTypeInformation28 paymentTypeInformation28=new PaymentTypeInformation28();
//
//          paymentTypeInformation28.setInstrPrty();
//        }
        if(b3.getFieldValue("111")!=null) {
            PaymentTypeInformation28 pTI=new PaymentTypeInformation28();
            //List<ServiceLevel8Choice> svcLvl = new ArrayList<>();
            ServiceLevel8Choice slc = new ServiceLevel8Choice();
            slc.setCd(b3.getFieldValue("111"));
            //  svcLvl.add(slc);
            pTI.getSvcLvl().add(slc);
            tx.setPmtTpInf(pTI);
        }
        if(b3.getFieldValue("403")!=null) {
            InstructionForNextAgent1 instr = new InstructionForNextAgent1();
            instr.setInstrInf(b3.getFieldValue("403"));
            tx.getInstrForNxtAgt().add(instr);
        }
        // ======================================================
        // 32A → Amount & Settlement Date
        // ======================================================
//        FieldNode f32A = b4.getField("32A");

        if ( b4.getFieldValue("32A")!= null) {

            Field32AValue val = Field32AParser.parse( b4.getField("32A"));
            if (val != null) {
                ActiveCurrencyAndAmount amt1 =
                        new ActiveCurrencyAndAmount();

                amt1.setCcy(val.getCurrency());
                amt1.setValue(val.getAmount());

                wrapper.setIntrBkSttlmAmt(amt1);

                wrapper.setIntrBkSttlmDt(toXmlDate(val.getValueDate()));
            }
        }
        String value33B = b4.getFieldValue("33B");
        if (value33B != null) {

            String currency = value33B.substring(0, 3);
            BigDecimal amount = new BigDecimal(value33B.substring(3).replace(",", "."));

            ActiveOrHistoricCurrencyAndAmount instdAmt =
                    new ActiveOrHistoricCurrencyAndAmount();

            instdAmt.setCcy(currency);
            instdAmt.setValue(amount);

            tx.setInstdAmt(instdAmt);
        }
        // ======================================================
        // 52A
        // ======================================================
        if(b4.getFieldValue("52A") != null){
            String bic52A = Field52AParser.parse(b4.getField("52A"));
            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(bic52A);

            dbtrAgt.setFinInstnId(finInst);

            tx.setDbtrAgt(dbtrAgt);
        }

        // ======================================================
        // 53A
        // ======================================================

        if(b4.getFieldValue("53A") != null){
            String bic53A = Field53AParser.parse(b4.getField("53A"));

            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(bic53A);

            dbtrAgt.setFinInstnId(finInst);

            tx.setIntrmyAgt1(dbtrAgt);
        }
        if(b4.getFieldValue("53B")!=null){
            OrderingInstitutionIdentifier accountNum= Field53BParser.parse(b4.getField("53B"));
            CashAccount40 intrmyAcct = new CashAccount40();
            AccountIdentification4Choice id = new AccountIdentification4Choice();
            GenericAccountIdentification1 gen = new GenericAccountIdentification1();
            gen.setId(accountNum.getAccount());
            id.setOthr(gen);
            intrmyAcct.setId(id);
            tx.setIntrmyAgt1Acct(intrmyAcct);
        }
        /// 53D Sender’s Correspondent

        if(b4.getFieldValue("53D")!=null){
            SenderCorrespondent senderCorrespondent53D= Field53DParser.parse(b4.getField("53D"));
            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();
            finInst.setNm(senderCorrespondent53D.getName());
            PostalAddress27 adr = new PostalAddress27();
            adr.getAdrLine().addAll(senderCorrespondent53D.getAddress());
            finInst.setPstlAdr(adr);
            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();
            dbtrAgt.setFinInstnId(finInst);
            tx.setDbtrAgt(dbtrAgt);
        }

        // ======================================================
        // 56A
        // ======================================================


        if(b4.getFieldValue("56A") != null){
            String bic56A = Field56AParser.parse(b4.getField("56A"));
            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(bic56A);

            dbtrAgt.setFinInstnId(finInst);

            tx.setIntrmyAgt2(dbtrAgt);
        }
        if(b4.getFieldValue("56D")!=null){
            IntermediaryInstitution intermediaryInstitution=Field56DParser.parse(b4.getField("56D"));
            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();
            finInst.setNm(intermediaryInstitution.getName());
            PostalAddress27 adr = new PostalAddress27();
            adr.getAdrLine().addAll(intermediaryInstitution.getAddress());
            finInst.setPstlAdr(adr);
            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();
            dbtrAgt.setFinInstnId(finInst);
            tx.setIntrmyAgt1(dbtrAgt);
        }
        // ======================================================
        // 57A
        // ======================================================
        String bic57A = Field57AParser.parse(b4.getField("57A"));

        if(bic57A != null){

            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(bic57A);

            dbtrAgt.setFinInstnId(finInst);

            tx.setCdtrAgt(dbtrAgt);
        }
        if(b4.getFieldValue("57D")!=null){
            AccountInstitution accountInstitution57D=Field57DParser.parse(b4.getField("57D"));
            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();
            finInst.setNm(accountInstitution57D.getName());
            PostalAddress27 adr = new PostalAddress27();
            adr.getAdrLine().addAll(accountInstitution57D.getAddress());
            finInst.setPstlAdr(adr);
            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();
            dbtrAgt.setFinInstnId(finInst);
            tx.setCdtrAgt(dbtrAgt);
        }
        // ======================================================
        // 58A
        // ======================================================
        String bic58A = Field58AParser.parse(b4.getField("58A"));

        if(bic58A != null){

            BranchAndFinancialInstitutionIdentification8 cdtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();
            finInst.setBICFI(bic58A);

            cdtrAgt.setFinInstnId(finInst);

            tx.setCdtrAgt(cdtrAgt);
        }
        if(b4.getFieldValue("58D")!=null){
            BeneficiaryInstitution beneficiaryInstitution58D=Field58DParser.parse(b4.getField("58D"));
            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();
            finInst.setNm(beneficiaryInstitution58D.getName());
            PostalAddress27 adr = new PostalAddress27();
            adr.getAdrLine().addAll(beneficiaryInstitution58D.getAddress());
            finInst.setPstlAdr(adr);
            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();
            dbtrAgt.setFinInstnId(finInst);
            tx.setCdtrAgt(dbtrAgt);
        }
        // ======================================================
        // 59 → Creditor
        // ======================================================
        BeneficiaryCustomer ben =
                Field59Parser.parse(b4.getField("59"));

        if (ben != null) {
            PartyIdentification272 partyIdentification272=new PartyIdentification272();
            partyIdentification272.setNm(ben.getName());

            if (ben.getAddress() != null) {
                PostalAddress27 adr = new PostalAddress27();
                adr.getAdrLine().addAll(ben.getAddress());
                partyIdentification272.setPstlAdr(adr);
            }
             tx.setCdtr(partyIdentification272);

            CashAccount40 cdtrAcct = new CashAccount40();
            AccountIdentification4Choice acctId =
                    new AccountIdentification4Choice();

            GenericAccountIdentification1 othr =
                    new GenericAccountIdentification1();
            othr.setId(ben.getAccount());

            acctId.setOthr(othr);
            cdtrAcct.setId(acctId);

            tx.setCdtrAcct(cdtrAcct);
        }
        List<Field72Instruction> instructions =
                Field72Parser.parse(b4.getField("72"));

        if (instructions != null && !instructions.isEmpty()) {
            for (Field72Instruction inst : instructions) {

                String code = inst.getCode();
                String text = inst.getInstruction();

                if (text == null || text.isBlank()) {
                    text = code;
                }
                System.out.println(code.toUpperCase());

                switch (code.toUpperCase().trim()) {
                    case "ACC":
                    case "BNF":
                        InstructionForCreditorAgent3 cdtrInstr =
                                new InstructionForCreditorAgent3();
                        cdtrInstr.setInstrInf(text);
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
                        nxtInstr.setInstrInf(text);
                        tx.getInstrForNxtAgt().add(nxtInstr);
                        break;

                    default:

                }
            }
        }
        // ======================================================
        // 59 → Creditor
        // ======================================================
        BeneficiaryCustomer ben59 =
                Field59Parser.parse(b4.getField("59"));

        if (ben59 != null) {

            // -------------------------
            // Creditor (Cdtr)
            // -------------------------
            PartyIdentification272 cdtr = new PartyIdentification272();

            cdtr.setNm(ben59.getName());

            if (ben59.getAddress() != null && !ben59.getAddress().isEmpty()) {
                PostalAddress27 adr = new PostalAddress27();
                adr.getAdrLine().addAll(ben59.getAddress());
                cdtr.setPstlAdr(adr);
            }

            tx.setCdtr(cdtr);

            // -------------------------
            // Creditor Account (CdtrAcct)
            // -------------------------
            if (ben59.getAccount() != null && !ben59.getAccount().isBlank()) {

                CashAccount40 cdtrAcct = new CashAccount40();

                AccountIdentification4Choice acctId =
                        new AccountIdentification4Choice();

                GenericAccountIdentification1 othr =
                        new GenericAccountIdentification1();

                othr.setId(ben59.getAccount());

                acctId.setOthr(othr);
                cdtrAcct.setId(acctId);

                tx.setCdtrAcct(cdtrAcct);
            }
        }
        // --- 70 → Remittance Information ---
        String rmtInfo = b4.getFieldValue("70");
        if (rmtInfo != null && !rmtInfo.isBlank()) {
           RemittanceInformation22 remittance = new RemittanceInformation22();
            remittance.getUstrd().add(rmtInfo);
            tx.setRmtInf(remittance);
        }
        // ======================================================
        // 50A → Debtor Agent (Bank)
        // ======================================================
        String bic50A = Field50AParser.parse(b4.getField("50A"));

        if (bic50A != null) {

            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(bic50A);

            dbtrAgt.setFinInstnId(finInst);

            tx.setDbtrAgt(dbtrAgt);
        }
        // 50K → Debtor
        // ======================================================
        OrderingCustomer ordering =
                Field50KParser.parse(b4.getField("50K"));

        if (ordering != null) {
            PartyIdentification272 partyIdentification272=new PartyIdentification272();

            partyIdentification272.setNm(ordering.getName());

            if (ordering.getAddress() != null) {
                PostalAddress27 adr = new PostalAddress27();
                adr.getAdrLine().addAll(ordering.getAddress());
                partyIdentification272.setPstlAdr(adr);
            }
            tx.setDbtr(partyIdentification272);

            CashAccount40 dbtrAcct = new CashAccount40();
            AccountIdentification4Choice acctId =
                    new AccountIdentification4Choice();

            GenericAccountIdentification1 othr =
                    new GenericAccountIdentification1();
            othr.setId(ordering.getAccount());

            acctId.setOthr(othr);
            dbtrAcct.setId(acctId);

            tx.setDbtrAcct(dbtrAcct);
        }
        /// 72
        String field72 = b4.getFieldValue("72");

        if (field72 != null && !field72.trim().isEmpty()) {
            InstructionForNextAgent1 instructionForNextAgent1 = new InstructionForNextAgent1();
            instructionForNextAgent1.setInstrInf(field72.trim());
            tx.getInstrForNxtAgt().add(instructionForNextAgent1);
        }
        wrapper.setUndrlygCstmrCdtTrf(tx);
        fiToFi.getCdtTrfTxInf().add(wrapper);
        document.setFICdtTrf(fiToFi);
        return document;
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

