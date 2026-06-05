package com.MT_MX.demo.utils;
import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.iso20022.pacs.*;
import com.MT_MX.demo.semantic.*;
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

public class DocumentUtil {
    private DocumentUtil(){}
    public static Document buildMX(SwiftAst ast) throws Exception {


        Document document = new Document();
        FIToFICustomerCreditTransferV13 fiToFi =new FIToFICustomerCreditTransferV13();

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
            document.setFIToFICstmrCdtTrf(fiToFi);
            return document;
        }

        // ======================================================
        // 2️⃣ TRANSACTION
        // ======================================================
        CreditTransferTransaction70 tx =
                new CreditTransferTransaction70();

        // --- Payment Identification ---
        PaymentIdentification13 pmtId = new PaymentIdentification13();
        pmtId.setInstrId(b4.getFieldValue("20"));
         pmtId.setEndToEndId("NOTPROVIDED");

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
            tx.setPmtTpInf(pTI);
        }
        if(b3.getFieldValue("403")!=null) {
            InstructionForNextAgent1 instr = new InstructionForNextAgent1();
            instr.setInstrInf(b3.getFieldValue("403"));
            tx.getInstrForNxtAgt().add(instr);
        }
        tx.setPmtId(pmtId);

        // ======================================================
// 23B → Bank Operation Code
// ======================================================
        String field23B = b4.getFieldValue("23B");

        if (field23B != null && !field23B.isBlank()) {

            PaymentTypeInformation28 pmtTpInf =
                    new PaymentTypeInformation28();

            LocalInstrument2Choice localInstr =
                    new LocalInstrument2Choice();

            localInstr.setCd(field23B.trim().toUpperCase());

            pmtTpInf.setLclInstrm(localInstr);

            tx.setPmtTpInf(pmtTpInf);
        }
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
// ======================================================
// 33B → Instructed Amount (optional fallback to 32A)
// ======================================================
        FieldNode f33B = b4.getField("33B");
        Field32AValue val = null;              ///bug

        if (f33B != null && !f33B.getValue().isBlank()) {
            String valStr = f33B.getValue().trim();
            String currency = valStr.substring(0, 3);
            BigDecimal amount = new BigDecimal(valStr.substring(3).replace(",", "."));

            ActiveOrHistoricCurrencyAndAmount  instdAmt = new ActiveOrHistoricCurrencyAndAmount ();
            instdAmt.setCcy(currency);
            instdAmt.setValue(amount);

            tx.setInstdAmt(instdAmt);
        } else if (val != null) {
            // fallback: use 32A value if 33B is missing
            ActiveOrHistoricCurrencyAndAmount  instdAmt = new ActiveOrHistoricCurrencyAndAmount ();
            instdAmt.setCcy(val.getCurrency());
            instdAmt.setValue(val.getAmount());
            tx.setInstdAmt(instdAmt);
        }        // ======================================================
        // 50K → Debtor
        // ======================================================
        OrderingCustomer ordering =
                Field50KParser.parse(b4.getField("50K"));

        if (ordering != null) {

            PartyIdentification272 debtor = new PartyIdentification272();
            debtor.setNm(ordering.getName());

            if (ordering.getAddress() != null) {
                PostalAddress27 adr = new PostalAddress27();
                adr.getAdrLine().addAll(ordering.getAddress());
                debtor.setPstlAdr(adr);
            }

            tx.setDbtr(debtor);

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
        // ======================================================
// 50F → Debtor (Structured Ordering Customer)
// ======================================================
        OrderingCustomer ordering50F =
                Field50FParser.parse(b4.getField("50F"));

        if (ordering50F != null) {

            PartyIdentification272 debtor = new PartyIdentification272();
            debtor.setNm(ordering50F.getName());

            if (ordering50F.getAddress() != null) {
                PostalAddress27 adr = new PostalAddress27();
                adr.getAdrLine().addAll(ordering50F.getAddress());
                debtor.setPstlAdr(adr);
            }

            tx.setDbtr(debtor);

            CashAccount40 dbtrAcct = new CashAccount40();
            AccountIdentification4Choice acctId =
                    new AccountIdentification4Choice();

            GenericAccountIdentification1 othr =
                    new GenericAccountIdentification1();
            othr.setId(ordering50F.getAccount());

            acctId.setOthr(othr);
            dbtrAcct.setId(acctId);

            tx.setDbtrAcct(dbtrAcct);
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
        // ======================================================
        // 52A
        // ======================================================
        String bic = Field52AParser.parse(b4.getField("52A"));

        if(bic != null){

            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(bic);

            dbtrAgt.setFinInstnId(finInst);

            tx.setDbtrAgt(dbtrAgt);
        }
        // ======================================================
        // 52D → Ordering Institution
        // ======================================================
        OrderingInstitution orderingInstitution52D=Field52DParser.parse(b4.getField("52D"));
        if(orderingInstitution52D!=null) {
            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setNm(orderingInstitution52D.getName());

            PostalAddress27 adr = new PostalAddress27();
            adr.getAdrLine().addAll(orderingInstitution52D.getAddress());

            finInst.setPstlAdr(adr);

            BranchAndFinancialInstitutionIdentification8 dbtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();

            dbtrAgt.setFinInstnId(finInst);

            tx.setDbtrAgt(dbtrAgt);
        }
        // 57A → Creditor Agent
        FieldNode f57A = b4.getField("57A");
        if (f57A != null && f57A.getValue() != null && !f57A.getValue().isBlank()) {
            String bic1 = SwiftBicUtil.normalizeBic(f57A.getValue());

            BranchAndFinancialInstitutionIdentification8 cdtrAgt =
                    new BranchAndFinancialInstitutionIdentification8();
            FinancialInstitutionIdentification23 finInst = new FinancialInstitutionIdentification23();
            finInst.setBICFI(bic1);  // normalized BIC
            cdtrAgt.setFinInstnId(finInst);
            tx.setCdtrAgt(cdtrAgt);
        }
        // ======================================================
        // 59 → Creditor
        // ======================================================
        BeneficiaryCustomer ben =
                Field59Parser.parse(b4.getField("59"));

        if (ben != null) {

            PartyIdentification272 creditor = new PartyIdentification272();
            creditor.setNm(ben.getName());

            if (ben.getAddress() != null) {
                PostalAddress27 adr = new PostalAddress27();
                adr.getAdrLine().addAll(ben.getAddress());
                creditor.setPstlAdr(adr);
            }

            tx.setCdtr(creditor);

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
        // --- 70 → Remittance Information ---
        String rmtInfo = b4.getFieldValue("70");
        if (rmtInfo != null && !rmtInfo.isBlank()) {
            RemittanceInformation22 remittance = new RemittanceInformation22();

            // Unstructured remittance
            remittance.getUstrd().add(rmtInfo);

            tx.setRmtInf(remittance);
        }
//        // 71F ->Sender charges
        String senderCharges = b4.getFieldValue("71F");

        if (senderCharges != null && !senderCharges.isBlank()) {
            System.out.println(senderCharges);
            // Extract currency (first 3 characters)
            String currency = senderCharges.substring(0, 3);

            // Extract amount part
            String amountStr = senderCharges.substring(3).replace(",", ".");

            BigDecimal amount = new BigDecimal(amountStr);

            Charges16 charges16 = new Charges16();

            // Set Amount
            ActiveOrHistoricCurrencyAndAmount amt =
                    new ActiveOrHistoricCurrencyAndAmount();

            amt.setCcy(currency);
            amt.setValue(amount);

            charges16.setAmt(amt);
            BranchAndFinancialInstitutionIdentification8 instgAgt =
                    new BranchAndFinancialInstitutionIdentification8();
            FinancialInstitutionIdentification23 finInst =
                    new FinancialInstitutionIdentification23();

            finInst.setBICFI(SwiftBicUtil.extractBic(b1));
            instgAgt.setFinInstnId(finInst);
            charges16.setAgt(instgAgt);
            tx.getChrgsInf().add(charges16);
        }
//        List<FieldNode> f71Fs = b4.getFieldValue("71F");
//
//        if (f71Fs != null) {
//            for (FieldNode f : f71Fs) {
//
//                String senderCharges = f.getValue();
//
//                if (senderCharges == null || senderCharges.isBlank()) continue;
//                if (senderCharges.length() < 4) continue;
//
//                String currency = senderCharges.substring(0, 3);
//                String amountStr = senderCharges.substring(3).replace(",", ".");
//
//                BigDecimal amount = new BigDecimal(amountStr);
//
//                Charges16 charges16 = new Charges16();
//
//                ActiveOrHistoricCurrencyAndAmount amt =
//                        new ActiveOrHistoricCurrencyAndAmount();
//
//                amt.setCcy(currency);
//                amt.setValue(amount);
//
//                charges16.setAmt(amt);
//
//                // Agent = Sender bank
//                if (b1 != null) {
//                    BranchAndFinancialInstitutionIdentification8 instgAgt =
//                            new BranchAndFinancialInstitutionIdentification8();
//
//                    FinancialInstitutionIdentification23 finInst =
//                            new FinancialInstitutionIdentification23();
//
//                    finInst.setBICFI(SwiftBicUtil.extractBic(b1));
//
//                    instgAgt.setFinInstnId(finInst);
//                    charges16.setAgt(instgAgt);
//                }
//
//                tx.getChrgsInf().add(charges16);
//            }
//        }
        // ======================================================
        // 71A → Charges
        // ======================================================
// 71A → Charges
        String charges = b4.getFieldValue("71A");

        if (charges == null || charges.isBlank()) {
            throw new RuntimeException("MT103 validation failed: missing 71A field");
        }

// Trim spaces and normalize to uppercase
        charges = charges.trim().toUpperCase();

// Now map safely
        tx.setChrgBr(mapChargeBearer(charges));
        System.out.println("Raw 71A value: '" + b4.getFieldValue("71A") + "'");
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
                        // Unknown → SupplementaryData
                        SupplementaryData1 sup = new SupplementaryData1();
                        sup.setPlcAndNm("Field72");
                        tx.getSplmtryData().add(sup);
                }
            }
        }
        String chargesG = b4.getFieldValue("71G");
        if (chargesG != null && !chargesG.isBlank()) {
            // 1️⃣ Parse currency & amount
            String currency = chargesG.substring(0, 3);
            BigDecimal amount = new BigDecimal(chargesG.substring(3).replace(",", "."));

            // 2️⃣ Create Charges16
            Charges16 charge = new Charges16();
            ActiveOrHistoricCurrencyAndAmount amt = new ActiveOrHistoricCurrencyAndAmount();
            amt.setCcy(currency);
            amt.setValue(amount);
            charge.setAmt(amt);

            // 3️⃣ Decide which agent to use (57A BIC if present, else Block 1)
            String bicFor71G = null;
            FieldNode f57 = b4.getField("57A");
            if (f57 != null && !f57.getValue().isBlank()) {
                bicFor71G = SwiftBicUtil.normalizeBic(f57.getValue());
            }

            BranchAndFinancialInstitutionIdentification8 instgAgt = new BranchAndFinancialInstitutionIdentification8();
            FinancialInstitutionIdentification23 finInst = new FinancialInstitutionIdentification23();

            if (bicFor71G != null) {
                finInst.setBICFI(bicFor71G);   // Use 57A BIC
            } else {
                finInst.setBICFI(SwiftBicUtil.extractBic(b1)); // fallback: Block 1 BIC
            }

            instgAgt.setFinInstnId(finInst);
            charge.setAgt(instgAgt);

            // 4️⃣ Add to transaction
            tx.getChrgsInf().add(charge);
        }
        if (b3.getFieldValue("77B") != null) {

            List<RegulatoryReporting> parsed =
                    Field77BParser.parse(b3.getField("77B"));

            RegulatoryReporting3 reg = new RegulatoryReporting3();

            RegulatoryAuthority2 auth = new RegulatoryAuthority2();

            for (RegulatoryReporting rr : parsed) {

                String code = rr.getCode();
                List<String> values = rr.getValues();

                switch (code) {
                    case "BENEFRES" -> {

                        if (!values.isEmpty()) {
                            auth.setCtry(values.get(0)); // IN
                        }
                        if (values.size() > 1) {
                            StructuredRegulatoryReporting3 dtls =
                                    new StructuredRegulatoryReporting3();
                            dtls.setCd(code);
                            dtls.getInf().add(values.get(1));

                            reg.getDtls().add(dtls);
                        }
                    }
                    case "PURP" -> {
                        Purpose2Choice purp = new Purpose2Choice();
                        purp.setCd(values.get(0)); // GOODS
                        tx.setPurp(purp);
                    }
                    case "INV" -> {
                        RemittanceInformation22 rmt = tx.getRmtInf();
                        if (rmt == null) {
                            rmt = new RemittanceInformation22();
                            tx.setRmtInf(rmt);
                        }
                        rmt.getUstrd().add(values.get(0));
                    }
                    default -> {
                        StructuredRegulatoryReporting3 dtls =
                                new StructuredRegulatoryReporting3();
                        dtls.setCd(code);
                        dtls.getInf().add(String.join(" ", values));
                        reg.getDtls().add(dtls);
                    }
                }
            }
            if (auth.getCtry() != null) {
                reg.setAuthrty(auth);
            }
            if (!reg.getDtls().isEmpty()) {
                tx.getRgltryRptg().add(reg);
            }
        }
        fiToFi.getCdtTrfTxInf().add(tx);
        document.setFIToFICstmrCdtTrf(fiToFi);
        return document;
    }
    // ==========================================================
    // Charge Mapping
    // ==========================================================
    private static ChargeBearerType1Code mapChargeBearer(String mt71A) {

        if (mt71A == null) return ChargeBearerType1Code.SHAR;

        switch (mt71A.toUpperCase().trim()) {
            case "OUR":
                return ChargeBearerType1Code.DEBT;
            case "BEN":
                return ChargeBearerType1Code.CRED;
            case "SHA":
                return ChargeBearerType1Code.SHAR;
            default:
                return ChargeBearerType1Code.SHAR;
        }
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
